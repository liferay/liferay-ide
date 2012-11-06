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

package com.liferay.ide.portlet.core;

import com.liferay.ide.core.model.IModel;
import com.liferay.ide.core.model.IModelChangeProvider;

/**
 * @author Greg Amerson
 */
public interface IPluginPackageModel extends IModel, IModelChangeProvider
{

    String PROPERTY_AUTHOR = "author";

    String PROPERTY_CHANGE_LOG = "change-log";

    String PROPERTY_LICENSES = "licenses";

    String PROPERTY_MODULE_GROUP_ID = "module-group-id";

    String PROPERTY_MODULE_INCREMENTAL_VERSION = "module-incremental-version";

    String PROPERTY_NAME = "name";

    String PROPERTY_PAGE_URL = "page-url";

    String PROPERTY_PORTAL_DEPENDENCY_JARS = "portal-dependency-jars";

    String PROPERTY_PORTAL_DEPENDENCY_TLDS = "portal-dependency-tlds";

    String PROPERTY_REQUIRED_DEPLOYMENT_CONTEXTS = "required-deployment-contexts";

    String PROPERTY_SHORT_DESCRIPTION = "short-description";

    String PROPERTY_SPEED_FILTERS_ENABLED = "speed-filters-enabled";

    String PROPERTY_TAGS = "tags";

}
