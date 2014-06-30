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

package com.liferay.ide.project.core;

import com.liferay.ide.sdk.core.ISDKConstants;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.server.util.ServerUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
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
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class SDKProjectBuilder extends AbstractProjectBuilder
{

    private SDK sdk;

    public SDKProjectBuilder( IProject project, SDK sdk )
    {
        super( project );
        this.sdk = sdk;
    }

    public IStatus buildLang( IFile langFile, IProgressMonitor monitor ) throws CoreException
    {
        return sdk.buildLanguage(
            getProject(), langFile, null, ServerUtil.configureAppServerProperties( getProject() ), monitor );
    }

    public IStatus buildService( IFile serviceXmlFile, IProgressMonitor monitor ) throws CoreException
    {
        IStatus retval =
            sdk.buildService(
                getProject(), serviceXmlFile, null, ServerUtil.configureAppServerProperties( getProject() ) );

        try
        {
            getProject().refreshLocal( IResource.DEPTH_INFINITE, monitor );
        }
        catch( Exception e )
        {
            retval = ProjectCore.createErrorStatus( e );
        }

        ResourcesPlugin.getWorkspace().build( IncrementalProjectBuilder.INCREMENTAL_BUILD, monitor );

        updateClasspath( getProject() );

        getProject().refreshLocal( IResource.DEPTH_INFINITE, monitor );

        return retval;
    }

    public IStatus buildWSDD( IFile serviceXmlFile, IProgressMonitor monitor ) throws CoreException
    {
        IStatus retval =
            sdk.buildWSDD( getProject(), serviceXmlFile, null, ServerUtil.configureAppServerProperties( getProject() ) );

        try
        {
            getProject().refreshLocal( IResource.DEPTH_INFINITE, monitor );
        }
        catch( Exception e )
        {
            retval = ProjectCore.createErrorStatus( e );
        }

        getProject().build( IncrementalProjectBuilder.INCREMENTAL_BUILD, monitor );

        try
        {
            getProject().refreshLocal( IResource.DEPTH_INFINITE, monitor );
        }
        catch( Exception e )
        {
            ProjectCore.logError( e );
        }

        return retval;
    }

    protected IStatus updateClasspath( IProject project ) throws CoreException
    {
        FlexibleProjectContainer container =
            J2EEComponentClasspathContainerUtils.getInstalledWebAppLibrariesContainer( project );

        if( container == null )
        {
            return Status.OK_STATUS;
        }

        container.refresh();

        container = J2EEComponentClasspathContainerUtils.getInstalledWebAppLibrariesContainer( project );

        IClasspathEntry[] webappEntries = container.getClasspathEntries();

        for( IClasspathEntry entry2 : webappEntries )
        {
            if( entry2.getPath().lastSegment().equals( getProject().getName() + "-service.jar" ) ) //$NON-NLS-1$
            {
                ( (ClasspathEntry) entry2 ).sourceAttachmentPath =
                    getProject().getFolder( ISDKConstants.DEFAULT_DOCROOT_FOLDER + "/WEB-INF/service" ).getFullPath(); //$NON-NLS-1$

                break;
            }
        }

        ClasspathContainerInitializer initializer =
            JavaCore.getClasspathContainerInitializer( "org.eclipse.jst.j2ee.internal.web.container" ); //$NON-NLS-1$

        IJavaProject javaProject = JavaCore.create( project );

        initializer.requestClasspathContainerUpdate( container.getPath(), javaProject, container );

        return Status.OK_STATUS;
    }

}
