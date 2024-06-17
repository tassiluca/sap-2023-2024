# Software Architecture and Platforms - a.y. 2023-2024

## Lab #08-20231103 - [Repo](https://github.com/pslab-unibo/sap-2023-2024.git) 

In module 2.4 we saw an overview of microservices patterns. In this lab, we see three patterns that are quite pervasive in microservice-based architectures:
- [API Gateways](https://docs.google.com/document/d/1SO1q7uRvtsIMaA_7niKKvATEIDmD1Z9ddXnaSkv5FUw/edit?usp=sharing)
- [Circuit Breaker](https://docs.google.com/document/d/1TPXAjO5mrZ2UPsN4iWQvs-67DUW4ni_p-4S2uCQztmg/edit?usp=sharing)
- [Service Discovery](https://docs.google.com/document/d/1fC5bEun1JTHwRCpqFiUj7_eXYG9j1Hxn8pmFiB5RJR4/edit?usp=sharing)

Activity

- Discussion
  - Developing microservices that interact with other microservices -- service composition

- [**Assignment #05**](https://github.com/pslab-unibo/sap-2023-2024/blob/master/Assignments/Assignment-5-20231103.md)

Tools of the day

- [Spring Framework](https://spring.io)
  - [Spring Boot](https://spring.io/microservices) for implementing microservices
    - implementing [RESTful Web Services](https://spring.io/guides/gs/rest-service/), including [Hypermedia-driven RESTful Web services](https://spring.io/guides/gs/rest-hateoas/) (HATEOAS)   
  - [Spring Reactive](https://spring.io/reactive) 
    - async and reactive programming model
  - [Spring Cloud](https://spring.io/projects/spring-cloud)  
    - ready-to-use implementation of different microservices patterns
  - Documentation
    - [Guides](https://spring.io/guides)

## API Gateway

Rarely used that clients invoke services directly:

- the fine-grained service APIs require clients to make multiple requests to retrieve the data they need => may result in a poor experience;
- making clients know about each service and its API makes it difficult to change the architecture and its APIs;
- services may use IPC mechanisms that aren't convenient for clients (e.g., gRPC).

:point_right: **API gateway**: a single entry point for all clients to access the services. It's a facade for all the services in the application.

It is responsible for:

- request routing to the appropriate service using a routing map specifying the mapping between the request and the service. This function is identical to the reverse proxying features provided by web servers, like NGINX.
- API composition: exposes coarse-grained API enabling clients to retrieve data with a single request;
  - may expose client-specific APIs (e.g. mobile vs web) - *backends for frontend* pattern:
    ```plaintext
    Mobile Client        Browser JS app         3rd party app
            |                     |                   |
    ________|_____________________|___________________|________
    |       |                     |                   |       |
    |  _____|_____________________|___________________|_____  |
    |  | ___V__________    _______V_______   _________V____ | |
    |  | | Mobile API |    | Browser API |   | Public API | | |
    |  | |____________|    |_____________|   |____________| | |
    |  |                                                    | |
    |  |                      API Layer                     | |
    |  |____________________________________________________| |
    |                        API Gateway                      |
    |_________________________________________________________|
    ```
- protocol translation: RESTful API to external clients, though services use a mixture of protocols (e.g., gRPC);
- implementing edge functions:
  - authentication: verifying the identity of the client (e.g., JWT);
  - rate-limiting: limiting the number of requests a client can make in a given time period;
  - authorization: determining whether a client has permission to perform an action;
  - caching: caching responses to reduce latency;
  - monitoring: health checks, logging, and metrics;

Concerns:

- performance and scalability: the gateway must be able to handle the load of all clients and can be a bottleneck;
- handling partial failures: run multiple instances of the gateway behind a load balancer + circuit breaker pattern (see below);

## Circuit breaker

**Problem**: a service may not be able to respond in a timely way to a client's request. Because the client is blocked waiting for a response, the danger is that the failure could cascade to the client's clients and so on and cause an outage (this is a problem with synchronous RPC).

**Solution**: the remote procedure proxy immediately rejects invocations for a timeout period after the number of consecutive failures exceeds a specified threshold.

Example:

```plaintext
                         |             |                |               |
Mobile App ----------->O-| API GATEWAY |-O------------->| ORDER SERVICE |
                       | |             | |              |               |
                  Create Order      Order Service      Unresponsive Service
                    endpoint            Proxy
```

The services must be designed to prevent partial failures from cascading throughout the application:

- the proxy must handle unreliable remote services;
- how to recover from a failed remote service?

Sync RPC should protect itself from:

- _network failures_: the client should have a timeout for the response;
- **_limit the number of outstanding requests from a client to a service_: the client should not send too many requests to a service;**
- **_circuit breaker pattern_: track the number of successful and failed requests and if the error rate exceeds some threshold trip the circuit breaker so that further attempts fail immediately** (in ReST, 503 `Service Unavailable`).
  - in some scenarios can simply return an error to the client;
  - in other scenarios can return a cached response;
  - if a request involves invoking multiple endpoints and sources of data (which may fail) if one of them is not available, then a partial response could be considered

## Service discovery
