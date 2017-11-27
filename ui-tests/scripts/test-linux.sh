#!/bin/sh

cd ..
mvn clean verify -P ui-repo
mvn clean verify -P ui-tests-linux
rm -rf ../tests-resources/bundles

exit 0