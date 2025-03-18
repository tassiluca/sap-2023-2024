# Software Architecture and Platforms - a.y. 2023-2024

## Lab #05-20231016 - [Repo](https://github.com/pslab-unibo/sap-2023-2024.git) 

In the previous lab [20231013](https://github.com/pslab-unibo/sap-2023-2024/blob/master/Labs/Lab-04-20231013/README.md) we focused on the layered architecture. In this lab:

- Focus on [Ports & Adapters / Hexagonal Architecture](https://docs.google.com/document/d/1PomKasGfZQuLNWwfVzK-DS-SLcZk_oKniPfgVKsSd8U/edit?usp=sharing)
  - [**Very nice blog**](https://jmgarridopaz.github.io)

Activity 

- [**Assignment #03**](https://github.com/pslab-unibo/sap-2023-2024/blob/master/Assignments/Assignment-3-20231016.md)

### How to run
- `./gradlew run`
- pages are served at:
  - http://localhost:8081/static/escooter-registration.html
  - http://localhost:8081/static/user-registration.html
  - http://localhost:8081/static/ride-dashboard.html
- data is saved in `app/dbase`

### Notes

- The pattern definition doesn’t say anything about how to implement the architecture.
- Hexagonal Architecture is an object structural pattern, it is not an architecture style. So it is not layered “per se”, it is not component-based (modular) “per se”, etc.
  - differently from the "clean architecture", ports and adapters (or hexagonal) do not categorize code in layers (domain, application...) but just differentiate from **_inside_ and _outside_ the hexagon**!
  - However, usually, a practical approach and almost a convention in all hexagonal projects is to follow the packaging and layering inspired and used in DDD:
    - **Application: Application Services (the use cases) and ports**
      - The Application Services and/or Command Handlers contain the logic to unfold a use case, a business process. Typically, their role is to:
        1. use a repository to find one or several entities; 
        2. tell those entities to do some domain logic; 
        3. and use the repository to persist the entities again, effectively saving the data changes.
      - This layer also contains the triggering of Application Events, which represent some outcome of a use case. These events trigger logic that is a side effect of a use case, like sending emails, notifying a 3rd party API, sending a push notification, or even starting another use case that belongs to a different component of the application.
    - **Domain model: domain (the entities)**
      - Domain services + domain logic: entities, value objects, domain events
    - **Controllers**
      - presentation, ...
    - **Infrastructure: adapters**
- **Ports are not a layer themselves. Ports are part of the hexagon, they are interfaces that belong to the business logic of the application, i.e. to the hexagon.**
- Driver vs Driven:
  - **Drivers, or Primary Actors.** The interaction **is triggered by the actor**. A driver is an actor that interacts with the application to achieve a goal. Drivers are the users (either humans or devices) of the application.
    - On the left side, the adapter depends on the port and gets injected a concrete implementation of the port, which contains the use case. On this side, both the port and its concrete implementation (the use case) belong inside the application;
    - On the right side, the adapter is the concrete implementation of the port and is injected in our business logic although our business logic only knows about the interface. On this side, the port belongs inside the application, but its concrete implementation belongs outside and it wraps around some external tool.
  - **Driven Actors, or Secondary Actors.** The interaction **is triggered by the application.** A driven actor provides some functionality needed by the application for implementing the business logic.
  - A **driver** adapter will be a class that **“uses”** a driver port interface, and a **driven** adapter will be a class that **“implements”** a driven port interface.
    ```plaintext
           ______                                  ______
           DRIVER                                  DRIVEN
           ‾‾‾‾‾‾            ___________           ‾‾‾‾‾‾
                            /           \         
       ForRentScooter ---->/             \-----o) ForStoringScooter
                           |   Hexagon   |-----o) ForPaying
       ForRegisterUser --->\             /-----o) ...
                            \___________/
    ```
    ![](https://jmgarridopaz.github.io/assets/images/hexagonalarchitecture-ig/figure2-3.png)
- **Adapters are independent of each other.**
- **What is outside depends on what is inside, the direction of the dependencies is towards the centre!**

### References
- [hexagonalarchitecture-ig](https://jmgarridopaz.github.io/content/hexagonalarchitecture-ig/)
- [Ports and adapters](https://herbertograca.com/2017/09/14/ports-adapters-architecture/)
