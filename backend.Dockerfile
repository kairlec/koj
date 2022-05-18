FROM openjdk:17 as build

COPY . /data

WORKDIR /data

RUN chmod +x gradlew && \
    chmod +x init.sh && \
    ./init.sh && ./gradlew backend:koj-backend-server:shadowJar -PJOOQ_CACHE


FROM openjdk:17

COPY --from=build /data/build/libs/koj-backend-server.jar /data/application.jar

WORKDIR /data

CMD ["java", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseZGC" , "-Xmx2g" , "-jar" , "/data/application.jar"]