#!/bin/sh
set -e

# first arg is `-f` or `--some-option`
if [ "${1#-}" != "$1" ]; then
    set -- haproxy "$@"
fi

NAMESERVER_IP=$(cat /etc/resolv.conf  | grep 'nameserver' | head -1 | awk '{ print $2; }');

sed -i 's/${ONAP_NAMESERVER_CLUSTER_IP}/'${NAMESERVER_IP}'/g' /usr/local/etc/haproxy/resolvers.conf || {
    echo "Unable to overwrite the nameserver in the haproxy configuration file";
    exit 1;
}

if [ "$1" = 'haproxy' ]; then
    # if the user wants "haproxy", let's use "haproxy-systemd-wrapper" instead so we can have proper reloadability implemented by upstream
    shift # "haproxy"
    set -- "$(which haproxy-systemd-wrapper)" -p /run/haproxy.pid "$@"
fi

exec "$@"
