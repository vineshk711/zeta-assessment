**Answers to questions:**

**Task 1: Invisible Bug**

Possible reasons for degraded accuracy and performance of API
   - Customer transaction patterns have changed significantly from the training data
   - The third-party API provider implemented rate limiting that's causing queued requests and timeouts when transaction volume is high
   - Use profiling tools to track CPU, memory usage, and network latency during peak and off-peak times

Debugging Steps:
   - Check API logs for response codes indicating rate limits (429 Too Many Requests)
   - Monitor system resources during operation
   - Check the API to log response times and pinpoint which parts of the request lifecycle are slowing down.





**Task 2: Banking API**

1. What database schema would you use?
    -  created two tables one for Accounts and another for Transactions.
    -  please check entity package in code to see schema.
    -  
2. How would you ensure consistency in case of a crash mid-transaction?
    - Used @Transactional to take care of roleback in case of incomplete transaction
    - 
3. What optimizations would you introduce for high performance?
    - Use Indexing for Faster Queries
    - Use Connection Pooling for DB
    - Use Asynchronous Processing for Non-Critical Operations




      
**Task 3: Rate Limiter**

Trade-offs of two rate limiter approaches

    Fixed Window Counter:
        Pros: Simplicity and low memory overhead.
        Cons: Burstiness at window boundaries may allow short-term rate spikes.
        
    Token Bucket:
        Pros: Smooth handling of bursts while ensuring a sustained average rate.
        Cons: More complex implementation with extra state management for token refilling.
