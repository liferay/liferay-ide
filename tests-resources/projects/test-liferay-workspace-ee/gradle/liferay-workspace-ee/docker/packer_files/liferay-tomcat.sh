#!/bin/sh

set -e

##
## This is the script to start Liferay Tomcat bundle inside built Docker image.
##

@UTILS_FILE_CONTENT@

##
## Constants
##

# JDK home which was installed for the bundle.
JAVA_HOME='@JAVA_HOME@'

# The Liferay bundle home, where bundle was extracted.
LIFERAY_HOME='@LIFERAY_HOME@'

# The name of the directory where Tomcat is located, inside Liferay bundle home.
LIFERAY_HOME_TOMCAT_DIR_NAME='@LIFERAY_HOME_TOMCAT_DIR_NAME@'

for const in \
		"$JAVA_HOME" \
		"$LIFERAY_HOME" \
		"$LIFERAY_HOME_TOMCAT_DIR_NAME"; do
	case "$const" in
		@*@) die "One of the constants was not replaced by Gradle (starts and ends with '@').";;
		'') die "One of the constants has an empty value";;
	esac
done

##
## Computed Constants
##

TOMCAT_HOME="$LIFERAY_HOME/$LIFERAY_HOME_TOMCAT_DIR_NAME"


# We have to export the Java home, otherwise catalina.sh would not be able to read it
export JAVA_HOME=$JAVA_HOME

# Manually start the Tomcat in current shell
$TOMCAT_HOME/bin/catalina.sh run