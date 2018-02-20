
# vertx-kotlin-service-starter

This has for objective to be a basic application that explores 
the capabilities of vert.x & Kotlin to build microservices.

It does not provide any actual useful functionality :)

## Done

- Basic vert.x & Kotlin project
- HTTP verticle
- API using OpenAPI3 Router Factory
- Coroutines based verticle example
- Bus service verticle
- Auto-generated proxy for bus services

## Todo

- Security
- Testing
- Logging
- Monitoring
- Scaling
- Deployment configuration


## Issues

- Change monitoring and redeployment in vertx doesn't work well with gradle and cause 
port already in use exception.

- I can't figure out how to generate the proxy and client class for the service from Kotlin interface so I'm using java interface



## Some useful command to remember

** How to set up gradle wrapper

    gradle wrapper

** How to run this example with auto-recompile

    ./gradlew vertxRun

** How to build a "fat jar"

    ./gradlew shadowJar

** How to run the fat jar

    java -jar build/libs/simple-project-fat.jar,


** How to kill all running vertx instances when gradle barf and doesn't release them

    pkill -9 -f io.vertx.core.Launcher


** Update Kotlin runtime

 - change kotlin version in build.gradle
 - run gradlew ?