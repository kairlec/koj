FROM openjdk:11

WORKDIR /tmp/koj

COPY koj /usr/sbin/
COPY policy/java.policy /etc/policy/java.policy
COPY kotlinc/ /usr/local/share/kotlinc

RUN ln -sf /usr/local/share/kotlinc/bin/kotlinc /usr/sbin/kotlinc && \
    ln -sf /usr/local/openjdk-11/bin/javac /usr/sbin/javac && \
    ln -sf /usr/local/openjdk-11/bin/java /usr/sbin/java && \
    chmod +x /usr/local/share/kotlinc/bin/kotlinc && \
    chmod +x /usr/sbin/kotlinc && \
    chmod +x /usr/sbin/javac && \
    chmod +x /usr/sbin/java && \
    chmod +x /usr/sbin/koj

ENV CLASSPATH /tmp/koj:/usr/local/share/kotlinc/lib/*

CMD ["koj"]