# AAI HAProxy README

### Building the image

Run the following command if building the image in internal network

```bash
    export NEW_VERSION=<NEW_VERSION>
    docker build \
           -t aaionap/haproxy:${NEW_VERSION} .
```

Replace the NEW\_VERSION with the new docker image version for aai-common
