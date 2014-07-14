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

package com.liferay.ide.maven.ui.action;

import com.liferay.ide.maven.ui.LiferayMavenUI;
import com.liferay.ide.maven.ui.MavenUIProjectBuilder;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.ui.action.AbstractObjectAction;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public abstract class MavenGoalAction extends AbstractObjectAction
{

    public MavenGoalAction()
    {
        super();
    }

    protected abstract String getMavenGoals();

    public void run( IAction action )
    {
        if( fSelection instanceof IStructuredSelection )
        {
            Object[] elems = ( (IStructuredSelection) fSelection ).toArray();

            IFile pomXml = null;
            IProject project = null;

            Object elem = elems[0];

            if( elem instanceof IFile )
            {
                pomXml = (IFile) elem;
                project = pomXml.getProject();
            }
            else if( elem instanceof IProject )
            {
                project = (IProject) elem;
                pomXml = project.getFile( IMavenConstants.POM_FILE_NAME );
            }

            if( pomXml.exists() )
            {
                final IProject p = project;
                final IFile pomXmlFile = pomXml;

                final Job job = new Job( p.getName() + " - " + getMavenGoals() ) //$NON-NLS-1$
                {
                    @Override
                    protected IStatus run( IProgressMonitor monitor )
                    {
                        try
                        {
                            monitor.beginTask( getMavenGoals(), 100 );

                            runMavenGoal( pomXmlFile, getMavenGoals(), monitor );

                            monitor.worked( 80 );

                            p.refreshLocal( IResource.DEPTH_INFINITE, monitor );

                            monitor.worked( 10 );

                            updateProject( p, monitor );

                            monitor.worked( 10 );
                        }
                        catch( Exception e )
                        {
                            return ProjectUI.createErrorStatus( "Error running Maven goal " + getMavenGoals(), e ); //$NON-NLS-1$
                        }

                        return Status.OK_STATUS;
                    }
                };

                job.schedule();
            }
        }
    }

    private void runMavenGoal( IFile pomFile, String goal, IProgressMonitor monitor ) throws CoreException
    {
        final IMavenProjectRegistry projectManager = MavenPlugin.getMavenProjectRegistry();
        final IMavenProjectFacade projectFacade = projectManager.create( pomFile, false, new NullProgressMonitor() );
        final MavenUIProjectBuilder builder = new MavenUIProjectBuilder( pomFile.getProject() );

        builder.runMavenGoal( projectFacade, goal, "run", monitor );
    }

    protected void updateProject( IProject p, IProgressMonitor monitor )
    {
        try
        {
            p.refreshLocal( IResource.DEPTH_INFINITE, monitor );
        }
        catch( CoreException e )
        {
            LiferayMavenUI.logError( "Error refreshing project after " + getMavenGoals(), e );
        }
    }
}
