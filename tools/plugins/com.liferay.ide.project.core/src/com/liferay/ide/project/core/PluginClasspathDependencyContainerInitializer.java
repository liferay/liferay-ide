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
public class PluginClasspathDependencyContainerInitializer extends ClasspathContainerInitializer
{

    public static final String ID = "com.liferay.ide.eclipse.plugin.dependency.container"; //$NON-NLS-1$

    protected static final ClasspathDecorationsManager cpDecorations = PluginClasspathDependencyContainer.getDecorationsManager();

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
            final String msg = "Invalid plugin classpath container should expecting 2 segments."; //$NON-NLS-1$
            throw new CoreException( ProjectCore.createErrorStatus( msg ) );
        }

        String root = containerPath.segment( 0 );

        if( !ID.equals( root ) )
        {
            final String msg = "Invalid plugin classpath container, expecting container root "; //$NON-NLS-1$
            throw new CoreException( ProjectCore.createErrorStatus( msg + ID ) );
        }

        PortalBundle bundle = ServerUtil.getPortalBundle(project.getProject());
        
        if ( bundle == null )
        {
            final String msg = "Invalid sdk properties setting.";
            throw new CoreException( ProjectCore.createErrorStatus( msg ) );
        }
        
        IPath globalDir = bundle.getAppServerLibGlobalDir();
        
        IPath portalDir = bundle.getPortalDir();
        
        IPath bundleDir = bundle.getAppServerDir();
        
        IPath[] bundleDependencyLibDir = bundle.getBundleDependencyJars();

        if( portalDir == null )
        {
            return;
        }

        classpathContainer = new PluginClasspathDependencyContainer( containerPath, project, portalDir, null, null, globalDir, bundleDir, bundleDependencyLibDir);

        JavaCore.setClasspathContainer(
            containerPath, new IJavaProject[] { project }, new IClasspathContainer[] { classpathContainer }, null );
    }

    @Override
    public void requestClasspathContainerUpdate(
        IPath containerPath, IJavaProject project, IClasspathContainer containerSuggestion ) throws CoreException
    {

        final String key =
            PluginClasspathDependencyContainer.getDecorationManagerKey( project.getProject(), containerPath.toString() );

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
        IPath[] bundleLibDependencyPath = null;
        
        if( containerSuggestion instanceof PluginClasspathDependencyContainer )
        {
            portalDir = ( (PluginClasspathDependencyContainer) containerSuggestion ).getPortalDir();
            bundleDir = ( (PluginClasspathDependencyContainer) containerSuggestion ).getBundleDir();
            portalGlobalDir = ( (PluginClasspathDependencyContainer) containerSuggestion ).getPortalGlobalDir();
            javadocURL = ( (PluginClasspathDependencyContainer) containerSuggestion ).getJavadocURL();
            sourceLocation = ( (PluginClasspathDependencyContainer) containerSuggestion ).getSourceLocation();
            bundleLibDependencyPath = ( (PluginClasspathDependencyContainer) containerSuggestion ).getBundleLibDependencyPath();
        }
        else
        {
            PortalBundle bundle = ServerUtil.getPortalBundle(project.getProject());
            
            if ( bundle == null )
            {
                final String msg = "Invalid sdk properties setting.";
                throw new CoreException( ProjectCore.createErrorStatus( msg ) );
            }
            
            portalDir = bundle.getPortalDir();
            portalGlobalDir = bundle.getAppServerLibGlobalDir();
            bundleLibDependencyPath = bundle.getBundleDependencyJars();
        }

        if( portalDir != null && portalGlobalDir != null )
        {
            IClasspathContainer newContainer = new PluginClasspathDependencyContainer( containerPath, project, portalDir, javadocURL, sourceLocation, portalGlobalDir, bundleDir, bundleLibDependencyPath);


            JavaCore.setClasspathContainer(
                containerPath, new IJavaProject[] { project }, new IClasspathContainer[] { newContainer }, null );
        }
    }

}
