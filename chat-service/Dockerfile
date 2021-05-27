FROM openjdk:8-jre-alpine
COPY target/universal/chat-service-0.1.0-SNAPSHOT.zip /chat-service/package.zip
RUN apk add --no-cache bash
RUN unzip /chat-service/package.zip -d /chat-service
RUN cat /chat-service/chat-service-0.1.0-SNAPSHOT/bin/chat-service | tail -n +2 | sed '1 i \#!/bin/bash' > /chat-service/chat-service-0.1.0-SNAPSHOT/bin/run
RUN chmod 777 /chat-service/chat-service-0.1.0-SNAPSHOT/bin/run

ENTRYPOINT ["./chat-service/chat-service-0.1.0-SNAPSHOT/bin/run"]