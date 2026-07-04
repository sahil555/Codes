
public class TokenBucketRateLimiter {
    
    // The maximum number of tokens that can be stored in the bucket
    private final int maxTokens;
    // The current number of tokens in the bucket
    private int currentTokens;
    // The rate at which tokens are added to the bucket (tokens per second)
    private final int refillRate;
    // The last time the bucket was refilled (in milliseconds)
    private long lastRefillTimestamp;

    public TokenBucketRateLimiter(int maxTokens, int refillRate) {
        this.maxTokens = maxTokens;
        this.refillRate = refillRate;
        this.currentTokens = maxTokens; // Start with a full bucket
        this.lastRefillTimestamp = System.currentTimeMillis();
    }
    
    public synchronized void refillTokens() {
        long now = System.currentTimeMillis();
        long elapsedTime = now - lastRefillTimestamp;
        // Calculate how many tokens to add based on the elapsed time and refill rate
        int tokensToAdd = (int) (elapsedTime / 1000.0 * refillRate);
        if (tokensToAdd > 0) {
            currentTokens = Math.min(maxTokens, currentTokens + tokensToAdd);
            lastRefillTimestamp = now; // Update the last refill timestamp
        }
    }

    public synchronized boolean allowRequest() {
        refillTokens();
        if (currentTokens > 0) {
            currentTokens--;
            return true; // Request is allowed
        }
        return false; // Request is denied
    }

    public static void main(String[] args) {
        // Create a TokenBucketRateLimiter with a maximum of 5 tokens and a refill rate of 1 token per second
        TokenBucketRateLimiter rateLimiter = new TokenBucketRateLimiter(5, 1); // 5 tokens max, 1 token per second

        // Simulate requests
        for (int i = 0; i < 10; i++) {
            if (rateLimiter.allowRequest()) {
                System.out.println("Request " + (i + 1) + " allowed.");
            } else {
                System.out.println("Request " + (i + 1) + " denied.");
            }

            try {
                Thread.sleep(200); // Wait for 200 milliseconds before the next request
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
