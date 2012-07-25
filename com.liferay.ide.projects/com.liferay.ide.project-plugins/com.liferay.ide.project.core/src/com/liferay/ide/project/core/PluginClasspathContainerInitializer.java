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

package com.liferay.ide.project.core;

import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.core.LiferayServerCorePlugin;
import com.liferay.ide.server.util.ServerUtil;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jst.common.jdt.internal.classpath.ClasspathDecorations;
import org.eclipse.jst.common.jdt.internal.classpath.ClasspathDecorationsManager;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public class PluginClasspathContainerInitializer extends ClasspathContainerInitializer
{

    public static final String ID = "com.liferay.ide.eclipse.server.plugin.container";

    protected static final ClasspathDecorationsManager cpDecorations = PluginClasspathContainer.getDecorationsManager();

    @Override
    public boolean canUpdateClasspathContainer( IPath containerPath, IJavaProject project )
    {
        return true;
    }

    @Override
    public void initialize( IPath containerPath, IJavaProject project ) throws CoreException
    {

        IClasspathContainer classpathContainer = null;

        int count = containerPath.segmentCount();

        if( count != 2 )
        {
            throw new CoreException(
                ProjectCorePlugin.createErrorStatus( "Invalid plugin classpath container should expecting 2 segments." ) );
        }

        String root = containerPath.segment( 0 );

        if( !ID.equals( root ) )
        {
            throw new CoreException(
                ProjectCorePlugin.createErrorStatus( "Invalid plugin classpath container, expecting container root " +
                    ID ) );
        }

        String finalSegment = containerPath.segment( 1 );

        IPath portalDir = ServerUtil.getPortalDir( project );

        if( portalDir == null )
        {
            return;
        }

        String javadocURL = null;
        IPath sourceLocation = null;

        try
        {
            ILiferayRuntime liferayRuntime = ServerUtil.getLiferayRuntime( project.getProject() );

            if( liferayRuntime != null )
            {
                javadocURL = liferayRuntime.getJavadocURL();

                sourceLocation = liferayRuntime.getSourceLocation();
            }
        }
        catch( Exception e )
        {
            ProjectCorePlugin.logError( e );
        }

        classpathContainer =
            getCorrectContainer( containerPath, finalSegment, project, portalDir, javadocURL, sourceLocation );

        JavaCore.setClasspathContainer(
            containerPath, new IJavaProject[] { project }, new IClasspathContainer[] { classpathContainer }, null );
    }

    @Override
    public void requestClasspathContainerUpdate(
        IPath containerPath, IJavaProject project, IClasspathContainer containerSuggestion ) throws CoreException
    {

        final String key =
            PluginClasspathContainer.getDecorationManagerKey( project.getProject(), containerPath.toString() );

        final IClasspathEntry[] entries = containerSuggestion.getClasspathEntries();

        cpDecorations.clearAllDecorations( key );

        for( int i = 0; i < entries.length; i++ )
        {
            final IClasspathEntry entry = entries[i];

            final IPath srcpath = entry.getSourceAttachmentPath();
            final IPath srcrootpath = entry.getSourceAttachmentRootPath();
            final IClasspathAttribute[] attrs = entry.getExtraAttributes();

            if( srcpath != null || attrs.length > 0 )
            {
                final String eid = entry.getPath().toString();
                final ClasspathDecorations dec = new ClasspathDecorations();

                dec.setSourceAttachmentPath( srcpath );
                dec.setSourceAttachmentRootPath( srcrootpath );
                dec.setExtraAttributes( attrs );

                cpDecorations.setDecorations( key, eid, dec );
            }
        }

        cpDecorations.save();

        IPath portalDir = null;
        String javadocURL = null;
        IPath sourceLocation = null;

        if( containerSuggestion instanceof PluginClasspathContainer )
        {
            portalDir = ( (PluginClasspathContainer) containerSuggestion ).getPortalDir();
            javadocURL = ( (PluginClasspathContainer) containerSuggestion ).getJavadocURL();
            sourceLocation = ( (PluginClasspathContainer) containerSuggestion ).getSourceLocation();
        }
        else
        {
            portalDir = ServerUtil.getPortalDir( project );

            try
            {
                ILiferayRuntime liferayRuntime = ServerUtil.getLiferayRuntime( project.getProject() );

                if( liferayRuntime != null )
                {
                    javadocURL = liferayRuntime.getJavadocURL();

                    sourceLocation = liferayRuntime.getSourceLocation();
                }
            }
            catch( Exception e )
            {
                ProjectCorePlugin.logError( e );
            }
        }

        IClasspathContainer newContainer =
            getCorrectContainer(
                containerPath, containerPath.segment( 1 ), project, portalDir, javadocURL, sourceLocation );

        JavaCore.setClasspathContainer(
            containerPath, new IJavaProject[] { project }, new IClasspathContainer[] { newContainer }, null );
    }

    protected IClasspathContainer getCorrectContainer(
        IPath containerPath, String finalSegment, IJavaProject project, IPath portalDir, String javadocURL,
        IPath sourceURL ) throws CoreException
    {

        IClasspathContainer classpathContainer = null;

        if( PortletClasspathContainer.SEGMENT_PATH.equals( finalSegment ) )
        {
            classpathContainer =
                new PortletClasspathContainer( containerPath, project, portalDir, javadocURL, sourceURL );
        }
        else if( HookClasspathContainer.SEGMENT_PATH.equals( finalSegment ) )
        {
            classpathContainer = new HookClasspathContainer( containerPath, project, portalDir, javadocURL, sourceURL );
        }
        else if( ExtClasspathContainer.SEGMENT_PATH.equals( finalSegment ) )
        {
            classpathContainer = new ExtClasspathContainer( containerPath, project, portalDir, javadocURL, sourceURL );
        }
        else
        {
            throw new CoreException( LiferayServerCorePlugin.createErrorStatus( "Invalid final segment of type: " +
                finalSegment ) );
        }

        return classpathContainer;
    }

}
