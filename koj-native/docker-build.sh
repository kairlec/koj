#!/bin/sh
set -e

if [ ! -d "./kotlinc" ]; then
  curl -L https://github.com/JetBrains/kotlin/releases/download/v1.6.10/kotlin-compiler-1.6.10.zip -o kotlin-compiler-1.6.10.zip
  unzip kotlin-compiler-1.6.10.zip -d ./
  rm kotlin-compiler-1.6.10.zip
fi

docker build . -t kairlec/koj-runtime:latest --target build

docker build . -t kairlec/koj-support:py36 --target koj-py36
docker build . -t kairlec/koj-support:py38 --target koj-py38
docker build . -t kairlec/koj-support:clike --target koj-clike
docker build . -t kairlec/koj-support:jvm8 --target koj-jvm8
docker build . -t kairlec/koj-support:jvm11 --target koj-jvm11
docker build . -t kairlec/koj-support:jvm17 --target koj-jvm17
