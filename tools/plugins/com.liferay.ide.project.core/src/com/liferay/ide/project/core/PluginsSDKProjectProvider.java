/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

import com.liferay.ide.core.AbstractLiferayProjectProvider;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOpMethods;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.sdk.core.ISDKConstants;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKManager;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.ServerCore;


/**
 * @author Gregory Amerson
 */
public class PluginsSDKProjectProvider extends AbstractLiferayProjectProvider
{

    public PluginsSDKProjectProvider()
    {
        super( new Class<?>[] { IProject.class, IRuntime.class } );
    }

    public ILiferayProject provide( Object type )
    {
        LiferayRuntimeProject retval = null;
        IProject project = null;
        ILiferayRuntime liferayRuntime = null;

        if( type instanceof IProject )
        {
            project = (IProject) type;

            try
            {
                liferayRuntime = ServerUtil.getLiferayRuntime( project );
            }
            catch( CoreException e )
            {
            }
        }
        else if( type instanceof IRuntime )
        {
            try
            {
                final IRuntime runtime = (IRuntime) type;

                liferayRuntime = ServerUtil.getLiferayRuntime( runtime );
            }
            catch( Exception e )
            {
            }
        }

        if( liferayRuntime != null )
        {
            retval = new LiferayRuntimeProject( project, liferayRuntime );
        }

        return retval;
    }

    public IStatus createNewProject( Object operation, IProgressMonitor monitor ) throws CoreException
    {
        if( ! ( operation instanceof NewLiferayPluginProjectOp ) )
        {
            throw new IllegalArgumentException( "Operation must be of type NewLiferayPluginProjectOp" ); //$NON-NLS-1$
        }

        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.class.cast( operation );

        final String sdkName = op.getPluginsSDKName().content( true );
        final PluginType pluginType = op.getPluginType().content( true );
        final String originalProjectName = op.getProjectName().content();
        final String pluginTypeSuffix = NewLiferayPluginProjectOpMethods.getPluginTypeSuffix( pluginType );

        String fixedProjectName = originalProjectName;

        if( originalProjectName.endsWith( pluginTypeSuffix ) )
        {
            fixedProjectName = originalProjectName.substring( 0, originalProjectName.length() - pluginTypeSuffix.length() );
        }

        final String projectName = fixedProjectName;

        final String displayName = op.getDisplayName().content( true );
        final IPortletFramework portletFramework = op.getPortletFramework().content( true );
        final boolean separateJRE = true;

        final SDK sdk = SDKManager.getInstance().getSDK( sdkName );
        final IRuntime runtime = getRuntime( op );
        final ILiferayRuntime liferayRuntime = getLiferayRuntime( runtime, monitor );
        final Map<String, String> appServerProperties = ServerUtil.configureAppServerProperties( liferayRuntime );

        // workingDir should always be the directory of the type of plugin /sdk/portlets/ for a portlet, etc
        String workingDir = null;
        // baseDir should only be set when we are wanting to specifically allow 'out-of-sdk' projects, i.e. custom/workspace
        String baseDir = null;

        if( ( !op.getUseDefaultLocation().content() ) ||
            ( op.getUseDefaultLocation().content() && ( !op.getUseSdkLocation().content() ) ) )
        {
            baseDir = sdk.getLocation().toString();
        }

        IPath newSDKProjectPath = null;

        switch( pluginType )
        {
            case portlet:
                String frameworkName = portletFramework.getShortName();

                if( portletFramework.isRequiresAdvanced() )
                {
                    frameworkName = op.getPortletFrameworkAdvanced().content( true ).getShortName();
                }

                workingDir = sdk.getLocation().append( ISDKConstants.PORTLET_PLUGIN_PROJECT_FOLDER ).toOSString();

                newSDKProjectPath =
                    sdk.createNewPortletProject(
                        projectName, displayName, frameworkName, appServerProperties, separateJRE, workingDir, baseDir,
                        monitor );
                break;

            case hook:
                workingDir = sdk.getLocation().append( ISDKConstants.HOOK_PLUGIN_PROJECT_FOLDER ).toOSString();

                newSDKProjectPath =
                    sdk.createNewHookProject( projectName, displayName, separateJRE, workingDir, baseDir, monitor );
                break;

            case ext:
                workingDir = sdk.getLocation().append( ISDKConstants.EXT_PLUGIN_PROJECT_FOLDER ).toOSString();

                newSDKProjectPath =
                    sdk.createNewExtProject(
                        projectName, displayName, appServerProperties, separateJRE, workingDir, baseDir, monitor );
                break;

            case layouttpl:
                workingDir = sdk.getLocation().append( ISDKConstants.LAYOUTTPL_PLUGIN_PROJECT_FOLDER ).toOSString();

                newSDKProjectPath =
                    sdk.createNewLayoutTplProject(
                        projectName, displayName, appServerProperties, separateJRE, workingDir, baseDir, monitor );
                break;

            case theme:
                workingDir = sdk.getLocation().append( ISDKConstants.THEME_PLUGIN_PROJECT_FOLDER ).toOSString();

                newSDKProjectPath =
                    sdk.createNewThemeProject( projectName, displayName, separateJRE, workingDir, baseDir, monitor );
                break;
        }

        final Path projectLocation = op.getLocation().content();

        final File projectDir = projectLocation.toFile();

        final File projectParent = projectDir.getParentFile();

        projectParent.mkdirs();

        final File newSDKProjectDir = newSDKProjectPath.toFile();

        try
        {
            FileUtils.copyDirectory( newSDKProjectDir, projectParent );

            FileUtils.deleteDirectory( newSDKProjectDir );
        }
        catch( IOException e )
        {
            throw new CoreException( LiferayProjectCore.createErrorStatus( e ) );
        }

        final ProjectRecord projectRecord = ProjectUtil.getProjectRecordForDir( projectLocation.toOSString() );

        final String sdkLocation = sdk.getLocation().toOSString();
        final IProject newProject =
            ProjectUtil.importProject( projectRecord, getFacetRuntime( runtime ), sdkLocation, op, monitor );

        newProject.open( monitor );

        // need to update project name incase the suffix was not correct
        op.setProjectName( newProject.getName() );

        switch( op.getPluginType().content() )
        {
            case portlet:
                final IStatus status = op.getPortletFramework().content().postProjectCreated( newProject, monitor );

                if( !status.isOK() )
                {
                    throw new CoreException( status );
                }
                break;
            case theme:
                Map<String, String> args = new HashMap<String, String>();
                args.put( "force", "true" );
                newProject.build(
                    IncrementalProjectBuilder.FULL_BUILD, "com.liferay.ide.eclipse.theme.core.cssBuilder", args, null );
                break;
            default:
                break;
        }

        return Status.OK_STATUS;
    }

    private static org.eclipse.wst.common.project.facet.core.runtime.IRuntime getFacetRuntime( IRuntime runtime )
    {
        return RuntimeManager.getRuntime( runtime.getName() );
    }

    private static IRuntime getRuntime( NewLiferayPluginProjectOp op )
    {
        final String runtimeName = op.getRuntimeName().content( true );

        return ServerCore.findRuntime( runtimeName );
    }

    private static ILiferayRuntime getLiferayRuntime( IRuntime runtime, IProgressMonitor monitor )
    {
        return (ILiferayRuntime) runtime.loadAdapter( ILiferayRuntime.class, monitor );
    }

}
