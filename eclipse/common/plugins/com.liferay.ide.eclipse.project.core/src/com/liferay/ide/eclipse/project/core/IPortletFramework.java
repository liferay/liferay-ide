/*******************************************************************************
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
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
 *******************************************************************************/

package com.liferay.ide.eclipse.project.core;

import java.net.URL;

/**
 * @author Greg Amerson
 */
public interface IPortletFramework {

	String DEFAULT = "default";

	String DESCRIPTION = "description";

	String DISPLAY_NAME = "displayName";

	String EXTENSION_ID = "com.liferay.ide.eclipse.project.core.portletFrameworks";

	String HELP_URL = "helpUrl";

	String ID = "id";

	String REQUIRED_SDK_VERSION = "requiredSDKVersion";

	String SHORT_NAME = "shortName";

	String getBundleId();

	String getDescription();

	String getDisplayName();

	URL getHelpUrl();

	String getId();

	String getRequiredSDKVersion();

	String getShortName();

	boolean isDefault();

}
