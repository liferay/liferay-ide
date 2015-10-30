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

package com.liferay.ide.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;

/**
 * @author Terry Jia
 */
public class LiferayNature implements IProjectNature
{

    public static final String NATURE_ID = LiferayCore.PLUGIN_ID + ".liferayNature";
    private static final String NATURE_IDS[] = { LiferayNature.NATURE_ID };

    private IProject currentProject;
    private IProgressMonitor monitor;


    public LiferayNature()
    {
        monitor = new NullProgressMonitor();
    }

    public LiferayNature( IProject project, IProgressMonitor monitor )
    {
        currentProject = project;

        if( monitor != null )
        {
            this.monitor = monitor;
        }
        else
        {
            monitor = new NullProgressMonitor();
        }

    }

    public static void addLiferayNature( IProject project, IProgressMonitor monitor ) throws CoreException
    {
        if( monitor != null && monitor.isCanceled() )
        {
            throw new OperationCanceledException();
        }

        if( !LiferayNature.hasNature( project ) )
        {
            IProjectDescription description = project.getDescription();

            String[] prevNatures = description.getNatureIds();
            String[] newNatures = new String[prevNatures.length + LiferayNature.NATURE_IDS.length];

            System.arraycopy( prevNatures, 0, newNatures, 0, prevNatures.length );

            for( int i = 0; i < LiferayNature.NATURE_IDS.length; i++ )
            {
                newNatures[prevNatures.length + i] = LiferayNature.NATURE_IDS[i];
            }

            description.setNatureIds( newNatures );
            project.setDescription( description, monitor );
        }
        else
        {
            if( monitor != null )
            {
                monitor.worked( 1 );
            }
        }
    }

    @Override
    public void configure() throws CoreException
    {
        LiferayNature.addLiferayNature( currentProject, monitor );

        currentProject.refreshLocal( IResource.DEPTH_INFINITE, monitor );
    }

    @Override
    public void deconfigure() throws CoreException
    {
        LiferayNature.removeLiferayNature( currentProject, monitor );

        currentProject.refreshLocal( IResource.DEPTH_INFINITE, monitor );
    }

    public IProject getProject()
    {
        return this.currentProject;
    }

    public static boolean hasNature( IProject project )
    {
        try
        {
            for( int i = 0; i < LiferayNature.NATURE_IDS.length; i++ )
            {
                if( !project.hasNature( LiferayNature.NATURE_IDS[i] ) )
                {
                    return false;
                }
            }
        }
        catch( CoreException e )
        {
            return false;
        }

        return true;
    }

    public static void removeLiferayNature( IProject project, IProgressMonitor monitor ) throws CoreException
    {
        if( monitor != null && monitor.isCanceled() )
        {
            throw new OperationCanceledException();
        }

        if( LiferayNature.hasNature( project ) )
        {
            IProjectDescription description = project.getDescription();

            String[] prevNatures = description.getNatureIds();
            String[] newNatures = new String[prevNatures.length - LiferayNature.NATURE_IDS.length];

            int k = 0;

            head: for( int i = 0; i < prevNatures.length; i++ )
            {
                for( int j = 0; j < LiferayNature.NATURE_IDS.length; j++ )
                {
                    if( prevNatures[i].equals( LiferayNature.NATURE_IDS[j] ) )
                    {
                        continue head;
                    }
                }

                newNatures[k++] = prevNatures[i];
            }

            description.setNatureIds( newNatures );
            project.setDescription( description, monitor );
        }
        else
        {
            if( monitor != null )
            {
                monitor.worked( 1 );
            }
        }
    }

    @Override
    public void setProject( IProject project )
    {
        this.currentProject = project;
    }

}
