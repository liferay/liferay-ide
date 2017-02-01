#!/bin/sh

set -e

## WARNING: make sure Liferay bundle is stopped _BEFORE_ running this script

##
## This script is intended to be invoked by Jenkins server, over SSH as user 'root' (or using sudo).
## It will take Liferay bundle archive as produced by Liferay Workspace's Gradle build (see LIFERAY_BUNDLE)
## and install it into known Liferay portal home (see LIFERAY_HOME).
##
## 'LIFERAY_HOME_DATA_DIR_NAME' and 'portal-setup-wizard.properties' are backed up and restored after
## installation of the new build, to preserve their content.
##

@UTILS_FILE_CONTENT@

##
## Constants
##

# The location of the Liferay bundle archive to install.
LIFERAY_BUNDLE_ARCHIVE='@LIFERAY_BUNDLE_ARCHIVE@'

# The name of OS user to own all installed bundle's files.
LIFERAY_USER='@LIFERAY_USER@'

# The name of OS group to own all installed bundle's files.
LIFERAY_GROUP='@LIFERAY_GROUP@'

# The Liferay bundle home, new bundle will be extracted there.
LIFERAY_HOME='@LIFERAY_HOME@'

# The name of data directory inside LIFERAY_HOME, typically 'data'.
# It will _not_ be deleted when removing the bundle files.
LIFERAY_HOME_DATA_DIR_NAME='@LIFERAY_HOME_DATA_DIR_NAME@'

# The name of the directory inside Liferay bundle home with the app server.
# For Tomcat server, this will most likely be like 'tomcat-8.0.32'. This is used
# to locate the validation file and set up app server, if necessary.
LIFERAY_HOME_APP_SERVER_DIR_NAME='@LIFERAY_HOME_APP_SERVER_DIR_NAME@'

# The file inside the app server (relative to the LIFERAY_HOME_APP_SERVER_DIR_NAME)
# which can be used to verify the bundle was installed correctly. Typically some script,
# like 'bin/catalina.sh' for Tomcat.
APP_SERVER_VALIDATION_FILE_PATH='@APP_SERVER_VALIDATION_FILE_PATH@'


# Make sure our custom 'die' command can be used safely
if ! command -v die >/dev/null 2>&1; then
    echo "==> ERROR: util command 'die' not found, was utils.sh included by Gradle?" && exit 127
fi

for const in \
		"$LIFERAY_BUNDLE_ARCHIVE" \
		"$LIFERAY_USER" \
		"$LIFERAY_GROUP" \
		"$LIFERAY_HOME" \
		"$LIFERAY_HOME_DATA_DIR_NAME" \
		"$LIFERAY_HOME_APP_SERVER_DIR_NAME" \
		"$APP_SERVER_VALIDATION_FILE_PATH"; do
	case "$const" in
		@*@) die "One of the constants was not replaced by Gradle (starts and ends with '@').";;
		'') die "One of the constants has an empty value";;
	esac
done

##
## Computed Constants
##

# The full path of the app server (like Tomcat) inside the bundle.
APP_SERVER_HOME="${LIFERAY_HOME}/${LIFERAY_HOME_APP_SERVER_DIR_NAME}"

# The working directory when the script was started. The zip file may be relative to this path,
# so we need to remember this path and use ('cd' into) it before unzipping the bundle
SCRIPT_PWD=`pwd`

# The timestamp
TIMESTAMP=`date +%s`

# The directory where important files from previous build will be backed before
# installing new build and up and restored.
TMP_DATA_BACKUP_DIR="/tmp/liferay.data.backup.$TIMESTAMP"


install_liferay_bundle() {

	echo "================================"
	echo " Installation of Liferay bundle "
	echo "================================"

	enforce_root_user

	echo "==> Installing Liferay bundle '$LIFERAY_BUNDLE_ARCHIVE' into '$LIFERAY_HOME'..."

	backup_previous_data_and_remove_bundle

	extract_new_bundle

	restore_previous_data

	set_bundle_files_owner_and_permissions

	verify_bundle_installed

	echo "==> New Liferay bundle '$LIFERAY_BUNDLE_ARCHIVE' was installed into '$LIFERAY_HOME' successfully"
}

enforce_root_user() {
	current_user=$(whoami)

 	if [ "$current_user" != "root" ]; then
		die "==> ERROR: Only 'root' (or sudo) is allowed to run this script, sorry. You are '$current_user'."
	fi
}

backup_previous_data_and_remove_bundle() {
	if [ ! -d "$LIFERAY_HOME" ]; then
		echo "==> Nothing to backup, Liferay bundle's directory '$LIFERAY_HOME' does not exist"

		return
	fi

	echo "==> Backing up data of previous bundle ('$LIFERAY_HOME')"

	# create clean directory for backup
	rm -rf $TMP_DATA_BACKUP_DIR
	mkdir -p $TMP_DATA_BACKUP_DIR

	# Based on http://unix.stackexchange.com/a/153863
	# remove all files / directories except the ones in the list
	cd $LIFERAY_HOME

	find . -maxdepth 1 ! -path . \
		! \( -name "$LIFERAY_HOME_DATA_DIR_NAME" -o -name 'portal-setup-wizard.properties' \) -exec rm -rf {} +

	# Backup data-related files and directories which were excluded from delete above
	#   (1) [bundle]/$LIFERAY_HOME_DATA_DIR_NAME/*   (typically, LIFERAY_HOME_DATA_DIR_NAME == 'data')
	#   (2) [bundle]/portal-setup-wizard.properties
	#
	# it's important to have '/' at the end of source nd target directory
	# based on: https://stackoverflow.com/questions/20300971/rsync-copy-directory-contents-but-not-directory-itself
	rsync -a $LIFERAY_HOME/ $TMP_DATA_BACKUP_DIR/

	echo "==> Finished backing up data from previous bundle"

	echo "==> Contents of '$TMP_DATA_BACKUP_DIR':"
	ls -l $TMP_DATA_BACKUP_DIR
	du -h -d 1 $TMP_DATA_BACKUP_DIR

	# remove old bundle
	rm -rf $LIFERAY_HOME

	echo "==> Previous bundle removed from '$LIFERAY_HOME'"
}

extract_new_bundle () {
	echo "==> Extract new bundle into '$LIFERAY_HOME'"

	mkdir -p $LIFERAY_HOME

	cd $SCRIPT_PWD

	# Unzip / untar Liferay bundle into target Liferay bundle home


	case "$LIFERAY_BUNDLE_ARCHIVE" in
		*.zip)
	        # -qq ~ very quiet = do not output anything
	        # -o ~ overwrite files = since we add overrides (from configs/ on top of e.g. tomcat-8.0.32),
	        #       its very likely there will be duplicate entries in the archive;
	        #       we want to overwrite without prompting
	        # -d ~ extract into given directory
		    unzip -qq -o $LIFERAY_BUNDLE_ARCHIVE -d $LIFERAY_HOME
			;;

		*.tar.gz)
			# x ~ extract
			# z ~ use GZip (un)compression
			# f ~ extract from file
			# --directory ~ extract into given directory

			# .tar (unlike .zip above) automatically overwrites during extracting and
			# does not prompt for confirmation of overwriting duplicate entries present in the archive

	        tar xzf $LIFERAY_BUNDLE_ARCHIVE --directory $LIFERAY_HOME
	        ;;

		*)
	        die "==> ERROR: Unknown format of Liferay bundle file ($LIFERAY_BUNDLE_ARCHIVE). Only .zip or .tar.gz archives are supported"
	esac

	# For some odd reason, Tomcat cannot create [tomcat]/logs/ directory (if missing)
	# and fails to start. So create it manually.
	mkdir -p $APP_SERVER_HOME/logs

	echo "==> New bundle extracted into '$LIFERAY_HOME':"
	ls -lah $LIFERAY_HOME
}

restore_previous_data () {
	if [ ! -d  "$TMP_DATA_BACKUP_DIR" ]; then
		echo "==> Nothing to restore, backup directory '$TMP_DATA_BACKUP_DIR' does not exist"

		return
	fi

	echo "==> Restoring previous bundle's data"

	echo "==> Contents of '$TMP_DATA_BACKUP_DIR':"
	ls -l $TMP_DATA_BACKUP_DIR
	du -h -d 1 $TMP_DATA_BACKUP_DIR

	# it's important to have '/' at the end of source and target directory
	# based on: https://stackoverflow.com/questions/20300971/rsync-copy-directory-contents-but-not-directory-itself
	rsync -a $TMP_DATA_BACKUP_DIR/ $LIFERAY_HOME/

	rm -rf $TMP_DATA_BACKUP_DIR

	echo "==> Restored previous bundle's data from '$TMP_DATA_BACKUP_DIR' into '$LIFERAY_HOME'"

	echo "==> New contents of '$LIFERAY_HOME':"
	ls -lah $LIFERAY_HOME
}

set_bundle_files_owner_and_permissions () {
	chown --recursive $LIFERAY_USER:$LIFERAY_GROUP $LIFERAY_HOME

	# The bundle files might contain passwords to DB or remote systems, which
	# should be visible only to user 'liferay'.

	# the properties in Liferay portal home may contain credentials to other systems
	chmod 600 $LIFERAY_HOME/*.properties

	echo "==> New bundle's file permissions / ownership set in '$LIFERAY_HOME':"
	ls -lah $LIFERAY_HOME
}

verify_bundle_installed () {
	app_server_validation_file="$APP_SERVER_HOME/$APP_SERVER_VALIDATION_FILE_PATH"

	if [ -f "$app_server_validation_file" ]; then
		echo "==> Liferay bundle was successfully installed, expected app server file '$app_server_validation_file' exists"
	else
	    die "==> ERROR: Liferay bundle was not installed, expected app server file '$app_server_validation_file' does not exist"
	fi
}

install_liferay_bundle