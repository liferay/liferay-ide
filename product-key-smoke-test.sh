#define variables(product_key and tomcat_version and bundle_url)
prodcut_key=$1
if [ 0"$prodcut_key" = "0" ]
then
	read -p "Please enter the product key to test:" prodcut_key
fi

tomcat_version=$2
if [ 0"$tomcat_version" = "0" ]
then
	read -p "Please enter the expected tomcat version:" tomcat_version
fi

bundle_url=$3
if [ 0"$bundle_url" = "0" ]
then
	read -p "Please enter the expected bundle url:" bundle_url
fi

# check bundle url contains the string in product key name
find_str=$prodcut_key
find_key_str=${find_str##*-}
[[ $bundle_url =~ "$find_key_str" ]]
if [ $? -eq 0 ]
then
	echo "Test Passed: Bundle url containers string in product key"
else
	echo "Error to find key string in product key name"
	exit 1
fi

# create a liferay workspace and set workspace plugin to 2.4.0
echo "create a liferay workspace project..."
rm -rf ws-smoke-test
mkdir ws-smoke-test && cd ws-smoke-test
blade init -v $prodcut_key

#sed -i 's/2.3.1/2.4.0/g' settings.gradle
# clean gradle.properties and set product key
#:>gradle.properties
#echo "liferay.workspace.product = dxp-7.2-ga1" > gradle.properties

# assert prodcut key in gradle.properties
echo "check product key exists..."
if [ `grep -c "$product_key" gradle.properties` -ne '0' ]
then
	echo "Test Passed: Product key has been set in gradle.properties."
fi

# create a mvc-portlet project and check no version in build.gradle
echo "create a project..."
blade create -t mvc-portlet sample

echo "assert no dependency version since target platform has been set by default"
result=$(grep -0 'version:' < modules/sample/build.gradle | wc -l)
if [ "$result" -eq "1" ]
then
	echo "Test Passed: Target platform works. There are no versions except for cssBuilder."
else
	echo "Error about target platfrom. Please check dependency version in build.gradle file."
	exit 1
fi

# build workspace project
echo "build project..."
./gradlew build
if [ $? -eq 0 ]
then
	echo "Test Passed: Workspace project build successfully"
else
	echo "Error building workspace project. Workspace project build failed"
	exit 1
fi

# init bundle and check tomcat version
echo "init bundle..."
./gradlew initBundle

if [ $? -eq 0 ]
then
	echo "Test Passed: Task initBundle build successfully"
else
	echo "Error. Command initBundle failed"
	exit 1
fi

#if [ ! -f "${HOME}/.liferay/bundles/${bundle_file}"]
#then
# echo "Error: Bundle file doesn't exist."
# exit 1
#else
# echo "Test Passed: Bundle file exists."
#fi

if [ ! -d "bundles/tomcat-$tomcat_version" ]
then
	echo "Error. Tomcat versions are not match."
	exit 1
else
	echo "Test Passed: Tomcat version is correct."
fi

# start and stop server
echo "starting server..."
#blade server start
./bundles/tomcat-$tomcat_version/bin/catalina.sh run

if [ $? -eq 0 ]
then
	echo "Test Passed: Server start successfully"
else
	echo "Error starting server."
	exit 1
fi

FIND_START_STR="Server startup in"
FIND_START_IN_FILE="bundles/tomcat-$tomcat_version/logs/catalina.out"
if [ `grep -c "$FIND_START_STR" $FIND_START_IN_FILE` -ne '0' ]
then
	echo "Error to start server"
	exit 1
else
	echo "Test Passed: Server start successfully"
fi

echo "stopping server..."
#blade server stop
./bundles/tomcat-$tomcat_version/bin/catalina.sh stop
if [ $? -eq 0 ]
then
	echo "Test Passed: Server stop successfully"
else
	echo "Error stopping server."
	exit 1
fi

# start docker container
echo "start a docker container..."
./gradlew startDockerContainer

if [ $? -eq 0 ]
then
	echo "Test Passed: Docker container start successfully"
else
	echo "Failed to start docker conatiner"
	exit 1
fi

# stop docker container
echo "stop a docker container..."
./gradlew stopDockerContainer
if [ $? -eq 0 ]
then
	"Test Passed: Docker container stop successfully"
else
	"Failed to stop docker container"
	exit 1
fi

# delete the workspace project
rm -rf ../ws-smoke-test