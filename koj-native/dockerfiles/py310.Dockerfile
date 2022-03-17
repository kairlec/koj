FROM python:3.10

COPY --from=kairlec/koj-runtime /usr/src/koj/koj /usr/sbin/

RUN chmod +x /usr/sbin/koj

CMD ["koj"]