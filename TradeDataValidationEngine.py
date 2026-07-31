import datetime
from abc import ABC, abstractmethod
from enum import Enum
from typing import Dict, List, Optional


# ==========================================
# 1. ENUMS AND DATA MODELS
# ==========================================

class OrderState(Enum):
    N = "N"    # New / Created
    PF = "PF"  # Partially Filled
    F = "F"    # Filled / Executed
    C = "C"    # Canceled


class OrderSide(Enum):
    BUY = "BUY"
    SELL = "SELL"


class OrderRecord:
    """Represents a single raw row in the dataset log."""
    def __init__(
        self, 
        time_str: str, 
        order_id: str, 
        state_str: str, 
        price: float, 
        qty: float, 
        side_str: str,
        price_filled: Optional[float] = None, 
        qty_filled: Optional[float] = None
    ):
        self.time: datetime.time = datetime.datetime.strptime(time_str, "%H:%M").time()
        self.id: str = order_id
        self.state: OrderState = OrderState[state_str]
        self.price: float = float(price)
        self.qty: float = float(qty)
        self.side: OrderSide = OrderSide[side_str]
        self.price_filled: Optional[float] = float(price_filled) if price_filled else None
        self.qty_filled: Optional[float] = float(qty_filled) if qty_filled else None

    def __repr__(self):
        return f"[{self.time}] {self.id} {self.state.value} Qty:{self.qty_filled}/{self.qty}"


# ==========================================
# 2. VALIDATION STRATEGY (LLD INTERFACE)
# ==========================================

class ValidationRule(ABC):
    """Abstract Strategy for validation."""
    @abstractmethod
    def validate(self, record: OrderRecord, history: List[OrderRecord], cum_filled: float) -> Optional[str]:
        """
        Returns an error message string if validation fails, otherwise returns None.
        """
        pass


# ==========================================
# 3. CONCRETE VALIDATION RULE IMPLEMENTATIONS
# ==========================================

class RowBoundaryRule(ValidationRule):
    """Checks basic value ranges and state-dependent nulls."""
    def validate(self, record: OrderRecord, history: List[OrderRecord], cum_filled: float) -> Optional[str]:
        if record.price <= 0 or record.qty <= 0:
            return "PRICE and QTY must be strictly positive numbers."
        
        if record.state in (OrderState.N, OrderState.C):
            if record.qty_filled is not None or record.price_filled is not None:
                return f"State {record.state.value} should not contain execution values (QTY_FILLED/PRICE_FILLED)."
                
        if record.state in (OrderState.PF, OrderState.F):
            if record.qty_filled is None or record.price_filled is None:
                return f"Execution state {record.state.value} missing QTY_FILLED or PRICE_FILLED."
            if record.qty_filled <= 0 or record.price_filled <= 0:
                return "Execution volume and price must be strictly positive numbers."
        return None


class InvarianceRule(ValidationRule):
    """Ensures static parameters do not change over an order's lifecycle."""
    def validate(self, record: OrderRecord, history: List[OrderRecord], cum_filled: float) -> Optional[str]:
        if not history:
            return None
        initial = history[0]
        if record.side != initial.side:
            return f"SIDE changed from {initial.side.value} to {record.side.value}."
        if record.qty != initial.qty:
            return f"Total QTY changed from {initial.qty} to {record.qty}."
        if record.price != initial.price:
            return f"Limit PRICE changed from {initial.price} to {record.price}."
        return None


class LifecycleStateRule(ValidationRule):
    """Enforces state machine logic and timeline terminal status."""
    def validate(self, record: OrderRecord, history: List[OrderRecord], cum_filled: float) -> Optional[str]:
        if not history:
            return None
        
        last_rec = history[-1]
        
        # Chronological sanity check
        if record.time < last_rec.time:
            return f"Chronological anomaly: timestamp {record.time} occurs before previous entry {last_rec.time}."
            
        # Terminal state protection
        if last_rec.state in (OrderState.F, OrderState.C):
            return f"Illegal update: Order already reached its terminal state ({last_rec.state.value})."
            
        return None


class MathematicalConsistencyRule(ValidationRule):
    """Tracks filling quotas and balances."""
    def validate(self, record: OrderRecord, history: List[OrderRecord], cum_filled: float) -> Optional[str]:
        current_fill = record.qty_filled if record.qty_filled else 0.0
        new_cum_total = cum_filled + current_fill
        
        if new_cum_total > record.qty:
            return f"Overfill Error: Cumulative volume ({new_cum_total}) exceeds order QTY ({record.qty})."
            
        if record.state == OrderState.F and not abs(new_cum_total - record.qty) < 1e-9:
            return f"State is F (Filled) but cumulative volume ({new_cum_total}) does not match total QTY ({record.qty})."
            
        if record.state == OrderState.C and abs(new_cum_total - record.qty) < 1e-9:
            return "State is C (Canceled) but the order was fully filled."
            
        return None


class FinancialMarketRule(ValidationRule):
    """Ensures that execution price honors or improves the limit rule constraint."""
    def validate(self, record: OrderRecord, history: List[OrderRecord], cum_filled: float) -> Optional[str]:
        if record.state in (OrderState.PF, OrderState.F):
            if record.side == OrderSide.BUY and record.price_filled > record.price:
                return f"Price Violation: BUY order filled at {record.price_filled}, higher than limit {record.price}."
            if record.side == OrderSide.SELL and record.price_filled < record.price:
                return f"Price Violation: SELL order filled at {record.price_filled}, lower than limit {record.price}."
        return None


# ==========================================
# 4. ORDER LIFECYCLE ENGINE (THE PIPELINE)
# ==========================================

class OrderLifecycleEngine:
    def __init__(self):
        self.rules: List[ValidationRule] = []
        # Maps order_id -> running list of valid historic records processed so far
        self.order_histories: Dict[str, List[OrderRecord]] = {}
        # Maps order_id -> aggregated running filled quantities
        self.running_fills: Dict[str, float] = {}

    def add_rule(self, rule: ValidationRule) -> "OrderLifecycleEngine":
        self.rules.append(rule)
        return self

    def validate_dataset(self, records: List[OrderRecord]) -> List[str]:
        """Processes logs sorted by timeline to accurately catch sequence bugs."""
        errors = []
        
        # Crucial for lifecycle testing: ensure we validate across chronologically ordered time
        sorted_records = sorted(records, key=lambda r: r.time)

        for rec in sorted_records:
            hist = self.order_histories.get(rec.id, [])
            cum_f = self.running_fills.get(rec.id, 0.0)
            
            row_failed = False
            for rule in self.rules:
                error_msg = rule.validate(rec, hist, cum_f)
                if error_msg:
                    errors.append(f"Row Error [ID: {rec.id} at {rec.time}]: {error_msg}")
                    row_failed = True
                    break # Halt checking this specific entry on first broken rule
            
            if not row_failed:
                # Update running state trackers only if the row is logically valid
                if rec.id not in self.order_histories:
                    self.order_histories[rec.id] = []
                self.order_histories[rec.id].append(rec)
                if rec.qty_filled:
                    self.running_fills[rec.id] = cum_f + rec.qty_filled

        return errors


# ==========================================
# 5. EXECUTION & TEST HARNESS
# ==========================================

if __name__ == "__main__":
    # Parsing your provided sample data inputs
    raw_data = [
        {"time_str": "00:00", "order_id": "a1", "state_str": "N",  "price": 100, "price_filled": None, "qty": 100, "qty_filled": None, "side_str": "BUY"},
        {"time_str": "00:01", "order_id": "a1", "state_str": "C",  "price": 100, "price_filled": None, "qty": 100, "qty_filled": None, "side_str": "BUY"},
        {"time_str": "00:02", "order_id": "a2", "state_str": "N",  "price": 100, "price_filled": None, "qty": 100, "qty_filled": None, "side_str": "SELL"},
        {"time_str": "00:03", "order_id": "a2", "state_str": "PF", "price": 100, "price_filled": 102,  "qty": 100, "qty_filled": 40,   "side_str": "SELL"},
        {"time_str": "00:04", "order_id": "a2", "state_str": "F",  "price": 100, "price_filled": 101,  "qty": 100, "qty_filled": 60,   "side_str": "SELL"},
        
        # Injecting bad test rows to verify engine catches exceptions
        {"time_str": "00:05", "order_id": "a2", "state_str": "C",  "price": 100, "price_filled": None, "qty": 100, "qty_filled": None, "side_str": "SELL"}, # Action after terminal filled state
        {"time_str": "00:06", "order_id": "a3", "state_str": "N",  "price": 100, "price_filled": None, "qty": 100, "qty_filled": None, "side_str": "BUY"},
        {"time_str": "00:07", "order_id": "a3", "state_str": "F",  "price": 100, "price_filled": 105,  "qty": 100, "qty_filled": 100,  "side_str": "BUY"}  # Price violation: buy filled higher than limit
    ]

    parsed_records = [OrderRecord(**row) for row in raw_data]

    # Instantiate LLD Engine Components
    engine = OrderLifecycleEngine()
    
    engine.add_rule(RowBoundaryRule())\
          .add_rule(InvarianceRule())\
          .add_rule(LifecycleStateRule())\
          .add_rule(MathematicalConsistencyRule())\
          .add_rule(FinancialMarketRule())

    # Execute assertions
    validation_failures = engine.validate_dataset(parsed_records)

    print(f"--- VALIDATION RESULTS ({len(validation_failures)} Failures Identified) ---")
    for failure in validation_failures:
        print(failure)
