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
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.server.core.LiferayServerCore;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Gregory Amerson
 */
public class BundlePublishFullAdd implements PublishOp
{
    private final PortalRuntime portalRuntime;
    private final IServer server;

    public BundlePublishFullAdd( IServer s )
    {
        this.server = s;
        this.portalRuntime = (PortalRuntime) this.server.getRuntime().loadAdapter( PortalRuntime.class, null );

        if( this.portalRuntime == null )
        {
            throw new IllegalArgumentException( "Could not get portal runtime from server " + s.getName() );
        }
    }

    private IStatus autoDeploy( IFile output ) throws CoreException
    {
        IStatus retval = null;

        final IPath autoDeployPath = portalRuntime.getPortalBundle().getAutoDeployPath();

        if( autoDeployPath.toFile().exists() )
        {
            try
            {
                FileUtil.writeFileFromStream(
                    autoDeployPath.append( output.getName() ).toFile(), output.getContents( true ) );

                retval = Status.OK_STATUS;
            }
            catch( IOException e )
            {
                retval = LiferayServerCore.error( "Unable to copy file to auto deploy folder", e );
            }
        }

        return retval;
    }

    public IStatus publish( IModule module, IProgressMonitor monitor ) throws CoreException
    {
        IStatus retval = Status.OK_STATUS;

        final ILiferayProject project = LiferayCore.create( module.getProject() );
        final IBundleProject bundleProject = project.adapt( IBundleProject.class );

        if( bundleProject != null )
        {
            //TODO catch error in getOUtputJar and show a popup notification instead
            final IFile outputJar = bundleProject.getOutputJar( true, monitor );

            if( outputJar.exists() )
            {
                if( this.server.getServerState() == IServer.STATE_STARTED )
                {
                    retval = remoteDeploy( outputJar );
                }
                else
                {
                    retval = autoDeploy( outputJar );
                }
            }
        }
        else
        {
            retval = LiferayServerCore.error( "Unable to get bundle project for " + module.getProject().getName() );
        }

        return retval;
    }

    private IStatus remoteDeploy( IFile output )
    {
        IStatus retval = null;

        final OsgiConnection osgi = LiferayServerCore.newOsgiConnection( this.server );

        final IPath rawLocation = output.getRawLocation();

        if( rawLocation != null )
        {
            retval = osgi.instalBundle( rawLocation.toPortableString(), rawLocation.toFile() );
        }
        else
        {
            retval =
                LiferayServerCore.error( "Uninstall to deploy file remotely " + output.getLocation().toPortableString() );
        }

        return retval;
    }
}