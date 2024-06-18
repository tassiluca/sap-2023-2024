# Software Architecture and Platforms - a.y. 2023-2024

## Lab #10-20231124 - [Repo](https://github.com/pslab-unibo/sap-2023-2024.git) 

As a follow-up of the lecture about [Event-Driven Architecture Style](https://docs.google.com/document/d/1Szif1ksYavi1-AOAm5LRF2pOO-J67udhoGohd_OHwPY/edit?usp=sharing) and Event-Driven Microservices (module-2.8), in this lab we have a look at some technologies useful for concretely design and implement event-driven architectures and event-driven microservices:

- An event broker/event store middleware:  [Apache Kafka](https://kafka.apache.org/) 
  - An open-source distributed event streaming platform  
  - [Background and Context](https://developer.confluent.io/faq/apache-kafka/architecture-and-terminology/)
  - [Main Kafka concepts](https://kafka.apache.org/intro)
  - [Kafka Architecture](https://kafka.apache.org/21/documentation/streams/architecture.html)
  - [Kafka Quick start](https://kafka.apache.org/quickstart)
  - [Setting up Kafka Using Docker](https://docs.google.com/document/d/1NKq_YHRi2_VTHSShyvBsFr6BWRwryZOXNW4mHsXrtU4/edit?usp=sharing)
    - using Docker Compose with `kafka-deplo.yaml` config file
  - Working with Kafka - Kafka clients
    - [Kafka clients in Java](https://docs.confluent.io/kafka-clients/java/current/overview.html)
    - A simple Kafka producer and consumer in Java (sources in `sap.kafka` package)
      
- Specifying API in Event-Driven Architectures: Back to the [AsyncAPI](https://www.asyncapi.com/) initiative 
   
- A framework for building event-driven microservices (among the many): [Axon](https://developer.axoniq.io/axon-framework/overview)
    - "Based on architectural principles, such as Domain-Driven Design (DDD) and Command-Query Responsibility Segregation (CQRS), the Axon Framework provides the building blocks that CQRS requires and helps create scalable and extensible applications while maintaining application consistency in distributed systems."
    - [Background - Inspiring View: Event Modelling](https://eventmodeling.org/)
        - [Event Modeling: What is it?](https://eventmodeling.org/posts/what-is-event-modeling/)
    - [Axon Concepts](https://developer.axoniq.io/concepts)
        - DDD, Event Sourcing, CQRS, Microservices
    - [Axon Code Samples](https://developer.axoniq.io/code-samples) 
        - [Hotel demo Example](https://github.com/AxonIQ/hotel-demo) discussed in the article [Event Modeling: What is it?](https://eventmodeling.org/posts/what-is-event-modeling/)  

Activity
- [**Assignment #06**](https://github.com/pslab-unibo/sap-2023-2024/blob/master/Assignments/Assignment-6-20231124.md)

Tools of the day
- [Apache Kafka](https://docs.docker.com/)
- [Offset Explore / Kafka Tool GUI](https://www.kafkatool.com/)
- [AsyncAPI](https://www.asyncapi.com/)
- [Axon Framework](https://developer.axoniq.io/axon-framework/overview)

## Asynchronous IPC

- **Asynchronous => messaging**
  - message $\neq$ event: message is the more general, meaning you may use events and commands 
  
  ``` mermaid
    graph TD
    A(Message)
    A --> B(Event)
    A --> C(Request)
    C --> D(Query)
    C --> E(Command)
    ```

    - The difference is in naming and intent:
      - **Event**: a notification that something has happened
      - **Command**: a request to do something
      - **Query**: a request to get data
- asynchronous request & response pattern: two channels, one for the request and one for the response; the binding between the request and the response is achieved by a correlation ID, i.e. a unique identifier that is included in the request and the response
- publish & subscribe channel is useful to publish domain events to multiple subscribers
- **AsyncAPI** as a standard for defining APIs in an asynchronous way
  - `HelloWorld` example, from the [AsyncAPI](https://www.asyncapi.com/) website
    ```yaml
    asyncapi: 3.0.0
    info:
      title: Hello world application  # API title
      version: '0.1.0'                # the version of the API
    channels:   # the mediums where messages flow
      hello:    # the name of the channel
        address: 'hello'
        messages:
          sayHelloMessage:
            payload:
              type: string
              pattern: '^hello .+$'
    operations: # the application behavior in terms of operations performed on the channels
      receiveHello: 
        action: 'receive'
        channel:
          $ref: '#/channels/hello'
    ```
- Brokerless architecture: the services communicate directly with each other using a messaging protocol.
  - Pros:
    - better latency
    - less operational complexity
    - no bottleneck
  - Cons:
    - the sender must know the address of the receiver
    - reduced availability since both sender and receiver must be available
    - implementing mechanisms, such as guaranteed delivery, are more complex
- Broker-based architectures: an intermediary (the broker) through which all messages flow
  - Pros:
    - loose coupling between sender and receiver
    - the broker buffers messages until the receiver can process them: this means, for example, that an online store can accept orders even if the order service is down (or slow) queueing the messages until the service is available
  - Cons:
    - performance bottleneck
    - potential single point of failure: most modern brokers are distributed and highly available
    - operational complexity to set up and maintain the broker
  - open source message brokers: RabbitMQ, Apache Kafka, ActiveMQ, ... each broker makes different trade-offs between performance, reliability, scalability and delivery guarantees. Choose the one that best fits your requirements (different parts may use different brokers)

> "I favor an architecture consisting of loosely coupled services that communicate with one another using async messaging. Synchronous protocols such as REST are used mostly to communicate with other applications"
>
> [Microservices Patterns]

Keep in mind that an **increase** in **availability** comes with a **decrease** in **consistency**: the more you decouple, the more you have to deal with eventual consistency.

## Event Sourcing

- Problems of traditional persistence:
  - "object-relational impedance mismatch": the object-oriented model of the application does not match the relational model of the database
    - [SoftEng discussion ](https://softwareengineering.stackexchange.com/questions/146065/is-there-really-object-relational-impedance-mismatch)
  - lack of aggregate state-change history
  - implementing audit logging is tedious
- **Event Sourcing** is a way of persisting the state of a business entity by **storing the history of state-changing events** that occurred in the system. **The current state of the entity is derived by replaying the events**.
  - a great analogy for developers: _"Event Sourcing provides to its users a system that works on their data the way version control works on the source code"_: the application state is the working copy and the log of events is the list of commits!
  - **every state change of an aggregate, including its creation, is represented by a domain event. Whenever the aggregate's state changes it must emit an event.**
  - an event should contain the data that the aggregate needs to perform the state change $\Rightarrow$ "Event-carried State Transfer"
  - event sourcing refactors command methods that have side effects into two or more methods: one that validates the command and emit event(s) representing the state change, and another that applies the event to the aggregate's state.
  - **Challenge** - for long-lived aggregates reconstructing the state by replaying all events may be too slow: in this case, **snapshots** can be used: a snapshot is a representation of the aggregate's state at a certain point in time and can be used to speed up the reconstruction of the aggregate's state replaying only the events that occurred after the snapshot.
  - _every processing of a message always results in an event being emitted_: if the aggregate doesn't emit an event it means there is no record of the fact the message has been processed; this could be a problem if the message is delivered and processed more than once!
  - **Challenge** - the structure of domain events can change over time. The application must be able to handle different versions of the same event in a backward-compatible way $\Rightarrow$ a component usually called _upcaster_ is used to transform event when they're loaded from the event store making the application code only ever deal with the current event schema.
  - ☘️ Benefits:
    - Reliably publishes domain events providing an audit log of all changes that's guaranteed to be accurate;
    - Preserves the history of aggregates: you can easily implement temporal queries that retrieve the past state of an aggregate;
    - Alternative state: if we find a past event was incorrect we can compute the consequences by replaying the events from that point, reversing the events;
      - reversing means undoing the effects of the event. Note an event "add $10 to account" can be reversed by subtracting $10 from the account, while an event "set the account to $110" cannot be reversed. In this case the event should store everything needed to compute the reverse operation, like the previous value of the account;
  - ☢️ Drawbacks:
    - Complexity (this is also due to the fact event sourcing is shipped with asynchronous messaging that is way more complex than synchronous messaging);
    - Evolving event schema can be tricky;
    - Querying the event store is not trivial;
    - Deleting data is not trivial if you want to keep the audit log intact and also deal with legal requirements (like GDPR).
- An app using event sourcing stores events in an **Event Store**: events must be stored in a database (e.g. MySQL) and applications consume events from a message broker (e.g. Kafka) leveraging a transaction log tailing mechanism to propagate events from the database to the message broker
  ![Event sourcing](https://miro.medium.com/v2/resize:fit:720/format:webp/1*SQikYTvGayDrXBd-v4RVSg.png)

## CQRS - _Command Query Responsibility Segregation_

=> Applying segregation/separation of concerns to repositories

**It splits a persistent data model and the modules that use it into two parts: the command side and the query side.**

- **The command side modules and data model implement CUD operations - Create, Update, Delete - while the query side modules and data implement Queries.**
- **The query side keeps a read-optimized view of the data updated by the command side (subscribing to the events published by the command side).**

![CQRS](https://martinfowler.com/bliki/images/cqrs/cqrs.png)

![event-based CQRS](https://www.kindsonthegenius.com/microservices/wp-content/uploads/2020/02/Event-Based-CQRS-Architecture.jpg?189db0&189db0)

:point_right: CQRS can also be used to define query services: it implements query operations by subscribing to events published by other services and updating a read-optimized view of the data. For example:

![CQRS for query services](https://microservices.io/i/patterns/data/QuerySideService.png)

Benefits:

- enables efficient implementation of queries 
- makes querying possible in an event-sourced system
- improve separation of concerns

Cons:

- more complex architecture
- "replication lag," i.e., the delay between when the command side publishes an event and when that event is processed and the query side updated.
  - a client app that updates an aggregate and then immediately queries the read model may not see the changes reflected in the read model
    - it must be written in a way that avoids exposing these potential inconsistencies to the user, like updating its local model without issuing a query (web app). Cons: the UI code may need to duplicate server-side code in order to update its model.
    - use a token approach (see 1.3.2 @ Microservices Patterns)
