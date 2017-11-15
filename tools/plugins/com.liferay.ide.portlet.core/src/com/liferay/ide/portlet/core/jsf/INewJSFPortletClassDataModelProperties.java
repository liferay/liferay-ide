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

import com.liferay.ide.portlet.core.operation.INewPortletClassDataModelProperties;

/**
 * @author Greg Amerson
 * @author Simon Jiang
 * @author Terry Jia
 */
public interface INewJSFPortletClassDataModelProperties extends INewPortletClassDataModelProperties {

	public String[] ALL_JSF_PORTLET_MODES = {
		"INewPortletClassDataModelProperties.VIEW_MODE", "INewPortletClassDataModelProperties.EDIT_MODE",
		"INewPortletClassDataModelProperties.HELP_MODE"
	};

	public String ICE_FACES = "INewJSFPortletClassDataModelProperties.ICE_FACES";

	public String JSF_EDIT_MODE_TEMPLATE = "com.liferay.ide.templates.portlet.jsf.edit";

	public String JSF_HELP_MODE_TEMPLATE = "com.liferay.ide.templates.portlet.jsf.help";

	public String JSF_PORTLET_CLASS = "INewJSFPortletClassDataModelProperties.JSF_PORTLET_CLASS";

	public String JSF_VIEW_MODE_TEMPLATE = "com.liferay.ide.templates.portlet.jsf.view";

	public String LIFERAY_FACES_ALLOY = "INewJSFPortletClassDataModelProperties.LIFERAY_FACES_ALLOY";

	public String PRIME_FACES = "INewJSFPortletClassDataModelProperties.PRIME_FACES";

	public String QUALIFIED_JSF_PORTLET = "javax.portlet.faces.GenericFacesPortlet";

	public String RICH_FACES = "INewJSFPortletClassDataModelProperties.RICH_FACES";

	public String STANDARD_JSF = "INewJSFPortletClassDataModelProperties.STANDARD_JSF";

}