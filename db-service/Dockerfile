FROM openjdk:8-jre-alpine
COPY target/universal/db-service-0.1.0-SNAPSHOT.zip /db-service/package.zip
RUN apk add --no-cache bash
RUN unzip /db-service/package.zip -d /db-service
RUN cat /db-service/db-service-0.1.0-SNAPSHOT/bin/db-service | tail -n +2 | sed '1 i \#!/bin/bash' > /db-service/db-service-0.1.0-SNAPSHOT/bin/run
RUN chmod 777 /db-service/db-service-0.1.0-SNAPSHOT/bin/run

WORKDIR /db-service/db-service-0.1.0-SNAPSHOT/bin
ENTRYPOINT ["./run"]