### Migration Pattern
* Expand -> Migrate -> Switch -> Contract
* Ensures Zero Downtime
* Easy Rollbacks
* In between failures if any, should be captured and saved for later reconciliation.

```
        ┌─────────────────────┐        ┌────────────────────────────────┐    ┌────────────────────────────────┐      
        │                     │        │                                │    │                                │      
        │  Couchbase Service  ┼────────►  Oracle and Couchbase Service  ┼────►  Oracle and Couchbase Service  ┼─────┐
        │                     │        │                                │    │                                │     │
        └─────────────────────┘        └────────────────────────────────┘    └────────────────────────────────┘     │
                                                                                                                    │
                                          couchbase-writes: true                couchbase-writes: true              │
                                          oracle-writes: false                  oracle-writes: true                 │
                                          reads: couchbase                      reads: couchbase                    │
                                                                                                                    │
                                                                                                                    │
                                                                               Gets updated data in both databases  │
                                                                                                                    │
                                                                                                                    │
                                                                                                                    │
  ┌────────────────────────────────┐   ┌────────────────────────────────┐    ┌─────────────────────────┐            │
  │                                │   │                                │    │                         │            │
┌─┼  Oracle and Couchbase Service  ◄───┼  Oracle and Couchbase Service  ◄────┼  Migrate Existing Data  ◄────────────┘
│ │                                │   │                                │    │                         │             
│ └────────────────────────────────┘   └────────────────────────────────┘    └─────────────────────────┘             
│                                                                                                                    
│    couchbase-writes: false              couchbase-writes: true              Gets Old Data in Oracle via            
│    oracle-writes: true                  oracle-writes: true                 Upsert Operation                       
│    reads: oracle                        reads: oracle                                                              
│                                                                                                                    
│                                                                                                                    
│                                                                                                                    
│                                                                                                                    
│          ┌────────────────┐                                                                                        
│          │                │                                                                                        
└──────────► Oracle Service │                                                                                        
           │                │                                                                                        
           └────────────────┘                                                                                        
                                                                                                                     
              code cleanup                                                                                           
```