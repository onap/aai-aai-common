FROM haproxy:1.7.10-alpine

# For building the image in a proxy environment if necessary
ARG HTTP_PROXY
ARG HTTPS_PROXY

ENV HTTP_PROXY   ${HTTP_PROXY}
ENV HTTPS_PROXY  ${HTTPS_PROXY}
ENV http_proxy   ${HTTP_PROXY}
ENV https_proxy  ${HTTPS_PROXY}

RUN apk add --no-cache \
    ca-certificates \
    curl \
    openssl \
    shadow \
    util-linux && \
    groupadd haproxy -g 1000 && \
    adduser -u 1000 -S -D -G haproxy -s /bin/bash haproxy && \
    chown -R haproxy:haproxy /usr/local/etc/haproxy

RUN mkdir -p /etc/ssl/certs/ && mkdir -p /etc/ssl/private

COPY --chown=haproxy aai.pem /etc/ssl/private/aai.pem
COPY --chown=haproxy docker-entrypoint.sh /docker-entrypoint.sh
COPY --chown=haproxy resolvers.conf /usr/local/etc/haproxy/resolvers.conf
COPY --chown=haproxy haproxy.cfg /usr/local/etc/haproxy/haproxy.cfg

RUN chmod +x /docker-entrypoint.sh
USER haproxy

ENTRYPOINT [ "/docker-entrypoint.sh" ]
CMD [ "haproxy", "-f", "/usr/local/etc/haproxy/haproxy.cfg", "-f", "/usr/local/etc/haproxy/resolvers.conf" ]
EXPOSE 8443
