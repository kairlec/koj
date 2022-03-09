FROM gcc:9.4

COPY koj /usr/sbin/

RUN chmod +x /usr/sbin/koj

CMD ["koj"]