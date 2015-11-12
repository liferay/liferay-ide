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

import com.gradleware.tooling.toolingclient.GradleDistribution;
import com.liferay.blade.api.ProjectBuild;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayNature;
import com.liferay.ide.project.core.ILiferayModuleProjectProvider;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.model.modules.NewLiferayModuleProjectOp;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.buildship.core.configuration.GradleProjectNature;
import org.eclipse.buildship.core.projectimport.ProjectImportConfiguration;
import org.eclipse.buildship.core.util.gradle.GradleDistributionWrapper;
import org.eclipse.buildship.core.util.progress.AsyncHandler;
import org.eclipse.buildship.core.workspace.SynchronizeGradleProjectJob;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.sapphire.platform.PathBridge;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Andy Wu
 * @author Simon Jiang
 */
public class GradleProjectProvider extends NewLiferayProjectProvider implements ILiferayModuleProjectProvider
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
                if( LiferayNature.hasNature( project ) && GradleProjectNature.INSTANCE.isPresentOn( project ) )
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
    public IStatus createNewProject( Object operation, IProgressMonitor monitor ) throws CoreException
    {
        IStatus retval = null;

        if( ! (operation instanceof NewLiferayModuleProjectOp ) )
        {
            throw new IllegalArgumentException( "Operation must be of type NewLiferayModuleProjectOp" ); //$NON-NLS-1$
        }

        final NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.class.cast( operation );

        final String projectName = op.getProjectName().content();

        IPath location = PathBridge.create( op.getLocation().content() );

        // for location we should use the parent location
        if( location.lastSegment().equals( projectName ) )
        {
            // use parent dir since maven archetype will generate new dir under this location
            location = location.removeLastSegments( 1 );
        }

        final String projectType = op.getProjectTemplate().content().toString();

        retval =
            createOSGIBundleProject(
                location.toFile(), location.toFile(), projectType, ProjectBuild.gradle.toString(), projectName,
                projectName, projectName );

        if( retval.isOK() )
        {
            ProjectImportConfiguration configuration = new ProjectImportConfiguration();
            GradleDistributionWrapper from = GradleDistributionWrapper.from( GradleDistribution.fromBuild() );
            configuration.setGradleDistribution( from );
            configuration.setProjectDir( location.append( projectName ).toFile() );
            configuration.setApplyWorkingSets( false );
            configuration.setWorkingSets( new ArrayList<String>() );
            new SynchronizeGradleProjectJob(
                configuration.toFixedAttributes(), configuration.getWorkingSets().getValue(),
                AsyncHandler.NO_OP ).schedule();
        }

        return retval;
    }

    @Override
    public IStatus validateProjectLocation( String projectName, IPath path )
    {
        IStatus retval = Status.OK_STATUS;
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
