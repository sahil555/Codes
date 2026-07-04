public class LeakyBucketRateLimiter {
    
    // The maximum number of requests that can be processed in a given time window
    private final int maxRequests;
    // The time window in milliseconds
    private final long timeWindowMillis;
    // The current number of requests in the current time window
    private int currentRequests;
    // The timestamp of the last request processed
    private long lastRequestTimestamp;

    // Constructor to initialize the LeakyBucketRateLimiter with the maximum number of requests and the time window in milliseconds
    public LeakyBucketRateLimiter(int maxRequests, long timeWindowMillis) {
        this.maxRequests = maxRequests;
        this.timeWindowMillis = timeWindowMillis;
        this.currentRequests = 0;
        this.lastRequestTimestamp = System.currentTimeMillis();
    }

    // Synchronized method to check if a request can be allowed based on the leaky bucket algorithm
    public synchronized boolean allowRequest() {
        long now = System.currentTimeMillis();
        // Calculate the time elapsed since the last request
        long elapsedTime = now - lastRequestTimestamp;

        // If the time window has passed, reset the current requests
        if (elapsedTime > timeWindowMillis) {
            currentRequests = 0;
            lastRequestTimestamp = now;
        }

        // If the current requests are less than the maximum allowed, allow the request
        if (currentRequests < maxRequests) {
            currentRequests++;
            return true; // Request is allowed
        }

        return false; // Request is denied
    }

    public static void main(String[] args) {
        // Create a LeakyBucketRateLimiter with a maximum of 5 requests and a time window of 1000 milliseconds (1 second)
        LeakyBucketRateLimiter rateLimiter = new LeakyBucketRateLimiter(5, 1000); // 5 requests max, 1 second window

        // Simulate requests
        for (int i = 0; i < 20; i++) {
            if (rateLimiter.allowRequest()) {
                System.out.println("Request " + (i + 1) + " allowed.");
            } else {
                System.out.println("Request " + (i + 1) + " denied.");
            }

            try {
                Thread.sleep(100); // Wait for 100 milliseconds before the next request
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
