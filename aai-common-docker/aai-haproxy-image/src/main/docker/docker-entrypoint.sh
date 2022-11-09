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
	shift # "haproxy"
	# if the user wants "haproxy", let's add a couple useful flags
	#   -W  -- "master-worker mode" (similar to the old "haproxy-systemd-wrapper"; allows for reload via "SIGUSR2")
	#   -db -- disables background mode
	set -- haproxy -W -db "$@"
fi

exec "$@"
