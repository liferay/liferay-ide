@echo off
cd .. & call mvn clean
cd ../build/com.liferay.ide.build.source.formatter/ & call mvn source-formatter:format -D baseDir="../../functional-tests"
pause