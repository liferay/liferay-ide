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

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.sdk.core.ISDKConstants;
import com.liferay.ide.sdk.core.SDK;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.ClasspathEntry;
import org.eclipse.jst.common.jdt.internal.classpath.FlexibleProjectContainer;
import org.eclipse.jst.j2ee.internal.common.classpath.J2EEComponentClasspathContainerUtils;
import org.eclipse.osgi.util.NLS;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
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

    @Override
    public IStatus buildLang( IFile langFile, IProgressMonitor monitor ) throws CoreException
    {
        IStatus status = sdk.validate();

        if ( !status.isOK())
        {
            return status;
        }

        return sdk.buildLanguage(
            getProject(), langFile, null, monitor );
    }

    @Override
    public IStatus buildService( IProgressMonitor monitor ) throws CoreException
    {
        final IFile servicesFile = getDocrootFile( "WEB-INF/" + ILiferayConstants.SERVICE_XML_FILE );

        if( servicesFile != null && servicesFile.exists() )
        {
            final IProgressMonitor sub = new SubProgressMonitor( monitor, 100 );

            sub.beginTask( Msgs.buildingServices, 100 );

            return buildService( servicesFile, sub );
        }

        return Status.OK_STATUS;
    }

    private IStatus buildService( IFile serviceXmlFile, IProgressMonitor monitor ) throws CoreException
    {
        IStatus status = sdk.validate();

        if ( !status.isOK() )
        {
            return status;
        }

        IStatus retval =
            sdk.buildService(
                getProject(), serviceXmlFile, null );

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

    @Override
    public IStatus buildWSDD( IProgressMonitor monitor ) throws CoreException
    {
        final IFile servicesFile = getDocrootFile( "WEB-INF/" + ILiferayConstants.SERVICE_XML_FILE );

        if( servicesFile != null && servicesFile.exists() )
        {
            final IProgressMonitor sub = new SubProgressMonitor( monitor, 100 );

            sub.beginTask( Msgs.buildingServices, 100 );

            return buildWSDD( servicesFile, sub );
        }

        return Status.OK_STATUS;
    }

    private IStatus buildWSDD( IFile serviceXmlFile, IProgressMonitor monitor ) throws CoreException
    {
        IStatus status = sdk.validate();

        if ( !status.isOK() )
        {
            return status;
        }

        IStatus retval =
            sdk.buildWSDD( getProject(), serviceXmlFile, null );

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

    protected static class Msgs extends NLS
    {

        public static String buildingServices;
        public static String buildingWSDD;

        static
        {
            initializeMessages( SDKProjectBuilder.class.getName(), Msgs.class );
        }
    }

    @Override
    public IStatus creatInitBundle( IProject project, String taskName, String bundleUrl, IProgressMonitor monitor )
        throws CoreException
    {
        return Status.OK_STATUS;
    }
}
