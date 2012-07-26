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

package com.liferay.ide.theme.core;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.sdk.ISDKConstants;
import com.liferay.ide.sdk.SDK;
import com.liferay.ide.sdk.util.SDKUtil;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.theme.core.operation.ThemeDescriptorHelper;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IRuntime;

/**
 * @author Greg Amerson
 */
public class ThemeDiffResourceListener implements IResourceChangeListener
{

    public void resourceChanged( IResourceChangeEvent event )
    {
        if( event == null )
        {
            return;
        }

        if( shouldProcessResourceChangedEvent( event ) )
        {
            IResourceDelta delta = event.getDelta();

            try
            {
                delta.accept( new IResourceDeltaVisitor()
                {

                    public boolean visit( IResourceDelta delta ) throws CoreException
                    {

                        if( shouldProcessResourceDelta( delta ) )
                        {
                            processResourceChanged( delta );

                            return false;
                        }

                        return true;
                    }
                } );
            }
            catch( CoreException e )
            {

            }
        }
    }

    private IFile getWorkspaceFile( IPath path )
    {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

        return root.getFile( path );
    }

    protected boolean isLiferayPluginProject( IPath deltaPath )
    {
        IFile pluginPackagePropertiesFile = getWorkspaceFile( deltaPath );

        if( pluginPackagePropertiesFile != null && pluginPackagePropertiesFile.exists() )
        {
            return ProjectUtil.isThemeProject( pluginPackagePropertiesFile.getProject() );
        }

        return false;
    }

    protected void processResourceChanged( final IResourceDelta delta ) throws CoreException
    {

        new WorkspaceJob( "Compiling theme" )
        {

            @Override
            public IStatus runInWorkspace( IProgressMonitor monitor ) throws CoreException
            {

                IProject project = delta.getResource().getProject();

                SDK sdk = SDKUtil.getSDK( project );

                if( sdk == null )
                {
                    throw new CoreException(
                        ThemeCore.createErrorStatus( "No SDK for project configured. Could not deploy theme module" ) );
                }

                Map<String, String> appServerProperties = ServerUtil.configureAppServerProperties( project );

                IStatus status = sdk.compileThemePlugin( project, null, appServerProperties );

                if( !status.isOK() )
                {
                    throw new CoreException( status );
                }

                IFolder docroot = CoreUtil.getDocroot( project );

                IFile lookAndFeelFile = docroot.getFile( "WEB-INF/" + ILiferayConstants.LIFERAY_LOOK_AND_FEEL_XML_FILE );

                if( !lookAndFeelFile.exists() )
                {
                    String id = project.getName().replaceAll( ISDKConstants.THEME_PLUGIN_PROJECT_SUFFIX, "" );
                    IFile propsFile =
                        docroot.getFile( "WEB-INF/" + ILiferayConstants.LIFERAY_PLUGIN_PACKAGE_PROPERTIES_FILE );
                    String name = id;
                    if( propsFile.exists() )
                    {
                        Properties props = new Properties();
                        try
                        {
                            props.load( propsFile.getContents() );
                            String nameValue = props.getProperty( "name" );
                            if( !CoreUtil.isNullOrEmpty( nameValue ) )
                            {
                                name = nameValue;
                            }
                        }
                        catch( IOException e )
                        {
                            ThemeCore.logError( "Unable to load plugin package properties.", e );
                        }
                    }

                    IRuntime runtime = ServerUtil.getRuntime( project );

                    ILiferayRuntime liferayRuntime = ServerUtil.getLiferayRuntime( runtime );

                    if( liferayRuntime != null )
                    {
                        ThemeDescriptorHelper.createDefaultFile( lookAndFeelFile, liferayRuntime.getPortalVersion() +
                            "+", id, name );
                    }
                }

                if( docroot != null && docroot.exists() )
                {
                    docroot.refreshLocal( IResource.DEPTH_INFINITE, null );
                }

                return Status.OK_STATUS;
            }
        }.schedule();
    }

    protected boolean shouldProcessResourceChangedEvent( IResourceChangeEvent event )
    {
        if( event == null )
        {
            return false;
        }

        IResourceDelta delta = event.getDelta();

        int deltaKind = delta.getKind();

        if( deltaKind == IResourceDelta.REMOVED || deltaKind == IResourceDelta.REMOVED_PHANTOM )
        {
            return false;
        }

        return true;
    }

    protected boolean shouldProcessResourceDelta( IResourceDelta delta )
    {
        IPath fullPath = delta.getFullPath();

        IFolder docroot = CoreUtil.getDocroot( delta.getResource().getProject() );

        if( docroot == null )
        {
            return false;
        }

        IPath diffPath = docroot.getFolder( "_diffs" ).getFullPath();

        return diffPath.isPrefixOf( fullPath );
    }

}
