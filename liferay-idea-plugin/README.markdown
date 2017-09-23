# Liferay Idea Plugin

## Quick Start
You can download the plugin zip file from:

```
https://dl.bintray.com/gamerson/liferay-idea-plugin-releases/liferay-idea-plugin-0.1.0.zip
```

## Building from source
If you would like to build from source, use this following command:

```
$ ./gradlew clean build
```

Once it finishes the locally built the IntelliJ Idea Plugin will be located here:

```
build/distributions/liferay-idea-plugin-<version>.zip
```

You can install this using _Preferences > Plugins > Install plugin from disk... > Point to newly built zip file_

## Key features

### Liferay Workspace
1. Create a Liferay Workspace Project from Start menu > click *Create New Project* or click *File* > *New* > *Project*
2. Choose *Liferay Workspace* from left list
3. Click *Next* button
4. Type your Liferay Workspace *Project Name* and choose a *Project Location* or leave it default.
5. Click *Finish* button and there will be one popup show up.
6. Click *OK* to finish

### Liferay Server
You must have one Liferay Workspace Project to do the following steps:
1. Right click on the root of your Liferay Workspace Project
2. Click Liferay > InitBundle
3. Then the gradle will run *initBundle* task
4. It will take several minutes to download the latest Liferay Server from remote if you don’t have the local cache
5. Click *Edit configruration...* on the right top corner
6. Click the plus icon
7. Choose *Liferay Server*
8. You can leave the config values by default or change to what you want
9. Click *OK* Bundle
10. After you add a new *Liferay Server*, you can *start* or *debug* it

### Liferay Module
You must have one Liferay Workspace Project to do the following steps:
1. Right click on existing Liferay Workspace Project and choose *New* > *Liferay Module*
2. Choose *Liferay Modules*
3. Depends on what template you choose, you can type *Package Name*, *Class Name* and *Service Name*
4. Click *Next* Button
5. Type *Project Name* and the *location* is not able to customize.
6. Click *Finish* Button

### Liferay Fragment Module
You must have one Liferay Workspace Project and alreay get the *Liferay Server* to do the following steps:
1. Right click on existing Liferay Workspace Project and choose *New* > *Liferay Module*
2. Choose *Liferay Fragment Modules*
3. Choose the osgi host and select the jsp files what you want to hook.
4. Click *Next* Button
5. Type *Project Name* and the *location* is not able to customize.
6. Click *Finish* Button

### Deployment
After you get the Liferay Modules you can deploy them to running or debugging Liferay Server:
1. Right Click on your *Liferay Module*
2. Choose Liferay > Deploy
3. When you see the log showing in the console view, you get your module successful deploying

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