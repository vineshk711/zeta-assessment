/**
 * RateLimiter interface defines the contract for rate limiting.
 */
public interface RateLimiter {
    boolean allowRequest(String userId);
}
