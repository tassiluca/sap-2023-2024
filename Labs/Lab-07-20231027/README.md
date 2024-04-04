# Software Architecture and Platforms - a.y. 2023-2024

## Lab #07-20231027 - [Repo](https://github.com/pslab-unibo/sap-2023-2024.git) 

In [previous lab (20231020)](https://github.com/pslab-unibo/sap-2023-2024/blob/master/Labs/Lab-06-20231020/README.md) and in module 2.3 we dug into microservices and the design of microservices. In this lab, we see the design and prototype implementation of a simple example/system based on microservice architecture: the [Cooperative PixelArt System](https://docs.google.com/document/d/1tZgkVA_i08DHmW3Wnpnq-AIvbmVA3CMiGn1aWEBDZYM/edit?usp=sharing) (same example seen in PCD course). 

Key points:
- how to design a microservice adopting a clean architecture, exposing a REST API
- how to design a library based on a service proxy, to interact with the microservice

Tool of the day: [Swagger](https://swagger.io/)
- editor/tool ecosystem to work with API
- based on [Open API 3.0 standard](https://www.openapis.org/)
- OpenAPI is good for REST-based API, not designed for Async API. A recent proposal to this purpose: [AsyncAPI](https://www.asyncapi.com/)
    - aiming at being a standard for Event-Driven Architectures

## IPC in microservice architectures

**The choice of IPC mechanism is crucial in microservices** since **it can impact application availability, reliability, and performance**.

=> **API-first approach** to defining services: first write the API interface definition, then review it with the client developers and only after iterating on the API definition implement the service.
This increases the chances of meeting the client's needs.

=> a service's API is rarely set in stone: it will evolve over time:
- version the API (semantic versioning): `MAJOR.MINOR.PATCH`
  - making `MINOR`, backward-compatible changes to the API. Obey the robustness principle: "be conservative in what you do, be liberal in what you accept from others", i.e. services should provide default values for missing request attributes and clients should ignore extra attributes in responses.
  - making `MAJOR` breaking changes

### Synchronous IPC

- **REST(ful) API:**
  ```plaintext
        Client                  Service
     _____________           _____________
    /             \         /             \
   /     _____     \       /     _____     \
  /     /     \     \     /     /     \     \
  \     \__O__/     /     \     \__O__/     /
   \       ^       /       \       ^       /
    \__| Proxy |__/         \__| Proxy |__/
          V  ^                    V  ^
          |  |-----< Reply <------|  |
          |-------> Request >--------|
  ```
    - [Notes about Rest - theory](https://tassiluca.github.io/distributed-systems-notes/notes/09-rest.html)
    - [Notes about ReST - practice](https://tassiluca.github.io/distributed-systems-notes/notes/lab06-web-services.html)
    - To be "ful"ly ReSTful the api should be hypertext driven (HATEOAS): the client should be able to navigate the API by following links in the responses. This is rarely used in practice => the client is decoupled from the server's URI endpoints
      - have a look at the [Richardson Maturity Model](https://martinfowler.com/articles/richardsonMaturityModel.html)
    - ReST did not originally have an IDL. Now there is [OpenAPI](https://www.openapis.org/), a standard for defining APIs
    - Problem: not suitable for fetching multiple resources in a single request - the client must make multiple requests to fetch related resources
      - GraphQL is an alternative to REST for fetching multiple resources in a single request

- **gRPC**
  - Binary message-based protocol
  - The API is defined using Protocol Buffers-based IDL, which is Google's language-neutral, platform-neutral, extensible mechanism for serializing structured data.
    - You use Protocol Buffers compiler to generate client and server stubs in multiple languages
  - gRPC supports streaming requests and responses
  - [Notes about gRPC](https://tassiluca.github.io/distributed-systems-notes/notes/lab08-gRPC.html)

> "I favor an architecture consisting of loosely coupled services that communicate with one another using async messaging. Synchronous protocols such as REST are used mostly yo communicate with other applications"
> 
> [Microservices Patterns]
