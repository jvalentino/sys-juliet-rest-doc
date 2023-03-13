#!/bin/bash
cd /opt/fluent-bit/bin
./fluent-bit -c fluentbit.conf > fluentbit.log 2>&1 &

cd /usr/local
java -jar -Dspring.datasource.url=jdbc:postgresql://pg-primary-postgresql:5432/examplesys sys-juliet-rest-doc-0.0.1.jar
