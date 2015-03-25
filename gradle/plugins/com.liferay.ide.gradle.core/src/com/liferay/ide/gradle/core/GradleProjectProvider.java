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
import com.liferay.ide.core.ILiferayProjectProvider;
import com.liferay.ide.gradle.toolingapi.custom.CustomModel;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.core.resources.IProject;
import org.springsource.ide.eclipse.gradle.core.GradleCore;
import org.springsource.ide.eclipse.gradle.core.GradleNature;
import org.springsource.ide.eclipse.gradle.core.GradleProject;
import org.springsource.ide.eclipse.gradle.core.modelmanager.IGradleModelListener;


/**
 * @author Gregory Amerson
 */
public class GradleProjectProvider extends AbstractLiferayProjectProvider implements ILiferayProjectProvider, IGradleModelListener
{

    private final Map<GradleProject, Map<String, Boolean>> projectPluginsMap =
        new WeakHashMap<GradleProject, Map<String, Boolean>>();

    public GradleProjectProvider()
    {
        super( new Class<?>[] { IProject.class } );
    }

    private Boolean checkModel( GradleProject gradleProject, String pluginClass )
    {
        final CustomModel model = LRGradleCore.getToolingModel( CustomModel.class, gradleProject );

        return model != null && model.hasPlugin( pluginClass );
    }

    private boolean hasGradleBndPlugin( GradleProject gradleProject )
    {
        return false;
    }

    private boolean hasPlugin( GradleProject gradleProject, String pluginClass )
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

            gradleProject.addModelListener( this );
        }

        return retval == null ? false : retval;
    }

    @Override
    public synchronized <T> void modelChanged( GradleProject project, Class<T> type, T model )
    {
        this.projectPluginsMap.remove( project );
    }

    @Override
    public synchronized ILiferayProject provide( Object adaptable )
    {
        ILiferayProject retval = null;

        if( adaptable instanceof IProject )
        {
            final IProject project = (IProject) adaptable;

            if( GradleNature.hasNature( project ) )
            {
                final GradleProject gradleProject = GradleCore.create( project );

                if( hasPlugin( gradleProject, "org.dm.gradle.plugins.bundle.BundlePlugin" ) )
                {
                    return new GradleBundlePluginProject( project );
                }
                else if( hasGradleBndPlugin( gradleProject ) )
                {
                    return new GradleBndPluginProject( project );
                }
            }
        }

        return retval;
    }

}
