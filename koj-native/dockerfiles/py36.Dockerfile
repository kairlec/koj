FROM python:3.6

WORKDIR /tmp/koj

COPY koj /usr/sbin/
RUN chmod +x /usr/sbin/koj

CMD ["koj"]