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

package com.liferay.ide.service.core.job;

import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.sdk.SDK;
import com.liferay.ide.sdk.job.SDKJob;
import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.service.core.ServiceCore;

import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.ClasspathEntry;
import org.eclipse.jst.common.jdt.internal.classpath.FlexibleProjectContainer;
import org.eclipse.jst.j2ee.internal.common.classpath.J2EEComponentClasspathContainerUtils;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public class BuildServiceJob extends SDKJob
{

    protected IFile serviceXmlFile;

    public BuildServiceJob( IFile serviceXmlFile )
    {
        super( "Build services" );

        this.serviceXmlFile = serviceXmlFile;
        setUser( true );
        setProject( serviceXmlFile.getProject() );
    }

    @Override
    protected IStatus run( IProgressMonitor monitor )
    {
        IStatus retval = null;

        if( getProject() == null )
        {
            return ServiceCore.createErrorStatus( "This action can only be executed from a Liferay project.  Use Liferay project import wizard to import the project before continuing to build services." );
        }

        if( !ProjectUtil.isLiferayProject( getProject() ) )
        {
            return ServiceCore.createErrorStatus( MessageFormat.format(
                "This action is unavailable because {0} is not a Liferay plugin project. Use \"Convert to Liferay project\" context-menu action and then run again.",
                getProject().getName() ) );
        }

        monitor.beginTask( "Building Liferay services...", 100 );

        try
        {
            getWorkspace().run( new IWorkspaceRunnable()
            {
                public void run( IProgressMonitor monitor ) throws CoreException
                {
                    runBuildService( monitor );

                    try
                    {
                        getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor);
                    }
                    catch( Exception e )
                    {
                        ServiceCore.logError( e );
                    }

                    ResourcesPlugin.getWorkspace().build( IncrementalProjectBuilder.INCREMENTAL_BUILD, monitor );

                    updateClasspath( project );
                }
            }, monitor );

            try
            {
                getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor);
            }
            catch( Exception e )
            {
                ServiceCore.logError( e );
            }
        }
        catch( CoreException e1 )
        {
            retval = ServiceCore.createErrorStatus( e1 );
        }

        return retval == null || retval.isOK() ? Status.OK_STATUS : retval;
    }

    protected void runBuildService( IProgressMonitor monitor ) throws CoreException
    {
        SDK sdk = getSDK();

        if( sdk == null )
        {
            throw new CoreException(
                ServiceCore.createErrorStatus( "Could not determine the SDK name for project. Specify correct SDK in Liferay Project Properties page" ) );
        }

        monitor.worked( 50 );

        sdk.buildService( getProject(), serviceXmlFile, null, ServerUtil.configureAppServerProperties( project ) );

        monitor.worked( 90 );
    }

    protected IStatus updateClasspath( IProject project ) throws CoreException
    {
        FlexibleProjectContainer container =
            J2EEComponentClasspathContainerUtils.getInstalledWebAppLibrariesContainer( project );

        if( container == null )
        {
            return ServiceCore.createWarningStatus( "Could not update classpath containers" );
        }

        container.refresh();

        container = J2EEComponentClasspathContainerUtils.getInstalledWebAppLibrariesContainer( project );

        IClasspathEntry[] webappEntries = container.getClasspathEntries();
        
        //TODO IDE-648 IDE-110

        for( IClasspathEntry entry2 : webappEntries )
        {
            if( entry2.getPath().lastSegment().equals( getProject().getName() + "-service.jar" ) )
            {
                ( (ClasspathEntry) entry2 ).sourceAttachmentPath =
                    getProject().getFolder( "docroot/WEB-INF/service" ).getFullPath();

                break;
            }
        }

        ClasspathContainerInitializer initializer =
            JavaCore.getClasspathContainerInitializer( "org.eclipse.jst.j2ee.internal.web.container" );

        IJavaProject javaProject = JavaCore.create( project );

        initializer.requestClasspathContainerUpdate( container.getPath(), javaProject, container );

        return Status.OK_STATUS;
    }

}
