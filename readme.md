### Migration Pattern
* Expand -> Migrate -> Switch -> Contract
* Ensures Zero Downtime
* Easy Rollbacks
* In between failures, if any, should be captured and saved for later reconciliation.
* Build batch/script to consume from Couchbase, transform and Produce in Oracle.
* Record timestamp when dual write was enabled and Migrate all data before that timestamp.

```
                                        Expand: New Deployment                Dual Writes Enabled
        ┌─────────────────────┐        ┌────────────────────────────────┐    ┌────────────────────────────────┐      
        │                     │        │                                │    │                                │  
        │  Couchbase Service  ┼────────►  Oracle and Couchbase Service  ┼────►  Oracle and Couchbase Service  ┼─┐
        │                     │        │                                │    │                                │ │
        └─────────────────────┘        └────────────────────────────────┘    └────────────────────────────────┘ │
                                          couchbase-writes: true                couchbase-writes: true          │
                                          oracle-writes: false                  oracle-writes: true             │
                                          reads: couchbase                      reads: couchbase                │
                                                                                                                │
                                                                                                                │
   Switch                               Observe to Verify Migration                                             │
  ┌────────────────────────────────┐   ┌────────────────────────────────┐    ┌─────────────────────────┐        │
  │                                │   │                                │    │                         │        │
┌─┼  Oracle and Couchbase Service  ◄───┼  Oracle and Couchbase Service  ◄────┼  Migrate Existing Data  ◄────────┘
│ │                                │   │                                │    │                         │         
│ └────────────────────────────────┘   └────────────────────────────────┘    └─────────────────────────┘         
│    couchbase-writes: false              couchbase-writes: true              Gets Old Data in Oracle via        
│    oracle-writes: true                  oracle-writes: true                 Upsert Operation                   
│    reads: oracle                        reads: oracle                                                          
│                                                                                                                
│                                                                                                                    
│          Contract                                                                                                          
│         ┌────────────────┐                                                                                        
│         │                │                                                                                        
└─────────► Oracle Service │                                                                                        
          │                │                                                                                        
          └────────────────┘                                                                                        
            Code Cleanup                                                                                           
```