/*******************************************************************************
 * Copyright (c) 2010-2012 Liferay, Inc. All rights reserved.
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

import java.net.URL;
import java.util.Collection;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;

/**
 * @author Gregory Amerson
 */
public interface IPortletFrameworkWizardProvider
{

    String DEFAULT = "default";

    String DESCRIPTION = "description";

    String DISPLAY_NAME = "displayName";

    String EXTENSION_ID = "com.liferay.ide.project.core.portletFrameworkWizardProviders";

    String HELP_URL = "helpUrl";

    String ID = "id";

    String REQUIRED_SDK_VERSION = "requiredSDKVersion";

    String SHORT_NAME = "shortName";

    IStatus configureNewProject( IDataModel dataModel, IFacetedProjectWorkingCopy facetedProject );

    String getBundleId();

    Object getDefaultProperty( String propertyName );

    String getDescription();

    String getDisplayName();

    IProjectFacet[] getFacets();

    URL getHelpUrl();

    String getId();

    Collection<String> getPropertyNames();

    String getRequiredSDKVersion();

    String getShortName();

    IStatus getUnsupportedSDKErrorMsg();

    boolean hasPropertyName( String propertyName );

    boolean isDefault();

    IStatus postProjectCreated( IDataModel dataModel, IFacetedProject facetedProject );

    void propertySet( String propertyName, Object propertyValue );

    void reinitialize();

    IStatus validate( final IDataModel model, final String propertyName );
}
