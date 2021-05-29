#!/bin/bash

sbt "project chatService" dist
sudo docker build chat-service/ -t arczipt/chat-service