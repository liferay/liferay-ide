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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.platform.PathBridge;

/**
 * @author Simon Jiang
 */
public class NewMavenModuleProjectProvider extends LiferayMavenProjectProvider implements NewLiferayProjectProvider<NewLiferayModuleProjectOp>
{
    @Override
    public IStatus createNewProject( NewLiferayModuleProjectOp op, IProgressMonitor monitor ) throws CoreException
    {
        IStatus retval = null;

        ElementList<ProjectName> projectNames = op.getProjectNames();

        final String projectName = op.getProjectName().content();

        IPath location = PathBridge.create( op.getLocation().content() );

        // for location we should use the parent location
        if( location.lastSegment().equals( projectName ) )
        {
            // use parent dir since maven archetype will generate new dir under this location
            location = location.removeLastSegments( 1 );
        }

        final List<IProject> newProjects = new ArrayList<IProject>();

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
        // TODO improve this
        return new NewMavenPluginProjectProvider().validateProjectLocation( projectName, path );
    }

}
