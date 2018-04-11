#!/bin/sh

set -e

##
## This script adds Liferay bundle's service of given name to OS startup.
##

@UTILS_FILE_CONTENT@

##
## Constants
##

# The name of the service to start / stop Liferay bundle. This is typically
# the name of the script placed inside '/etc/init.d'.
LIFERAY_SERVICE_NAME='@LIFERAY_SERVICE_NAME@'


# Make sure our custom 'die' command can be used safely
if ! command -v die >/dev/null 2>&1; then
    echo "==> ERROR: util command 'die' not found, was utils.sh included by Gradle?" && exit 127
fi

for const in \
		"$LIFERAY_SERVICE_NAME"; do
	case "$const" in
		@*@) die "One of the constants was not replaced by Gradle (starts and ends with '@').";;
		'') die "One of the constants has an empty value";;
	esac
done


echo '============================================='
echo ' Enabling of Liferay bundle as an OS service '
echo '============================================='

# Various distros use various commands: https://fedoraproject.org/wiki/SysVinit_to_Systemd_Cheatsheet
# First test if the command exists, based on http://stackoverflow.com/a/677212/4966203

command_found=0

# SysVinit
#
# Debian: https://www.debian-administration.org/article/28/Making_scripts_run_at_boot_time_with_Debian
# Ubuntu: http://manpages.ubuntu.com/manpages/hardy/man8/update-rc.d.8.html
if command -v update-rc.d >/dev/null 2>&1; then
	echo "==> using 'update-rc.d'"

	update-rc.d $LIFERAY_SERVICE_NAME defaults

	command_found=1
else
	echo "==> skipping 'update-rc.d', command not found"
fi

# CentOS: https://www.centos.org/docs/5/html/Deployment_Guide-en-US/s1-services-chkconfig.html
if command -v chkconfig >/dev/null 2>&1; then
	echo "==> using 'chkconfig'"

	chkconfig $LIFERAY_SERVICE_NAME on
	chkconfig $LIFERAY_SERVICE_NAME --list

	command_found=1
else
	echo "==> skipping 'chkconfig', command not found"
fi

# Systemd
#
if command -v systemctl >/dev/null 2>&1; then
	echo "==> using 'systemctl'"

	systemctl enable $LIFERAY_SERVICE_NAME

	command_found=1
else
	echo "==> skipping 'systemctl', command not found"
fi

if [ $command_found -eq 0 ]; then
	echo "==> ERROR: No command to manage OS services found, cannot enable the service"

	exit 1
fi