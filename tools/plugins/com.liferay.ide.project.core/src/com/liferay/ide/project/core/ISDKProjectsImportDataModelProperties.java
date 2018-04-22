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

package com.liferay.ide.project.core;

import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetProjectCreationDataModelProperties;

/**
 * @author Gregory Amerson
 */
public interface ISDKProjectsImportDataModelProperties extends IFacetProjectCreationDataModelProperties {

	public static final String LIFERAY_SDK_NAME = "ISDKProjectsImportDataModelProperties.LIFERAY_SDK_NAME";

	public static final String SDK_LOCATION = "ISDKProjectsImportDataModelProperties.SDK_LOCATION";

	public static final String SDK_VERSION = "ISDKProjectsImportDataModelProperties.SDK_VERSION";

	public static final String SELECTED_PROJECTS = "ISDKProjectsImportDataModelProperties.SELECTED_PROJECTS";

}