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

package com.liferay.ide.maven.core;

import com.liferay.ide.theme.core.facet.ThemePluginFacetInstallDataModelProvider;

/**
 * @author Gregory Amerson
 */
public class MavenThemePluginFacetInstallProvider extends ThemePluginFacetInstallDataModelProvider {

	@Override
	public Object getDefaultProperty(String propertyName) {
		if (INSTALL_LIFERAY_PLUGIN_LIBRARY_DELEGATE.equals(propertyName)) {
			return false;
		}
		else if (INSTALL_THEME_CSS_BUILDER.equals(propertyName)) {
			return false;
		}
		else if (SETUP_DEFAULT_OUTPUT_LOCATION.equals(propertyName)) {
			return false;
		}
		else if (CONFIGURE_DEPLOYMENT_ASSEMBLY.equals(propertyName)) {
			return false;
		}
		else if (UPDATE_BUILD_XML.equals(propertyName)) {
			return false;
		}

		return super.getDefaultProperty(propertyName);
	}

}