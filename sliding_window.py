""" 
You are interviewing for a Senior Software Engineer contractor position with a fintech company that handles millions of API requests daily. The team needs a reliable rate limiting layer to protect downstream payment processing services from accidental client misbehavior and to enforce fair-use SLAs across tenants.

The interviewer has set a strict 30-minute timer. They want to see how you approach a focused, well-scoped engineering problem under realistic constraints. Your code should reflect production-quality thinking — not just a working solution, but one that demonstrates an understanding of concurrency, data structure choice, and algorithmic trade-offs.

PROBLEM STATEMENT:
Design and implement a simple in-memory Rate Limiter in Python (preferred) or TypeScript/Node.js.

Implement a class called RateLimiter exposing:
    bool isAllowed(clientId: str, requestsPerMinute: int) -> bool

RULES:
• Each client (identified by clientId) can make at most requestsPerMinute requests in any rolling 60-second window.
• Use a SLIDING WINDOW approach (not fixed window).
• Return True if the request is allowed; False if it should be rejected.
• The solution must be thread-safe (threading or asyncio context).

ADDITIONAL INSTRUCTIONS:
• Write clean, production-ready code with good naming and comments.
• Include a short explanation (max 150 words) covering: data structures used and why; how the sliding window works; time/space complexity; and any trade-offs.

DELIVERABLES:
1. Complete RateLimiter class implementation
2. A few test cases showing correctness
3. The short written explanation

EVALUATION CRITERIA:
The interviewer will assess: algorithmic correctness, thread safety, code quality and naming, complexity reasoning, ability to articulate trade-offs, and overall communication.


"""
import threading
import time
from collections import defaultdict, deque

class RateLimiter:
    def __init__(self):
        self.requests = defaultdict(deque)
        self.lock = threading.Lock()

    def isAllowed(self, clientId: str, requestsPerMinute: int) -> bool:
        now = time.time()
        starting = now - 60

        with self.lock:
            queue = self.requests[clientId]

            #remove outdated
            while queue and queue[0] < starting:
                queue.popleft()

            queuesize = len(queue)
            if queuesize < requestsPerMinute:
                queue.append(now)
                print(f"Request allowed for {clientId}. Current count: {queuesize}")
                return True
            else:
                print(f"Request denied for {clientId}. Current count: {queuesize}")
                return False
            

## test the app func

def tests():
    rateinstance = RateLimiter()
    client = "users1"

    # allowing first 3 req.
    print(rateinstance.isAllowed(client, 3))
    print(rateinstance.isAllowed(client, 3))
    print(rateinstance.isAllowed(client, 3))

    # 4th one will be denied
    print(rateinstance.isAllowed(client, 3))


def test_window():
    rateinstance = RateLimiter()
    client = "users2"

    for I in range(3):
        # allowed
        print(rateinstance.isAllowed(client, 3))
    
    # failed
    print(rateinstance.isAllowed(client, 3))
    time.sleep(61)
    print(rateinstance.isAllowed(client, 3))

def thread_safe_test():
    rateinstance = RateLimiter()
    client = "users3"

    results = []

    def makerequest():
        result = rateinstance.isAllowed(client, 5)
        results.append(result)

    threads = [threading.Thread(target=makerequest) for i in range(10)]

    for t in threads:
        t.start()
    
    for t in threads:
        t.join()

    print("Allowed Count :", sum(results))


if __name__ == "__main__":
    print("Running Basic Tests...")
    tests()

    print("\nTesting Sliding Window...")
    test_window()

    print("\nTesting Concurrency Safety...")
    thread_safe_test()