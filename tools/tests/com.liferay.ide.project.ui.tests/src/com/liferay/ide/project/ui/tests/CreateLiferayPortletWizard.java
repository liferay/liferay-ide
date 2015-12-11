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

package com.liferay.ide.project.ui.tests;

import com.liferay.ide.ui.tests.UIBase;

/**
 * @author Ashley Yuan
 */
public interface CreateLiferayPortletWizard extends UIBase
{

    String LABEL_PORTLET_PLUGIN_PROJECT = "Portlet plugin project:";
    String LABEL_SOURCE_FOLDER = "Source folder:";
    String LABEL_PORTLET_CLASS = "Portlet class:";
    String LABEL_JAVA_PACKAGE = "Java Package:";
    String LABEL_SUPERCLASS = "Superclass:";

    String RADIO_CREATE_NEW_PORTLET = "Create new portlet";
    String RADIO_USE_DEFAULT_PORTLET = "Use default portlet (MVCPortlet)";

}
