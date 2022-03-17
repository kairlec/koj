#!/bin/sh
set -e

if [ ! -d "./jvm-context/kotlinc" ]; then
  curl -L https://github.com/JetBrains/kotlin/releases/download/v1.6.10/kotlin-compiler-1.6.10.zip -o kotlin-compiler-1.6.10.zip
  unzip kotlin-compiler-1.6.10.zip -d ./jvm-context
  rm kotlin-compiler-1.6.10.zip
fi

docker pull gcc:9.4
docker pull openjdk:8
docker pull openjdk:11
docker pull openjdk:17
docker pull python:3.6
docker pull python:3.8
docker pull python:3.10

docker buildx build --no-cache -t kairlec/koj-runtime:latest -f runtime.Dockerfile --platform linux/amd64,linux/arm64 cc-src --push

docker buildx build --no-cache -t kairlec/koj-support:py36  -f py36.Dockerfile --platform linux/amd64,linux/arm64 py-context --push
docker buildx build --no-cache -t kairlec/koj-support:py38  -f py38.Dockerfile --platform linux/amd64,linux/arm64 py-context --push
docker buildx build --no-cache -t kairlec/koj-support:py310  -f py310.Dockerfile --platform linux/amd64,linux/arm64 py-context --push
docker buildx build --no-cache -t kairlec/koj-support:clike -f clike.Dockerfile --platform linux/amd64,linux/arm64 clike-context --push
docker buildx build --no-cache -t kairlec/koj-support:jvm8  -f jvm8.Dockerfile --platform linux/amd64,linux/arm64 jvm-context --push
docker buildx build --no-cache -t kairlec/koj-support:jvm11 -f jvm11.Dockerfile --platform linux/amd64,linux/arm64 jvm-context --push
docker buildx build --no-cache -t kairlec/koj-support:jvm17 -f jvm17.Dockerfile --platform linux/amd64,linux/arm64 jvm-context --push
