#!/bin/sh

set -e

##
## This script adds user and group into OS which can later be used by Liferay portal.
##

@UTILS_FILE_CONTENT@

##
## Constants
##

# The name of OS user to check for presence and to be created, if missing.
# This user will be have default group LIFERAY_GROUP.
LIFERAY_USER='@LIFERAY_USER@'

# The name of OS group to check for presence and to be created, if missing
LIFERAY_GROUP='@LIFERAY_GROUP@'

##
## Computed Constants
##

# none

# Make sure our custom 'die' command can be used safely
if ! command -v die >/dev/null 2>&1; then
    echo "==> ERROR: util command 'die' not found, was utils.sh included by Gradle?" && exit 127
fi

for const in \
		"$LIFERAY_USER" \
		"$LIFERAY_GROUP"; do
	case "$const" in
		@*@) die "One of the constants was not replaced by Gradle (starts and ends with '@').";;
		'') die "One of the constants has an empty value";;
	esac
done


run_script_add_liferay_user_and_group () {

	echo '========================================'
	echo ' Creation of OS user / group for Liferay'
	echo '========================================'

	enforce_root_user

	echo "==> Checking user:group for Liferay are present ($LIFERAY_USER:$LIFERAY_GROUP), creating them if necessary"

	add_group_if_missing

	add_user_if_missing

	echo "==> Finished checking user:group for Liferay ($LIFERAY_USER:$LIFERAY_GROUP)"
}

enforce_root_user() {
	current_user=$(whoami)

 	if [ "$current_user" != "root" ]; then
		echo "==> ERROR: Only 'root' (or sudo) is allowed to run this script, sorry. You are '$current_user'."

		exit 1
	fi
}

add_group_if_missing() {
	# the name of user & group mat not be the same (like 'liferay' user + 'apps' group), so
	# don't automatically assume we'll be good with always adding the group by `useradd` - rather
	# create our group manually and assign to the user created by `useradd` below

	if ! getent group $LIFERAY_GROUP > /dev/null 2>&1; then
		groupadd \
			$LIFERAY_GROUP

		echo "Group '$LIFERAY_GROUP' created"
	fi
}

add_user_if_missing() {

	if ! id -u $LIFERAY_USER > /dev/null 2>&1; then

		# TODO or use '/sbin/false'? Not sure if there is some universal value
		# for most Linux OSes
		no_login_shell='/sbin/nologin'

		# TODO create home, in case Liferay portal wants to store some files there?
		#       '--create-home' or manually adding that dir?

		# create user as system one (--system)
		# assign primary group as the one created above (--gid)
		# do not auto-create the groups named by the user - we are creating our own above (--no-user-group)
		# prevent login by setting non-login binary as (--shell)
		# add friendly full name of the user (--comment)
		#   (source: https://linux.die.net/man/8/useradd)
		useradd \
			--system \
			--gid $LIFERAY_GROUP \
			--no-user-group \
			--shell $no_login_shell \
			--comment 'User for running Liferay Portal / DXP' \
			$LIFERAY_USER

		echo "User '$LIFERAY_USER' created (system user, with primary group '$LIFERAY_GROUP')"
	fi
}

run_script_add_liferay_user_and_group