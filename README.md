# Transport Java 

**Latest Release:** 1.0-SNAPSHOT<br>
**License:** Apache 2.0<br>
**JDK:** Java 11

Very very very opinionated json communication protocol between your services.

## Features
* JSON Model
* Exception handling
* Data Validator
* PubSub: Queue, Notification
* Client
* Service

## Dependencies
Hosted in Maven Central.

```groovy
// Transport Core Utils
compile group: 'dev.fuxing', name: 'transport-core', version: '1.0-SNAPSHOT'
compile group: 'dev.fuxing', name: 'transport-model', version: '1.0-SNAPSHOT'
compile group: 'dev.fuxing', name: 'transport-exception', version: '1.0-SNAPSHOT'
compile group: 'dev.fuxing', name: 'transport-validator', version: '1.0-SNAPSHOT'

// Transport Communicator
compile group: 'dev.fuxing', name: 'transport-service', version: '1.0-SNAPSHOT'
compile group: 'dev.fuxing', name: 'transport-client', version: '1.0-SNAPSHOT'
compile group: 'dev.fuxing', name: 'transport-pubsub', version: '1.0-SNAPSHOT'
```