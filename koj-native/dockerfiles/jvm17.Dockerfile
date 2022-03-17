FROM openjdk:17

WORKDIR /tmp/koj

COPY policy/java.policy /etc/policy/java.policy
COPY kotlinc/ /usr/local/share/kotlinc
COPY --from=kairlec/koj-runtime /usr/src/koj/koj /usr/sbin/

RUN ln -s /usr/local/share/kotlinc/bin/kotlinc /usr/sbin/kotlinc && \
    ln -s /usr/java/openjdk-17/bin/javac /usr/sbin/javac && \
    ln -s /usr/java/openjdk-17/bin/java /usr/sbin/java && \
    chmod +x /usr/local/share/kotlinc/bin/kotlinc && \
    chmod +x /usr/sbin/kotlinc && \
    chmod +x /usr/sbin/javac && \
    chmod +x /usr/sbin/java && \
    chmod +x /usr/sbin/koj

ENV CLASSPATH /tmp/koj:/usr/local/share/kotlinc/lib/*

CMD ["koj"]