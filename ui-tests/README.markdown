# Liferay IDE Ui Tests

## How to run ui tests in local command line
1. make sure you already have the local ui testing repository under *ui-tests/com.liferay.ide.ui.testing-repository/target/repository* before you run the ui tests, if there is not or you run first time, use the following command in *ui-tests* folder to generate it:

```
mvn clean verify -P ui-repo
```

2. use the following commands to run the ui tests in *ui-tests* folder or special test folder(like tools/com.liferay.ide.ui.fragment.tests/):

```
mvn clean verify -P ui-tests-win
```
for Windows

```
mvn clean verify -P ui-tests-linux
```
for Linux

```
mvn clean verify -P ui-tests-mac
```
for MacOS

you also need to append  *-D liferay.bundles.dir="${your.tests.resource}"* to point tests resource(for locally you can point to *ide source/tests-resources/*), for example:
```
mvn clean verify -P ui-tests-mac -D liferay.bundles.dir="/Users/terry/work/github/liferay/liferay-ide/tests-resources"
```

you also can to append *-D liferay-ide-site="${your.ide.site}"* to point which ide you want to tests, if you don't set it will use the remote site.

Or you can go into the /ui-tests/scripts/ and run the shell and bat scripts.

## Notes
1. please make sure you have blade cli jar before you run any tests about blade (liferay workspace, gradle module prooject), here is the steps how to get this file:
download the blade cli jar file from **${blade-latest-download-url}** in **/build/parent/pom.xml** and put it into **/tools/plugins/com.liferay.ide.project.core/lib** and rename it to **com.liferay.blade.cli.jar**.
And if this file updated you also need to redownload it.
2. please make sure you have run *mvc source-formatter:format* in /build/com.liferay.ide.build.source.formatter folder for *ui-tests*.
3. if you have to add *sleep* for some reasons, remember you should also add **ide.sleep()** or **ide.sleep(millis)** into action class(e.g. WizardAction or DialogAction), or not add them into any testcase files or page object.

## Short Name
If you want to use one short name instead of one long name, please follow this map:
Liferay Workspace/LiferayWorkspace => lrws
Service Builder/ServiceBuilder => sb

**DO NOT** use short name for any Class Names and Methon names.
