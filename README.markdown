# Liferay IDE

[Liferay IDE](http://www.liferay.com/community/liferay-projects/liferay-ide) is a
collection of Eclipse plugins created by Liferay, Inc. to support developing
plugins, e.g. portlets, hooks, themes, etc, for the Liferay Portal platform.

To get started, check out the project's community homepage at
[http://www.liferay.com/community/liferay-projects/liferay-ide](http://www.liferay.com/community/liferay-projects/liferay-ide)

To install the Liferay IDE plugins into your Eclipse install using either the stable or milestone updatesite:

- Latest stable release (2.2.x): [http://releases.liferay.com/tools/ide/stable/](http://releases.liferay.com/tools/ide/latest/stable/)
- Latest milestone release towards 3.0: [http://releases.liferay.com/tools/ide/latest/milestone/](http://releases.liferay.com/tools/ide/latest/milestone/)
- Additional downloads available here: [https://www.liferay.com/downloads/liferay-projects/liferay-ide](https://www.liferay.com/downloads/liferay-projects/liferay-ide)

## Quick Start

To get up and running quickly, *download* a [pre-built Liferay IDE
release](http://www.liferay.com/downloads/liferay-projects/liferay-ide) and install it into
your Eclipse install.  Follow the [Installation Guide](https://dev.liferay.com/develop/learning-paths/-/knowledge_base/6-2/developing-apps-with-liferay-ide)
for instructions. Then use the Getting Started Tutorial for how to create and deploy a Liferay Project using Liferay IDE.

## Building from source

If you would like to build from source, use this following command:

```
$ mvn clean verify -DskipTests
```

Once it finishes the locally built Eclipse updatesite will be located here:

```
build/com.liferay.ide-repository/target/liferay-ide-<version>-<timestamp>-updatesite.zip
```

You can install this using _Help > Install New Software... > Add > Archive > Point to newly built zip file_

## Debug a fix? Send a pull request?

If you want to help submit a bug fix or just step through the code to see what we are doing wrong :) you are going to need to import the source and launch a test eclipse with your fix and then step through the code.  Here is the process you can follow

1. Download [Eclipse for Committers package](https://www.eclipse.org/downloads/packages/eclipse-ide-eclipse-committers-451/mars1)
2. Run this eclipse with 2048M memory setting (set -Xmx2048m in eclipse.ini)
3. Install the latest Liferay IDE CI build
  1. Use [this updatesite url](http://files.liferay.org.es/staged/public-files/liferay-ide/unstable/build/com.liferay.ide-repository/target/repository/).
  2. Install all features
4. Clone this repo
5. Import all projects into Eclipse
  1. File > Import > Existing Maven projects...
  2. Select all
  3. Finish
  4. Close all projects that have .tests suffix (unless you want to submit some unit tests :)
6. If everything compiles, make your changes, set your breakpoints and then launch a new Eclipse test workbench
  1. Go to Run > Debug Configurations...
  2. Right-click "Eclipse Application" and choose "New"
  3. Change name to "liferay ide test"
  4. Switch to "Argements" tab and add "-XX:MaxPermSize=256m" to "VM arguments"
  5. Click "Apply" and then click "Debug"
7. When new Eclipse opens it will be the base Eclipse + your new IDE plugins running from source, so test your change and send a pull request :)!
8. Once your bugfix (or feature :) is ready, build it (read the section above on how to build it) and then try it out in a brand new Eclipse install
9. Send a pull request!  https://github.com/gamerson/liferay-ide/pulls

## Bug Tracker

Have a bug? Please file an issue at Liferay's JIRA and use the [IDE project](http://issues.liferay.com/browse/IDE).

## Blog

Read detailed announcements, discussions, and more on [Liferay IDE's Blog
Stream](http://www.liferay.com/web/gregory.amerson/blog).

## Forum

Have questions? Ask them on our own category for Liferay IDE on the
[forums](http://www.liferay.com/community/forums/-/message_boards/category/4627757)

## License

This library, *Liferay IDE*, is free software ("Licensed
Software"); you can redistribute it and/or modify it under the terms of the [GNU
Lesser General Public License](http://www.gnu.org/licenses/lgpl-2.1.html) as
published by the Free Software Foundation; either version 2.1 of the License, or
(at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; including but not limited to, the implied warranty of MERCHANTABILITY,
NONINFRINGEMENT, or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General
Public License for more details.

You should have received a copy of the [GNU Lesser General Public
License](http://www.gnu.org/licenses/lgpl-2.1.html) along with this library; if
not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth
Floor, Boston, MA 02110-1301 USA