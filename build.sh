#!/bin/bash

sbt dist
sudo docker build . -t arczipt/chat-app