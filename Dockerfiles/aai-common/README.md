# AAI Common Docker README

### Building the image

Run the following command to build AAI common images at internal network

```bash
    export NEW_VERSION=<NEW_VERSION>
    export BASE_IMAGE=<alpine | ubuntu>
    sudo docker build -t \
        nexus3.onap.org:10003/onap/aai-common-${BASE_IMAGE}:${NEW_VERSION} \
        -f Dockerfile.${BASE_IMAGE} .
```

Replace the **NEW\_VERSION** with the new docker image version for aai-common.
Set **BASE\_IMAGE** to **alpine** or **ubuntu** to build aai-common-alpine or
aai-common-ubuntu image.

NOTE: In order to push images into Nexus3, you have to be logged into Nexus3
with appropriate credentials first.

NOTE2: Both alpine and ubuntu based aai-common images are built automatically
by jenkins jobs and they are available at official ONAP docker registry
(currently nexus3).
