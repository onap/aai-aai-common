FROM haproxy:1.7

# Set up your corporate proxy if there is
ENV HTTP_PROXY  ""
ENV HTTPS_PROXY  ""
ENV http_proxy  ""
ENV https_proxy  ""

ENV DEBIAN_FRONTEND=noninteractive

RUN if [ ! -z ${HTTP_PROXY} ]; \
    then echo "Acquire::http::proxy  \"${HTTP_PROXY}\";" >> /etc/apt/apt.conf; \
    fi && \
    if [ ! -z ${HTTP_PROXY} ]; \
    then echo "Acquire::https::proxy \"${HTTP_PROXY}\";" >> /etc/apt/apt.conf; \
    fi && \
    apt-get update && \
    apt-get -y install ca-certificates openssl curl && \
    rm -rf /var/lib/apt/lists/*

RUN mkdir -p /etc/ssl/certs/ && mkdir -p /etc/ssl/private

COPY aai.pem /etc/ssl/private/aai.pem
COPY docker-entrypoint.sh /docker-entrypoint.sh
COPY resolvers.conf /usr/local/etc/haproxy/resolvers.conf
COPY haproxy.cfg /usr/local/etc/haproxy/haproxy.cfg

RUN chmod +x /docker-entrypoint.sh

EXPOSE 8443
