/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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
public interface IPortletFramework
{
    String ADVANCED = "advanced"; //$NON-NLS-1$

    String DEFAULT = "default"; //$NON-NLS-1$

    String DESCRIPTION = "description"; //$NON-NLS-1$

    String DISPLAY_NAME = "displayName"; //$NON-NLS-1$

    String EXTENSION_ID = "com.liferay.ide.project.core.portletFrameworks"; //$NON-NLS-1$

    String HELP_URL = "helpUrl"; //$NON-NLS-1$

    String ID = "id"; //$NON-NLS-1$

    String REQUIRED_SDK_VERSION = "requiredSDKVersion"; //$NON-NLS-1$

    String REQUIRES_ADVANCED = "requiresAdvanced";  //$NON-NLS-1$

    String SHORT_NAME = "shortName"; //$NON-NLS-1$

    IStatus configureNewProject( IDataModel dataModel, IFacetedProjectWorkingCopy facetedProject );

    String getBundleId();

    String getDescription();

    String getDisplayName();

    IProjectFacet[] getFacets();

    URL getHelpUrl();

    String getId();

    String getRequiredSDKVersion();

    String getShortName();

    boolean isAdvanced();

    boolean isDefault();

    boolean isRequiresAdvanced();

    IStatus postProjectCreated( IProject project, String frameworkName, String portletName, IProgressMonitor monitor );

    boolean supports( ILiferayProjectProvider provider );
}
