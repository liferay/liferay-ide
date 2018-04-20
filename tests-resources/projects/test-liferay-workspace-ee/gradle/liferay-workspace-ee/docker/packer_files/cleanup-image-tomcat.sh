#!/bin/sh

set -e

##
## This is the script to cleanup the Docker image built by Packer, with Liferay Tomcat
## bundle and Oracle JDK inside.
##

@UTILS_FILE_CONTENT@

##
## Constants
##

# The Liferay bundle home, where bundle was extracted. It will be cleaned up.
LIFERAY_HOME='@LIFERAY_HOME@'

# The name of the directory where Tomcat is located, inside Liferay bundle home.
LIFERAY_HOME_TOMCAT_DIR_NAME='@LIFERAY_HOME_TOMCAT_DIR_NAME@'

# JDK home which was installed for the bundle. It will be cleaned up.
JAVA_HOME='@JAVA_HOME@'


# Make sure our custom 'die' command can be used safely
if ! command -v die >/dev/null 2>&1; then
    echo "==> ERROR: util command 'die' not found, was utils.sh included by Gradle?" && exit 127
fi

for const in \
		"$LIFERAY_HOME" \
		"$LIFERAY_HOME_TOMCAT_DIR_NAME" \
		"$JAVA_HOME"; do
	case "$const" in
		@*@) die "One of the constants was not replaced by Gradle (starts and ends with '@').";;
		'') die "One of the constants has an empty value";;
	esac
done

##
## Computed Constants
##

TOMCAT_HOME="$LIFERAY_HOME/$LIFERAY_HOME_TOMCAT_DIR_NAME"


cleanup() {
	cleanup_os
	cleanup_jdk
	cleanup_liferay
	cleanup_tomcat
}

cleanup_os() {
	echo "Cleaning up OS: '/tmp', '/var', apt-get cache"

	# Based on http://chrisstump.online/2016/02/23/docker-image-reduction-techniques/
	apt-get clean autoclean
    apt-get autoremove -y

	rm -rf \
		/tmp/* \
		/var/lib/apt \
		/var/lib/dpkg \
		/var/lib/cache \
		/var/lib/log \
		/var/cache/apt/*
}

cleanup_liferay() {
	echo "Cleaning up Liferay '$LIFERAY_HOME'"

	if [ ! -d "$LIFERAY_HOME" ]; then
	 	die "Path '$LIFERAY_HOME' is not a directory, it has to point to Liferay home (like '/opt/liferay/liferay-portal-tomcat')"
	fi

	rm -rf \
		${LIFERAY_HOME}/osgi/apps/*.test.jar
}

cleanup_tomcat() {
	echo "Cleaning up Tomcat '$TOMCAT_HOME'"

	if [ ! -d "$TOMCAT_HOME" ]; then
	 	die "Path '$TOMCAT_HOME' is not a directory, it has to point to Tomcat home (like '/opt/liferay/liferay-portal-tomcat/tomcat-8.0.32')"
	fi

	rm -rf \
		${TOMCAT_HOME}/temp/* \
        ${TOMCAT_HOME}/work/*
}

cleanup_jdk() {
	echo "Cleaning up Oracle JDK '$JAVA_HOME'"

	if [ ! -d "$JAVA_HOME" ]; then
	 	die "Path '$JAVA_HOME' is not a directory (or symlink of a directory), it has to point to Oracle's JDK (like '/opt/liferay/jdk1.8.0_91' or '/opt/liferay/oracle-jdk-8')"
	fi

	# Clean up Java
	rm -rf \
			$JAVA_HOME/*src.zip \
	        $JAVA_HOME/lib/missioncontrol/ \
	        $JAVA_HOME/lib/visualvm/ \
	        $JAVA_HOME/lib/*javafx*  \
	        $JAVA_HOME/jre/lib/plugin.jar  \
	        $JAVA_HOME/jre/lib/ext/jfxrt.jar  \
	        $JAVA_HOME/jre/bin/javaws/  \
	        $JAVA_HOME/jre/lib/javaws.jar  \
	        $JAVA_HOME/jre/lib/desktop/  \
	        $JAVA_HOME/jre/plugin/  \
	        $JAVA_HOME/jre/lib/deploy*  \
	        $JAVA_HOME/jre/lib/*javafx*  \
	        $JAVA_HOME/jre/lib/*jfx*  \
	        $JAVA_HOME/jre/lib/amd64/libdecora_sse.so \
	        $JAVA_HOME/jre/lib/amd64/libprism_*.so  \
	        $JAVA_HOME/jre/lib/amd64/libfxplugins.so  \
	        $JAVA_HOME/jre/lib/amd64/libglass.so  \
	        $JAVA_HOME/jre/lib/amd64/libgstreamer-lite.so  \
	        $JAVA_HOME/jre/lib/amd64/libjavafx*.so  \
	        $JAVA_HOME/jre/lib/amd64/libjfx*.so \
	        $JAVA_HOME/man \
	        $JAVA_HOME/db
}

cleanup