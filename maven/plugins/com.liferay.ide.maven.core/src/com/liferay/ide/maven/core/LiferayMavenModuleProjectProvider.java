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

import com.liferay.blade.api.ProjectBuild;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.ILiferayModuleProjectProvider;
import com.liferay.ide.project.core.model.ProjectName;
import com.liferay.ide.project.core.model.modules.NewLiferayModuleProjectOp;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Model;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMavenConfiguration;
import org.eclipse.m2e.core.embedder.MavenModelManager;
import org.eclipse.m2e.core.project.IProjectConfigurationManager;
import org.eclipse.m2e.core.project.MavenProjectInfo;
import org.eclipse.m2e.core.project.MavenUpdateRequest;
import org.eclipse.m2e.core.project.ProjectImportConfiguration;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.platform.PathBridge;

/**
 * @author Simon Jiang
 */
public class LiferayMavenModuleProjectProvider extends LiferayMavenProjectProvider implements ILiferayModuleProjectProvider
{
    public LiferayMavenModuleProjectProvider()
    {
        super( new Class<?>[] { IProject.class } );
    }

    @Override
    public IStatus createNewProject( Object operation, IProgressMonitor monitor ) throws CoreException
    {
        IStatus retval = null;

        if( ! (operation instanceof NewLiferayModuleProjectOp ) )
        {
            throw new IllegalArgumentException( "Operation must be of type NewLiferayModuleProjectOp" ); //$NON-NLS-1$
        }

        final NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.class.cast( operation );

        ElementList<ProjectName> projectNames = op.getProjectNames();

        final String projectName = op.getProjectName().content();

        IPath location = PathBridge.create( op.getLocation().content() );

        // for location we should use the parent location
        if( location.lastSegment().equals( projectName ) )
        {
            // use parent dir since maven archetype will generate new dir under this location
            location = location.removeLastSegments( 1 );
        }

        final String projectType = op.getProjectTemplate().content().toString();

        final List<IProject> newProjects = new ArrayList<IProject>();

        retval = createOSGIBundleProject(
                location.toFile(), location.toFile(), projectType, ProjectBuild.maven.toString(), projectName,
                projectName, projectName );

        if ( retval.isOK() )
        {
            final IWorkspace workspace = ResourcesPlugin.getWorkspace();

            IPath pomPath = location.append( projectName ).append( "pom.xml" );

            if ( pomPath != null && pomPath.toFile().exists() )
            {
                File pomFile = new File( location.append( projectName ).append( "pom.xml" ).toPortableString() );
                MavenModelManager mavenModelManager = MavenPlugin.getMavenModelManager();
                final ResolverConfiguration resolverConfig = new ResolverConfiguration();
                final ArrayList<MavenProjectInfo> projectInfos = new ArrayList<MavenProjectInfo>();

                Model model = mavenModelManager.readMavenModel( pomFile );
                MavenProjectInfo projectInfo = new MavenProjectInfo( pomFile.getName(), pomFile, model, null );

                projectInfos.add( projectInfo );

                ProjectImportConfiguration importConfiguration = new ProjectImportConfiguration( resolverConfig );

                final IMavenConfiguration mavenConfiguration = MavenPlugin.getMavenConfiguration();
                final IProjectConfigurationManager projectConfigurationManager = MavenPlugin.getProjectConfigurationManager();

                projectConfigurationManager.importProjects( projectInfos, importConfiguration, monitor );

                IProject project = workspace.getRoot().getProject( projectName );

                if ( project.exists() )
                {
                    newProjects.add( project );

                    if( !CoreUtil.isNullOrEmpty( newProjects ) )
                    {
                        for( IProject iProject : newProjects )
                        {
                            projectNames.insert().setName( iProject.getName() );
                        }
                    }

                    if( CoreUtil.isNullOrEmpty( newProjects ) )
                    {
                        retval = LiferayMavenCore.createErrorStatus( "New project was not created due to unknown error" );
                    }
                    else
                    {
                        for( final IProject iProject : newProjects )
                        {
                            try
                            {
                                projectConfigurationManager.updateProjectConfiguration( new MavenUpdateRequest(
                                    iProject, mavenConfiguration.isOffline(), false ), monitor );
                            }
                            catch( Exception e )
                            {
                                LiferayMavenCore.logError( "Unable to update configuration for " + project.getName(), e );
                            }
                        }
                    }
                }
                else
                {
                    retval = LiferayMavenCore.createErrorStatus( "Unable to import this Project " );
                }
            }
        }

        if( retval == null )
        {
            retval = Status.OK_STATUS;
        }

        return retval;
    }

    @Override
    public IStatus createOSGIBundleProject(
        File baseLocation, File dir, String projectType, String buildType, String projectName, String className,
        String serviceName )
    {
        IStatus retVal = Status.OK_STATUS;

        retVal = ProjectUtil.createOSGIBundleProject( baseLocation, dir, projectType, buildType, projectName, className, serviceName );

        return retVal;
    }

}
