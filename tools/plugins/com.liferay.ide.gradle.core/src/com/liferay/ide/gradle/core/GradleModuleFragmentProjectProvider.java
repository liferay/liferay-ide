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
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.modules.BladeCLI;
import com.liferay.ide.project.core.modules.fragment.NewModuleFragmentOp;
import com.liferay.ide.project.core.modules.fragment.NewModuleFragmentOpMethods;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.buildship.core.CorePlugin;
import org.eclipse.buildship.core.workspace.NewProjectHandler;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.sapphire.platform.PathBridge;

/**
 * @author Terry Jia
 * @author Lovett Li
 */
public class GradleModuleFragmentProjectProvider extends AbstractLiferayProjectProvider
    implements NewLiferayProjectProvider<NewModuleFragmentOp>
{

    public GradleModuleFragmentProjectProvider()
    {
        super( null );
    }

    @Override
    public synchronized ILiferayProject provide( Object adaptable )
    {
        return null; // this only provides new projects
    }

    @Override
    public IStatus createNewProject( NewModuleFragmentOp op, IProgressMonitor monitor ) throws CoreException
    {
        IStatus retval = Status.OK_STATUS;

        final String projectName = op.getProjectName().content();
        final IPath location = PathBridge.create( op.getLocation().content() );

        String[] bsnAndVersion = NewModuleFragmentOpMethods.getBsnAndVersion( op );
        String bundleSymbolicName = bsnAndVersion[0];
        String version = bsnAndVersion[1];

        final StringBuilder sb = new StringBuilder();

        sb.append( "create " );
        sb.append( "-d \"" + location.toFile().getAbsolutePath() + "\" " );
        sb.append( "-t " + "fragment" + " " );

        if( !bundleSymbolicName.equals( "" ) )
        {
            sb.append( "-h " + bundleSymbolicName + " " );
        }

        if( !version.equals( "" ) )
        {
            sb.append( "-H " + version + " " );
        }

        sb.append( "\"" + projectName + "\" " );

        try
        {
            BladeCLI.execute( sb.toString() );
        }
        catch( Exception e )
        {
            return GradleCore.createErrorStatus( "Could not create module fragment project.", e );
        }

        NewModuleFragmentOpMethods.copyOverrideFiles( op );

        IPath projecLocation = location.append( projectName );

        final boolean hasLiferayWorkspace = LiferayWorkspaceUtil.hasWorkspace();
        final boolean useDefaultLocation = op.getUseDefaultLocation().content( true );
        boolean inWorkspacePath = false;

        final IProject liferayWorkspaceProject = LiferayWorkspaceUtil.getWorkspaceProject();

        if( liferayWorkspaceProject != null && !useDefaultLocation )
        {
            IPath workspaceLocation = liferayWorkspaceProject.getLocation();

            if( workspaceLocation != null )
            {
                String liferayWorkspaceProjectModulesDir =
                    LiferayWorkspaceUtil.getModulesDir( liferayWorkspaceProject );

                if( liferayWorkspaceProjectModulesDir != null )
                {
                    IPath modulesPath = workspaceLocation.append( liferayWorkspaceProjectModulesDir );

                    if( modulesPath.isPrefixOf( projecLocation ) )
                    {
                        inWorkspacePath = true;
                    }
                }
            }
        }

        if( ( hasLiferayWorkspace && useDefaultLocation ) || inWorkspacePath )
        {
            Set<IProject> projects = new HashSet<>();

            projects.add( liferayWorkspaceProject );

            CorePlugin.gradleWorkspaceManager().getCompositeBuild( projects ).synchronize(
                NewProjectHandler.IMPORT_AND_MERGE );
        }
        else
        {
            GradleUtil.importGradleProject( projecLocation.toFile(), monitor );
        }

        return retval;
    }

    @Override
    public IStatus validateProjectLocation( String projectName, IPath path )
    {
        return Status.OK_STATUS;
    }
}
