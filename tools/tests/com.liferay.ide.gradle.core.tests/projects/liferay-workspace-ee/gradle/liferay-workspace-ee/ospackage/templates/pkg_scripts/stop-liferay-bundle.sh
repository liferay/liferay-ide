#!/bin/sh

set -e

##
## This script stops Liferay bundle using script of given name expected to be
## found in /etc/init.d.
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

##
## Computed Constants
##

# The full path to the service script used to start / stop Liferay bundle.
# This is typically a script placed inside '/etc/init.d', named according to LIFERAY_SERVICE_NAME
LIFERAY_SERVICE_SCRIPT="/etc/init.d/$LIFERAY_SERVICE_NAME"


echo '============================'
echo ' Stopping of Liferay bundle '
echo '============================'

# Before removing / before upgrading the package:
# Make sure Liferay is stopped before installing the new bundle;
# file should be present, since it gets installed by the DEB / RPM; but play is safe and
# test first (file may have been removed manually from the OS)
echo "==> Making sure Liferay bundle is stopped (using $LIFERAY_SERVICE_SCRIPT)"

if [ -x "$LIFERAY_SERVICE_SCRIPT" ]; then
     $LIFERAY_SERVICE_SCRIPT stop
else
     echo "==> WARN: cannot find script '$LIFERAY_SERVICE_SCRIPT', proceeding without stopping Liferay bundle"
fi