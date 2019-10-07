#!/bin/bash

IMAGE=$1

if [ -z "$IMAGE" ]; then
    echo "Usage: $0 IMAGE"
    exit 1
fi

IMAGE_TAG="${IMAGE##*:}"
if [ "$IMAGE" = "$IMAGE_TAG" ]; then
    IMAGE_TAG=latest
fi

if [ "$IMAGE_TAG" = "latest" ]; then
    IMAGE_OVERRIDE=true
else
    if [ -z "$IMAGE_OVERRIDE" ] || [ "$IMAGE_OVERRIDE" = "default" ]; then
        IMAGE_OVERRIDE=false
    fi
fi

if docker pull $IMAGE >/dev/null 2>&1; then
    if [ "$IMAGE_OVERRIDE" = "true" ]; then
        echo "Updating existing $IMAGE image."
        docker push "$IMAGE"
    else
        echo "$IMAGE image already exists."
        exit 1
    fi
else
    echo "Uploading new $IMAGE image."
    docker push "$IMAGE"
fi
