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

docker build --no-cache -t kairlec/koj-runtime:latest -f runtime.Dockerfile cc-src

mkdir -p $(pwd)/jvm-context
mkdir -p $(pwd)/clike-context
mkdir -p $(pwd)/py-context

id=$(docker create kairlec/koj-runtime)
rm -rf $(pwd)/jvm-context/koj
docker cp $id:/usr/src/koj/koj $(pwd)/jvm-context/koj
rm -rf $(pwd)/clike-context/koj
docker cp $id:/usr/src/koj/koj $(pwd)/clike-context/koj
rm -rf $(pwd)/py-context/koj
docker cp $id:/usr/src/koj/koj $(pwd)/py-context/koj
docker rm -v $id

docker build --no-cache -t kairlec/koj-support:py36  -f py36.Dockerfile py-context
docker build --no-cache -t kairlec/koj-support:py38  -f py38.Dockerfile py-context
docker build --no-cache -t kairlec/koj-support:py310  -f py310.Dockerfile py-context
docker build --no-cache -t kairlec/koj-support:clike -f clike.Dockerfile clike-context
docker build --no-cache -t kairlec/koj-support:jvm8  -f jvm8.Dockerfile jvm-context
docker build --no-cache -t kairlec/koj-support:jvm11 -f jvm11.Dockerfile jvm-context
docker build --no-cache -t kairlec/koj-support:jvm17 -f jvm17.Dockerfile jvm-context
