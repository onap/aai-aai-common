FROM haproxy:2.4.13-alpine

# For building the image in a proxy environment if necessary
ARG HTTP_PROXY
ARG HTTPS_PROXY

ENV HTTP_PROXY   ${HTTP_PROXY}
ENV HTTPS_PROXY  ${HTTPS_PROXY}
ENV http_proxy   ${HTTP_PROXY}
ENV https_proxy  ${HTTPS_PROXY}

# Added to execute commands which required root permission
USER root

RUN apk add --no-cache \
    ca-certificates \
    curl \
    bash \
    socat \
    openssl \
    shadow \
    util-linux && \
    chown -R haproxy:haproxy /usr/local/etc/haproxy

RUN mkdir -p /etc/ssl/certs/ && mkdir -p /etc/ssl/private

COPY --chown=haproxy aai.pem /etc/ssl/private/aai.pem
COPY --chown=haproxy docker-entrypoint.sh /docker-entrypoint.sh
COPY --chown=haproxy resolvers.conf /usr/local/etc/haproxy/resolvers.conf
COPY --chown=haproxy haproxy.cfg /usr/local/etc/haproxy/haproxy.cfg

# Changing group and group permission to allow haproxy user to execute sed comamnd ot change files
RUN chgrp haproxy /usr/local/etc/haproxy; \
    chgrp haproxy /docker-entrypoint.sh /usr/local/etc/haproxy/haproxy.cfg /usr/local/etc/haproxy/resolvers.conf

RUN chmod +x /docker-entrypoint.sh; \
    chmod g+wx /usr/local/etc/haproxy; \
    chmod g+w /docker-entrypoint.sh /usr/local/etc/haproxy/haproxy.cfg /usr/local/etc/haproxy/resolvers.conf

# Reverting to haproxy use to not run the pod with root permissions
USER haproxy

ENTRYPOINT [ "/docker-entrypoint.sh" ]
CMD [ "haproxy", "-f", "/usr/local/etc/haproxy/haproxy.cfg", "-f", "/usr/local/etc/haproxy/resolvers.conf" ]
EXPOSE 8443
