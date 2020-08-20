#!/bin/bash -x

if [ $# -lt 1 ]; then
	echo "Error usage: must pass in location of service-upgrade-bot repository"
	exit 1
fi

lugbotRepo=$1

rsync \
	-av \
	"$lugbotRepo/lugbot/com.liferay.code.upgrade.providers/src/com/liferay/ide/upgrade/problems/core/" \
	"./plugins/com.liferay.ide.upgrade.problems.core/src/com/liferay/ide/upgrade/problems/core"

rsync \
	-av \
	"$lugbotRepo/lugbot/com.liferay.code.upgrade.providers/src/com/liferay/ide/upgrade/problems/core/internal/" \
	"./plugins/com.liferay.ide.upgrade.problems.core/src/com/liferay/ide/upgrade/problems/core/internal"

rsync \
	-av \
	"$lugbotRepo/lugbot/com.liferay.code.upgrade.providers/src/com/liferay/ide/upgrade/problems/core/internal/liferay70/" \
	"./plugins/com.liferay.ide.upgrade.problems.core/src/com/liferay/ide/upgrade/problems/core/internal/liferay70"

rsync \
	-av \
	"$lugbotRepo/lugbot/com.liferay.code.upgrade.providers/src/com/liferay/ide/upgrade/problems/core/internal/liferay71/" \
	"./plugins/com.liferay.ide.upgrade.problems.core/src/com/liferay/ide/upgrade/problems/core/internal/liferay71"

rsync \
	-av \
	"$lugbotRepo/lugbot/com.liferay.code.upgrade.providers/src/com/liferay/ide/upgrade/problems/core/internal/liferay72/" \
	"./plugins/com.liferay.ide.upgrade.problems.core/src/com/liferay/ide/upgrade/problems/core/internal/liferay72"

rsync \
	-av \
	"$lugbotRepo/lugbot/com.liferay.code.upgrade.providers/src/com/liferay/ide/upgrade/problems/core/internal/liferay73/" \
	"./plugins/com.liferay.ide.upgrade.problems.core/src/com/liferay/ide/upgrade/problems/core/internal/liferay73"