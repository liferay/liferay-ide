#!/bin/sh

cd ..
mvn clean verify -P ui-repo
mvn clean verify -P ui-tests-mac
rm -rf ../tests-resources/bundles

exit 0