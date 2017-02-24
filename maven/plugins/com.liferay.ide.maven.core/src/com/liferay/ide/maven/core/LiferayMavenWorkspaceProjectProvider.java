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

package com.liferay.ide.maven.core;

import java.util.List;
import java.util.Properties;

import org.apache.maven.archetype.catalog.Archetype;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IProjectConfigurationManager;
import org.eclipse.m2e.core.project.ProjectImportConfiguration;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.sapphire.platform.PathBridge;

import com.liferay.ide.project.core.workspace.NewLiferayWorkspaceOp;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.util.ProjectUtil;

/**
 * @author Joye Luo
 */
@SuppressWarnings( "restriction" )
public class LiferayMavenWorkspaceProjectProvider extends LiferayMavenProjectProvider
    implements NewLiferayProjectProvider<NewLiferayWorkspaceOp>
{

    @Override
    public IStatus createNewProject( NewLiferayWorkspaceOp op, IProgressMonitor monitor ) throws CoreException
    {
        IStatus retval = Status.OK_STATUS;;

        final IProjectConfigurationManager projectConfigurationManager = MavenPlugin.getProjectConfigurationManager();

        IPath location = PathBridge.create( op.getLocation().content() );

        final String projectName = op.getWorkspaceName().content();

        String groupId = "com.liferay";
        String artifactId = op.getWorkspaceName().content();
        String version = "1.0.2";
        String javaPackage = "com.liferay";

        final Archetype archetype = new Archetype();
        archetype.setGroupId( "com.liferay" );
        archetype.setArtifactId( "com.liferay.project.templates.workspace" );
        archetype.setVersion( "1.0.2" );

        final Properties properties = new Properties();

        final ResolverConfiguration resolverConfig = new ResolverConfiguration();
        ProjectImportConfiguration configuration = new ProjectImportConfiguration( resolverConfig );

        final List<IProject> newProjects = projectConfigurationManager.createArchetypeProjects(
            location, archetype, groupId, artifactId, version, javaPackage, properties, configuration, monitor );

        if( newProjects == null || newProjects.size() == 0 )
        {
            retval = LiferayMavenCore.createErrorStatus( "Unable to create liferay workspace project from archetype." );
        }
        else
        {
            for( IProject newProject : newProjects )
            {
                String[] gradleFiles = new String[] { "build.gradle", "settings.gradle", "gradle.properties" };

                for( String path : gradleFiles )
                {
                    IFile gradleFile = newProject.getFile( path );

                    if( gradleFile.exists() )
                    {
                        gradleFile.delete( true, monitor );
                    }
                }
            }
        }

        boolean isInitBundle = op.getProvisionLiferayBundle().content();

        if( retval.isOK() && isInitBundle )
        {
            IProject workspaceProject = ProjectUtil.getProject( projectName );
            String bundleUrl = op.getBundleUrl().content();

            final MavenProjectBuilder mavenProjectBuilder = new MavenProjectBuilder( workspaceProject );

            mavenProjectBuilder.execInitBundle( workspaceProject, "init-bundle", bundleUrl, monitor );
        }

        return retval;
    }

    @Override
    public IStatus validateProjectLocation( String projectName, IPath path )
    {
        IStatus retval = Status.OK_STATUS;

        // TODO validation maven project location

        return retval;
    }
}
