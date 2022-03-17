FROM python:3.6

WORKDIR /tmp/koj

COPY --from=kairlec/koj-runtime /usr/src/koj/koj /usr/sbin/
RUN chmod +x /usr/sbin/koj

CMD ["koj"]