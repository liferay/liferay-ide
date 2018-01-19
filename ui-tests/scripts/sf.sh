#!/bin/sh

cd ..
cd ../build/com.liferay.ide.build.source.formatter/
mvn source-formatter:format -D baseDir="../../ui-tests"

exit 0