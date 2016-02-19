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
 *******************************************************************************/

package com.liferay.ide.portlet.ui.tests.swtbot;

import com.liferay.ide.ui.tests.UIBase;

/**
 * @author Ashley Yuan
 * @author Li Lu
 */
public interface PortletWizard extends UIBase
{

    public final String LABEL_PORTLET_PLUGIN_PROJECT = "Portlet plugin project:";
    public final String LABEL_SOURCE_FOLDER = "Source folder:";
    public final String LABEL_PORTLET_CLASS = "Portlet class:";
    public final String LABEL_JAVA_PACKAGE = "Java package:";
    public final String LABEL_SUPERCLASS = "Superclass:";

    public final String RADIO_CREATE_NEW_PORTLET = "Create new portlet";
    public final String RADIO_USE_DEFAULT_PORTLET = "Use default portlet (MVCPortlet)";

    public final String TEXT_CLASS_NAME_CANNOT_BE_EMPTY = " The class name cannot be empty.";

}
