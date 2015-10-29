/*******************************************************************************
 * Copyright (c) 2000-present Liferay, Inc. All riproghts reserved.
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

import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.server.core.LiferayServerCore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Gregory Amerson
 */
public class BundlePublishFullRemove extends BundlePublishOperation
{

    public BundlePublishFullRemove( IServer server, IModule[] modules )
    {
        super( server, modules );
    }

    @Override
    public void execute( IProgressMonitor monitor, IAdaptable info ) throws CoreException
    {
        for( IModule module : modules )
        {
            if( module.getProject() == null )
            {
                continue;
            }

            IStatus status = null;

            final IBundleProject bundleProject = LiferayCore.create( IBundleProject.class, module.getProject() );

            if( bundleProject != null )
            {
                final String symbolicName = bundleProject.getSymbolicName();

                if( this.server.getServerState() == IServer.STATE_STARTED )
                {
                    status = remoteUninstall( bundleProject, symbolicName );
                }

                if( status == null || status.isOK() ) // remote uninstall succeedded
                {
                    status = localUninstall( bundleProject, symbolicName );
                }

                if( status.isOK() )
                {
                    this.portalServerBehavior.setModulePublishState2(
                        new IModule[] { module }, IServer.PUBLISH_STATE_NONE );
                }
            }
            else
            {
                status =
                    LiferayServerCore.error( "Could not get module publisher for project " +
                        module.getProject().getName() );
            }

            if( !status.isOK() )
            {
                throw new CoreException( status );
            }
        }
    }

    private void findFilesInPath( final File dir, final String pattern, List<File> retval  )
    {
        if( dir.exists() && dir.isDirectory() )
        {
            final File[] files = dir.listFiles();

            for( File f : files )
            {
                if( f.getName().contains( pattern ) && !retval.contains( f ) )
                {
                    retval.add( f );
                }
                else if( f.isDirectory() )
                {
                    findFilesInPath( f, pattern, retval );
                }
            }
        }
    }

    private IStatus localUninstall( IBundleProject bundleProject , String symbolicName )
    {
        IStatus retval = null;

        final PortalRuntime runtime = (PortalRuntime) server.getRuntime().loadAdapter( PortalRuntime.class, null );


        final List<File> moduleFiles = new ArrayList<File>();

        // TODO this may not always match
        final IPath modulesPath = runtime.getPortalBundle().getModulesPath();
        findFilesInPath( modulesPath.toFile(), symbolicName, moduleFiles );

        final IPath deployPath = runtime.getPortalBundle().getAutoDeployPath();
        findFilesInPath( deployPath.toFile(), symbolicName, moduleFiles );

        final IPath appServerDeployPath = runtime.getPortalBundle().getAppServerDeployDir();
        findFilesInPath( appServerDeployPath.toFile(), symbolicName, moduleFiles );

        try
        {
            IPath outputFile = bundleProject.getOutputJar( false, null );

            findFilesInPath( modulesPath.toFile(), outputFile.lastSegment(), moduleFiles );
            findFilesInPath( deployPath.toFile(), outputFile.lastSegment(), moduleFiles );
            findFilesInPath( appServerDeployPath.toFile(), symbolicName, moduleFiles );
        }
        catch( CoreException e )
        {
        }

        // look for wabs that have been deployed
        final IPath appServerDeployDir = runtime.getPortalBundle().getAppServerDeployDir();
        findFilesInPath( appServerDeployDir.toFile(), symbolicName, moduleFiles );

        if( moduleFiles.size() > 0 )
        {
            // TODO convert to multi-statuses
            for( File moduleFile : moduleFiles )
            {
                if( moduleFile.isDirectory() )
                {
                    FileUtil.deleteDir( moduleFile, true );
                }
                else
                {
                    if( moduleFile.exists() && moduleFile.delete() )
                    {
                        retval = Status.OK_STATUS;
                    }
                    else
                    {
                        retval = LiferayServerCore.error( "Could not delete module file " + moduleFile.getName() );
                    }
                }
            }
        }

        if( retval == null )
        {
            LiferayServerCore.logInfo( "No module to remove " + symbolicName );

            retval = Status.OK_STATUS;
        }

        return retval;
    }

    private IStatus remoteUninstall( IBundleProject bundleProject , String symbolicName )
    {
        IStatus retval = null;

        final BundleDeployer deployer = getBundleDeployer();

        if( deployer != null )
        {
            try
            {
                deployer.uninstall( symbolicName );
                retval = Status.OK_STATUS;
            }
            catch( Exception e )
            {
                retval = LiferayServerCore.error( "Unable to uninstall bundle" + symbolicName, e );
            }
        }
        else
        {
            retval = LiferayServerCore.error( "Unable to uninstall bundle" + symbolicName );
        }

        return retval;
    }

}
