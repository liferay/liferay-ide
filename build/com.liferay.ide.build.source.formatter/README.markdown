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
7. /tools/plugins/com.liferay.ide.ui.notifications
8. /enterprise/plugins/com.liferay.ide.studio.ui
9. /tools/plugins/com.liferay.ide.theme.core
10. /tools/plugins/com.liferay.ide.ui.snippets
11. /tools/plugins/com.liferay.ide.xml.search.ui
12. /tools/plugins/com.liferay.ide.ui
13. /tools/plugins/com.liferay.ide.upgrade.core
14. /maven/plugins/com.liferay.ide.maven.core
15. /maven/plugins/com.liferay.ide.maven.ui
16. /tools/plugins/com.liferay.ide.portlet.vaadin.core
17. /tools/plugins/com.liferay.ide.portlet.vaadin.ui
18. /tools/plugins/com.liferay.ide.portlet.core

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

#### Please note which situation can be used with *final*:
1. Static inner class, like this( please refer to link [Potal Code](https://github.com/liferay/liferay-portal/search?utf8=%E2%9C%93&q=amnotfound&type=) ):
```java
public class AMException extends PortalException{
  ...
  public static final class AMNotFound extends AMException{
    ...
  }
}
```
2. Static porperty and conform to the constant naming conventions, like this( please refer to link [Potal Code](https://github.com/liferay/liferay-portal/search?utf8=%E2%9C%93&q=public+static+final+&type=) ):
```java
public static final String ACTION = "action";
```
3. Private static property, like this( please refer to link [Poratl Code](https://github.com/liferay/liferay-portal/search?utf8=%E2%9C%93&q=private+static+final&type=)  ):
```java
private static final String _CODE_NAME = "Judson";
```
