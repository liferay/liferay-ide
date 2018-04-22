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
public interface ILiferayProjectImportDataModelProperties extends IFacetProjectCreationDataModelProperties {

	public static final String PLUGIN_TYPE = "ILiferayProjectImportDataModelProperties.PLUGIN_TYPE";

	public static final String PROJECT_LOCATION = "ILiferayProjectImportDataModelProperties.PROJECT_LOCATION";

	public static final String PROJECT_RECORD = "ILiferayProjectImportDataModelProperties.PROJECT_RECORD";

	public static final String SDK_VERSION = "ILiferayProjectImportDataModelProperties.SDK_VERSION";

}