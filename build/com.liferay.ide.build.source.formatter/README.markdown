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

## Something you had better to do firstly by yourself before you run source-formatter:
1. remove all of *//$NON-NLS-1$* *//$NON-NLS-2$* ...
2. remove all of *final* if it is no need
3. import Liferay_Portal_Code_Style_Formatter_Prefs into Eclipse and format the source code
4. add the *target* folder in source-formatter.properties to exclude it, see /ui-tests/source-formatter.properties

For sapphire model, we must keep the "TYPE" filed in first and we cannot rename it to another.
So add it into source-formatter.perperties to exclude if the class have it, see     /tools/plugins/com.liferay.ide.hook.core/source-formatter.properties

#### Code comments Style:

* Do not use block comments in single line
    #### Incorrect:
    ```java
    /** here is my comment */
    ```
    or this
    ```java
    /* here is my comment */
    ```
    
    #### Correct:
    ```
    /**
	 * here is my comment
	 */
    ```
    and this 
    ```
    /*
	 * here is my comment
	 */
    ```
    If you have only one line comment,it's better to use // comment delimiter 
    ```
      //here is my comment
    ```
    You can reference this [***link***](https://github.com/liferay/liferay-portal/blob/master/portal-kernel/src/com/liferay/portal/kernel/cal/Recurrence.java) 