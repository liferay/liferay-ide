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

package com.liferay.ide.gradle.core;

import com.liferay.ide.core.AbstractLiferayProjectProvider;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayNature;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.model.ProjectName;
import com.liferay.ide.project.core.modules.BladeCLI;
import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOp;
import com.liferay.ide.project.core.modules.PropertyKey;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.buildship.core.configuration.GradleProjectNature;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.platform.PathBridge;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Andy Wu
 * @author Simon Jiang
 */
public class GradleProjectProvider extends AbstractLiferayProjectProvider
    implements NewLiferayProjectProvider<NewLiferayModuleProjectOp>
{

    public GradleProjectProvider()
    {
        super( new Class<?>[] { IProject.class } );
    }

    @Override
    public synchronized ILiferayProject provide( Object adaptable )
    {
        ILiferayProject retval = null;

        if( adaptable instanceof IProject )
        {
            final IProject project = (IProject) adaptable;

            try
            {
                if( LiferayNature.hasNature( project ) && GradleProjectNature.isPresentOn( project ) )
                {
                    return new LiferayGradleProject( project );
                }
            }
            catch( Exception e )
            {
                // ignore errors
            }
        }

        return retval;
    }

    @Override
    public IStatus createNewProject( NewLiferayModuleProjectOp op, IProgressMonitor monitor ) throws CoreException
    {
        IStatus retval = Status.OK_STATUS;

        final String projectName = op.getProjectName().content();

        IPath location = PathBridge.create( op.getLocation().content() );

        String className = op.getComponentName().content();

        final String serviceName = op.getServiceName().content();

        final String packageName = op.getPackageName().content();

        ElementList<PropertyKey> propertyKeys = op.getPropertyKeys();

        final List<String> properties = new ArrayList<String>();

        for( PropertyKey propertyKey : propertyKeys )
        {
            properties.add( propertyKey.getName().content( true ) + "=" + propertyKey.getValue().content( true ) );
        }

        final String projectTemplateName = op.getProjectTemplateName().content();

        StringBuilder sb = new StringBuilder();
        sb.append( "create " );
        sb.append( "-d \"" + location.toFile().getAbsolutePath() + "\" " );
        sb.append( "-t " + projectTemplateName + " " );

        if( className != null )
        {
            sb.append( "-c " + className + " " );
        }

        if( serviceName != null )
        {
            sb.append( "-s " + serviceName + " " );
        }

        if( packageName != null )
        {
            sb.append( "-p " + packageName + " " );
        }

        sb.append( "\"" + projectName + "\" " );

        try
        {
            final String[] ret = BladeCLI.execute( sb.toString() );

            final String errors = BladeCLI.checkForErrors( ret );

            if( errors.length() > 0 )
            {
                retval = GradleCore.createErrorStatus( "Project create error: " + errors );
                return retval;
            }

            ElementList<ProjectName> projectNames = op.getProjectNames();

            projectNames.insert().setName( projectName );

            IPath projectLocation = location;

            final String lastSegment = location.lastSegment();

            if( location != null && location.segmentCount() > 0 )
            {
                if( !lastSegment.equals( projectName ) )
                {
                    projectLocation = location.append( projectName );
                }
            }

            /*
            IPath buildGradlePath = projecLocation.append( "build.gradle" );

            if( buildGradlePath.toFile().exists() && serviceName != null )
            {
                NewLiferayModuleProjectOpMethods.addDependencies( buildGradlePath.toFile(), serviceName );
            }
            */

            boolean hasLiferayWorkspace = LiferayWorkspaceUtil.hasLiferayWorkspace();
            boolean useDefaultLocation = op.getUseDefaultLocation().content( true );
            boolean inWorkspacePath = false;

            IProject liferayWorkspaceProject = LiferayWorkspaceUtil.getLiferayWorkspaceProject();

            if( liferayWorkspaceProject != null && !useDefaultLocation )
            {
                IPath workspaceLocation = liferayWorkspaceProject.getLocation();

                if( workspaceLocation != null )
                {
                    String liferayWorkspaceProjectModulesDir =
                        LiferayWorkspaceUtil.getLiferayWorkspaceProjectModulesDir( liferayWorkspaceProject );

                    if( liferayWorkspaceProjectModulesDir != null )
                    {
                        IPath modulesPath = workspaceLocation.append( liferayWorkspaceProjectModulesDir );

                        if( modulesPath.isPrefixOf( projectLocation ) )
                        {
                            inWorkspacePath = true;
                        }
                    }
                }
            }

            if( ( hasLiferayWorkspace && useDefaultLocation ) || inWorkspacePath )
            {
                GradleUtil.refreshGradleProject( liferayWorkspaceProject );
            }
            else
            {
                GradleUtil.importGradleProject( projectLocation.toFile(), monitor );
            }
        }
        catch( Exception e )
        {
            retval = GradleCore.createErrorStatus( "can't create module project.", e );
        }

        return retval;
    }



    @Override
    public IStatus validateProjectLocation( String projectName, IPath path )
    {
        IStatus retval = Status.OK_STATUS;

        if( path != null )
        {
            if( LiferayWorkspaceUtil.isValidWorkspaceLocation( path.toOSString() ) )
            {
                retval =
                    GradleCore.createErrorStatus( " Can't set WorkspaceProject root folder as project directory. " );
            }
        }

        return retval;
    }

}
