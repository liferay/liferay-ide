#!/bin/sh

cd ..
mvn clean verify -P functional-repo
mvn clean verify -P functional-tests-linux
rm -rf ../tests-resources/bundles

exit 0