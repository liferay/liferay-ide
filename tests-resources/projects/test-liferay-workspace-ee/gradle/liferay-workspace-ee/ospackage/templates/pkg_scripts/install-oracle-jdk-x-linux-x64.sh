#!/bin/sh

set -e

##
## This script installs Oracle JDK into location where Liferay portal (/etc/init.d script)
## expects it.
##

@UTILS_FILE_CONTENT@

##
## Constants
##

# The major versin of this JDK, like '8'
JDK_VERSION_MAJOR='@JDK_VERSION_MAJOR@'

# The location (on oracle.com) where the JDK can be fetched
JDK_DOWNLOAD_URL='@JDK_DOWNLOAD_URL@'

# The name of the fetched archive, corresponding to DOWNLOAD_URL
JDK_DOWNLOAD_ARCHIVE_NAME='@JDK_DOWNLOAD_ARCHIVE_NAME@'

# The top-level directory containing JDK in the downloaded archive, corresponding to DOWNLOAD_URL
JDK_DOWNLOAD_ARCHIVE_TOP_LEVEL_DIR='@JDK_DOWNLOAD_ARCHIVE_TOP_LEVEL_DIR@'

# The checksum of the downloaded file. If local file with matching checksum if found, archive
# does not have to be downloaded. Useful when using this script in VM where we pre-uploaded
# the JDK's tar.
JDK_DOWNLOAD_ARCHIVE_MD5_CHECKSUM='@JDK_DOWNLOAD_ARCHIVE_MD5_CHECKSUM@'

# The _parent_ directory where JDK archive will be downloaded.
JDK_INSTALLATION_SOURCE_ARCHIVE_PARENT_DIR='@JDK_INSTALLATION_SOURCE_ARCHIVE_PARENT_DIR@'

# The _parent_ path where JDK archive will be downloaded. The actual installation will go
# into subdirectory in this directory, e.g. '/opt/liferay/jdk1.8.0_73' (see DOWNLOAD_ARCHIVE_TOP_LEVEL_DIR)
JDK_INSTALLATION_PARENT_DIR='@JDK_INSTALLATION_PARENT_DIR@'

# The name of symlink inside JDK_PARENT_DIR where JDK will be accessible
JDK_INSTALLATION_SYMLINK_NAME='@JDK_INSTALLATION_SYMLINK_NAME@'


# Make sure our custom 'die' command can be used safely
if ! command -v die >/dev/null 2>&1; then
    echo "==> ERROR: util command 'die' not found, was utils.sh included by Gradle?" && exit 127
fi

for const in \
		"$JDK_VERSION_MAJOR" \
		"$JDK_DOWNLOAD_URL" \
		"$JDK_DOWNLOAD_ARCHIVE_NAME" \
		"$JDK_DOWNLOAD_ARCHIVE_TOP_LEVEL_DIR" \
		"$JDK_DOWNLOAD_ARCHIVE_MD5_CHECKSUM" \
		"$JDK_INSTALLATION_SOURCE_ARCHIVE_PARENT_DIR" \
		"$JDK_INSTALLATION_PARENT_DIR" \
		"$JDK_INSTALLATION_SYMLINK_NAME"; do
	case "$const" in
		@*@) die "One of the constants was not replaced by Gradle (starts and ends with '@').";;
		'') die "One of the constants has an empty value";;
	esac
done

##
## Computed Constants
##

JDK_DOWNLOAD_ARCHIVE_PATH="$JDK_INSTALLATION_SOURCE_ARCHIVE_PARENT_DIR/$JDK_DOWNLOAD_ARCHIVE_NAME"

# The full path to where JDK will be accessible
JAVA_HOME="$JDK_INSTALLATION_PARENT_DIR/$JDK_INSTALLATION_SYMLINK_NAME"

# A file to test for presence (typically inside JAVA_HOME). If it exists, it means the Oracle JDK
# is already installed in this OS and ready to be used by Liferay or other JVMs.
EXPECTED_JAVA_HOME_JAVA_BIN_PATH="$JAVA_HOME/bin/java"


run_script_install_oracle_jdk() {

	echo '=============================='
	echo " Installation of Oracle JDK $JDK_VERSION_MAJOR"
	echo '=============================='

	enforce_root_user

	jdk_installed=$(is_jdk_installed)

	if [ "$jdk_installed" = "false" ]; then
		download_jdk_if_missing

		install_jdk

		verify_jdk_installation
	else
		echo "==> Java home directory '$JAVA_HOME' exists and binary file '$EXPECTED_JAVA_HOME_JAVA_BIN_PATH' is present, assuming JDK was already installed previously and is available."
	fi
}

enforce_root_user() {
	current_user=$(whoami)

 	if [ "$current_user" != "root" ]; then
		die "==> ERROR: Only 'root' (or sudo) is allowed to run this script, sorry. You are '$current_user'."
	fi
}

# If JDK is NOT installed in expected path (the common, usual case)     -> print 'false'
# If JDK is installed in expected path (special, unexpected case)       -> print 'true'
#
is_jdk_installed() {

	# install only if given path (JAVA_HOME) does not exist

	if [ -f "$EXPECTED_JAVA_HOME_JAVA_BIN_PATH" ]; then
	    echo 'true'
	fi

	echo 'false'
}

download_jdk_if_missing() {

    # check if file exists and matches the checksum to prevent unnecessary download

	if [ -f "$JDK_DOWNLOAD_ARCHIVE_PATH" ]; then

		echo "==> JDK installation archive '$JDK_DOWNLOAD_ARCHIVE_PATH' found, computing MD5 checksum (ls -l *.tar.gz)..."

		ls -l $JDK_DOWNLOAD_ARCHIVE_PATH

		# based on http://stackoverflow.com/a/3679329
		md5Checksum=`md5sum $JDK_DOWNLOAD_ARCHIVE_PATH | cut -c 1-32`

		if [ "$md5Checksum" = "$JDK_DOWNLOAD_ARCHIVE_MD5_CHECKSUM" ]; then
			echo "==> MD5 checksum of '$JDK_DOWNLOAD_ARCHIVE_PATH' matches configured value, skipping download."

			return
		else
			die "==> MD5 checksum '$md5Checksum' of $JDK_DOWNLOAD_ARCHIVE_PATH does NOT match the expected value ($JDK_DOWNLOAD_ARCHIVE_MD5_CHECKSUM), please remove $JDK_DOWNLOAD_ARCHIVE_PATH or upload correct archive from oracle.com"
		fi
	fi

	mkdir -p $JDK_INSTALLATION_SOURCE_ARCHIVE_PARENT_DIR

	cd $JDK_INSTALLATION_SOURCE_ARCHIVE_PARENT_DIR

	echo '==> Downloading Oracle JDK...'

	# based on http://stackoverflow.com/a/10959815/4966203
	# wget is typically on Debian systems, curl on Fedora

	license_cookie_header='Cookie: oraclelicense=accept-securebackup-cookie'

	if command -v curl >/dev/null 2>&1; then
		echo "==> Using 'curl'"

		curl \
			--junk-session-cookies --insecure --location \
			--header "$license_cookie_header" \
			-o $JDK_DOWNLOAD_ARCHIVE_NAME \
			$JDK_DOWNLOAD_URL

	elif command -v wget >/dev/null 2>&1; then
		echo "==> Using 'wget'"

		wget \
			--no-cookies --no-check-certificate \
			--header "$license_cookie_header" \
			$JDK_DOWNLOAD_URL \
			-O $JDK_DOWNLOAD_ARCHIVE_NAME

	else
		die "==> ERROR: neither 'wget' nor 'curl' found, at least one of these commands is needed to be able to download the JDK archive"
	fi

	echo "==> Oracle JDK downloaded (ls -l $JDK_INSTALLATION_SOURCE_ARCHIVE_PARENT_DIR):"
	ls -l $JDK_INSTALLATION_SOURCE_ARCHIVE_PARENT_DIR


	# Prevent situation where the URL serves just HTML with warning. Oracle serves
	# only the very latest JDK's release publicly, for older files it gives you a misleading
	# error page with text like:
	#   "Thank you for accessing the Oracle Software Delivery Cloud. Due to your country location,
	#   we are unable to process your request. If you have an active support contract..."

	# based on http://stackoverflow.com/a/3679329
	md5Checksum=`md5sum $JDK_DOWNLOAD_ARCHIVE_PATH | cut -c 1-32`

	if [ "$md5Checksum" = "$JDK_DOWNLOAD_ARCHIVE_MD5_CHECKSUM" ]; then
			echo "==> MD5 checksum of '$JDK_DOWNLOAD_ARCHIVE_PATH' matches configured value, download was successful."

			return
		else
			die "==> MD5 checksum of $JDK_DOWNLOAD_ARCHIVE_PATH does _not_ match the expected value ($JDK_DOWNLOAD_ARCHIVE_MD5_CHECKSUM), download of the JDK archive using URL '$JDK_DOWNLOAD_URL' was unsuccessful. Try the URL in your browser to possibly see the reasons of the failure. Possibly you are not using the _latest_ release of the JDK $JDK_VERSION_MAJOR (http://www.oracle.com/technetwork/java/javase/downloads/index-jsp-138363.html) - it seems that only the files from the latest release will be served publicly from oracle.com. In liferay-workspace-ee project, the JDK download details (URL, file name etc.) are configured at the top of ospackage.gradle and then put into the constants at the top of this file."
		fi
}

install_jdk() {
	mkdir -p $JDK_INSTALLATION_PARENT_DIR

	cd $JDK_INSTALLATION_PARENT_DIR

	echo '==> Extracting Oracle JDK, creating symlink...'
	tar xzf $JDK_DOWNLOAD_ARCHIVE_PATH

	# make sure all owner for all extracted files is set - prevent files ending up as
	# owned by e.g. 'uucp 143' due to permissions stored inside .tar.gz archive
	chown -R root:root $JDK_DOWNLOAD_ARCHIVE_TOP_LEVEL_DIR

	# there is a top-level directory like 'jdk1.7.0_79' in every JDK archive, so it will
	# be created inside JDK_INSTALLATION_PARENT_DIR as a result of 'tar'
	ln -s $JDK_DOWNLOAD_ARCHIVE_TOP_LEVEL_DIR $JDK_INSTALLATION_SYMLINK_NAME

	echo "==> Oracle JDK extracted, symlink created ($JDK_INSTALLATION_PARENT_DIR):"
	ls -l $JDK_INSTALLATION_PARENT_DIR
}

verify_jdk_installation() {

	if [ -f "$EXPECTED_JAVA_HOME_JAVA_BIN_PATH" ]; then
		echo "==> Oracle JDK was successfully installed, expected file '$EXPECTED_JAVA_HOME_JAVA_BIN_PATH' exists."
	else
		die "==> ERROR: Oracle JDK was not installed, expected file '$EXPECTED_JAVA_BIN_PATH' does not exist."
	fi
}

run_script_install_oracle_jdk