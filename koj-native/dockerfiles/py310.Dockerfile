FROM python:3.10

COPY koj /usr/sbin/

RUN chmod +x /usr/sbin/koj

CMD ["koj"]