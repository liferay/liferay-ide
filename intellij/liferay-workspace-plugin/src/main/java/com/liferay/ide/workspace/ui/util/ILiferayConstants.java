/*******************************************************************************
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
 *
 * Contributors:
 * 	Gregory Amerson - initial implementation and ongoing maintenance
 *  Kamesh Sampath - IDE-381
 *******************************************************************************/

package com.liferay.ide.workspace.ui.util;

import org.osgi.framework.Version;

/**
 * @author Gregory Amerson
 */
public interface ILiferayConstants
{
    Version EMPTY_VERSION = new Version( 0, 0, 0 );
    String LANGUAGE_PROPERTIES_FILE_ENCODING_CHARSET = "UTF-8";//$NON-NLS-1$
    Version LEAST_SUPPORTED_VERSION = new Version( 6, 0, 2 );
    String LIFERAY_DISPLAY_XML_FILE = "liferay-display.xml"; //$NON-NLS-1$
    String LIFERAY_HOOK_XML_FILE = "liferay-hook.xml"; //$NON-NLS-1$
    String LIFERAY_LAYOUTTPL_XML_FILE = "liferay-layout-templates.xml"; //$NON-NLS-1$
    String LIFERAY_LOOK_AND_FEEL_XML_FILE = "liferay-look-and-feel.xml"; //$NON-NLS-1$
    String LIFERAY_PLUGIN_PACKAGE_PROPERTIES_FILE = "liferay-plugin-package.properties"; //$NON-NLS-1$
    String LIFERAY_PLUGIN_PACKAGE_PROPERTIES_XML_FILE = "liferay-plugin-package.xml"; //$NON-NLS-1$
    String LIFERAY_PORTLET_XML_FILE = "liferay-portlet.xml"; //$NON-NLS-1$
    String PORTLET_XML_FILE = "portlet.xml"; //$NON-NLS-1$
    String SERVICE_XML_FILE = "service.xml"; //$NON-NLS-1$
    Version V601 = new Version( 6, 0, 1 );
    Version V610 = new Version( 6, 1, 0 );
    Version V611 = new Version( 6, 1, 1 );
    Version V6110 = new Version( 6, 1, 10 );
    Version V612 = new Version( 6, 1, 2 );
    Version V6120 = new Version( 6, 1, 20 );
    Version V6130 = new Version( 6, 1, 30 );
    Version V620 = new Version( 6, 2, 0 );
    Version V700 = new Version( 7, 0, 0 );
    String WEB_XML_FILE = "web.xml"; //$NON-NLS-1$
}
