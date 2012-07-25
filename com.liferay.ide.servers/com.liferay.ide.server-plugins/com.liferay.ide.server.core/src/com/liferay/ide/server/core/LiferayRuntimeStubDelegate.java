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
 * Contributors:
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.server.core;

import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.model.RuntimeDelegate;

/**
 * @author Gregory Amerson
 */
public class LiferayRuntimeStubDelegate extends RuntimeDelegate implements ILiferayRuntime
{

    protected static final String PROP_STUB_TYPE_ID = "stub-type-id";
    // protected ILiferayRuntimeStub runtimeStub = null;
    protected IRuntimeWorkingCopy tempRuntime = null;

    public LiferayRuntimeStubDelegate()
    {
        super();
    }

    public IPath[] getAllUserClasspathLibraries()
    {
        return getLiferayRuntime().getAllUserClasspathLibraries();
    }

    public IPath getAppServerDir()
    {
        return getLiferayRuntime().getAppServerDir();
    }

    public String getAppServerType()
    {
        return getLiferayRuntime().getAppServerType();
    }

    // public ILiferayRuntimeStub getRuntimeStub() {
    // if ( runtimeStub == null ) {
    // runtimeStub = (ILiferayRuntimeStub) LiferayServerCorePlugin.getRuntimeStub( getRuntimeStubTypeId() );
    // }
    //
    // return runtimeStub;
    // }

    public IPath getDeployDir()
    {
        return getLiferayRuntime().getDeployDir();
    }

    public String getJavadocURL()
    {
        return getLiferayRuntime().getJavadocURL();
    }

    public IPath getLibGlobalDir()
    {
        return getLiferayRuntime().getLibGlobalDir();
    }

    public ILiferayRuntime getLiferayRuntime()
    {
        return (ILiferayRuntime) getTempRuntime().loadAdapter( ILiferayRuntime.class, new NullProgressMonitor() );
    }

    public String getName()
    {
        return getRuntime().getName();
    }

    public IPath getPortalDir()
    {
        return getLiferayRuntime().getPortalDir();
    }

    public String getPortalVersion()
    {
        return getLiferayRuntime().getPortalVersion();
    }

    public Properties getPortletCategories()
    {
        return getLiferayRuntime().getPortletCategories();
    }

    public IPath getRuntimeLocation()
    {
        return getRuntime().getLocation();
    }

    public String getRuntimeStubTypeId()
    {
        return getAttribute( PROP_STUB_TYPE_ID, "" );
    }

    public String[] getServletFilterNames()
    {
        return getLiferayRuntime().getServletFilterNames();
    }

    public IPath getSourceLocation()
    {
        return getLiferayRuntime().getSourceLocation();
    }

    public String[] getSupportedHookProperties()
    {
        return getLiferayRuntime().getSupportedHookProperties();
    }

    protected IRuntimeWorkingCopy getTempRuntime()
    {
        if( tempRuntime == null && getRuntime().getLocation() != null )
        {
            IRuntimeType runtimeType = ServerCore.findRuntimeType( getRuntimeStubTypeId() );
            try
            {
                tempRuntime = runtimeType.createRuntime( getRuntimeStubTypeId() + "-stub", new NullProgressMonitor() );
                tempRuntime.setLocation( getRuntime().getLocation() );
            }
            catch( CoreException e )
            {
                LiferayServerCorePlugin.logError( "Error creating runtime", e );
            }
        }

        if( tempRuntime.getLocation() == null || !( tempRuntime.getLocation().equals( getRuntime().getLocation() ) ) )
        {
            tempRuntime.setLocation( getRuntime().getLocation() );
        }

        return tempRuntime;
    }

    public IVMInstall getVMInstall()
    {
        return JavaRuntime.getDefaultVMInstall();
    }

    public boolean isUsingDefaultJRE()
    {
        return true;
    }

    public void setRuntimeStubTypeId( String typeId )
    {
        setAttribute( PROP_STUB_TYPE_ID, typeId );
        tempRuntime = null;
    }

    @Override
    public IStatus validate()
    {
        IStatus status = super.validate();

        if( !status.isOK() )
        {
            return status;
        }

        return ( (RuntimeDelegate) getTempRuntime().loadAdapter( RuntimeDelegate.class, new NullProgressMonitor() ) ).validate();
    }

}
