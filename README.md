**Answers to questions:
**

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
