/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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
 * @author Simon Jiang
 * @author Terry Jia
 */
public interface INewJSFPortletClassDataModelProperties extends INewPortletClassDataModelProperties
{
    String[] ALL_JSF_PORTLET_MODES = 
    { 
        "INewPortletClassDataModelProperties.VIEW_MODE", //$NON-NLS-1$
        "INewPortletClassDataModelProperties.EDIT_MODE",  //$NON-NLS-1$
        "INewPortletClassDataModelProperties.HELP_MODE",  //$NON-NLS-1$
    };

    String ICE_FACES = "INewJSFPortletClassDataModelProperties.ICE_FACES"; //$NON-NLS-1$

    String JSF_EDIT_MODE_TEMPLATE = "com.liferay.ide.templates.portlet.jsf.edit"; //$NON-NLS-1$

    String JSF_HELP_MODE_TEMPLATE = "com.liferay.ide.templates.portlet.jsf.help"; //$NON-NLS-1$

    String JSF_PORTLET_CLASS = "INewJSFPortletClassDataModelProperties.JSF_PORTLET_CLASS"; //$NON-NLS-1$

    String JSF_VIEW_MODE_TEMPLATE = "com.liferay.ide.templates.portlet.jsf.view"; //$NON-NLS-1$

    String LIFERAY_FACES_ALLOY = "INewJSFPortletClassDataModelProperties.LIFERAY_FACES_ALLOY"; //$NON-NLS-1$

    String PRIME_FACES = "INewJSFPortletClassDataModelProperties.PRIME_FACES"; //$NON-NLS-1$

    String RICH_FACES = "INewJSFPortletClassDataModelProperties.RICH_FACES"; //$NON-NLS-1$

    String QUALIFIED_JSF_PORTLET = "javax.portlet.faces.GenericFacesPortlet"; //$NON-NLS-1$

    String STANDARD_JSF = "INewJSFPortletClassDataModelProperties.STANDARD_JSF"; //$NON-NLS-1$

}
