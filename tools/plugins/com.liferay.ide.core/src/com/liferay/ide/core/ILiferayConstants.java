/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.ide.core;

import org.osgi.framework.Version;

/**
 * @author Gregory Amerson
 */
public interface ILiferayConstants {

	public Version EMPTY_VERSION = new Version(0, 0, 0);

	public String LANGUAGE_PROPERTIES_FILE_ENCODING_CHARSET = "UTF-8";

	public Version LEAST_SUPPORTED_VERSION = new Version(6, 0, 2);

	public String LIFERAY_DISPLAY_XML_FILE = "liferay-display.xml";

	public String LIFERAY_HOOK_XML_FILE = "liferay-hook.xml";

	public String LIFERAY_LAYOUTTPL_XML_FILE = "liferay-layout-templates.xml";

	public String LIFERAY_LOOK_AND_FEEL_XML_FILE = "liferay-look-and-feel.xml";

	public String LIFERAY_PLUGIN_PACKAGE_PROPERTIES_FILE = "liferay-plugin-package.properties";

	public String LIFERAY_PLUGIN_PACKAGE_PROPERTIES_XML_FILE = "liferay-plugin-package.xml";

	public String LIFERAY_PORTLET_XML_FILE = "liferay-portlet.xml";

	public String PORTLET_XML_FILE = "portlet.xml";

	public String SERVICE_XML_FILE = "service.xml";

	public Version V601 = new Version(6, 0, 1);

	public Version V610 = new Version(6, 1, 0);

	public Version V611 = new Version(6, 1, 1);

	public Version V612 = new Version(6, 1, 2);

	public Version V620 = new Version(6, 2, 0);

	public Version V700 = new Version(7, 0, 0);

	public Version V6110 = new Version(6, 1, 10);

	public Version V6120 = new Version(6, 1, 20);

	public Version V6130 = new Version(6, 1, 30);

	public String WEB_XML_FILE = "web.xml";

}