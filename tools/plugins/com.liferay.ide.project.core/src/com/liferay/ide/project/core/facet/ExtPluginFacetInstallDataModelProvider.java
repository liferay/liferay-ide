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

package com.liferay.ide.project.core.facet;

/**
 * @author Gregory Amerson
 */
public class ExtPluginFacetInstallDataModelProvider extends PluginFacetInstallDataModelProvider {

	@Override
	public Object getDefaultProperty(String propertyName) {
		if (SETUP_EXT_CLASSPATH.equals(propertyName)) {
			return true;
		}

		return super.getDefaultProperty(propertyName);
	}

	@Override
	protected String getPluginFacetId() {
		return IPluginFacetConstants.LIFERAY_EXT_FACET_ID;
	}

}