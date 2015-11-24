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

import com.liferay.ide.core.AbstractLiferayProjectProvider;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOpMethods;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.project.core.model.ProjectName;
import com.liferay.ide.project.core.util.ClasspathUtil;
import com.liferay.ide.project.core.util.ProjectImportUtil;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.core.util.WizardUtil;
import com.liferay.ide.sdk.core.ISDKConstants;
import com.liferay.ide.sdk.core.SDK;
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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.wst.server.core.IRuntime;
import org.osgi.framework.Version;


/**
 * @author Gregory Amerson
 * @author Simon Jiang
 * @author Terry Jia
 */
public class PluginsSDKProjectProvider extends AbstractLiferayProjectProvider implements NewLiferayProjectProvider<NewLiferayPluginProjectOp>
{

    public PluginsSDKProjectProvider()
    {
        super( new Class<?>[] { IProject.class, IRuntime.class } );
    }

    private void portletProjectCreated( NewLiferayPluginProjectOp op, IProject newProject, IProgressMonitor monitor )
        throws CoreException
    {
        final IPortletFramework portletFramework = op.getPortletFramework().content();
        final String portletName = op.getPortletName().content( false );
        final String frameworkName = NewLiferayPluginProjectOpMethods.getFrameworkName( op );

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

    @Override
    public ILiferayProject provide( Object type )
    {
        ILiferayProject retval = null;

        if( type instanceof IProject )
        {
            final IProject project = (IProject) type;

            try
            {
                if( SDKUtil.isSDKProject( project ) )
                {
                    final IJavaProject javaProject = JavaCore.create( project );

                    final boolean hasNewSdk =
                        ClasspathUtil.hasNewLiferaySDKContainer( javaProject.getRawClasspath() );

                    if( hasNewSdk )
                    {
                        final PortalBundle portalBundle = ServerUtil.getPortalBundle( project );

                        if( portalBundle != null )
                        {
                            retval = new PluginsSDKBundleProject( project, portalBundle );
                        }
                    }
                    else
                    {
                        final ILiferayRuntime liferayRuntime = ServerUtil.getLiferayRuntime( project );

                        if( liferayRuntime != null )
                        {
                            retval = new PluginsSDKRuntimeProject( project, liferayRuntime );
                        }
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

                final ILiferayRuntime liferayRuntime = ServerUtil.getLiferayRuntime( runtime );

                if( liferayRuntime != null )
                {
                    retval = new PluginsSDKRuntimeProject( null, liferayRuntime );
                }
            }
            catch( Exception e )
            {
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

    @Override
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



    @Override
    public IStatus createNewProject( NewLiferayPluginProjectOp op, IProgressMonitor monitor ) throws CoreException
    {
        ElementList<ProjectName> projectNames = op.getProjectNames();

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

        SDK sdk = SDKUtil.getWorkspaceSDK();

        if ( sdk == null )
        {
            sdk = SDKUtil.createSDKFromLocation( PathBridge.create( op.getSdkLocation().content() ) );

            if ( sdk == null )
            {
                throw new CoreException( ProjectCore.createErrorStatus( "Can't get correct sdk." ) );
            }
        }

        IStatus sdkStatus = sdk.validate();

        if ( !sdkStatus.isOK() )
        {
            throw new CoreException( sdkStatus );
        }

        // workingDir should always be the directory of the type of plugin /sdk/portlets/ for a portlet, etc
        String workingDir = null;

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
                final String frameworkName = NewLiferayPluginProjectOpMethods.getFrameworkName( op );

                workingDir = sdk.getLocation().append( ISDKConstants.PORTLET_PLUGIN_PROJECT_FOLDER ).toOSString();

                if( hasGradleTools )
                {
                    arguments.add( frameworkName );

                    sdk.createNewProject( projectName, arguments, "portlet", workingDir, monitor );
                }
                else
                {
                    newSDKProjectPath =
                        sdk.createNewPortletProject(
                            projectName, displayName, frameworkName, separateJRE, workingDir,
                            null, monitor );
                }

                break;

            case hook:
                workingDir = sdk.getLocation().append( ISDKConstants.HOOK_PLUGIN_PROJECT_FOLDER ).toOSString();

                if( hasGradleTools )
                {
                    sdk.createNewProject( projectName, arguments, "hook", workingDir, monitor );
                }
                else
                {
                    newSDKProjectPath =
                        sdk.createNewHookProject(
                            projectName, displayName, separateJRE, workingDir, null, monitor );
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
                    newSDKProjectPath =
                        sdk.createNewExtProject(
                            projectName, displayName, separateJRE, workingDir, null, monitor );
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
                    newSDKProjectPath =
                        sdk.createNewLayoutTplProject(
                            projectName, displayName, separateJRE, workingDir, null, monitor );
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
                    newSDKProjectPath =
                        sdk.createNewThemeProject( projectName, displayName, separateJRE, workingDir, null, monitor );
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
                    newSDKProjectPath =
                        sdk.createNewWebProject(
                            projectName, displayName, separateJRE, workingDir, null, monitor );
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

        final IProject newProject =
            ProjectImportUtil.importProject( projectRecord.getProjectLocation(), monitor, op );

        newProject.open( monitor );

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

                PortalBundle bundle = ServerUtil.getPortalBundle( newProject );
                serviceBuilderProjectCreated( op, bundle.getVersion(), newProject, monitor );

                break;
            case theme:

                themeProjectCreated( newProject );

                break;
            default:
                break;
        }

        return Status.OK_STATUS;

    }
}
