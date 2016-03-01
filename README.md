Nucleus Search Indexer
================

This is the Search indexer for Project Nucleus. 

This project contains just one main verticle which is responsible for listening for event message on kafka. 

DONE
----
* Configured listener
* Provided a initializer and finalizer mechanism for components to initialize and clean up themselves
* Created a kafka consumer registry and register it as component for initialization and finalization
* Logging and app configuration

TODO
----
* Add message processor logic
* Provide Elasticsearch client configuration 
* Provide Cassandra client configuration 
* Message processor and service layer to process consumed message by kafka consumer 
* Index content with elastic search client 

To understand build related stuff, take a look at **BUILD_README.md**.



