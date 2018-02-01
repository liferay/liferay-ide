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
import com.liferay.ide.server.core.gogo.GogoBundleDeployer;

import org.eclipse.core.resources.IProject;
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
 * @author Andy Wu
 * @author Terry Jia
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
            IProject project = module.getProject();

            if( project == null )
            {
                continue;
            }

            IStatus status = Status.OK_STATUS;

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

    private IStatus remoteUninstall( IBundleProject bundleProject, String symbolicName, IProgressMonitor monitor ) throws CoreException
    {
        //TODO we should get bsn first and try use the bsn to uninstall
        IStatus retval = null;

        IPath outputJar = bundleProject.getOutputBundlePath();

        GogoBundleDeployer bundleDeployer = null;

        try
        {
            bundleDeployer = createBundleDeployer();

            String error = bundleDeployer.uninstall( bundleProject, outputJar );

            if( error == null )
            {
                retval = Status.OK_STATUS;
            }
            else
            {
                retval = LiferayServerCore.error( "Unable to uninstall bundle " + error );
            }

        }
        catch( Exception e )
        {
            retval = LiferayServerCore.error( "Unable to uninstall bundle " + symbolicName, e );
        }

        if( retval == null )
        {
            retval = Status.OK_STATUS;
        }

        return retval;
    }

}
