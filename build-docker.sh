#!/bin/bash

NAME=sys-juliet-rest-doc
VERSION=latest
HELM_NAME=sys-rest-doc

helm delete $HELM_NAME || true
minikube image rm $NAME:$VERSION
rm -rf ~/.minikube/cache/images/arm64/$NAME_$VERSION || true
docker build --no-cache . -t $NAME
minikube image load $NAME:$VERSION
# minikube cache reload
