#!/bin/bash

function archiveTests {
	tar cvzf surefire-reports.tar.gz $(find . -name '*surefire-reports*')
}

function checkError {
	local retcode=$?

	if [ $retcode -ne 0 ]; then
		archiveTests

		exit $retcode
	fi
}

./mvnw clean --fail-at-end -e -Dmaven.repo.local=${GITHUB_WORKSPACE}/liferay-ide-m2-repository verify -Dliferay.bundles.dir="./tests-resources"

checkError