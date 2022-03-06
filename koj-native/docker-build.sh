#!/bin/sh
set -e

docker build . -t koj-runtime:latest --target build

docker build . -t kairlec/koj-py:3.8 --target koj-py38
docker build . -t kairlec/koj-clike:latest --target koj-clike
docker build . -t kairlec/koj-jvm:8 --target koj-jvm8
