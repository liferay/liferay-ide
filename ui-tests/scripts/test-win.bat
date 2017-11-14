@echo off
cd ..
call mvn clean verify -P ui-repo
call mvn clean verify -P ui-tests-win
pause