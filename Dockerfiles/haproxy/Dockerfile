FROM haproxy:1.7-alpine

# Set up your corporate proxy if there is
ENV HTTP_PROXY  ""
ENV HTTPS_PROXY  ""
ENV http_proxy  ""
ENV https_proxy  ""

RUN apk add --no-cache \
    ca-certificates \
    openssl \
    curl

RUN mkdir -p /etc/ssl/certs/ && mkdir -p /etc/ssl/private

COPY aai.pem /etc/ssl/private/aai.pem
COPY docker-entrypoint.sh /docker-entrypoint.sh
COPY resolvers.conf /usr/local/etc/haproxy/resolvers.conf
COPY haproxy.cfg /usr/local/etc/haproxy/haproxy.cfg

RUN chmod +x /docker-entrypoint.sh

ENTRYPOINT [ "/docker-entrypoint.sh" ]
CMD [ "haproxy", "-f", "/usr/local/etc/haproxy/haproxy.cfg", "-f", "/usr/local/etc/haproxy/resolvers.conf" ] 
EXPOSE 8443
