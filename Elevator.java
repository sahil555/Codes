import java.util.Collections;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

enum Direction {
    UP, DOWN, IDLE
}

enum DoorState {
    OPEN, CLOSED
}

/**
 * Thread-safe Model representing the physical elevator attributes.
 */
class ElevatorCar {
    private final int id;
    private final AtomicInteger currentFloor = new AtomicInteger(0);
    private volatile Direction currentDirection = Direction.IDLE;
    private volatile DoorState doorState = DoorState.CLOSED;

    public ElevatorCar(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getCurrentFloor() {
        return currentFloor.get();
    }

    public void setCurrentFloor(int floor) {
        this.currentFloor.set(floor);
    }

    public Direction getDirection() {
        return currentDirection;
    }

    public void setDirection(Direction direction) {
        this.currentDirection = direction;
    }

    public DoorState getDoorState() {
        return doorState;
    }

    public void setDoorState(DoorState state) {
        this.doorState = state;
    }
}

/**
 * Handles concurrent state updates and request queues using the SCAN Algorithm.
 */
class ElevatorController implements Runnable {
    private final ElevatorCar elevatorCar;

    // SCAN Data structures: Min-Heap for UP tracking, Max-Heap for DOWN tracking
    private final PriorityQueue<Integer> upQueue;
    private final PriorityQueue<Integer> downQueue;

    // Concurrency Tools
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition hasRequests = lock.newCondition();

    public ElevatorController(ElevatorCar elevatorCar) {
        this.elevatorCar = elevatorCar;
        this.upQueue = new PriorityQueue<>(); // Natural order (Min-Heap)
        this.downQueue = new PriorityQueue<>(Collections.reverseOrder()); // Reverse order (Max-Heap)
    }

    /**
     * Thread-Safe entry point for adding a floor request (hall call or cabin call)
     */
    public void addRequest(int targetFloor) {
        lock.lock();
        try {
            int currentFloor = elevatorCar.getCurrentFloor();
            
            // If already there, do nothing or just trigger a door toggle
            if (currentFloor == targetFloor) {
                hasRequests.signal();
                return;
            }

            // Distribute requests based on relative floor positions (SCAN Strategy)
            if (targetFloor > currentFloor) {
                if (!upQueue.contains(targetFloor)) upQueue.add(targetFloor);
            } else {
                if (!downQueue.contains(targetFloor)) downQueue.add(targetFloor);
            }

            System.out.println("Thread [" + Thread.currentThread().getName() + 
                               "] added request to floor " + targetFloor + " for Car " + elevatorCar.getId());
            
            // Wake up the processing thread if it was idling
            hasRequests.signal();
        } catch(Exception e) {
            lock.unlock();
            System.err.println(e);
        }
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            lock.lock();
            try {
                // Thread Sleep Condition: Wait safely while both queues are empty
                while (upQueue.isEmpty() && downQueue.isEmpty()) {
                    elevatorCar.setDirection(Direction.IDLE);
                    System.out.println("Car " + elevatorCar.getId() + " is IDLE. Awaiting requests...");
                    hasRequests.await();
                }

                // Determine or switch current scanning directions
                determineDirection();

                int nextFloor = getNextTargetFloor();
                lock.unlock(); // Release lock while performing physical movement/delays

                moveToFloor(nextFloor);

                lock.lock(); // Re-acquire lock to safely clean completed request
                removeReachedFloor(nextFloor);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
    }

    private void determineDirection() {
        if (elevatorCar.getDirection() == Direction.IDLE) {
            elevatorCar.setDirection(upQueue.isEmpty() ? Direction.DOWN : Direction.UP);
        } else if (elevatorCar.getDirection() == Direction.UP && upQueue.isEmpty()) {
            elevatorCar.setDirection(downQueue.isEmpty() ? Direction.IDLE : Direction.DOWN);
        } else if (elevatorCar.getDirection() == Direction.DOWN && downQueue.isEmpty()) {
            elevatorCar.setDirection(upQueue.isEmpty() ? Direction.IDLE : Direction.UP);
        }
    }

    private int getNextTargetFloor() {
        if (elevatorCar.getDirection() == Direction.UP) {
            return upQueue.peek();
        } else {
            return downQueue.peek();
        }
    }

    private void removeReachedFloor(int floor) {
        if (elevatorCar.getDirection() == Direction.UP) {
            upQueue.remove(floor);
        } else {
            downQueue.remove(floor);
        }
    }

    private void moveToFloor(int targetFloor) throws InterruptedException {
        int current = elevatorCar.getCurrentFloor();
        System.out.println("Car " + elevatorCar.getId() + " leaves floor " + current + " towards floor " + targetFloor);

        // Simulating physical movement step by step
        while (current != targetFloor) {
            Thread.sleep(500); // 0.5s per floor simulation
            current += (targetFloor > current) ? 1 : -1;
            elevatorCar.setCurrentFloor(current);
            System.out.println("Car " + elevatorCar.getId() + " passing floor: " + current);
        }

        // Simulating arrival sequence safely
        elevatorCar.setDoorState(DoorState.OPEN);
        System.out.println(">> Car " + elevatorCar.getId() + " ARRIVED at floor " + targetFloor + ". Doors OPEN.");
        Thread.sleep(1000); // Passengers getting in/out
        elevatorCar.setDoorState(DoorState.CLOSED);
        System.out.println(">> Car " + elevatorCar.getId() + " doors CLOSED.");
    }}

public class Elevator {
        public static void main(String[] args) throws InterruptedException {
            ElevatorCar car = new ElevatorCar(1);
            ElevatorController controller = new ElevatorController(car);

            // Start the Elevator background thread execution
            Thread elevatorThread = new Thread(controller, "Elevator-Engine-1");
            elevatorThread.start();

            Thread.sleep(50); // Let it boot up to IDLE state

            // Simulate concurrent button pushes from passengers on various floors
            Runnable passengerA = () -> controller.addRequest(5);
            Runnable passengerB = () -> controller.addRequest(2);
            Runnable passengerC = () -> controller.addRequest(8);
            Runnable passengerD = () -> controller.addRequest(1);

            new Thread(passengerA, "User-Floor-5").start();
            new Thread(passengerB, "User-Floor-2").start();
            new Thread(passengerC, "User-Floor-8").start();

            Thread.sleep(20); // Wait a bit while it scans upwards

            // Late request comes in down below while the elevator is high up
            new Thread(passengerD, "User-Floor-1").start();

            // Let the simulation run for a few seconds before wrapping up
            Thread.sleep(1000);
            elevatorThread.interrupt();
        }
}