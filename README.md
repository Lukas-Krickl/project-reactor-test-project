# project-reactor-test-project
This project contains two java applications for demonstration purpose for my [Project Reactor: From Zero to Hero](https://gist.github.com/Lukas-Krickl/50f1daebebaa72c7e944b7c319e3c073) tutorial.

## Use Case
Given an organization that provides services of car sharing. The organization provides a mobile app and a website, users can view and rent cars with. The front-end and the back-end services are managed and developed by the organization. The back-end is a microservice architecture and needs to provide, among other requirements, following functionality:
+ clients can request a list of all available cars to display them at the screen
+ clients can request information about a specific car
+ clients can request cars via radial search with given center and radius
+ client can request clusters of cars:
  + cars are clustered based on their distance to another

## Microservice Architecture
There are two microservice to fulfill given requirements. The car_service that provides REST endpoints to serve most of the functionality and the clustering_service that calls the car_service and provides a REST endpoint to serve clusters of cars. Both services are spring boot webflux projects with gradle build management. One could view car and clustering service as a producer-consumer setup, which will be needed when showcasing distributed tracing and RSockets. The docker-compose file and artifacts in the docker folder contains all containers needed as infrastructure, e.g. for distributed tracing.

### car_service
This service serves following HTTP endpoints:
+ `HTTP GET /cars` to request all available cars
+ `HTTP GET /cars?center=<lat>,<lon>&radius=<radius>`
  + where `<lat>` and `<lon>` are coordinates
  + and `<radius>` is the radius in meters
  + e.g. `/cars?center=48.5,16.5&radius=5000`
+ `HTTP GET /cars/{car-id}` to request a single car

These will be extended with an RSocket interface to stream all available cars later on.

### clustering_service
This service calls the car_services endpoint `HTTP GET /cars` to retrieve all cars, clusters them using dbscan and provides following HTTP endpoint to provide them for the clients:
+ `HTTP GET /cars/clusters` to request all available cars

For 1:1 comparison of REST vs RSocket interfaces, the `HTTP GET /cars` endpoint will be available as pass through on the one hand using REST and on the other hand the RSockets interface between both services. 

## Git Setup
The main branch will contain the applications in their final form (as I'll progress through writing the tutorials).
Commits will show the advancement through out the parts of the series. Additional branches are used to separate code, needed for demonstration or testing purpose only. e.g. in branch 5-observability-debugging I introduced some bugs, that can be debugged used the tools described in part "5 Observability"