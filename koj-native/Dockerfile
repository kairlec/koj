FROM gcc:9.4 AS build

WORKDIR /usr/src/koj

COPY . .
RUN apt-get update && \
    DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends cmake ninja-build && \
    cmake -GNinja . && \
    ninja

FROM python:3.8 as koj-py38
WORKDIR /root
COPY --from=build /usr/src/koj /usr/sbin/

CMD ["koj"]

FROM gcc:9.4 as koj-clike
WORKDIR /root
COPY --from=build /usr/src/koj /usr/sbin/

CMD ["koj"]

FROM openjdk:8 as koj-jvm8
WORKDIR /root
RUN apt-get update && \
    DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends unzip curl && \
    curl -L https://github.com/JetBrains/kotlin/releases/download/v1.6.10/kotlin-compiler-1.6.10.zip -o /usr/local/share/kotlin-compiler-1.6.10.zip && \
    unzip /usr/local/share/kotlin-compiler-1.6.10.zip -d /usr/local/share/ && \
    rm /usr/local/share/kotlin-compiler-1.6.10.zip && \
    chmod +x /usr/local/share/kotlinc/bin/kotlinc && \
    ln -s /usr/local/share/kotlinc/bin/kotlinc /usr/sbin/kotlinc
COPY --from=build /usr/src/koj /usr/sbin/

CMD ["koj"]