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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.core.LiferayServerCore;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
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
import org.eclipse.jdt.launching.IVMInstall2;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.model.RuntimeDelegate;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 * @author Yuqiang Wang
 */
public class PortalRuntime extends RuntimeDelegate implements ILiferayRuntime, PropertyChangeListener
{
    static final String PROP_VM_INSTALL_ID = "vm-install-id";
    static final String PROP_VM_INSTALL_TYPE_ID = "vm-install-type-id";
    public static final String ID = "com.liferay.ide.server.portal.runtime";

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
        return getPortalBundle().getAppServerDeployDir();
    }

    public IPath getAppServerDir()
    {
        return getPortalBundle().getAppServerDir();
    }

    public IPath getAppServerLibGlobalDir()
    {
        return getPortalBundle().getAppServerLibGlobalDir();
    }

    public IPath getAppServerPortalDir()
    {
        return getPortalBundle().getAppServerPortalDir();
    }

    public String getAppServerType()
    {
        return getPortalBundle().getType();
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
        PortalBundle portalBundle = getPortalBundle();

        if( portalBundle != null )
        {
            return portalBundle.getLiferayHome();
        }
        else
        {
            return null;
        }
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
        PortalBundle tempPortalBundle = getPortalBundle();

        if( tempPortalBundle != null )
        {
            return tempPortalBundle.getVersion();
        }

        return null;
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
        return getRuntime().getLocation();
    }

    public IPath getSourceLocation()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public IPath[] getUserLibs()
    {
        return this.portalBundle.getUserLibs();
    }

    public IVMInstall getVMInstall()
    {
        if( getVMInstallTypeId() == null )
        {
           return JavaRuntime.getDefaultVMInstall();
        }

        try
        {
            final IVMInstallType vmInstallType = JavaRuntime.getVMInstallType( getVMInstallTypeId() );
            final IVMInstall[] vmInstalls = vmInstallType.getVMInstalls();
            final int size = vmInstalls.length;
            final String id = getVMInstallId();

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
        return getVMInstallTypeId() == null;
    }

    private boolean isVMRequireVersion( String javaVersion, int requireVersion )
    {
        Integer version = null;
        int index = javaVersion.indexOf( '.' );

        if( index > 0 )
        {
            try
            {
                int major = Integer.parseInt( javaVersion.substring( 0, index ) ) * 100;
                index++;
                int index2 = javaVersion.indexOf( '.', index );

                if( index2 > 0 )
                {
                    int minor = Integer.parseInt( javaVersion.substring( index, index2 ) );
                    version = new Integer( major + minor );
                }
            }
            catch( NumberFormatException e )
            {
                // Ignore
            }
        }

        // If we have a version, and it isn't equal to the required version, fail the check
        if( version != null && version.intValue() != requireVersion )
        {
            return false;
        }

        return true;
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
    public void setDefaults( IProgressMonitor monitor )
    {
        final IRuntimeType type = getRuntimeWorkingCopy().getRuntimeType();

        if( type != null )
        {
            getRuntimeWorkingCopy().setLocation(
                new Path( LiferayServerCore.getPreference( "location." + type.getId() ) ) );
        }
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
    public IStatus validate()
    {
        IStatus status = super.validate();

        if( !status.isOK() )
        {
            return status;
        }

        if ( portalBundle == null)
        {
            return new Status( IStatus.ERROR, LiferayServerCore.PLUGIN_ID, 0, Msgs.errorPortalNotExisted, null );
        }

        if( !portalBundle.getVersion().startsWith( "7" ) )
        {
            return new Status( IStatus.ERROR, LiferayServerCore.PLUGIN_ID, 0, Msgs.errorPortalVersion70, null );
        }

        if( getVMInstall() == null )
        {
            return new Status( IStatus.ERROR, LiferayServerCore.PLUGIN_ID, 0, Msgs.errorJRE, null );
        }

        if( portalBundle.getVersion().startsWith( "7" ) )
        {
            IVMInstall vmInstall = getVMInstall();

            if( vmInstall instanceof IVMInstall2 )
            {
                String javaVersion = ( (IVMInstall2) vmInstall ).getJavaVersion();

                if( javaVersion != null && !isVMRequireVersion( javaVersion, 108 ) )
                {
                    return new Status( IStatus.ERROR, LiferayServerCore.PLUGIN_ID, 0, Msgs.errorJRE80, null );
                }
            }
        }

        File jdkInstallLocation = getVMInstall().getInstallLocation();

        if( jdkInstallLocation != null )
        {
            String rootPath = jdkInstallLocation.getAbsolutePath();

            StringBuilder javacPath = new StringBuilder(rootPath);

            javacPath.append( File.separator ).append( "bin" ).append( File.separator );

            if( CoreUtil.isWindows() )
            {
                javacPath.append( "javac.exe" );
            }
            else if( CoreUtil.isLinux() || CoreUtil.isMac() )
            {
                javacPath.append( "javac" );
            }

            if( FileUtil.notExists( new File( javacPath.toString() ) ) )
            {
                return new Status( IStatus.WARNING, LiferayServerCore.PLUGIN_ID, 0, Msgs.warningjre, null );
            }
        }

        return Status.OK_STATUS;
    }

    private static class Msgs extends NLS
    {
        public static String errorJRE;
        public static String errorJRE80;
        public static String errorPortalVersion70;
        public static String errorPortalNotExisted;
        public static String warningjre;

        static
        {
            initializeMessages( PortalRuntime.class.getName(), Msgs.class );
        }
    }
}