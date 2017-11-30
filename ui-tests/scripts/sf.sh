#!/bin/sh

cd ..
mvn clean verify -P ui-repo
mvn clean -P ui-tests-mac
mvn clean -P ui-repo
cd ../build/com.liferay.ide.build.source.formatter/
mvn source-formatter:format -D baseDir="../../ui-tests"

exit 0