@echo off
cd ..
call mvn clean verify -P functional-repo
call mvn clean verify -P functional-tests-win
pause