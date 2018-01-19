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

package com.liferay.ide.portlet.core.jsf;

import com.liferay.ide.project.core.facet.IPluginProjectDataModelProperties;

/**
 * @author Gregory Amerson
 */
public interface IJSFPortletFrameworkProperties extends IPluginProjectDataModelProperties {

	public String COMPONENT_SUITE_ICEFACES = "IJSFPortletFrameworkProperties.COMPONENT_SUITE_ICEFACES";

	public String COMPONENT_SUITE_JSF_STANDARD = "IJSFPortletFrameworkProperties.COMPONENT_SUITE_JSF_STANDARD";

	public String COMPONENT_SUITE_LIFERAY_FACES_ALLOY =
		"IJSFPortletFrameworkProperties.COMPONENT_SUITE_LIFERAY_FACES_ALLOY";

	public String COMPONENT_SUITE_PRIMEFACES = "IJSFPortletFrameworkProperties.COMPONENT_SUITE_PRIMEFACES";

	public String COMPONENT_SUITE_RICHFACES = "IJSFPortletFrameworkProperties.COMPONENT_SUITE_RICHFACES";

}