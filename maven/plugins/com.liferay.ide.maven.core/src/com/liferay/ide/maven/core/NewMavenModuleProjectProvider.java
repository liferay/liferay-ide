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
import com.liferay.ide.project.core.model.ProjectName;
import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOp;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.maven.archetype.catalog.Archetype;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMavenConfiguration;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;
import org.eclipse.m2e.core.project.IProjectConfigurationManager;
import org.eclipse.m2e.core.project.ProjectImportConfiguration;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.sapphire.ElementList;
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

        final IMavenConfiguration mavenConfiguration = MavenPlugin.getMavenConfiguration();
        final IMavenProjectRegistry mavenProjectRegistry = MavenPlugin.getMavenProjectRegistry();
        final IProjectConfigurationManager projectConfigurationManager = MavenPlugin.getProjectConfigurationManager();

        final ElementList<ProjectName> projectNames = op.getProjectNames();

        final String projectName = op.getProjectName().content();

        IPath location = PathBridge.create( op.getLocation().content() );

        // for location we should use the parent location
        if( location.lastSegment().equals( projectName ) )
        {
            // use parent dir since maven archetype will generate new dir under this location
            location = location.removeLastSegments( 1 );
        }

        final Archetype archetype = new Archetype();
        archetype.setGroupId( "" );
        archetype.setArtifactId( "" );
        archetype.setVersion( "" );

        String groupId = "";
        String artifactId = "";
        String version = "";
        String javaPackage = "";
        Properties properties = new Properties();
        final ResolverConfiguration resolverConfig = new ResolverConfiguration();
        ProjectImportConfiguration configuration = new ProjectImportConfiguration( resolverConfig );

        final List<IProject> newProjects =
            projectConfigurationManager.createArchetypeProjects(
                location, archetype, groupId, artifactId, version, javaPackage, properties, configuration, monitor );


//        retval = ModulesUtil.createModuleProject(
//            location.toFile(), op.getProjectTemplate().content(), projectName, null, null, null );
//
//        if ( retval.isOK() )
//        {
//            final IWorkspace workspace = ResourcesPlugin.getWorkspace();
//
//            IPath pomPath = location.append( projectName ).append( "pom.xml" );
//
//            if ( pomPath != null && pomPath.toFile().exists() )
//            {
//                File pomFile = new File( location.append( projectName ).append( "pom.xml" ).toPortableString() );
//                MavenModelManager mavenModelManager = MavenPlugin.getMavenModelManager();
//                final ResolverConfiguration resolverConfig = new ResolverConfiguration();
//                final ArrayList<MavenProjectInfo> projectInfos = new ArrayList<MavenProjectInfo>();
//
//                Model model = mavenModelManager.readMavenModel( pomFile );
//                MavenProjectInfo projectInfo = new MavenProjectInfo( pomFile.getName(), pomFile, model, null );
//
//                projectInfos.add( projectInfo );
//
//                ProjectImportConfiguration importConfiguration = new ProjectImportConfiguration( resolverConfig );
//
//                final IMavenConfiguration mavenConfiguration = MavenPlugin.getMavenConfiguration();
//                final IProjectConfigurationManager projectConfigurationManager = MavenPlugin.getProjectConfigurationManager();
//
//                projectConfigurationManager.importProjects( projectInfos, importConfiguration, monitor );
//
//                IProject project = workspace.getRoot().getProject( projectName );
//
//                if ( project.exists() )
//                {
//                    newProjects.add( project );
//
//                    if( !CoreUtil.isNullOrEmpty( newProjects ) )
//                    {
//                        for( IProject iProject : newProjects )
//                        {
//                            projectNames.insert().setName( iProject.getName() );
//                        }
//                    }
//
//                    if( CoreUtil.isNullOrEmpty( newProjects ) )
//                    {
//                        retval = LiferayMavenCore.createErrorStatus( "New project was not created due to unknown error" );
//                    }
//                    else
//                    {
//                        for( final IProject iProject : newProjects )
//                        {
//                            try
//                            {
//                                projectConfigurationManager.updateProjectConfiguration( new MavenUpdateRequest(
//                                    iProject, mavenConfiguration.isOffline(), false ), monitor );
//                            }
//                            catch( Exception e )
//                            {
//                                LiferayMavenCore.logError( "Unable to update configuration for " + project.getName(), e );
//                            }
//                        }
//                    }
//                }
//                else
//                {
//                    retval = LiferayMavenCore.createErrorStatus( "Unable to import this Project " );
//                }
//            }
//        }
//
//        if( retval == null )
//        {
//            retval = Status.OK_STATUS;
//        }

        return retval;
    }

    @Override
    public IStatus validateProjectLocation( String projectName, IPath path )
    {
        return Status.OK_STATUS;
    }

}
