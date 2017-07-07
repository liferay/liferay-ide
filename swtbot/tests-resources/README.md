please make sure your tests resources folder includes the following files:
1. bundles.csv
2. portal-ext.properties
3. com.liferay.portal.plugins.sdk-1.0.11-withdependencies-20170613175008905.zip

bundles.csv will contains the the list of servers and the format of row should be:
liferay-dxp-digital-enterprise-{bundle-type}-${version-and-timestamp}.zip,liferay-dxp-digital-enterprise-{version},{bundle-type}-{version},{bundle-type},{version}

for example:
liferay-ce-portal-tomcat-7.0-ga3-20160804222206210.zip,liferay-ce-portal-7.0-ga3,tomcat-8.0.32,tomcat,7.0-ga3

