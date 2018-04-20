# EE samples of configs for various parts of Liferay stack

Following are sample configurations provided by Global Services, which you can use as a base for creating your own configs in Liferay Workspace, based on you project's needs. Investigate them a use the relevant parts in your Liferay Workspace, if interested. 

Please note that the samples are currently based on **6.2 EE**, check against Liferay 7.0 and updated samples will be created once 7.0 EE is out.

Following are notable samples.

## portal-ext*.properties and their inclusion
  
Inside [configs]([configs]), you will find example for to apply base configuration from `common` and override it with properties in the appropriate `[environment]` directory. See the top-most section of files:

* [configs/common/portal-ext.properties](configs/common/portal-ext.properties)   
* [configs/dev/portal-ext.env.properties](configs/dev/portal-ext.env.properties)   
  
## Basic setup of Liferay
JDBC connection, DocLib store (filesystem / S3), Cluster Link

See:

* [configs/common/portal-ext.properties](configs/common/portal-ext.properties)   
* [configs/local/portal-ext.env.properties](configs/dev/portal-ext.env.properties)
* [configs/dev/portal-ext.env.properties](configs/dev/portal-ext.env.properties)
* [configs/common/tomcat-8.0.32/conf/localhost/ROOT.xml](configs/common/tomcat-8.0.32/conf/localhost/ROOT.xml)

## Basic JVM tuning overview for Oracle JDK 8
See [configs/common/tomcat-8.0.32/bin/setenv.sh](configs/common/tomcat-8.0.32/bin/setenv.sh).

## Basic setup of Tomcat 8
See [configs/common/tomcat-8.0.32/conf](configs/common/tomcat-8.0.32/conf).