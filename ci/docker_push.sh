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

if docker pull $IMAGE >/dev/null 2>&1; then
if [[ ${REGISTRY} == *"artifactory"* ]]; then
    TAG_CHECK=$(curl -su "${DOCKER_REGISTRY_USER}:${DOCKER_REGISTRY_PASSWORD}" "https://${REGISTRY}/artifactory/api/docker/hyc-icap-open-site-images-docker-local/v2/icpa/${IMAGE_NAME}/tags/list" | jq ".tags[] | select(. == \"${IMAGE_TAG}\")")
elif [[ ${REGISTRY} == *"stg.icr.io"* ]]; then
    ibmcloud login --apikey "${DOCKER_REGISTRY_PASSWORD}" -a test.cloud.ibm.com -r us-south
    if [ "$?" -ne 0 ]; then
        echo "Failed to login to ibmcloud"
        exit 1
    fi
    TAG_CHECK=$(ibmcloud cr image-list --format "{{ if and (eq .Repository \"stg.icr.io/cp/icpa/${IMAGE_NAME}\") (eq .Tag \"${IMAGE_TAG}\") }}{{ .Tag }}{{ end }}")
else
    echo "Invalid registry ${REGISTRY}"
    exit 1
fi

if [ ! -z "$TAG_CHECK" ]; then
  if [ "$IMAGE_OVERRIDE" = "true" ]; then
    echo "Updating existing $IMAGE image."
    docker push "$IMAGE"