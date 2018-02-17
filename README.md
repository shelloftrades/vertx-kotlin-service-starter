# About Path2Be(come a better dev)
This file contains information and notes about the application.

## Path to become a better developer

The path to become a better developer is fraught with the difficult choices of deciding which material we should consume. Time is limited and researching the information is both time consuming and dangerous. Material that were fantastic at some point become outdated.

Path2BeABetterDev solves this issue by crowdsourcing the suggestions.

Path:
    Ordered list of reference material:
        Books: By ISBN from Amazon
        Video: Youtube / ... ?
        Educational link: 
        Links:

    Description: 
        text: [125 ch]
        Starting Level: [Novice, Beginner, Intermediate, Advanced, Expert]
        Ending Level:

    Tags: ["List of tags"]

    Comments/Ratings: [{
        User: {}
        Rating: [Up, Down, Meh]
        Text: "optional comment"
    }*]


Material:
    Type: [Book, Video, Link]
    Name: [200 ch]
    Link: [Url]
    ....
    Author: ....
    Price: 0.....+
    Alternatives: [List of alternative material (Path+Material)]

    Comments/Ratings: [{
        User: {}
        Rating: [Up, Down, Meh]
        Text: "optional comment"
    }*]


Monetize: 
    Referal links to Amazon, .. etc..

## About the Stack
    API Format: REST & GraphQl

    [BACKEND]
        Language: Kotlin
        Web Framework: Spring Boot
        ORM:
        Database:
        Cache:
        Logging:  SLF4J compatible (Logback)
        Service Bus:
        Queue:
        Server: Netty || Jetty || Undertow
        Documentation: Dokka?

    [FRONTEND]
        Language: Javascript
        Framework: Vue.js


## How to manually test:

curl -D - -H 'Content-Type: application/json' -X GET  http://localhost:5000/user/1/books
curl -D - -H 'Content-Type: application/json' -X GET  http://localhost:5000/user/1/books/3
curl -D - -H 'Content-Type: application/json' -X DELETE http://localhost:5000/user/1/books/3
curl -D - -H 'Content-Type: application/json' -X POST -d '{"id":1, "title":"hello", "author":"joe"}' http://localhost:8090/user/1/books
curl -D - -H 'Content-Type: application/json' -X PUT -d '{"id":1, "title":"hello", "author":"joe"}' http://localhost:8090/user/1/books