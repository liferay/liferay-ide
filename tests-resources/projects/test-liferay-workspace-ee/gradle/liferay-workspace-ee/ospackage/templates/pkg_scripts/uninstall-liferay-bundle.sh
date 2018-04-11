#!/bin/sh

set -e

##
## This script removes Liferay bundle from given path, except 'data/' and
## 'portal-setup-wizard.properties' - these are left in place, if found
##

@UTILS_FILE_CONTENT@

##
## Constants
##

# The home Liferay bundle , which should be uninstalled.
LIFERAY_HOME='@LIFERAY_HOME@'

# The name of data directory inside LIFERAY_HOME, typically 'data'.
# It will _not_ be deleted when removing the bundle files.
LIFERAY_HOME_DATA_DIR_NAME='@LIFERAY_HOME_DATA_DIR_NAME@'


# Make sure our custom 'die' command can be used safely
if ! command -v die >/dev/null 2>&1; then
    echo "==> ERROR: util command 'die' not found, was utils.sh included by Gradle?" && exit 127
fi

for const in \
		"$LIFERAY_HOME" \
		"$LIFERAY_HOME_DATA_DIR_NAME"; do
	case "$const" in
		@*@) die "One of the constants was not replaced by Gradle (starts and ends with '@').";;
		'') die "One of the constants has an empty value";;
	esac
done


echo "=================================="
echo " Uninstallation of Liferay bundle "
echo "=================================="

if [ -d "$LIFERAY_HOME" ]; then

	# Based on http://unix.stackexchange.com/a/153863
	# remove all files / directories except the ones in the list
	cd $LIFERAY_HOME

	find . -maxdepth 1 ! -path . ! \( -name "$LIFERAY_HOME_DATA_DIR_NAME" -o -name 'portal-setup-wizard.properties' \) -exec rm -rf {} +

	echo "==> Liferay bundle uninstalled from '$LIFERAY_HOME', only '$LIFERAY_HOME_DATA_DIR_NAME' directory and 'portal-setup-wizard.properties' were left untouched (if present)"

	ls -lha $LIFERAY_HOME
else
	echo "==> No Liferay bundle to uninstall in path '$LIFERAY_HOME' (directory does not exist)"
fi