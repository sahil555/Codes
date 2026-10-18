import asyncio
import concurrent.futures
import multiprocessing
import threading
import time


# ==========================================
# 1. WORKLOAD DEFINITIONS
# ==========================================
def io_bound_task(task_id: int, duration: float = 0.1) -> str:
    """Simulates blocking I/O (e.g., HTTP request or database query)."""
    time.sleep(duration)
    return f"IO Task {task_id} complete"


async def async_io_bound_task(task_id: int, duration: float = 0.1) -> str:
    """Asynchronous I/O task that cooperatively yields control."""
    await asyncio.sleep(duration)
    return f"Async Task {task_id} complete"


def cpu_bound_task(n: int = 10_000_000) -> int:
    """Simulates CPU-bound computation."""
    count = 0
    for i in range(n):
        count += i
    return count


# ==========================================
# 2. RUNNERS
# ==========================================
def run_threading(tasks_count: int) -> float:
    start = time.perf_counter()
    threads = []
    for i in range(tasks_count):
        t = threading.Thread(target=io_bound_task, args=(i,))
        threads.append(t)
        t.start()
    for t in threads:
        t.join()
    return time.perf_counter() - start


def run_multiprocessing(tasks_count: int) -> float:
    start = time.perf_counter()
    # Using ProcessPoolExecutor to manage worker processes
    with concurrent.futures.ProcessPoolExecutor() as executor:
        futures = [
            executor.submit(cpu_bound_task) for _ in range(tasks_count)
        ]
        concurrent.futures.wait(futures)
    return time.perf_counter() - start


async def run_asyncio(tasks_count: int) -> float:
    start = time.perf_counter()
    async with asyncio.TaskGroup() as tg:
        for i in range(tasks_count):
            tg.create_task(async_io_bound_task(i))
    return time.perf_counter() - start


if __name__ == "__main__":
    NUM_TASKS = 50
    print(
        f"Threading (50 I/O tasks): {run_threading(NUM_TASKS):.3f}s"
    )
    print(
        f"Asyncio (50 Async I/O tasks): {asyncio.run(run_asyncio(NUM_TASKS)):.3f}s"
    )

    # Running 4 heavy CPU tasks via Multiprocessing
    print(
        f"Multiprocessing (4 CPU tasks): {run_multiprocessing(4):.3f}s"
    )
