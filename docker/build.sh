#!/bin/bash

if [ -n "$1" ]; then
  echo "building the image $1"
  docker build --tag=$1 -f ./docker/Dockerfile .
else
  echo "building the image functionaltodoapp"
  docker build --tag="functionaltodoapp" -f ./docker/Dockerfile .
fi