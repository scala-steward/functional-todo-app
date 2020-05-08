#!/bin/bash

if [ -n "$1" ]; then
  echo "running the image $1"
  docker run -p 8080:8080 $1
else
  echo "running the image functionaltodoapp"
  docker run -p 8080:8080 functionaltodoapp
fi