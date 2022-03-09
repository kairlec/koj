FROM gcc:9.4

WORKDIR /usr/src/koj

COPY . .
RUN apt-get update && \
    DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends cmake ninja-build && \
    cmake -GNinja . && \
    ninja
