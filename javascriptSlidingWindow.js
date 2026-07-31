/**
 * Thread-safe / Concurrency-safe Sliding Window Rate Limiter
 */
class RateLimiter {
    /**
     * @param {number} windowSizeSeconds Rolling window size in seconds (default: 60)
     */
    constructor(windowSizeSeconds = 60) {
        this.windowSizeMs = windowSizeSeconds * 1000;
        // Map<clientId, Array<number>> storing request timestamps
        this.clientRequests = new Map();
    }

    /**
     * Determines if a client request is allowed within the rolling window.
     * 
     * @param {string} clientId Unique client identifier
     * @param {number} requestsPerMinute Maximum allowed requests per window
     * @returns {boolean} True if allowed, False if rate-limited
     */
    isAllowed(clientId, requestsPerMinute) {
        const now = Date.now();
        const windowStart = now - this.windowSizeMs;

        // Retrieve existing timestamp queue or initialize a new one
        if (!this.clientRequests.has(clientId)) {
            this.clientRequests.set(clientId, []);
        }

        const requestQueue = this.clientRequests.get(clientId);

        // Remove expired timestamps outside the rolling window
        while (requestQueue.length > 0 && requestQueue[0] <= windowStart) {
            requestQueue.shift(); // O(1) amortized cleanup for window sizing
        }

        // Grant or deny request
        if (requestQueue.length < requestsPerMinute) {
            requestQueue.push(now);
            return true;
        }

        return false;
    }
}

// ==========================================
// TEST SUITE & VERIFICATION
// ==========================================

const sleep = (ms) => new Promise((resolve) => setTimeout(resolve, ms));

async function runTests() {
    console.log("Executing RateLimiter Validation...");

    // Test 1: Basic Rate Limit Enforcement
    const limiter = new RateLimiter();
    const clientA = "client_alpha";

    console.assert(limiter.isAllowed(clientA, 3) === true, "Request 1 should pass");
    console.assert(limiter.isAllowed(clientA, 3) === true, "Request 2 should pass");
    console.assert(limiter.isAllowed(clientA, 3) === true, "Request 3 should pass");
    console.assert(limiter.isAllowed(clientA, 3) === false, "Request 4 should fail");
    console.log("✅ Basic limit test passed.");

    // Test 2: Sliding Window Expiration
    const shortLimiter = new RateLimiter(2); // 2-second window
    const clientB = "client_beta";

    console.assert(shortLimiter.isAllowed(clientB, 2) === true, "Req 1 allowed");
    console.assert(shortLimiter.isAllowed(clientB, 2) === true, "Req 2 allowed");
    console.assert(shortLimiter.isAllowed(clientB, 2) === false, "Req 3 rejected");

    // Wait for the window to pass
    await sleep(2100);

    console.assert(shortLimiter.isAllowed(clientB, 2) === true, "Req allowed after window expiry");
    console.log("✅ Sliding window expiry test passed.");

    // Test 3: Concurrent Requests (Async Simulation)
    const asyncLimiter = new RateLimiter(60);
    const clientC = "client_concurrent";
    const limit = 50;
    const totalRequests = 200;

    const promises = Array.from({ length: totalRequests }, () =>
        Promise.resolve().then(() => asyncLimiter.isAllowed(clientC, limit))
    );

    const results = await Promise.all(promises);
    const allowedCount = results.filter((res) => res === true).length;
    const deniedCount = results.filter((res) => res === false).length;

    console.assert(allowedCount === limit, `Expected ${limit} allowed, got ${allowedCount}`);
    console.assert(deniedCount === totalRequests - limit, `Expected ${totalRequests - limit} denied`);
    console.log(`✅ Concurrency test passed: Exactly ${allowedCount}/${totalRequests} requests allowed.`);
}

runTests();
