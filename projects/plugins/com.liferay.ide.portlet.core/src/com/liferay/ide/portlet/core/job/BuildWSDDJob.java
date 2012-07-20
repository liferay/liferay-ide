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
 *******************************************************************************/

package com.liferay.ide.portlet.core.job;

import com.liferay.ide.portlet.core.PortletCore;
import com.liferay.ide.sdk.SDK;
import com.liferay.ide.sdk.job.SDKJob;
import com.liferay.ide.server.util.ServerUtil;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @author Greg Amerson
 */
public class BuildWSDDJob extends SDKJob
{

    protected IFile serviceXmlFile;

    public BuildWSDDJob( IFile serviceXmlFile )
    {
        super( "Build web services descriptor" );

        this.serviceXmlFile = serviceXmlFile;

        setUser( true );

        setProject( serviceXmlFile.getProject() );
    }

    @Override
    protected IStatus run( IProgressMonitor monitor )
    {
        IStatus retval = null;

        monitor.beginTask( "Building Liferay web services deployment descriptor...", 100 );

        try
        {
            getWorkspace().run( new IWorkspaceRunnable()
            {

                public void run( IProgressMonitor monitor ) throws CoreException
                {

                    SDK sdk = getSDK();

                    monitor.worked( 10 );

                    Map<String, String> appServerProperties = ServerUtil.configureAppServerProperties( project );

                    sdk.buildWSDD( getProject(), serviceXmlFile, null, appServerProperties );

                    monitor.worked( 90 );

                    final IProject project = getProject();

                    project.refreshLocal( IResource.DEPTH_INFINITE, monitor );
                    project.build( IncrementalProjectBuilder.INCREMENTAL_BUILD, monitor );

                    project.refreshLocal( IResource.DEPTH_INFINITE, monitor );
                }
            }, monitor );

            getProject().refreshLocal( IResource.DEPTH_INFINITE, monitor );
        }
        catch( CoreException e1 )
        {
            retval = PortletCore.createErrorStatus( e1 );
        }

        return retval == null || retval.isOK() ? Status.OK_STATUS : retval;
    }

}
