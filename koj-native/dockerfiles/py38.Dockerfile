FROM python:3.8

COPY koj /usr/sbin/

RUN chmod +x /usr/sbin/koj

CMD ["koj"]