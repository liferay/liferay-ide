
package com.liferay.ide.eclipse.server.jboss.core;

import com.liferay.ide.eclipse.server.core.ILiferayServer;

public interface ILiferayJBossServer extends ILiferayServer
{

	/**
	 * Property which specifies the directory where liferay scans for autodeployment
	 */
	String PROPERTY_AUTO_DEPLOY_DIR = "autoDeployDir";

	String PROPERTY_AUTO_DEPLOY_INTERVAL = "autoDeployInterval";

	String PROPERTY_EXTERNAL_PROPERTIES = "externalProperties";

	String PROPERTY_MEMORY_ARGS = "memoryArgs";

	String PROPERTY_USER_TIMEZONE = "userTimezone";

	String getAutoDeployDirectory();

	String getAutoDeployInterval();

	String getExternalProperties();

	String getMemoryArgs();

	String getUserTimezone();

	String DEFAULT_JBOSS_JAVA_MEM_ARGS =
		" -Dsun.rmi.dgc.client.gcInterval=3600000 -Dsun.rmi.dgc.server.gcInterval=3600000 -Dorg.jboss.resolver.warning=true -Djboss.modules.system.pkgs=org.jboss.byteman";
}
