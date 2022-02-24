FROM gcc:9.4 AS build

WORKDIR /src

COPY . .
RUN apt-get update && \
    DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends cmake ninja-build && \
    cmake -GNinja .. && \
    ninja


#FROM nginx
FROM ubuntu:22.04

COPY --from=build /usr/src/koj /usr/sbin/

ENTRYPOINT ["koj"]