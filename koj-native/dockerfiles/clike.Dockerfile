FROM gcc:9.4

COPY --from=kairlec/koj-runtime /usr/src/koj/koj /usr/sbin/

RUN chmod +x /usr/sbin/koj

CMD ["koj"]