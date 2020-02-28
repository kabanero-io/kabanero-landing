#!/bin/bash
docker run -it --rm -e TRAVIS_EVENT_TYPE=${TRAVIS_EVENT_TYPE} landing-travis:latest bash -c "$1"   
