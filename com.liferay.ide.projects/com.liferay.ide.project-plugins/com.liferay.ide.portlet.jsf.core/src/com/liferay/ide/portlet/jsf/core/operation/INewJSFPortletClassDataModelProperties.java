/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.portlet.jsf.core.operation;

import com.liferay.ide.portlet.core.operation.INewPortletClassDataModelProperties;

/**
 * @author Greg Amerson
 */
public interface INewJSFPortletClassDataModelProperties extends INewPortletClassDataModelProperties
{
    String[] ALL_JSF_PORTLET_MODES = 
    { 
        "INewPortletClassDataModelProperties.VIEW_MODE",
        "INewPortletClassDataModelProperties.EDIT_MODE", 
        "INewPortletClassDataModelProperties.HELP_MODE", 
    };

    String JSF_EDIT_MODE_TEMPLATE = "com.liferay.ide.templates.portlet.jsf.edit";

    String JSF_HELP_MODE_TEMPLATE = "com.liferay.ide.templates.portlet.jsf.help";

    String JSF_PORTLET_CLASS = "INewJSFPortletClassDataModelProperties.JSF_PORTLET_CLASS";

    String JSF_VIEW_MODE_TEMPLATE = "com.liferay.ide.templates.portlet.jsf.view";

    String QUALIFIED_JSF_PORTLET = "org.portletfaces.bridge.GenericFacesPortlet";
}
