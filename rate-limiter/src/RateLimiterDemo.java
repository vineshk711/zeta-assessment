public class RateLimiterDemo {
    public static void main(String[] args) throws InterruptedException {
        // Limit: 5 requests per second (i.e., per 1000 milliseconds)
        RateLimiter rateLimiter = new FixedWindowRateLimiter(5, 1000);
        String userId = "user123";

        // Simulate 7 requests in quick succession.
        for (int i = 1; i <= 7; i++) {
            boolean allowed = rateLimiter.allowRequest(userId);
            System.out.println(STR."Request \{i} allowed? \{allowed}");
        }

        // Wait for the next window to see the counter reset.
        Thread.sleep(1100);
        boolean allowedAfterReset = rateLimiter.allowRequest(userId);
        System.out.println(STR."Request after waiting allowed? \{allowedAfterReset}");
    }
}