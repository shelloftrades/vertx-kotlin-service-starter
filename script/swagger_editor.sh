#!/bin/bash

docker pull swaggerapi/swagger-editor
docker run -d -p 8081:8080 swaggerapi/swagger-editor

docker pull apicurio/apicurio-studio
docker ru n -it -p 8082:8080 apicurio/apicurio-studio