# Liferay Workspace EE

Liferay Workspace EE is an extension of [Liferay Workspace](https://github.com/david-truong/liferay-workspace), providing extra features around the same project structure that Liferay Workspace defines. You can get the latest version of Liferay Workspace EE from GitHub / Nexus, please see instructions in GitHub:

* [https://github.com/liferay/lfrgs-liferay-ironman/tree/master/liferay-build-tool/liferay-workspace-ee](https://github.com/liferay/lfrgs-liferay-ironman/tree/master/liferay-build-tool/liferay-workspace-ee) 

## Build configuration

### Project-specific properties (gradle.properties)

[gradle.properties](gradle.properties) is a standard Gradle file used to store configuration of the build. These properties are typically project-wide and there should be no need for each user to customize these. The master set of these properties should be maintained in [gradle.properties](gradle.properties) and committed into SCM.

All the properties used by Liferay Workspace EE share common prefix `liferay.workspace.ee.`, similar to properties from Liferay Workspace (CE), which all start with `liferay.workspace.`.

For complete list of supported EE properties, their description and default values, please see [gradle-ee-default.properties](gradle-ee-default.properties).

### User-specific properties (init script or command line options)

The build sometimes needs a few configuration properties, which likely will be different for each project's user (or project's CI server) building the sources. 

These are typically credentials to remote systems (Jenkins, AWS) or paths to external executables used by the build (like Packer). Even though these could be set in [gradle.properties](gradle.properties) as well, you should not do so. You will prevent committing your private information / settings into SCM, by for example accidentally including [gradle.properties](gradle.properties) in the commit.

You should use init script or command line options to provide values of these properties. Using init script, use `gradlew ... --init-script <path_to_your_init_script>` and in your script file put line like this one (see [sample-ee-init-script.gradle](sample-ee-init-script.gradle) for full example):
```
System.setProperty('org.gradle.project.propertyKey', 'propertyValue')
``` 

In command line, use `gradlew ... -PpropertyKey=propertyValue -PanotherPropertyKey=anotherPropertyValue` to provide custom value of one or more the properties. Please note the property values will be visible to other users on your system (in listing of running OS processes) and the entry will be also in your shell's history. For this reasons, it's recommended to use init script approach described in previous paragraph.

Following is the list of user-specific properties used by the Liferay Workspace EE build and their description:

| Property          | Default value | Sample custom value   | Description   |
|---	            |---	        |---	            |---	                |
| awsAccessKey      | *not set*  	| AKIAJBDX3XAUXFBXWX5Q | The AWS access key to access project's AWS account. The user owning the key has to have sufficient permissions, which are the same as [Packer requires](https://www.packer.io/docs/builders/amazon.html). Used when creating AMIs -  `gradlew distBundleAmi` |
| awsSecretKey      | *not set*     | oRET8s0Oq0aU1fLt9A41zdRnBBta  | The AWS secret key belonging to access key above. Used when creating AMIs -  `gradlew distBundleAmi` |
| dockerExecutable  | docker        | /opt/docker_1.0/docker | The resolvable path the Docker executable. Either simply `docker` (default), or absolute path to where Docker binary is installed on your local machine. Used for building Docker images with Liferay. |
| packerExecutable  | packer        | /opt/packer_0.9.0_linux_amd64/packer  | The resolvable path the Packer executable. Either simply `packer` (default), or absolute path to where Packer binary is installed on your local machine. Used for building AMIs in AWS, so the same usage as for `awsAccessKey`.   |
| jenkinsUserName   | *not set*     | jane.doe  | The login for project's Jenkins server (see `liferay.workspace.ee.jenkins.server.url`). Used in tasks interacting with your Jenkins server, like `gradlew updateJenkinsItems`. | 
| jenkinsPassword   | *not set*     | janesSecretToken  | The password / token for project's Jenkins server which was set up for `jenkinsUserName` in your Jenkins server. Used in tasks interacting with your Jenkins server, like `gradlew updateJenkinsItems`. |
| releaseNumber     | 1             | 17  | Incremental integer denoting the release number of this build. Useful for example in Jenkins, where you can ask job to do `gradlew -PreleaseNumber=$BUILD_NUMBER ...`. Used when building DEB or RPM packages (and AMIs since they are built from DEB ord RPM archives). |

### Other recommended settings (not strictly EE related, but strongly recommended)

#### rootProject.name (settings.gradle)

It is highly recommended to cement the name of your root project and not rely on Gradle to figure this out based on the name of your root directory. Liferay Workspace EE will use `rootProject.name` as package name for DEB / RPM archives. Root project's name can be set in [settings.gradle](settings.gradle):
```
rootProject.name = 'liferay-project-abc'
```

This prevents issues when users / CI will fetch your source code to a special directory not named after your project's assumed name. Choose reasonably short and self-explanatory name of your project, many artifacts produced by the build will be named after it.

#### project.version (build.gradle) 

It's also a good idea to think about versions of your project and set it explicitly in your root project's [build.gradle](build.gradle):
```
project.version = '1.0.0'
```

The default `project.version` in Gradle is String `undefined` (see [documentation](https://docs.gradle.org/current/dsl/org.gradle.api.Project.html#org.gradle.api.Project:version)). However, Liferay Workspace EE will use `0.0.0` if the version was not explicitly set, since `project.version` is used as package version for the DEB / RPM packages.

Increment your project's version as you add and release new features.


## Building Liferay bundle archives (ZIP and TAR)

This is a functionality of base Liferay Workspace. You can produce .zip with:
```
gradlew distBundleZip
```

In a similar way, you can produce .tar.gz with:
```
gradlew distBundleTar
```

Files will be created in [build]([build]) subdirectory of the root project and typically will be named like `${rootProject.name}.zip` / `${rootProject.name}.tar.gz`.


## Building Liferay bundle for various project's environments

This is a functionality of base Liferay Workspace. The built bundle will include configs from subdirectory of [configs](configs) based on value of project property `liferay.workspace.environment`:
```
gradlew distBundleZip -Pliferay.workspace.environment=dev
```

Please note that configs from [configs/common](configs/common) will always be applied (for any built environment) and that they are applied first, before any environment-specific files are copied into the bundle.

Default environment for the project can be specified in [gradle.properties](gradle.properties), if not provided, it defaults to `local`.


## Building Linux packages (DEB and RPM)

Note: Only Tomcat bundles are supported for now. You need to make sure that `liferay.workspace.url` points to a Tomcat bundle with expected direcotry structure. If you select a unsupported bundle type (non-Tomcat one for now) using `liferay.workspace.ee.bundle.type` in [gradle.properties](gradle.properties) you won't be able to build DEB / RPM packages.

Your DEB packages, suited for Debian-based Linux systems (Debian, Ubuntu), can be built using:
```
gradlew distBundleDeb
```

Your RPM packages, suited for RedHat-based Linux systems (RHEL, CentOS, Fedora), can be built using:
```
gradlew distBundleRpm
```

Files will be created in [build]([build]) subdirectory of the root project. Both of these tasks use the product of `distBundleTar` - the tarball archive containing Liferay bundle. This archive is contained inside produced DEB / RPM package, together with installation / uninstallation scripts.

The tasks performed by DEB / RPM packages during installation of the package into OS are:

1. user and group for Liferay is created, if not existing (liferay:liferay)
2. presence of Oracle JDK 8 is checked and it is installed if missing (see next section)
3. */etc/init.d/liferay-tomcat* script is installed 
4. */etc/init.d/liferay-tomcat* script is added to be run on OS startup / shutdown runtime
  * to start / stop Liferay Tomcat as a service
5. Liferay bundle is installed into */opt/liferay/liferay-portal-tomcat*
  * the directory *data/* and file *portal-setup-wizard.properties* inside Liferay home are backed up and restored (copied back), if present
  * this means you can install new version / release of the DEB / RPM package over the old package and your data written by Liferay should be preserved

Note that Liferay Tomcat is not started after the package is installed, since it's not always desirable (like in the case of baking AMI, see below). You need to start Liferay Tomcat manually (using the installed file */etc/init.d/liferay-tomcat*) or it will be started after next reboot of the OS.

The tasks performed by DEB / RPM packages during removal of the package into OS:

1. */etc/init.d/liferay-tomcat* script is removed from OS startup / shutdown runtime
2. */etc/init.d/liferay-tomcat* script is removed
3. the contents of */opt/liferay/liferay-portal-tomcat* are cleaned up
  * only the directory *data/* and file *portal-setup-wizard.properties* are left untouched, if present

Note the Oracle JDK 8 remains installed in the OS. Since we did not set any global environment variable pointing to this JDK, there is not need to remove the files.

### JDK setup and installation

Both `distBundleDeb` and `distBundleRpm` will produce packages which will attempt to install Oracle JDK 8, into */opt/liferay/oracle-jdk-8*, if there is no *bin/java* file under this path. Oracle JDK archive is downloaded during build and bundled in the resulting DEB / RPM. 

This path (*/opt/liferay/oracle-jdk-8*) is then passed to Liferay Tomcat bundle inside */etc/init.d/liferay-tomcat* as environment variable `JAVA_HOME=/opt/liferay/oracle-jdk-8`. File */etc/init.d/liferay-tomcat* is also installed into the OS from DEB / RPM, see above.

## Building Docker images

Note: Only Tomcat bundles are supported for now. You need to make sure that `liferay.workspace.url` points to a Tomcat bundle with expected direcotry structure. If you select a unsupported bundle type (non-Tomcat one for now) using `liferay.workspace.ee.bundle.type` in [gradle.properties](gradle.properties) you won't be able to build Docker images.

Your local Docker image can be built and tagged using:
```
gradlew distBundleDockerImageLocal
```

If you only want to build Dockerfile and not commit & tag the final image, you can use:
```
gradlew distBundleDockerfile
```

The build process of the Docker image has following steps:

1. OS package (.deb) with project's Liferay bundle is built by the 'ospackage' module
2. Packer is used to install this file and run some other setup & cleanup tasks
3. Packer tags the *raw* Docker image locally
	* this will produce Docker tag like: 
	
		```
		$ docker images
		REPOSITORY                     TAG                 IMAGE ID            CREATED             SIZE
        acme/liferay-workspace.local   0.0.0-1_raw         9df2cec53ea8        12 seconds ago      735.4 MB
        ```
    * the *-raw* suffix indicates that there is no metadata in this tag, only binary contents, since Packer does not use the Dockerfile syntax to produce the images
    * this tag can be either used with the help of our Dockerfile, built by the next step
4. Dockerfile is constructed and written
	* starting FROM the *-raw* tag built by Packer and adding all the metadata to start Liferay bundle
    * *maintainer*, *user*, *startup command* and *exposed ports* instructions as added
5. the Dockerfile is built & tagged into local Docker repository
	* **only** if you chose to run the *distBundleDockerImageLocal* task (not just the *distBundleDockerfile* task) 
    * this will produce the final Docker tag (e.g. *0.0.0-1*):
    
        ```
        $ docker images
        REPOSITORY                     TAG                 IMAGE ID            CREATED             SIZE
        acme/liferay-workspace.local   0.0.0-1             e101abc8b85f        11 seconds ago      735.4 MB
        acme/liferay-workspace.local   0.0.0-1_raw         9df2cec53ea8        12 seconds ago      735.4 MB
		```

The first part of the repository name (*acme*) can be customized with `liferay.workspace.ee.docker.repository.company` in gradle.properties. The tag value is built based on project's version and release number: 
* *0.0.0* is the version of the project, as computed by Liferay Workspace EE, defaulting to *0.0.0* in no Gradle project version was found. You can change this value in the root Gradle project (build.gradle) by setting e.g. `version = 1.0.1`. 
* *-1* is the release number specified for the current build. This is 1 by default and can be altered by passing e.g. `-PreleaseNumber=17` on command line when running the build.

The Packer step is used on purpose, even though we could just specify all the steps using (a slightly longer) Dockerfile. The reason is the size of the final image - it will be much smalled when built by Packer + minimalistic Dockerfile. Packer does all the installation in one Docker step, not increasing the size of the image with every installtion step. The difference is significant, image has about 740MB when built with Packer + Dockerfile vs. 1530 MB when built with Dockerfile only.

## Building AWS AMIs

Note: Only Tomcat bundles are supported for now. You need to make sure that `liferay.workspace.url` points to a Tomcat bundle with expected direcotry structure. If you select a unsupported bundle type (non-Tomcat one for now) using `liferay.workspace.ee.bundle.type` in [gradle.properties](gradle.properties) you won't be able to build DEB / RPM packages.

Your AMI can be built with `gradlew distBundleAmi --init-script init.gradle` where *init.gradle* contains at least the AWS credentials and valid path to Packer (if it is not available in PATH as `packer`). See [sample-ee-init-script.gradle] for sample.

However, you need to make sure the setup for Liferay Workspace EE is matching your AWS account and desired Linux OS running your Liferay bundles. Please see next section for details.

### Choosing the right base AMI for your needs

You can select from a wide variety of AMIs in EC2, so which one should you pick? First step is to find the base AMI, with clean installation of Linux OS. The rule of thumb is, go for the Linux distribution which you are most familiar with.

As a second step, you will need to tell the Liferay Workspace EE which AMI have you chosen and that it should be used to bake your project's AMIs with Liferay bundle installed inside, together with providing some basic details about this AMI, like SSH username and the type of packaging system used by its Linux OS.

Following are some sample configurations on how to set up your [gradle.properties] to produces the desired AMIs.

#### Debian-based AMI (Ubuntu) with default VPC available in region eu-central-1 (Frankfurt)

EE values in [gradle.properties]:
```
liferay.workspace.ee.aws.ami.primary.region=eu-central-1
liferay.workspace.ee.aws.ami.base.ami.id=ami-766d771a
```

These default values in [gradle-ee-default.properties] will complete your setup:
```
liferay.workspace.ee.aws.ami.base.ami.linux.packages.format=deb
liferay.workspace.ee.aws.ami.base.ami.ssh.user.name=ubuntu

liferay.workspace.ee.aws.ami.build.ec2.vpc.id=
liferay.workspace.ee.aws.ami.build.ec2.subnet.id=
```

#### Debian-based AMI (Ubuntu) without default VPC available, in region us-east-1 (N. Virginia)

Values in [gradle.properties]:
```
liferay.workspace.ee.aws.ami.build.ec2.vpc.id=vpc-63490f07
liferay.workspace.ee.aws.ami.build.ec2.subnet.id=subnet-48ad183e
```

These default values from [gradle-ee-default.properties] will complete your setup:
```
liferay.workspace.ee.aws.ami.primary.region=us-east-1
liferay.workspace.ee.aws.ami.base.ami.id=ami-5aa69030

liferay.workspace.ee.aws.ami.base.ami.linux.packages.format=deb
liferay.workspace.ee.aws.ami.base.ami.ssh.user.name=ubuntu
```


#### RedHat-based AMI (CentOS) with default VPC available in region eu-central-1 (Frankfurt)

EE values in [gradle.properties]:
```
liferay.workspace.ee.aws.ami.primary.region=eu-central-1
liferay.workspace.ee.aws.ami.base.ami.id=ami-2a868b37

liferay.workspace.ee.aws.ami.base.ami.linux.packages.format=rpm
liferay.workspace.ee.aws.ami.base.ami.ssh.user.name=centos

```

These default values in [gradle-ee-default.properties] will complete your setup:
```
liferay.workspace.ee.aws.ami.build.ec2.vpc.id=
liferay.workspace.ee.aws.ami.build.ec2.subnet.id=
```

#### RedHat-based AMI (CentOS) without default VPC available, in region us-east-1 (N. Virginia)

Values in [gradle.properties]:
```
liferay.workspace.ee.aws.ami.base.ami.id=ami-57cd8732

liferay.workspace.ee.aws.ami.base.ami.linux.packages.format=rpmeb
liferay.workspace.ee.aws.ami.base.ami.ssh.user.name=centos

liferay.workspace.ee.aws.ami.build.ec2.vpc.id=vpc-63490f07
liferay.workspace.ee.aws.ami.build.ec2.subnet.id=subnet-48ad183e
```

These default values from [gradle-ee-default.properties] will complete your setup:
```
liferay.workspace.ee.aws.ami.primary.region=us-east-1
```


## Managing Jenkins server items

Liferay Workspace EE can help you with managing your Jenkins server jobs, views and plugins. Once you have your project's Jenkins server up and running - accessible over HTTP(s) - your Liferay Workspace EE can interact with it using Jenkins REST API.

Your Jenkins server most likely has security enabled (enforces users to log in), which means unauthenticated users will not be able to perform too many action (if any). If this is the case, you will need to pass your Jenkins credentials to Liferay Workspace EE using init script. Jenkins tasks will need two project properties: `jenkinsUserName` and `jenkinsPassword`. Although you can pass these using `-P...` as well, it's strongly recommended to use init script: `gradlew ... --init-script=init.gradle`
 
Check [sample-ee-init-script.gradle](sample-ee-init-script.gradle) for details on how to write your own (private) init script and use it to provide credentials for Jenkins tasks.
 
If your Jenkins server is not secured and anonymous users can perform any action, you can set:

```
liferay.workspace.ee.jenkins.server.secure=false
```

in your [gradle.properties](gradle.properties) and Liferay Workspace EE will not require any Jenkins credentials to run Jenkins-related tasks. Please note this Jenkins setup is strongly discouraged, unless your Jenkins server is secured by other means, like being accessible only using private VPN connection.
 
You can set up your Jenkins server from the workspace using:
  ```
  gradlew initJenkinsServer
  ```

`initJenkinsServer` is a container task, it will delegate all work to two child tasks: `installJenkinsPlugins` and `updateJenkinsItems`. Please see following sections for details.

### Jenkins plugins
 
`installJenkinsPlugins`, which will ask your Jenkins server to install several plugins, referenced by their name. These plugins are used by sample jobs (see below). If you do not wish to install the recommended plugins, only the jobs, use `updateJenkinsItems` instead of `initJenkinsServer`.

**Note**: Make sure to restart your Jenkins server after _first successful_ run of `initJenkinsServer` task, since some of the installed plugins require restarting Jenkins server to take effect.

### Jenkins items

`initJenkinsServer` will also invoke `updateJenkinsItems`, which reads local definition of sample Jenkins jobs and views and creates them in your Jenkins server. 

To check the list of items currently known to your workspace, use `gradlew listDefinedJenkinsItems`.

#### Jobs

After installation of fresh workspace (EE), the project will contain a set of initial jobs for your workspace project. You can use this set of as a baseline for setting up CI - building your project's bundle (*build* jobs) and pushing it to remote server over SSH (*deploy* jobs).

`updateJenkinsItems` will create two jobs for every configured environment (see below). These jobs will be defined as following, assuming your workspace is named *liferay-project-abc*:

* *liferay-project-abc_build-dev*
	1. fetches sources from SCM (poling / webhook)
	2. invokes Gradle build of your workspace
	3. archives the built artifacts in Jenkins (.zip, .tar.gz, .deb, .rpm)	
* *liferay-project-abc_deploy-dev*
	1. copies latest successful artifact from previous job (.deb file)
	2. pushes this file to remote SSH server and used `dpkg` to install it

Please note that you will need to finish the configuration of the jobs through your Jenkins server UI, for example to provide SCM credentials (and verify the URL you'll be using) or provide the IP and credentials for SSH connection to your target Liferay server. The description of each job contains TODOs with list of items your should check or update through your Jenkins UI.

Jobs are by default defined and created for every environment except `local`. This behavior can be changed using property `liferay.workspace.ee.jenkins.initial.jobs.exclude.environments` in your [gradle.properties](gradle.properties). Please check [gradle-ee-default.properties](gradle-ee-default.properties) for details. 

#### Views

One view is defined in the workspace by default - named after your workspace (the `rootProject.name` in Gradle) and grouping all the *deploy* and *build* jobs together. This will make it easier to find your project's Jenkins jobs in case your are sharing Jenkins instance with other projects. So for example, view *liferay-project-abc* will be created, listing all *liferay-project-abc-...* jobs. 

## Backup & restore of Jenkins jobs & views

You can use your workspace to backup & restore your project's jobs & views (see previous sections) in your Jenkins server. The workflow is following:

1. You initialize your jobs and views in your server using `initJenkinsServer`
	* or just `updateJenkinsItems` if you want to install your plugins manually, outside of the workspace
2. You finish the setup of your jobs using Jenkins server UI
	* cleanup, adjustments based on your project etc. -- see recommended TODOs in the jobs' description
3. You can backup your Jenkins jobs & views using `dumpRemoteJenkinsItems`
	* this will create XMLs of all the items in *[workspace]/jenkins* directory
	* please note that **only the jobs known to the workspace will be dumped from Jenkins server** - the initial set of build & deploy jobs as described above
	* if you need to backup additional jobs, please check [gradle-ee-default.properties](gradle-ee-default.properties) -> `liferay.workspace.ee.jenkins.initial.jobs.extra.dumped.job.names`
		* for example, if you have created additional jobs like *liferay-project-abc_deploy-prod-node-1* and *liferay-project-abc_deploy-prod-node-2* instead of the default *your-project_deploy-prod*, you will want to add `liferay.workspace.ee.jenkins.initial.jobs.extra.dumped.job.names=liferay-project-abc_deploy-prod-node-1, liferay-project-abc_deploy-prod-node-2` into your [gradle.properties](gradle.properties)
4. From now on, your workspace will work with Jenkins jobs & views defined by the XMLs in *[workspace]/jenkins* 		
	* the decision (initial items vs. XML-based items) is done based on test "does *[workspace]/jenkins/{jobs,views}* exists?"
		* *[workspace]/jenkins* does not exists -> initial set of items is defined using Jenkins DSL
		* *[workspace]/jenkins* exists -> XMLs are parsed and used as the items recognized by the workspace
5. You can use `updateJenkinsItems` to restore items in Jenkins server based on local XMLs		

## Limitation - CSRF protection in Jenkins

If you have CSRF protection enabdle in Jenkins (this seems to be the default starting with Jenkins 2.0), you will most 
likely see errors like this one when running any Jenkins-related tasks:

    403: No valid crumb was included in the request
     
The full HTML respose from Jenkins will look like this:
    
    <html>
        <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <title>Error 403 No valid crumb was included in the request</title>
        </head>
        <body><h2>HTTP ERROR 403</h2>
        <p>Problem accessing //pluginManager/installNecessaryPlugins. Reason:
        <pre>    No valid crumb was included in the request</pre></p><hr><i><small>Powered by Jetty://</small></i><hr/>
        
        </body>
    </html>

This means you have CSRF protection enabled. Unfortunately, our `jenkins` module cannot yet get around this security 
measure. The only option for now is disabling CSRF in Jenkins as advised in this issue:
* https://github.com/ghale/gradle-jenkins-plugin/issues/78

This limitation will be removed in one of future releases of liferay-workspace-ee


## Liferay Workspace EE samples

Liferay Workspace EE ships with various samples which can be used as part of your Liferay Workspace to configure your Liferay bundle - Liferay and Tomcat. Please note that the samples are currently based on **6.2 EE**, check against 7.0 and updated samples will be created once 7.0 EE is out.

You can check the samples in [gradle/liferay-workspace-ee/samples](gradle/liferay-workspace-ee/samples).


## Integration of Liferay Workspace EE with base Liferay Workspace

The main integration point is file [settings.gradle](settings.gradle), which in case of EE features installed should contain line:
```
apply from: 'gradle/liferay-workspace-ee/settings-ee.gradle'
```

This will instruct Gradle to load given script during initialization phase, before any projects are evaluated or tasks being executed. This script will configures all separate EE subprojects of the root Liferay Workspace Build, which contain all EE features. The EE subprojects' files are organized inside [gradle/liferay-workspace-ee](gradle/liferay-workspace-ee).

[gradle-ee-default.properties](gradle-ee-default.properties) lists all EE properties used by the build and can be overridden using standard [gradle.properties](gradle.properties), see above. If you do not provide custom value in project's [gradle.properties](gradle.properties), the default values as specified in file [gradle-ee-default.properties](gradle-ee-default.properties) will be loaded and used by the build.

[sample-ee-init-script.gradle](sample-ee-init-script.gradle) lists all EE properties used by the build, which users can use to pass sensitive / user-specific information to the build script, see above. Most of these properties do not have default values, so if you use any task which needs these properties, the build will fail and prompt you to provide valid values of the necessary properties. Each user should create his / her own version of the file, called e.g. *init.gradle*, use it to run the local builds and never push this file into the CSM repository.


## Customizing Liferay Workspace EE scripts (discouraged)

All the scripts which implement the Liferay Workspace EE features are installed inside [gradle/liferay-workspace-ee](gradle/liferay-workspace-ee). Ideally, you shoudl not have any need to customize any of these, the necessary configurations can be provided to the build using either gradle.properties (keys liferay.workspace.ee.*) or on command line / init script (see complete list above). If you customize any scripts, it will be harder for you to upgrade to newer version of Liferay Workspace EE in the future.

However, if you feel confident to change the scripts, here are some basic rules you should follow:

1. Learn Gradle (and Groovy) first, to understand how the build system was designed. Gradle is very powerful and differs from both Ant or Maven significantly, so make sure you know at least the basics.
2. Version-control all your Liferay Workspace files (which is a good idea anyway, for any source code).
3. Do not update file [gradle-ee-default.properties](gradle-ee-default.properties), you can override all properties using [gradle.properties](gradle.properties). File [gradle-ee-default.properties](gradle-ee-default.properties) will be overwritten by Liferay Workspace EE installer when you run `upgrade` in the future.
4. Do not update file [sample-ee-init-script.gradle](sample-ee-init-script.gradle), every user should create his / her own (private) version of this file (e.g. *init.gradle*) and use it for local builds where necessary. You can also pass the properties on command line, using `-P...`, but remember these will be visible to other users (in listing of running OS processes) and will also remain in the history of your shell. File [sample-ee-init-script.gradle](sample-ee-init-script.gradle) will be overwritten by Liferay Workspace EE installer when you run `upgrade` in the future.
5. Do not alter the Liferay Workspace EE's `apply from ...` line in [settings.gradle](settings.gradle), it should read:
	```
	// Liferay Workspace EE START
	apply from: 'gradle/liferay-workspace-ee/settings-ee.gradle'
	// Liferay Workspace EE END
	```
    Liferay Workspace EE installer will look for this line to determine if EE features were installed in base Liferay Workspace or not.
 
6. When changing some script / file from Liferay Workspace EE (inside [gradle/liferay-workspace-ee](gradle/liferay-workspace-ee)), clearly mark the customization (its start and end line) in the file and describe its purpose, for example:

	```
	... other uncustomized code...
	
	// CUSTOM BEGIN
	// Change the path where Liferay bundle tar is extracted inside DEB / RPM
	
	... your customized code...
	
	// CUSTOM END
	
	...other uncustomized code...
	```
This will allow you to upgrade your customization later, by manually merging your custom code into the new version of the file, coming from new version of Liferay Workspace EE.