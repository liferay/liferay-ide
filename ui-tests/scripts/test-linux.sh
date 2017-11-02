#!/bin/sh

cd ..
mvn clean verify -P ui-repo
mvn clean verify -P ui-tests-mac

exit 0