#!/bin/bash

sbt "project chatService" dist
cd chat-app
npm run build --prod
cd ../
cp -r chat-app/build/* chat-service/public/
sudo docker build chat-service/ -t arczipt/chat-service