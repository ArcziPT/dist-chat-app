#!/bin/bash

sbt "project chatService" dist
sbt "project dbService" universal:packageBin
sudo docker build chat-service/ -t arczipt/chat-service
sudo docker build db-service/ -t arczipt/db-service