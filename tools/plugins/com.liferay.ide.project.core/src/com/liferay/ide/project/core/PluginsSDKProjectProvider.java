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

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayNature;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOpMethods;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.project.core.model.ProjectName;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.core.util.WizardUtil;
import com.liferay.ide.sdk.core.ISDKConstants;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKCorePlugin;
import com.liferay.ide.sdk.core.SDKManager;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.core.portal.PortalBundle;
import com.liferay.ide.server.util.ServerUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.wst.server.core.IRuntime;
import org.osgi.framework.Version;
import org.osgi.service.prefs.BackingStoreException;


/**
 * @author Gregory Amerson
 * @author Simon Jiang
 * @author Terry Jia
 */
public class PluginsSDKProjectProvider extends NewLiferayProjectProvider
{

    public PluginsSDKProjectProvider()
    {
        super( new Class<?>[] { IProject.class } );
    }

    public IStatus doCreateNewProject(
        NewLiferayPluginProjectOp op, IProgressMonitor monitor, ElementList<ProjectName> projectNames )
        throws CoreException
    {
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
        final boolean separateJRE = true;

        final SDK sdk = SDKManager.getInstance().getSDK( sdkName );
        final IRuntime runtime = NewLiferayPluginProjectOpMethods.getRuntime( op );
        final ILiferayRuntime liferayRuntime = ServerUtil.getLiferayRuntime( runtime, monitor );
        final Map<String, String> appServerProperties = ServerUtil.configureAppServerProperties( liferayRuntime );
      
        //final Map<String, String> appServerProperties = sdk.getSDKProperties( PathBridge.create( op.getLocation().content(true) ) );

        
        // workingDir should always be the directory of the type of plugin /sdk/portlets/ for a portlet, etc
        String workingDir = null;
        // baseDir should only be set when we are wanting to specifically allow 'out-of-sdk' projects, i.e. custom/workspace
        String baseDir = null;
        boolean updateBaseDir = false;

        if( ( !op.getUseDefaultLocation().content() ) ||
            ( op.getUseDefaultLocation().content() && ( !op.getUseSdkLocation().content() ) ) )
        {
            updateBaseDir = true;
        }

        ArrayList<String> arguments = new ArrayList<String>();
        arguments.add( projectName );
        arguments.add( displayName );

        final boolean hasGradleTools = SDKUtil.hasGradleTools( sdk.getLocation() );

        IPath newSDKProjectPath = null;

        switch( pluginType )
        {
            case servicebuilder:
                op.setPortletFramework( "mvc" );
            case portlet:
                final String frameworkName = getFrameworkName( op );

                workingDir = sdk.getLocation().append( ISDKConstants.PORTLET_PLUGIN_PROJECT_FOLDER ).toOSString();

                if( hasGradleTools )
                {
                    arguments.add( frameworkName );

                    sdk.createNewProject( projectName, arguments, "portlet", workingDir, monitor );
                }
                else
                {
                    baseDir = updateBaseDir ? workingDir : null;

                    newSDKProjectPath =
                        sdk.createNewPortletProject(
                            projectName, displayName, frameworkName, appServerProperties, separateJRE, workingDir,
                            baseDir, monitor );
                }

                break;

            case hook:
                workingDir = sdk.getLocation().append( ISDKConstants.HOOK_PLUGIN_PROJECT_FOLDER ).toOSString();

                baseDir = updateBaseDir ? workingDir : null;

                if( hasGradleTools )
                {
                    sdk.createNewProject( projectName, arguments, "hook", workingDir, monitor );
                }
                else
                {
                    baseDir = updateBaseDir ? workingDir : null;

                    newSDKProjectPath =
                        sdk.createNewHookProject(
                            projectName, displayName, appServerProperties, separateJRE, workingDir, baseDir, monitor );
                }

                break;

            case ext:
                workingDir = sdk.getLocation().append( ISDKConstants.EXT_PLUGIN_PROJECT_FOLDER ).toOSString();

                if( hasGradleTools )
                {
                    sdk.createNewProject( projectName, arguments, "ext", workingDir, monitor );
                }
                else
                {
                    baseDir = updateBaseDir ? workingDir : null;

                    newSDKProjectPath =
                        sdk.createNewExtProject(
                            projectName, displayName, appServerProperties, separateJRE, workingDir, baseDir, monitor );
                }

                break;

            case layouttpl:
                workingDir = sdk.getLocation().append( ISDKConstants.LAYOUTTPL_PLUGIN_PROJECT_FOLDER ).toOSString();

                if( hasGradleTools )
                {
                    sdk.createNewProject( projectName, arguments, "layouttpl", workingDir, monitor );
                }
                else
                {
                    baseDir = updateBaseDir ? workingDir : null;
    
                    newSDKProjectPath =
                        sdk.createNewLayoutTplProject(
                            projectName, displayName, appServerProperties, separateJRE, workingDir, baseDir, monitor );
                }

                break;

            case theme:
                workingDir = sdk.getLocation().append( ISDKConstants.THEME_PLUGIN_PROJECT_FOLDER ).toOSString();

                if( hasGradleTools )
                {
                    sdk.createNewProject( projectName, arguments, "theme", workingDir, monitor );
                }
                else
                {
                    baseDir = updateBaseDir ? workingDir : null;

                    newSDKProjectPath =
                        sdk.createNewThemeProject( projectName, displayName, separateJRE, workingDir, baseDir, monitor );
                }

                break;

            case web:
                workingDir = sdk.getLocation().append( ISDKConstants.WEB_PLUGIN_PROJECT_FOLDER ).toOSString();

                if( hasGradleTools )
                {
                    sdk.createNewProject( projectName, arguments, "web", workingDir, monitor );
                }
                else
                {
                    baseDir = updateBaseDir ? workingDir : null;

                    newSDKProjectPath =
                        sdk.createNewWebProject(
                            projectName, displayName, appServerProperties, separateJRE, workingDir, baseDir, monitor );
                }

                break;
        }

        final Path projectLocation = op.getLocation().content();

        if( !hasGradleTools )
        {
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
                throw new CoreException( ProjectCore.createErrorStatus( e ) );
            }
        }

        final ProjectRecord projectRecord = ProjectUtil.getProjectRecordForDir( projectLocation.toOSString() );

        final String sdkLocation = sdk.getLocation().toOSString();
        final IProject newProject =
            ProjectUtil.importProject( projectRecord, ServerUtil.getFacetRuntime( runtime ), sdkLocation, op, monitor );

        newProject.open( monitor );

        if( baseDir != null )
        {
            try
            {
                // we have an 'out-of-sdk' style project so we need to persist the SDK name
                final IEclipsePreferences prefs = new ProjectScope( newProject ).getNode( SDKCorePlugin.PLUGIN_ID );
                prefs.put( SDKCorePlugin.PREF_KEY_SDK_NAME, sdkName );
                prefs.flush();
            }
            catch( BackingStoreException e )
            {
                ProjectCore.logError( "Unable to persist sdk name to project " + projectName, e );
            }
        }

        // need to update project name incase the suffix was not correct
        op.setFinalProjectName( newProject.getName() );

        projectNames.insert().setName( op.getFinalProjectName().content() );

        projectCreated( newProject );

        switch( op.getPluginType().content() )
        {
            case portlet:

                portletProjectCreated( op, newProject, monitor );

                break;

            case servicebuilder:

                serviceBuilderProjectCreated( op, liferayRuntime.getPortalVersion(), newProject, monitor );

                break;
            case theme:

                themeProjectCreated( newProject );

                break;
            default:
                break;
        }

        return Status.OK_STATUS;
    }

    private void portletProjectCreated( NewLiferayPluginProjectOp op, IProject newProject, IProgressMonitor monitor )
        throws CoreException
    {
        final IPortletFramework portletFramework = op.getPortletFramework().content();
        final String portletName = op.getPortletName().content( false );
        final String frameworkName = getFrameworkName( op );

        final IStatus status = portletFramework.postProjectCreated( newProject, frameworkName, portletName, monitor );

        if( ! status.isOK() )
        {
            throw new CoreException( status );
        }
    }

    private void projectCreated( IProject project )
    {
        final IFile ivyFile = project.getFile( "ivy.xml" );

        if( ivyFile.exists() )
        {
            try
            {
                String contents = CoreUtil.readStreamToString( ivyFile.getContents() );

                contents = contents.replace( "${sdk.dir}/ivy.xml", "../../ivy.xml" );

                ivyFile.setContents(
                    new ByteArrayInputStream( contents.toString().getBytes( "UTF-8" ) ), IResource.FORCE,
                    new NullProgressMonitor() );
            }
            catch( Exception e )
            {
                ProjectCore.logError( e );
            }
        }
    }

    public ILiferayProject provide( Object type )
    {
        ILiferayProject retval = null;
        IProject project = null;
        ILiferayRuntime liferayRuntime = null;

        if( type instanceof IProject )
        {
            project = (IProject) type;

            try
            {
                if ( SDKUtil.isSDKProject( project ) && LiferayNature.hasNature( project ) )
                {
                    PortalBundle portalBundle = ServerUtil.getPortalBundle( project );  
                    
                    if( portalBundle != null )
                    {
                        retval = new PluginsSDKProject( project, portalBundle );
                    }                    
                }
                else if ( SDKUtil.isSDKProject( project ))
                {
                    liferayRuntime = ServerUtil.getLiferayRuntime( project );
                    
                    if( liferayRuntime != null )
                    {
                        retval = new PluginsSDKRuntimeProject( project, liferayRuntime );
                    }                    
                }
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
            
            if( liferayRuntime != null )
            {
                retval = new PluginsSDKRuntimeProject( project, liferayRuntime );
            }     
        }
        
        return retval;
    }

    private void serviceBuilderProjectCreated(
        NewLiferayPluginProjectOp op, String version, IProject newProject, IProgressMonitor monitor ) throws CoreException
    {
        // create a default service.xml file in the project
        final IFile serviceXmlFile = newProject.getFile( ISDKConstants.DEFAULT_DOCROOT_FOLDER + "/WEB-INF/service.xml" );

        String descriptorVersion = null;

        try
        {
            Version portalVersion = new Version( version );

            descriptorVersion = portalVersion.getMajor() + "." + portalVersion.getMinor() + ".0";  //$NON-NLS-1$//$NON-NLS-2$
        }
        catch( Exception e )
        {
            ProjectCore.logError( "Could not determine liferay runtime version", e ); //$NON-NLS-1$
            descriptorVersion = "6.0.0"; //$NON-NLS-1$
        }

        WizardUtil.createDefaultServiceBuilderFile(
            serviceXmlFile, descriptorVersion, true, "com.liferay.sample", "SAMPLE", System.getProperty( "user.name" ), monitor );
    }

    private void themeProjectCreated( IProject newProject ) throws CoreException
    {
        final Map<String, String> args = new HashMap<String, String>();
        args.put( "force", "true" );

        newProject.build(
            IncrementalProjectBuilder.FULL_BUILD, "com.liferay.ide.eclipse.theme.core.cssBuilder", args, null );
    }

    public IStatus validateProjectLocation( String name, IPath path )
    {
        IStatus retval = Status.OK_STATUS;

        if( path.append(".project").toFile().exists() ) //$NON-NLS-1$
        {
            retval = ProjectCore.createErrorStatus( "\"" + path + //$NON-NLS-1$
                    "\" is not valid because a project already exists at that location." ); //$NON-NLS-1$
        }
        else
        {
            final File pathFile = path.toFile();

            if( pathFile.exists() && pathFile.isDirectory() && pathFile.listFiles().length > 0 )
            {
                retval = ProjectCore.createErrorStatus( "\"" + path + //$NON-NLS-1$
                        "\" is not valid because it already contains files." ); //$NON-NLS-1$
            }
        }

        return retval;
    }

}
