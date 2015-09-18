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

import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.server.core.portal.PortalBundle;
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
 * @author Simon.Jiang
 */
@SuppressWarnings( "restriction" )
public class SDKClasspathContainerInitializer extends ClasspathContainerInitializer
{
    protected static final ClasspathDecorationsManager cpDecorations = SDKClasspathContainer.getDecorationsManager();

    @Override
    public boolean canUpdateClasspathContainer( IPath containerPath, IJavaProject project )
    {
        return true;
    }

    private IPath[] getSDKDependencies( IJavaProject project )
    {
        IPath[] dependencyJarPaths = null;

        SDK sdk = SDKUtil.getSDKFromProjectDir( project.getProject().getLocation().toFile() );

        if ( sdk != null )
        {
            dependencyJarPaths = sdk.getDependencyJarPaths();
        }

        return dependencyJarPaths;
    }

    @Override
    public void initialize( IPath containerPath, IJavaProject project ) throws CoreException
    {
        IClasspathContainer classpathContainer = null;

        String root = containerPath.segment( 0 );

        if( !SDKClasspathContainer.ID.equals( root ) )
        {
            final String msg = "Invalid plugin classpath container, expecting container root "; //$NON-NLS-1$
            throw new CoreException( ProjectCore.createErrorStatus( msg + SDKClasspathContainer.ID ) );
        }

        PortalBundle bundle = ServerUtil.getPortalBundle(project.getProject());

        if ( bundle == null )
        {
            final String msg = "Invalid sdk properties setting.";
            throw new CoreException( ProjectCore.createErrorStatus( msg ) );
        }

        IPath globalDir = bundle.getAppServerLibGlobalDir();

        IPath portalDir = bundle.getAppServerPortalDir();

        IPath bundleDir = bundle.getAppServerDir();

        IPath[] bundleDependencyJars = bundle.getBundleDependencyJars();

        IPath[] sdkDependencyJarPaths = getSDKDependencies( project );

        if( portalDir == null )
        {
            return;
        }

        classpathContainer =
            new SDKClasspathContainer(
                containerPath, project, portalDir, null, null, globalDir, bundleDir, bundleDependencyJars,
                sdkDependencyJarPaths );

        JavaCore.setClasspathContainer(
            containerPath, new IJavaProject[] { project }, new IClasspathContainer[] { classpathContainer }, null );
    }

    @Override
    public void requestClasspathContainerUpdate(
        IPath containerPath, IJavaProject project, IClasspathContainer containerSuggestion ) throws CoreException
    {
        final String key =
            SDKClasspathContainer.getDecorationManagerKey( project.getProject(), containerPath.toString() );

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
        IPath portalGlobalDir = null;
        String javadocURL = null;
        IPath sourceLocation = null;
        IPath bundleDir = null;
        IPath[] bundleDependencyJarPaths = null;

        PortalBundle bundle = ServerUtil.getPortalBundle(project.getProject());

        boolean containerChanged = true;

        if( containerSuggestion instanceof SDKClasspathContainer )
        {
            portalDir = ( (SDKClasspathContainer) containerSuggestion ).getPortalDir();
            bundleDir = ( (SDKClasspathContainer) containerSuggestion ).getBundleDir();
            portalGlobalDir = ( (SDKClasspathContainer) containerSuggestion ).getPortalGlobalDir();
            javadocURL = ( (SDKClasspathContainer) containerSuggestion ).getJavadocURL();
            sourceLocation = ( (SDKClasspathContainer) containerSuggestion ).getSourceLocation();
            bundleDependencyJarPaths = ( (SDKClasspathContainer) containerSuggestion ).getBundleLibDependencyPath();

            if ( bundle != null && bundle.getAppServerPortalDir().equals( portalDir ) )
            {
                containerChanged = false;
            }
        }

        if ( containerChanged == true)
        {
            if ( bundle == null )
            {
                return;
            }

            portalDir = bundle.getAppServerPortalDir();
            portalGlobalDir = bundle.getAppServerLibGlobalDir();
            bundleDependencyJarPaths = bundle.getBundleDependencyJars();
        }

        IPath[] sdkDependencyPaths = getSDKDependencies( project );

        if( portalDir != null && portalGlobalDir != null )
        {
            IClasspathContainer newContainer =
                new SDKClasspathContainer(
                    containerPath, project, portalDir, javadocURL, sourceLocation, portalGlobalDir, bundleDir,
                    bundleDependencyJarPaths, sdkDependencyPaths );

            JavaCore.setClasspathContainer(
                containerPath, new IJavaProject[] { project }, new IClasspathContainer[] { newContainer }, null );
        }
    }

}
