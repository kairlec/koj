#!/bin/sh
set -e

docker build . -t koj-runtime:latest --target build

docker build . -t koj-py:3.8 --target koj-py38
docker build . -t koj-clike:latest --target koj-clike
docker build . -t koj-jvm:8 --target koj-jvm8
