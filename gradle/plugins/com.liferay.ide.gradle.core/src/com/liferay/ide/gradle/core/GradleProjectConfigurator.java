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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.liferay.ide.core.LiferayNature;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.gradle.toolingapi.custom.CustomModel;

/**
 * @author Andy Wu
 */
public class GradleProjectConfigurator implements IResourceChangeListener
{
    private final Map<IProject, Map<String, Boolean>> projectPluginsMap =
                    new WeakHashMap<IProject, Map<String, Boolean>>();

    private Boolean checkModel( IProject gradleProject, String pluginClass )
    {
        final CustomModel model = LRGradleCore.getToolingModel( CustomModel.class, gradleProject );

        return model != null && model.hasPlugin( pluginClass );
    }

    private List<IProject> getProjects( IResourceDelta delta )
    {
        final List<IProject> projects = new ArrayList<IProject>();

        try
        {
            delta.accept( new IResourceDeltaVisitor()
            {
                public boolean visit( IResourceDelta delta ) throws CoreException
                {
                    if( delta.getKind() == IResourceDelta.ADDED && delta.getResource().getType() == IResource.PROJECT )
                    {
                        IProject project = (IProject) delta.getResource();

                        if( project.isAccessible() )
                        {
                            projects.add( project );
                        }
                    }

                    // only continue for the workspace root
                    return delta.getResource().getType() == IResource.ROOT;
                }
            } );
        }
        catch( CoreException e )
        {
            // ignore errors
        }

        return projects;
    }

    private boolean hasPlugin( IProject gradleProject, String pluginClass )
    {
        Boolean retval = null;

        final Map<String, Boolean> projectPlugins = this.projectPluginsMap.get( gradleProject );

        if( projectPlugins != null )
        {
            if( projectPlugins.containsKey( pluginClass ) )
            {
                retval = projectPlugins.get( pluginClass );
            }
            else
            {
                retval = checkModel( gradleProject, pluginClass );
                projectPlugins.put( pluginClass, retval );
            }
        }
        else
        {
            retval = checkModel( gradleProject, pluginClass );

            final Map<String, Boolean> plugins = new HashMap<String, Boolean>();
            plugins.put( pluginClass, retval );
            this.projectPluginsMap.put( gradleProject, plugins );
        }

        return retval == null ? false : retval;
    }

    public void resourceChanged( IResourceChangeEvent event )
    {
        if( event.getType() == IResourceChangeEvent.POST_CHANGE )
        {
            final List<IProject> projects = getProjects( event.getDelta() );

            if( projects.size() > 0)
            {
                final Job job = new WorkspaceJob( "Configuring Gradle project..." )
                {
                    @Override
                    public IStatus runInWorkspace( IProgressMonitor monitor ) throws CoreException
                    {
                        for( IProject project : projects )
                        {
                            if( hasPlugin( project, "aQute.bnd.gradle.BndBuilderPlugin" ) ||
                                hasPlugin( project, "org.dm.gradle.plugins.bundle.BundlePlugin" ) )
                            {
                                LiferayNature.addLiferayNature( project, monitor );
                            }
                        }

                        return Status.OK_STATUS;
                    }
                };

                job.setRule( CoreUtil.getWorkspaceRoot() );
                job.schedule();
            }
        }
    }


}
