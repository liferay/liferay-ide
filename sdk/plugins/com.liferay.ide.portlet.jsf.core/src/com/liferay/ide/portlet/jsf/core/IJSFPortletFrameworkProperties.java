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
 * Contributors:
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.portlet.jsf.core;

import com.liferay.ide.project.core.facet.IPluginProjectDataModelProperties;

/**
 * @author Gregory Amerson
 */
public interface IJSFPortletFrameworkProperties extends IPluginProjectDataModelProperties
{
    String COMPONENT_SUITE_JSF_STANDARD = "IJSFPortletFrameworkProperties.COMPONENT_SUITE_JSF_STANDARD"; //$NON-NLS-1$
    String COMPONENT_SUITE_LIFERAY_FACES_ALLOY = "IJSFPortletFrameworkProperties.COMPONENT_SUITE_LIFERAY_FACES_ALLOY"; //$NON-NLS-1$
    String COMPONENT_SUITE_ICEFACES = "IJSFPortletFrameworkProperties.COMPONENT_SUITE_ICEFACES"; //$NON-NLS-1$
    String COMPONENT_SUITE_PRIMEFACES = "IJSFPortletFrameworkProperties.COMPONENT_SUITE_PRIMEFACES"; //$NON-NLS-1$
    String COMPONENT_SUITE_RICHFACES = "IJSFPortletFrameworkProperties.COMPONENT_SUITE_RICHFACES"; //$NON-NLS-1$
}
