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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.model.RuntimeDelegate;


/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class PortalRuntime extends RuntimeDelegate implements ILiferayRuntime, PropertyChangeListener
{
    protected static final String PROP_VM_INSTALL_TYPE_ID = "vm-install-type-id";
    protected static final String PROP_VM_INSTALL_ID = "vm-install-id";

    private PortalBundle portalBundle;

    @Override
    public void dispose()
    {
        super.dispose();

        if( this.getRuntimeWorkingCopy() != null )
        {
            this.getRuntimeWorkingCopy().removePropertyChangeListener( this );
        }
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

    public PortalBundle getPortalBundle()
    {
        if( this.portalBundle == null )
        {
            initPortalBundle();
        }

        return this.portalBundle;
    }

    public String getPortalVersion()
    {
        return getPortalBundle().getVersion();
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

    public IVMInstall getVMInstall()
    {
        if( getVMInstallTypeId() == null )
        {
           return JavaRuntime.getDefaultVMInstall();
        }

        try
        {
            IVMInstallType vmInstallType = JavaRuntime.getVMInstallType( getVMInstallTypeId() );
            IVMInstall[] vmInstalls = vmInstallType.getVMInstalls();
            int size = vmInstalls.length;
            String id = getVMInstallId();

            for( int i = 0; i < size; i++ )
            {
                if( id.equals( vmInstalls[i].getId() ) )
                {
                    return vmInstalls[i];
                }
            }
        }
        catch( Exception e )
        {
            // ignore
        }
        return null;
    }

    protected String getVMInstallId() 
    {
        return getAttribute(PROP_VM_INSTALL_ID, (String)null);
    }

    protected String getVMInstallTypeId() 
    {
        return getAttribute(PROP_VM_INSTALL_TYPE_ID, (String)null);
    }

    @Override
    public void setDefaults( IProgressMonitor monitor )
    {
        IRuntimeType type = getRuntimeWorkingCopy().getRuntimeType();
        getRuntimeWorkingCopy().setLocation( new Path( LiferayServerCore.getPreference( "location." + type.getId() ) ) );
    }

    public void setVMInstall( IVMInstall vmInstall )
    {
        if( vmInstall == null )
        {
            setVMInstall( null, null );
        }
        else
        {
            setVMInstall( vmInstall.getVMInstallType().getId(), vmInstall.getId() );
        }
    }

    protected void setVMInstall( String typeId, String id )
    {
        if( typeId == null )
        {
            setAttribute( PROP_VM_INSTALL_TYPE_ID, (String) null );
        }
        else
        {
            setAttribute( PROP_VM_INSTALL_TYPE_ID, typeId );
        }

        if( id == null )
        {
            setAttribute( PROP_VM_INSTALL_ID, (String) null );
        }
        else
        {
            setAttribute( PROP_VM_INSTALL_ID, id );
        }
    }

    @Override
    protected void initialize()
    {
        super.initialize();

        if( this.getRuntimeWorkingCopy() != null )
        {
            this.getRuntimeWorkingCopy().addPropertyChangeListener( this );
        }

        if( this.portalBundle == null )
        {
            initPortalBundle();
        }
    }

    private void initPortalBundle()
    {
        if( this.getRuntime().getLocation() != null )
        {
            final PortalBundleFactory[] factories = LiferayServerCore.getPortalBundleFactories();

            for( PortalBundleFactory factory : factories )
            {
                final IPath path = factory.canCreateFromPath( this.getRuntime().getLocation() );

                if( path != null )
                {
                    this.portalBundle = factory.create( path );
                    return;
                }
            }
        }
    }

    public boolean isUsingDefaultJRE()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void propertyChange( PropertyChangeEvent evt )
    {
        if( "location".equals( evt.getPropertyName() ) )
        {
            this.portalBundle = null;

            if( evt.getNewValue() != null )
            {
                initPortalBundle();
            }
        }
    }

    @Override
    public IStatus validate()
    {
        return Status.OK_STATUS;
    }
}