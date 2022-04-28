#!/bin/sh

set -e

cp -r ./.mvn ./reactive-lock/.mvn
cp mvnw ./reactive-lock/mvnw
cp mvnw.cmd ./reactive-lock/mvnw.cmd

cd reactive-lock
chmod +x mvnw
./mvnw install -DskipTests -Dspring-boot.repackage.skip=true