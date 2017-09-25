1. please make sure you have blade cli jar before you run any tests about blade (liferay workspace, gradle module prooject), here is the steps how to get this file:
download the blade cli jar file from **${blade-latest-download-url}** in **/build/parent/pom.xml** and put it into **/tools/plugins/com.liferay.ide.project.core/lib** and rename it to **com.liferay.blade.cli.jar**.
2. And if this file updated you also need to redownload it.
3. please make sure you have run *mvc source-formatter:format* under tests folder.