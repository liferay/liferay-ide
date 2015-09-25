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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.MarkerUtil;
import com.liferay.ide.project.core.util.ClasspathUtil;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * @author Simon Jiang
 */
public class SDKBuildPropertiesResourceListener implements IResourceChangeListener, IResourceDeltaVisitor
{

    private final static String MARKER_ID_SDK_PROPERTIES_INVALID = "sdk-properties-invalid";

    private final static Pattern PATTERN_BUILD_PROPERTIES  = Pattern.compile("build.[\\w|\\W.]*properties");

    protected void processPropertiesFileChanged( final IFile deltaFile ) throws CoreException
    {

        IProject deltaProject = deltaFile.getProject();

        final SDK sdk = SDKUtil.createSDKFromLocation( deltaProject.getLocation() );

        if ( sdk != null )
        {
            final IMarker[] problemMarkers =
                MarkerUtil.findMarkers( deltaFile, IMarker.PROBLEM, MARKER_ID_SDK_PROPERTIES_INVALID );
            final IStatus sdkStatus = sdk.validate( true );

            if( sdkStatus.isOK() )
            {
                if ( problemMarkers != null && problemMarkers.length > 0)
                {
                    MarkerUtil.clearMarkers( deltaFile, IMarker.PROBLEM, MARKER_ID_SDK_PROPERTIES_INVALID );
                }

                for( final IProject project : CoreUtil.getAllProjects() )
                {
                    if( SDKUtil.isSDKProject( project ) && sdk.getLocation().isPrefixOf( project.getLocation() ) )
                    {
                        Job job = new WorkspaceJob( "Updating dependencies " + project.getName() )
                        {
                            @Override
                            public IStatus runInWorkspace( final IProgressMonitor monitor )
                                throws CoreException
                            {
                                ClasspathUtil.updateRequestContainer( project );

                                return Status.OK_STATUS;
                            }
                        };

                        job.setRule( project );
                        job.schedule();
                    }
                }
            }
            else
            {
                if ( problemMarkers == null || problemMarkers.length < 1 )
                {
                    final IStatus[] statuses = sdkStatus.getChildren();

                    for( final IStatus status : statuses )
                    {
                        MarkerUtil.setMarker(
                            deltaFile, IMarker.PROBLEM, IMarker.SEVERITY_ERROR, status.getMessage(),
                            deltaFile.getFullPath().toPortableString(), MARKER_ID_SDK_PROPERTIES_INVALID );
                    }
                }
            }
        }
    }

    @Override
    public void resourceChanged( final IResourceChangeEvent event )
    {
        if( event == null )
        {
            return;
        }

        try
        {
            final IResourceDelta delta = event.getDelta();
            final SDK sdk = SDKUtil.getWorkspaceSDK();

            if( delta != null && sdk != null )
            {
                for( IResourceDelta child : delta.getAffectedChildren() )
                {
                    final IPath deltaLocation = child.getResource().getLocation();

                    if( deltaLocation != null )
                    {
                        if( sdk.getLocation().isPrefixOf( deltaLocation ) )
                        {
                            final IResourceDelta[] sdkChangedFiles =
                                child.getAffectedChildren( IResourceDelta.CHANGED | IResourceDelta.ADDED |
                                    IResourceDelta.REMOVED );

                            for( IResourceDelta sdkDelta : sdkChangedFiles )
                            {
                                final String deltaLastSegment = sdkDelta.getResource().getLocation().lastSegment();

                                final Matcher propertiesMatcher =
                                    PATTERN_BUILD_PROPERTIES.matcher( deltaLastSegment );

                                if( propertiesMatcher.matches() )
                                {
                                    sdkDelta.accept( this );
                                }
                            }
                        }
                    }
                }
            }
        }
        catch( Throwable e )
        {
           ProjectCore.logError( "build.properties resource listener failed.", e );
        }
    }

    @Override
    public boolean visit( final IResourceDelta delta ) throws CoreException
    {
        switch( delta.getResource().getType() )
        {
            case IResource.ROOT:
            case IResource.PROJECT:
            case IResource.FOLDER:
                return true;

            case IResource.FILE:
            {
                final IFile deltaFile = (IFile)delta.getResource();
                Job job = new WorkspaceJob( "Processing SDK build properties file" )
                {
                    @Override
                    public IStatus runInWorkspace( final IProgressMonitor monitor ) throws CoreException
                    {
                        try
                        {
                            processPropertiesFileChanged( deltaFile );
                        }
                        catch(CoreException e)
                        {
                            ProjectCore.logError( e );
                        }

                        return Status.OK_STATUS;
                    }
                };
                job.setRule( deltaFile );
                job.schedule();
            }

            return false;
        }

        return false;
    }
}