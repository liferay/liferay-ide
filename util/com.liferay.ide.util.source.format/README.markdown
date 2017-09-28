You can use:

```
mvn source-formatter:format -DbaseDir="your.project.location"
```

in current folder to format your project, for example:

```
mvn source-formatter:format -DbaseDir="../../tools/plugins/com.liferay.ide.gradle.core"
```

The following folders are already applied to portal code style:
1. /liferay-idea-plugin
2. /ui-tests
3. /tools/plugins/com.liferay.ide.gradle.core
4. /tools/plugins/com.liferay.ide.gradle.ui