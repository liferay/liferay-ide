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

import com.liferay.ide.core.ILiferayProjectProvider;

import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public interface IPortletFramework {

	public IStatus configureNewProject(IDataModel dataModel, IFacetedProjectWorkingCopy facetedProject);

	public String getBundleId();

	public String getDescription();

	public String getDisplayName();

	public IProjectFacet[] getFacets();

	public URL getHelpUrl();

	public String getId();

	public String getRequiredSDKVersion();

	public String getShortName();

	public boolean isAdvanced();

	public boolean isDefault();

	public boolean isRequiresAdvanced();

	public IStatus postProjectCreated(
		IProject project, String frameworkName, String portletName, IProgressMonitor monitor);

	public boolean supports(ILiferayProjectProvider provider);

	public String ADVANCED = "advanced";

	public String DEFAULT = "default";

	public String DESCRIPTION = "description";

	public String DISPLAY_NAME = "displayName";

	public String EXTENSION_ID = "com.liferay.ide.project.core.portletFrameworks";

	public String HELP_URL = "helpUrl";

	public String ID = "id";

	public String REQUIRED_SDK_VERSION = "requiredSDKVersion";

	public String REQUIRES_ADVANCED = "requiresAdvanced";

	public String SHORT_NAME = "shortName";

}