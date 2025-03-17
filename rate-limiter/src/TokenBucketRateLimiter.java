import java.util.concurrent.ConcurrentHashMap;

/**
 * TokenBucketRateLimiter implements the token bucket algorithm for rate limiting.
 * Each user is allowed up to a certain burst (bucket capacity) with tokens refilling at a fixed rate.
 */
public class TokenBucketRateLimiter implements RateLimiter {

    private final double capacity;    // Maximum tokens (burst capacity)
    private final double refillRate;  // Tokens per second
    private final ConcurrentHashMap<String, TokenBucket> userBuckets = new ConcurrentHashMap<>();


    //Constructs a TokenBucketRateLimiter.
    public TokenBucketRateLimiter(double capacity, double refillRate) {
        this.capacity = capacity;
        this.refillRate = refillRate;
    }

    //Checks if the request from the specified user should be allowed.
    @Override
    public boolean allowRequest(String userId) {
        long currentTimeMillis = System.currentTimeMillis();
        TokenBucket bucket = userBuckets.computeIfAbsent(userId, id -> new TokenBucket(capacity, currentTimeMillis));

        synchronized (bucket) {
            refillBucket(bucket, currentTimeMillis);
            if (bucket.tokens >= 1) {
                bucket.tokens -= 1;
                return true;
            } else {
                return false;
            }
        }
    }


    //Refills the token bucket based on the elapsed time since the last refill.
    private void refillBucket(TokenBucket bucket, long currentTimeMillis) {
        long elapsedMillis = currentTimeMillis - bucket.lastRefillTimestamp;
        if (elapsedMillis > 0) {
            double tokensToAdd = (elapsedMillis / 1000.0) * refillRate;
            bucket.tokens = Math.min(capacity, bucket.tokens + tokensToAdd);
            bucket.lastRefillTimestamp = currentTimeMillis;
        }
    }

    private static class TokenBucket {
        double tokens;
        long lastRefillTimestamp;

        TokenBucket(double tokens, long lastRefillTimestamp) {
            this.tokens = tokens;
            this.lastRefillTimestamp = lastRefillTimestamp;
        }
    }
}

