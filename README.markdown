# Liferay IDE

[Liferay IDE](http://www.liferay.com/community/liferay-projects/liferay-ide) is a
collection of Eclipse plugins created by Liferay, Inc. to support developing
applications, e.g. portlets, hooks, themes, etc, for the Liferay Portal platform.

To get started, check out the project's community homepage at
[http://www.liferay.com/community/liferay-projects/liferay-ide](http://www.liferay.com/community/liferay-projects/liferay-ide)

To install the Liferay IDE plugins into your Eclipse install using either the stable or milestone updatesite:

- Latest stable release (2.0.x): [http://releases.liferay.com/tools/ide/stable/](http://releases.liferay.com/tools/ide/latest/stable/)
- Latest milestone release towards 2.1: [http://releases.liferay.com/tools/ide/latest/milestone/](http://releases.liferay.com/tools/ide/latest/milestone/)
- Additional downloads available here: [https://www.liferay.com/downloads/liferay-projects/liferay-ide](https://www.liferay.com/downloads/liferay-projects/liferay-ide)
 

## Source Code

All of Liferay IDE's source code resides in this current repository. Liferay IDE
*releases* are built from this repository and the output of the build is an
Eclipse style update-site that can be used to install the Liferay IDE plugins
into a user's Eclipse installation.  Freshly built updatesites will reside in
the target folder of the `build/releng/com.liferay.ide-repository/` module.

## Quick Start

To get up and running quickly, *download* a [pre-built Liferay IDE
release](http://www.liferay.com/downloads/liferay-projects/liferay-ide) and install it into
your Eclipse install.  Follow the [Installation Guide](http://www.liferay.com/documentation/liferay-portal/6.1/development/-/ai/installati-6)
for instructions. Then use the Getting Started Tutorial for how to create and deploy a Liferay Project using Liferay IDE.

## Versioning

Liferay IDE versions do not coorespond with [Liferay Portal](http://www.liferay.com/community/liferay-projects/liferay-portal) but instead follow their own versions.

- 1.x - supports Liferay Portal 6.0 and 6.1 and only PluginsSDK based projects
- 2.x - (at this time still in development) supports Liferay Portal 6.2 and also Maven based Liferay projects

## Bug Tracker

Have a bug? Please file an issue at Liferay's JIRA and use the [IDE project](http://issues.liferay.com/browse/IDE).

## Blog

Read detailed announcements, discussions, and more on [Liferay IDE's Blog
Stream](http://www.liferay.com/web/gregory.amerson/blog).

## Forum

Have questions? Ask them on our own category for Liferay IDE on the
[forums](http://www.liferay.com/community/forums/-/message_boards/category/4627757)

## Team

Liferay IDE is written by these people and some community contributors.

[![Gregory Amerson](http://gravatar.com/avatar/7fc2766b6aec8fe1803aefc4df1b8cf6?s=70)](https://github.com/gamerson) | [![Kuo Zhang](http://gravatar.com/avatar/34137a3bba5d84f1faeda7981c43f9f4?s=70)](https://github.com/kuozhang) | [![Simon Jiang](http://gravatar.com/avatar/aa87d5d5400ac8298c0851bd0589320a?s=70)](https://github.com/aquilabj) | [![Terry Jia](http://gravatar.com/avatar/67db5a472f85dc5010a861891af7efd4?s=70)](https://github.com/jtydhr88)
--- | --- | --- | --- 
[Gregory Amerson](https://github.com/gamerson) | [Kuo Zhang](https://github.com/kuozhang) | [Simon Jiang](https://github.com/aquilabj) | [Terry Jia](https://github.com/jtydhr88)



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
