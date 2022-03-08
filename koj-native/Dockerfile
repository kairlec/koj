FROM gcc:9.4 AS build

WORKDIR /usr/src/koj

COPY . .
RUN apt-get update && \
    DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends cmake ninja-build && \
    cmake -GNinja . && \
    ninja

FROM python:3.8 as koj-py38
WORKDIR /tmp/koj
COPY --from=build /usr/src/koj /usr/sbin/

CMD ["koj"]

FROM python:3.6 as koj-py36
WORKDIR /tmp/koj
COPY --from=build /usr/src/koj /usr/sbin/

CMD ["koj"]

FROM gcc:9.4 as koj-clike
WORKDIR /tmp/koj
COPY --from=build /usr/src/koj /usr/sbin/

CMD ["koj"]

FROM openjdk:8 as koj-jvm8

WORKDIR /tmp/koj

COPY --from=build /usr/src/koj /usr/sbin/
COPY policy/java.policy /etc/policy/java.policy
COPY kotlinc /usr/local/share/kotlinc

RUN ln -s /usr/local/share/kotlinc/bin/kotlinc /usr/sbin/kotlinc && \
    ln -s /usr/local/openjdk-8/bin/javac /usr/sbin/javac && \
    ln -s /usr/local/openjdk-8/bin/java /usr/sbin/java && \
    chmod +x /usr/local/share/kotlinc/bin/kotlinc && \
    chmod +x /usr/sbin/kotlinc && \
    chmod +x /usr/sbin/javac && \
    chmod +x /usr/sbin/java

ENV CLASSPATH /tmp/koj:/usr/local/share/kotlinc/lib/*

CMD ["koj"]

FROM openjdk:11 as koj-jvm11

WORKDIR /tmp/koj

COPY --from=build /usr/src/koj /usr/sbin/
COPY policy/java.policy /etc/policy/java.policy
COPY kotlinc /usr/local/share/kotlinc

RUN ln -s /usr/local/share/kotlinc/bin/kotlinc /usr/sbin/kotlinc && \
    ln -s /usr/local/openjdk-11/bin/javac /usr/sbin/javac && \
    ln -s /usr/local/openjdk-11/bin/java /usr/sbin/java && \
    chmod +x /usr/local/share/kotlinc/bin/kotlinc && \
    chmod +x /usr/sbin/kotlinc && \
    chmod +x /usr/sbin/javac && \
    chmod +x /usr/sbin/java

ENV CLASSPATH /tmp/koj:/usr/local/share/kotlinc/lib/*

CMD ["koj"]

FROM openjdk:17 as koj-jvm17

WORKDIR /tmp/koj

COPY --from=build /usr/src/koj /usr/sbin/
COPY policy/java.policy /etc/policy/java.policy
COPY kotlinc /usr/local/share/kotlinc

RUN ln -s /usr/local/share/kotlinc/bin/kotlinc /usr/sbin/kotlinc && \
    ln -s /usr/java/openjdk-17/bin/javac /usr/sbin/javac && \
    ln -s /usr/java/openjdk-17/bin/java /usr/sbin/java && \
    chmod +x /usr/local/share/kotlinc/bin/kotlinc && \
    chmod +x /usr/sbin/kotlinc && \
    chmod +x /usr/sbin/javac && \
    chmod +x /usr/sbin/java

ENV CLASSPATH /tmp/koj:/usr/local/share/kotlinc/lib/*

CMD ["koj"]