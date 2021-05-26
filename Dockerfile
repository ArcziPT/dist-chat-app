FROM openjdk:8-jre-alpine
COPY target/universal/chat-app-1.0-SNAPSHOT.zip /chat-app/package.zip
RUN apk add --no-cache bash
RUN unzip /chat-app/package.zip -d /chat-app
RUN cat /chat-app/chat-app-1.0-SNAPSHOT/bin/chat-app | tail -n +2 | sed '1 i \#!/bin/bash' > /chat-app/chat-app-1.0-SNAPSHOT/bin/run
RUN chmod 777 /chat-app/chat-app-1.0-SNAPSHOT/bin/run

ENTRYPOINT ["./chat-app/chat-app-1.0-SNAPSHOT/bin/run"]