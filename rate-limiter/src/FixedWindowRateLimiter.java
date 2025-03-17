

import java.util.concurrent.ConcurrentHashMap;

/**
 * FixedWindowRateLimiter implements a simple fixed window counter algorithm.
 * It limits each user to a maximum number of requests within a defined window.
 */
public class FixedWindowRateLimiter implements RateLimiter {

    private final int maxRequests;
    private final long windowTimeMillis;
    // Map to store per-user request information. ConcurrentHashMap ensures thread safety.
    private final ConcurrentHashMap<String, UserRequestInfo> userRequestMap = new ConcurrentHashMap<>();

    public FixedWindowRateLimiter(int maxRequests, long windowTimeMillis) {
        this.maxRequests = maxRequests;
        this.windowTimeMillis = windowTimeMillis;
    }

    //Checks if the request from the specified user should be allowed.
    @Override
    public boolean allowRequest(String userId) {
        long currentTimeMillis = System.currentTimeMillis();
        // Determine the current window based on the configured window duration.
        long currentWindow = currentTimeMillis / windowTimeMillis;

        // Get or create the user's request info.
        UserRequestInfo info = userRequestMap.computeIfAbsent(userId, id -> new UserRequestInfo(currentWindow));

        // Synchronize on the user's info to handle concurrent requests.
        synchronized (info) {
            if (info.window != currentWindow) {
                // New window: reset the counter.
                info.window = currentWindow;
                info.count = 0;
            }
            if (info.count < maxRequests) {
                info.count++;
                return true;
            } else {
                return false;
            }
        }
    }
}
