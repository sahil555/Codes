from __future__ import annotations
from abc import ABC, abstractmethod
from dataclasses import dataclass
from enum import Enum
from typing import Protocol, runtime_checkable

class PaymentStatus(Enum):
    PENDING = "PENDING"
    SUCCESS = "SUCCESS"
    FAILED = "FAILED"
    REFUNDED = "REFUNDED"

class PaymentException(Exception):
    """Base exception for all payment-related processing errors."""
    def __init__(self, message: str, provider: str):
        super().__init__(f"[{provider} Error] {message}")
        self.provider = provider

class PaymentFailedException(PaymentException):
    """Raised when a payment transaction explicitly fails or gets rejected."""
    pass

class RefundFailedException(PaymentException):
    """Raised when a refund request fails processing rules."""
    pass


@dataclass(frozen=True)
class PaymentRequest:
    amount: float
    currency: str
    order_id: str

@dataclass(frozen=True)
class PaymentResponse:
    transaction_id: str
    status: PaymentStatus
    error_message: str | None = None

@dataclass(frozen=True)
class RefundRequest:
    transaction_id: str
    amount: float

@dataclass(frozen=True)
class RefundResponse:
    refund_id: str
    status: PaymentStatus

# Pattern A: Modern Structural Interface (Implicit Duck-Typing with Runtime Check support)
@runtime_checkable
class PaymentProvider(Protocol):
    def create_payment(self, request: PaymentRequest) -> PaymentResponse: ...
    def get_payment_status(self, transaction_id: str) -> PaymentStatus: ...
    def refund(self, request: RefundRequest) -> RefundResponse: ...


# Pattern B: Old-School Nominal Interface (Strict Java-like Inheritance Validation)
class StrictPaymentProvider(ABC):
    @abstractmethod
    def create_payment(self, request: PaymentRequest) -> PaymentResponse:
        pass

    @abstractmethod
    def get_payment_status(self, transaction_id: str) -> PaymentStatus:
        pass

    @abstractmethod
    def refund(self, request: RefundRequest) -> RefundResponse:
        pass


class StripePaymentProvider:
    """Implements PaymentProvider via modern Protocol structural typing."""
    
    def create_payment(self, request: PaymentRequest) -> PaymentResponse:
        print(f"[Stripe] Processing payment of {request.amount} {request.currency} for order {request.order_id}")
        if request.amount <= 0:
            raise PaymentFailedException("Amount must be greater than zero.", provider="Stripe")
        return PaymentResponse(transaction_id="ch_stripe_12345", status=PaymentStatus.SUCCESS)

    def get_payment_status(self, transaction_id: str) -> PaymentStatus:
        print(f"[Stripe] Fetching status for {transaction_id}")
        return PaymentStatus.SUCCESS

    def refund(self, request: RefundRequest) -> RefundResponse:
        print(f"[Stripe] Refunding {request.amount} on transaction {request.transaction_id}")
        if not request.transaction_id:
            raise RefundFailedException("Invalid transaction ID provided.", provider="Stripe")
        return RefundResponse(refund_id="re_stripe_98765", status=PaymentStatus.REFUNDED)


class StrictPayPalPaymentProvider(StrictPaymentProvider):
    """Implements StrictPaymentProvider via Java-style Nominal ABC inheritance."""
    
    def create_payment(self, request: PaymentRequest) -> PaymentResponse:
        print(f"[PayPal] Creating express checkout for {request.amount} {request.currency}")
        if request.amount > 10000:
            raise PaymentFailedException("Transaction exceeds maximum limit.", provider="PayPal")
        return PaymentResponse(transaction_id="PAYID-LP54321", status=PaymentStatus.PENDING)

    def get_payment_status(self, transaction_id: str) -> PaymentStatus:
        print(f"[PayPal] Checking order log for {transaction_id}")
        return PaymentStatus.PENDING

    def refund(self, request: RefundRequest) -> RefundResponse:
        print(f"[PayPal] Issuing refund for transaction {request.transaction_id}")
        return RefundResponse(refund_id="REFID-5555", status=PaymentStatus.REFUNDED)

class CheckoutService:
    def __init__(self, provider: PaymentProvider):
        # Enforces object conforms structurally to our interface layout
        if not isinstance(provider, PaymentProvider):
            raise TypeError("Injected provider does not conform to the PaymentProvider interface template!")
        self.provider = provider

    def process_checkout(self, amount: float, order_id: str) -> None:
        try:
            req = PaymentRequest(amount=amount, currency="INR", order_id=order_id)
            res = self.provider.create_payment(req)
            print(f"Checkout Result -> ID: {res.transaction_id}, Status: {res.status.value}\n")
        except PaymentException as e:
            print(f"Checkout System caught operational exception: {e}\n")

if __name__ == "__main__":
    # Test Case 1: Stripe (Protocol Structural Verification)
    print("--- Executing Stripe Checkout (Structural Interface) ---")
    stripe_service = CheckoutService(provider=StripePaymentProvider())
    stripe_service.process_checkout(199.99, "ORD-001")

    # Test Case 2: PayPal (ABC Subclass Verification)
    print("--- Executing PayPal Checkout (Nominal Interface) ---")
    paypal_service = CheckoutService(provider=StrictPayPalPaymentProvider())
    paypal_service.process_checkout(145.50, "ORD-002")

    # Test Case 3: Error Exception Handling Safety
    print("--- Executing Error Scenario Case ---")
    error_flow_service = CheckoutService(provider=StripePaymentProvider())
    error_flow_service.process_checkout(-10.50, "ORD-ERR")
