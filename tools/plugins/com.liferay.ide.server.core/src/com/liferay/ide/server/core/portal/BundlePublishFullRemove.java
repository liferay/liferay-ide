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
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.osgi.framework.dto.BundleDTO;

/**
 * @author Gregory Amerson
 * @author Andy Wu
 */
public class BundlePublishFullRemove extends BundlePublishOperation
{

    public BundlePublishFullRemove( IServer server, IModule[] modules, BundleSupervisor supervisor, BundleDTO[] existingBundles )
    {
        super( server, modules, supervisor, existingBundles );
    }

    @Override
    public void execute( IProgressMonitor monitor, IAdaptable info ) throws CoreException
    {
        for( IModule module : modules )
        {
            IProject project = module.getProject();

            if( project == null )
            {
                continue;
            }

            IStatus status = null;

            final IBundleProject bundleProject = LiferayCore.create( IBundleProject.class, project );

            if( bundleProject != null )
            {
                final String symbolicName = bundleProject.getSymbolicName();

                if( this.server.getServerState() == IServer.STATE_STARTED )
                {
                    monitor.subTask( "Remotely undeploying " + module.getName() + " from Liferay module framework..." );

                    status = remoteUninstall( bundleProject, symbolicName , monitor);
                }

                if( status == null || status.isOK() ) // remote uninstall succeedded
                {
                    status = localUninstall( bundleProject, symbolicName );
                }

                if( status.isOK() )
                {
                    this.portalServerBehavior.setModulePublishState2(
                        new IModule[] { module }, IServer.PUBLISH_STATE_NONE );

                    project.deleteMarkers( LiferayServerCore.BUNDLE_OUTPUT_ERROR_MARKER_TYPE, false, 0 );
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
            IPath outputFile = bundleProject.getOutputBundle( false, null );

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

    private IStatus remoteUninstall( IBundleProject bundleProject , String symbolicName, IProgressMonitor monitor ) throws CoreException
    {
        IStatus retval = null;

        IPath outputJar = bundleProject.getOutputBundle( false, monitor );

        String fragmentHostName = ServerUtil.getFragemtHostName( outputJar.toFile() );

        boolean isFragment = (fragmentHostName != null);

        if( symbolicName != null && _existingBundles != null && _supervisor != null )
        {
            try
            {
                for( BundleDTO bundle : _existingBundles )
                {
                    if( symbolicName.equals( bundle.symbolicName ) )
                    {
                        String error = _supervisor.getAgent().uninstall( bundle.id );

                        if( isFragment )
                        {
                            _supervisor.refreshHostBundle( fragmentHostName, _existingBundles );
                        }

                        if( error == null )
                        {
                            retval = Status.OK_STATUS;
                        }
                        else
                        {
                            retval = LiferayServerCore.error( "Unable to uninstall bundle " + error );
                        }

                        break;
                    }
                }
            }
            catch( Exception e )
            {
                retval = LiferayServerCore.error( "Unable to uninstall bundle " + symbolicName, e );
            }
        }

        if( retval == null )
        {
            retval = Status.OK_STATUS;
        }

        return retval;
    }

}
