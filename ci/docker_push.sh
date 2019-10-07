#!/bin/bash
IMAGE=$1

if [ -z "$IMAGE" ]; then
  echo "Usage: $0 IMAGE"
  exit 1
fi

if [ -z "$DOCKER_REGISTRY_PASSWORD" ]; then
  echo "You MUST set the DOCKER_REGISTRY_PASSWORD environment variable"
  exit 1
fi

if [ -z "$DOCKER_REGISTRY_USER" ]; then
  echo "You MUST set the DOCKER_REGISTRY_USER environment variable"
  exit 1
fi

REG_IMAGE="${IMAGE%%:*}"
IMAGE_NAME="${REG_IMAGE##*/}"
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

if [[ ${DOCKER_REGISTRY} == *"artifactory"* ]]; then
    TAG_CHECK=$(curl -su "${DOCKER_REGISTRY_USER}:${DOCKER_REGISTRY_PASSWORD}" "https://${DOCKER_REGISTRY}/artifactory/api/docker/hyc-icap-open-site-images-docker-local/v2/kabanero/${IMAGE_NAME}/tags/list" | jq ".tags[] | select(. == \"${IMAGE_TAG}\")")
elif [[ ${DOCKER_REGISTRY} == *"hub.docker.com"* ]]; then
	TOKEN=$(curl -s -H "Content-Type: application/json" -X POST -d '{"username": "'${DOCKER_REGISTRY_USER}'", "password": "'${DOCKER_REGISTRY_PASSWORD}'"}' https://hub.docker.com/v2/users/login/ | jq -r .token)
  	TAG_CHECK=$(curl -s -H "Authorization: JWT ${TOKEN}" https://hub.docker.com/v2/repositories/${DOCKER_REGISTRY_USER}/${IMAGE_NAME}/tags/?page_size=10000 | jq -r ".results[] | select(.name  == \"${IMAGE_TAG}\")")
else
    echo "Invalid registry ${DOCKER_REGISTRY}"
    exit 1
fi

if [ ! -z "$TAG_CHECK" ]; then
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