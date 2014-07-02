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

package com.liferay.ide.portlet.core.job;

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.portlet.core.PortletCore;
import com.liferay.ide.project.core.IProjectBuilder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;

/**
 * @author Gregory Amerson
 */
public class BuildLanguageJob extends Job
{

    protected IFile langFile;

    public BuildLanguageJob( IFile langFile )
    {
        super( Msgs.buildLanguages );

        this.langFile = langFile;
        setUser( true );
    }

    @Override
    protected IStatus run( IProgressMonitor monitor )
    {
        IStatus retval = null;

        IWorkspaceDescription desc = ResourcesPlugin.getWorkspace().getDescription();

        boolean saveAutoBuild = desc.isAutoBuilding();

        desc.setAutoBuilding( false );

        monitor.beginTask( Msgs.buildingLanguages, 100 );

        final IWorkspaceRunnable workspaceRunner = new IWorkspaceRunnable()
        {
            public void run( IProgressMonitor monitor ) throws CoreException
            {
                runBuildLang( monitor );
            }
        };

        try
        {
            ResourcesPlugin.getWorkspace().setDescription( desc );

            ResourcesPlugin.getWorkspace().run( workspaceRunner, monitor );
        }
        catch( CoreException e1 )
        {
            retval = PortletCore.createErrorStatus( e1 );
        }
        finally
        {
            desc = ResourcesPlugin.getWorkspace().getDescription();
            desc.setAutoBuilding( saveAutoBuild );

            try
            {
                ResourcesPlugin.getWorkspace().setDescription( desc );
            }
            catch( CoreException e1 )
            {
                retval = PortletCore.createErrorStatus( e1 );
            }
        }

        return retval == null || retval.isOK() ? Status.OK_STATUS : retval;
    }

    protected void runBuildLang( IProgressMonitor monitor ) throws CoreException
    {
        final ILiferayProject liferayProject = LiferayCore.create( getProject() );

        if( liferayProject == null )
        {
            throw new CoreException( PortletCore.createErrorStatus( NLS.bind(
                Msgs.couldNotCreateLiferayProject, getProject() ) ) );
        }

        final IProjectBuilder builder = liferayProject.adapt( IProjectBuilder.class );

        if( builder == null )
        {
            throw new CoreException( PortletCore.createErrorStatus( NLS.bind(
                Msgs.couldNotCreateProjectBuilder, getProject() ) ) );
        }

        monitor.worked( 50 );

        IStatus retval = builder.buildLang( this.langFile, monitor );

        if( retval == null )
        {
            retval = PortletCore.createErrorStatus( NLS.bind( Msgs.errorRunningBuildLang, getProject() ) );
        }

        try
        {
            getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor);
        }
        catch( Exception e )
        {
            PortletCore.logError( e );
        }

        getProject().build( IncrementalProjectBuilder.INCREMENTAL_BUILD, monitor );

        try
        {
            getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor);
        }
        catch( Exception e )
        {
            PortletCore.logError( e );
        }

        if( retval == null || ! retval.isOK() )
        {
            throw new CoreException( retval );
        }

        monitor.worked( 90 );
    }

    private IProject getProject()
    {
        return this.langFile.getProject();
    }

    private static class Msgs extends NLS
    {
        public static String buildingLanguages;
        public static String buildLanguages;
        public static String couldNotCreateProjectBuilder;
        public static String couldNotCreateLiferayProject;
        public static String errorRunningBuildLang;

        static
        {
            initializeMessages( BuildLanguageJob.class.getName(), Msgs.class );
        }
    }
}
