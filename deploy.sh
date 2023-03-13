#!/bin/sh
HELM_NAME=sys-rest-doc

helm delete $HELM_NAME --wait
helm install $HELM_NAME --wait config/helm/sys-rest-doc --values config/helm/sys-rest-doc/values.yaml
sh -x ./verify.sh
