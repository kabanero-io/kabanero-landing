#!/bin/bash -e

DOCKER_IMAGE_NAME=${1:-landing}
DOCKER_IMAGE_TAG=${2:-latest}

CUR_DIR="$(cd $(dirname $0) && pwd)"

cd $CUR_DIR/../

GIT_REVISION=$(git rev-parse HEAD)
PRODUCT_VERSION=$(git describe --tags)

if [ "$DOCKER_USE_CACHE" = "false" ]; then
    echo "Docker cache: off"
    DOCKER_OPTS="--no-cache"
else
    echo "Docker cache: on"
fi

docker build $DOCKER_OPTS --pull \
--build-arg GIT_REVISION="$GIT_REVISION" \
--build-arg PRODUCT_VERSION="$PRODUCT_VERSION" \
--build-arg DOCS_GIT_URL="$DOCS_GIT_URL" \
--build-arg DOCS_GIT_REVISION="$DOCS_GIT_REVISION" \
-t "$DOCKER_IMAGE_NAME:$DOCKER_IMAGE_TAG" \
.