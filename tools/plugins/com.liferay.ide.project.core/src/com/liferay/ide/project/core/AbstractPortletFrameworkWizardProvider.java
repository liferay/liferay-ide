/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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
import java.util.Collections;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;

/**
 * @author Gregory Amerson
 */
public abstract class AbstractPortletFrameworkWizardProvider implements IPortletFrameworkWizardProvider
{
    protected String bundleId;
    protected String description;
    protected String displayName;
    protected URL helpUrl;
    protected String id;
    protected boolean isDefault;
    protected String requiredSDKVersion;
    protected String shortName;

    public String getBundleId()
    {
        return bundleId;
    }

    public Object getDefaultProperty( String propertyName )
    {
        return null;
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

    public Collection<String> getPropertyNames()
    {
        return Collections.emptySet();
    }

    public String getRequiredSDKVersion()
    {
        return requiredSDKVersion;
    }

    public String getShortName()
    {
        return shortName;
    }

    public IStatus getUnsupportedSDKErrorMsg()
    {
        return LiferayProjectCore.createErrorStatus( NLS.bind( Msgs.SDKVersionRequired, requiredSDKVersion ) );
    }

    public boolean hasPropertyName( String propertyName )
    {
        return false;
    }

    public boolean isDefault()
    {
        return isDefault;
    }

    public IStatus postProjectCreated( IDataModel dataModel, IFacetedProject facetedProject )
    {
        // do nothing;
        return Status.OK_STATUS;
    }

    public void propertySet( String propertyName, Object propertyValue )
    {
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

    public void setShortName( String shortName )
    {
        this.shortName = shortName;
    }

    public IStatus validate( final IDataModel model, final String propertyName )
    {
        return Status.OK_STATUS;
    }

    private static class Msgs extends NLS
    {
        public static String SDKVersionRequired;

        static
        {
            initializeMessages( AbstractPortletFrameworkWizardProvider.class.getName(), Msgs.class );
        }
    }
}
