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

package com.liferay.ide.portlet.core;

import com.liferay.ide.core.model.IModel;
import com.liferay.ide.core.model.IModelChangeProvider;

/**
 * @author Greg Amerson
 */
public interface IPluginPackageModel extends IModel, IModelChangeProvider {

	public String PROPERTY_AUTHOR = "author";

	public String PROPERTY_CHANGE_LOG = "change-log";

	public String PROPERTY_DEPLOY_EXCLUDE = "deploy-excludes";

	public String PROPERTY_LICENSES = "licenses";

	public String PROPERTY_LIFERAY_VERSIONS = "liferay-versions";

	public String PROPERTY_LONG_DESCRIPTION = "long-description";

	public String PROPERTY_MODULE_GROUP_ID = "module-group-id";

	public String PROPERTY_MODULE_INCREMENTAL_VERSION = "module-incremental-version";

	public String PROPERTY_NAME = "name";

	public String PROPERTY_PAGE_URL = "page-url";

	public String PROPERTY_PORTAL_DEPENDENCY_JARS = "portal-dependency-jars";

	public String PROPERTY_PORTAL_DEPENDENCY_TLDS = "portal-dependency-tlds";

	public String PROPERTY_REQUIRED_DEPLOYMENT_CONTEXTS = "required-deployment-contexts";

	public String PROPERTY_SHORT_DESCRIPTION = "short-description";

	public String PROPERTY_SPEED_FILTERS_ENABLED = "speed-filters-enabled";

	public String PROPERTY_TAGS = "tags";

}