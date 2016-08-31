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

import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOp;

import java.util.ArrayList;
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

/**
 * @author Simon Jiang
 */
@SuppressWarnings( "restriction" )
public class NewMavenModuleProjectProvider extends LiferayMavenProjectProvider implements NewLiferayProjectProvider<NewLiferayModuleProjectOp>
{
    @Override
    public IStatus createNewProject( NewLiferayModuleProjectOp op, IProgressMonitor monitor ) throws CoreException
    {
        IStatus retval = null;

        final IProjectConfigurationManager projectConfigurationManager = MavenPlugin.getProjectConfigurationManager();

        final String projectName = op.getProjectName().content();

        IPath location = PathBridge.create( op.getLocation().content() );

        // for location we should use the parent location
        if( location.lastSegment().equals( projectName ) )
        {
            // use parent dir since maven archetype will generate new dir under this location
            location = location.removeLastSegments( 1 );
        }

        final String groupId = op.getGroupId().content();
        final String artifactId = op.getProjectName().content();
        final String version = op.getArtifactVersion().content();
        final String javaPackage = op.getGroupId().content();
        final String className = op.getComponentName().content();

        final String archetypeArtifactId = op.getArchetype().content( true );

        final Archetype archetype = new Archetype();

        final String[] gav = archetypeArtifactId.split( ":" );

        final String archetypeVersion = gav[gav.length - 1];

        archetype.setGroupId( gav[0] );
        archetype.setArtifactId( gav[1] );
        archetype.setVersion( archetypeVersion );

//        final ArchetypeManager archetypeManager = MavenPluginActivator.getDefault().getArchetypeManager();
//        final ArtifactRepository archetypeRepository = archetypeManager.getArchetypeRepository( archetype );

        final Properties properties = new Properties();

        properties.put( "package", javaPackage );
        properties.put( "className", className );
        properties.put( "projectType", "standalone" );

        final ResolverConfiguration resolverConfig = new ResolverConfiguration();
        ProjectImportConfiguration configuration = new ProjectImportConfiguration( resolverConfig );

        final List<IProject> newProjects =
            projectConfigurationManager.createArchetypeProjects(
                location, archetype, groupId, artifactId, version, javaPackage, properties, configuration, monitor );

        if (newProjects == null || newProjects.size() == 0)
        {
            retval = LiferayMavenCore.createErrorStatus( "Unable to create project from archetype." );
        }
        else
        {
            for( IProject newProject : newProjects )
            {
                IFile gradleFile = newProject.getFile( "build.gradle" );

                if( gradleFile.exists() )
                {
                    gradleFile.delete( true, monitor );
                }
            }

            retval = Status.OK_STATUS;
        }

        return retval;
    }

    @Override
    public <T> List<T> getData( String key, Class<T> type, Object... params )
    {
        if( "archetypeGAV".equals( key ) && type.equals( String.class ) && params.length == 1 )
        {
            String templateName = params[0].toString();

            List<T> retval = new ArrayList<>();

            retval.add( type.cast( "com.liferay:com.liferay.project.templates." + templateName + ":1.0.0" ) );

            return retval;
        }

        return super.getData( key, type, params );
    }

    @Override
    public IStatus validateProjectLocation( String projectName, IPath path )
    {
        return Status.OK_STATUS;
    }

}
