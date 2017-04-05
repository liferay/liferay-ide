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
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.util.ServerUtil;

import java.io.IOException;

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

    public BundlePublishFullRemove( IServer server, IModule[] modules, BundleDTO[] existingBundles )
    {
        super( server, modules, existingBundles );
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

                if( status != null && status.isOK() )
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

    private IStatus remoteUninstall( IBundleProject bundleProject , String symbolicName, IProgressMonitor monitor ) throws CoreException
    {
        IStatus retval = null;

        IPath outputJar = bundleProject.getOutputBundle( false, monitor );

        String fragmentHostName = ServerUtil.getFragemtHostName( outputJar.toFile() );

        boolean isFragment = (fragmentHostName != null);

        if( symbolicName != null && _existingBundles != null )
        {
            BundleSupervisor bundleSupervisor = null;

            try
            {
                bundleSupervisor = createBundleSupervisor();

                for( BundleDTO bundle : _existingBundles )
                {
                    if( symbolicName.equals( bundle.symbolicName ) )
                    {
                        String error = bundleSupervisor.getAgent().uninstall( bundle.id );

                        if( isFragment )
                        {
                            bundleSupervisor.refreshHostBundle( fragmentHostName, _existingBundles );
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
            finally
            {
                if( bundleSupervisor != null )
                {
                    try
                    {
                        bundleSupervisor.close();
                    }
                    catch( IOException e )
                    {
                    }
                }
            }
        }

        if( retval == null )
        {
            retval = Status.OK_STATUS;
        }

        return retval;
    }

}
