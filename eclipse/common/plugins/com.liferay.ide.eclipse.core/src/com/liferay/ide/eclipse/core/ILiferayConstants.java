package com.liferay.ide.eclipse.core;

import org.osgi.framework.Version;


public interface ILiferayConstants {

	String LIFERAY_PLUGIN_PACKAGE_PROPERTIES_FILE = "liferay-plugin-package.properties";
	String LIFERAY_DISPLAY_XML_FILE = "liferay-display.xml";
	String LIFERAY_HOOK_XML_FILE = "liferay-hook.xml";
	String LIFERAY_LAYOUTTPL_XML_FILE = "liferay-layout-templates.xml";
	String LIFERAY_LOOK_AND_FEEL_XML_FILE = "liferay-look-and-feel.xml";
	String LIFERAY_PORTLET_XML_FILE = "liferay-portlet.xml";
	String LIFERAY_SERVICE_BUILDER_XML_FILE = "service.xml";
	String PORTLET_XML_FILE = "portlet.xml";
	String WEB_XML_FILE = "web.xml";
	Version LEAST_SUPPORTED_VERSION = new Version(6, 0, 2);

}
