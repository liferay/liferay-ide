/*******************************************************************************
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
 *
 *******************************************************************************/
package com.liferay.ide.server.core.portal;

import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.core.LiferayServerCore;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.wst.server.core.model.RuntimeDelegate;


/**
 * @author Gregory Amerson
 */
public class PortalRuntime extends RuntimeDelegate implements ILiferayRuntime
{
    public static final String PROP_PORTAL_BUNDLE_TYPE = "portal-bundle-type";

    public PortalRuntime()
    {
        super();
    }

    @Override
    public IStatus validate()
    {
        return Status.OK_STATUS;
    }

    public IVMInstall getVMInstall()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isUsingDefaultJRE()
    {
        // TODO Auto-generated method stub
        return false;
    }

    public IPath getAppServerDeployDir()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public IPath getAppServerDir()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public IPath getAppServerLibGlobalDir()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public IPath getAppServerPortalDir()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getAppServerType()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String[] getHookSupportedProperties()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getJavadocURL()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public IPath getLiferayHome()
    {
        return getPortalBundle().getLiferayHome();
    }

    public String getPortalVersion()
    {
        return "7.0.0";
    }

    public Properties getPortletCategories()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Properties getPortletEntryCategories()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public IPath getRuntimeLocation()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public IPath getSourceLocation()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public IPath[] getUserLibs()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<IRuntimeClasspathEntry> getRuntimeClasspathEntries()
    {
        final List<IRuntimeClasspathEntry> entries = new ArrayList<IRuntimeClasspathEntry>();

        final IPath[] paths = getPortalBundle().getRuntimeClasspath();

        for( IPath path : paths )
        {
            if( path.toFile().exists() )
            {
                entries.add( JavaRuntime.newArchiveRuntimeClasspathEntry( path ) );
            }
        }

        return entries;
    }

    public PortalBundle getPortalBundle()
    {
        return LiferayServerCore.getPortalBundle( this, getPortalBundleType() );
    }

    public String getPortalBundleType()
    {
        return getAttribute( PROP_PORTAL_BUNDLE_TYPE, (String) null );
    }

    public void setPortalBundleType( String type )
    {
        if( type != null )
        {
            setAttribute( PROP_PORTAL_BUNDLE_TYPE, type );
        }
    }

}
