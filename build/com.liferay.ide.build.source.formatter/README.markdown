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
5. /tools/plugins/com.liferay.ide.hook.core
6. /tools/plugins/com.liferay.ide.hook.ui

Some something you had better to do firstly by yourself before you run source-formatter:
1. remove all of *//$NON-NLS-1$* *//$NON-NLS-2$* ...
2. remove all of *final* if it is no need
3. import Liferay_Portal_Code_Style_Formatter_Prefs into Eclipse and format the source code

For sapphire model, we must keep the "TYPE" filed in first and we cannot rename it to another.
So add it into source-formatter.perperties to exclude if the class have it, see /tools/plugins/com.liferay.ide.hook.core/source-formatter.properties