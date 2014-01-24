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

import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;


/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public abstract class AbstractPortletFramework implements IPortletFramework
{

    private String bundleId;
    private String description;
    private String displayName;
    private URL helpUrl;
    private String id;
    private boolean isAdvanced;
    private boolean isDefault;
    private String requiredSDKVersion;
    private boolean requiresAdvanced;
    private String shortName;

    public String getBundleId()
    {
        return bundleId;
    }

    public String getDescription()
    {
        return description;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public IProjectFacet[] getFacets()
    {
        return new IProjectFacet[0];
    }

    public URL getHelpUrl()
    {
        return helpUrl;
    }

    public String getId()
    {
        return id;
    }

    public String getRequiredSDKVersion()
    {
        return requiredSDKVersion;
    }

    public String getShortName()
    {
        return shortName;
    }

    public boolean isAdvanced()
    {
        return isAdvanced;
    }

    public boolean isDefault()
    {
        return isDefault;
    }

    public boolean isRequiresAdvanced()
    {
        return this.requiresAdvanced;
    }

    public IStatus postProjectCreated( IProject project, String frameworkName, String portletName, IProgressMonitor monitor )
    {
        // by default do nothing;
        return Status.OK_STATUS;
    }

    public void setAdvanced( boolean adv )
    {
        this.isAdvanced = adv;
    }

    public void setBundleId( String bundleId )
    {
        this.bundleId = bundleId;
    }

    public void setDefault( boolean isDefault )
    {
        this.isDefault = isDefault;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public void setDisplayName( String displayName )
    {
        this.displayName = displayName;
    }

    public void setHelpUrl( URL helpUrl )
    {
        this.helpUrl = helpUrl;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public void setRequiredSDKVersion( String version )
    {
        this.requiredSDKVersion = version;
    }

    public void setRequiresAdvanced( boolean adv )
    {
        this.requiresAdvanced = adv;
    }

    public void setShortName( String shortName )
    {
        this.shortName = shortName;
    }

    @Override
    public String toString()
    {
        return getShortName();
    }
}