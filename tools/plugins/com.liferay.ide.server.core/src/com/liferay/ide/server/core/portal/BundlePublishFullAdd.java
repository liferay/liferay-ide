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

import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.server.core.LiferayServerCore;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;

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
 */
public class BundlePublishFullAdd extends BundlePublishOperation
{

    public BundlePublishFullAdd( IServer s, IModule[] modules, BundleSupervisor supervisor, BundleDTO[] existingBundles )
    {
        super( s, modules, supervisor, existingBundles );
    }

    private IStatus autoDeploy( IPath output ) throws CoreException
    {
        IStatus retval = null;

        final IPath autoDeployPath = portalRuntime.getPortalBundle().getAutoDeployPath();
        final IPath statePath = portalRuntime.getPortalBundle().getModulesPath().append( "state" );

        if( autoDeployPath.toFile().exists() )
        {
            try
            {
                FileUtil.writeFileFromStream(
                    autoDeployPath.append( output.lastSegment() ).toFile(), new FileInputStream( output.toFile() ) );

                retval = Status.OK_STATUS;
            }
            catch( IOException e )
            {
                retval = LiferayServerCore.error( "Unable to copy file to auto deploy folder", e );
            }
        }

        if( statePath.toFile().exists() )
        {
            FileUtil.deleteDir( statePath.toFile(), true );
        }

        return retval;
    }

    @Override
    public void execute( IProgressMonitor monitor, IAdaptable info ) throws CoreException
    {
        for( IModule module : modules )
        {
            IStatus retval = Status.OK_STATUS;

            if( module.getProject() == null )
            {
                continue;
            }

            final IBundleProject bundleProject = LiferayCore.create( IBundleProject.class, module.getProject() );

            if( bundleProject != null )
            {
                // TODO catch error in getOutputJar and show a popup notification instead

                monitor.subTask( "Building " + module.getName() + " output bundle..." );

                final IPath outputJar = bundleProject.getOutputBundle( true, monitor );

                if( outputJar!= null && outputJar.toFile().exists() )
                {
                    if( this.server.getServerState() == IServer.STATE_STARTED )
                    {
                        monitor.subTask( "Remotely deploying " + module.getName() + " to Liferay module framework..." );

                        retval = remoteDeploy( bundleProject.getSymbolicName(), outputJar, _existingBundles );
                    }
                    else
                    {
                        retval = autoDeploy( outputJar );
                    }
                }
                else
                {
                    retval = LiferayServerCore.error( "Could not create output jar" );
                }
            }
            else
            {
                retval =
                    LiferayServerCore.error( "Unable to get bundle project for " + module.getProject().getName() );
            }

            if( retval.isOK() )
            {
                this.portalServerBehavior.setModulePublishState2(
                    new IModule[] { module }, IServer.PUBLISH_STATE_NONE );
            }
            else
            {
                this.portalServerBehavior.setModulePublishState2(
                    new IModule[] { module }, IServer.PUBLISH_STATE_FULL );
            }
        }
    }

    private String getBundleUrl( File bundleFile, String bsn ) throws MalformedURLException
    {
        String bundleUrl = null;

        if( bundleFile.toPath().toString().toLowerCase().endsWith( ".war" ) )
        {
            bundleUrl = "webbundle:" + bundleFile.toURI().toURL().toExternalForm() + "?Web-ContextPath=/" + bsn;
        }
        else
        {
            bundleUrl = bundleFile.toURI().toURL().toExternalForm();
        }

        return bundleUrl;
    }

    private IStatus remoteDeploy( String bsn , IPath output, BundleDTO[] existingBundles )
    {
        IStatus retval = null;

        if( output != null && output.toFile().exists() )
        {
            try
            {
                BundleDTO deployed = _supervisor.deploy(
                    bsn, output.toFile(), getBundleUrl( output.toFile(), bsn ), existingBundles );

                retval = new Status( IStatus.OK, LiferayServerCore.PLUGIN_ID, (int) deployed.id, null, null );
            }
            catch( Exception e )
            {
                retval = LiferayServerCore.error( "Unable to deploy bundle remotely " +
                    output.toPortableString(), e );
            }
        }
        else
        {
            retval =
                LiferayServerCore.error( "Unable to deploy bundle remotely " +
                    output.toPortableString() );
        }

        return retval;
    }
}
