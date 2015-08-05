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

import org.eclipse.buildship.core.configuration.GradleProjectNature;
import org.eclipse.core.resources.IProject;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class GradleProjectProvider extends AbstractLiferayProjectProvider implements ILiferayProjectProvider
{

    private final Map<IProject, Map<String, Boolean>> projectPluginsMap =
        new WeakHashMap<IProject, Map<String, Boolean>>();

    public GradleProjectProvider()
    {
        super( new Class<?>[] { IProject.class } );
    }

    private Boolean checkModel( IProject gradleProject, String pluginClass )
    {
        final CustomModel model = LRGradleCore.getToolingModel( CustomModel.class, gradleProject );

        return model != null && model.hasPlugin( pluginClass );
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

    @Override
    public synchronized ILiferayProject provide( Object adaptable )
    {
        ILiferayProject retval = null;

        if( adaptable instanceof IProject )
        {
            final IProject project = (IProject) adaptable;

            if( GradleProjectNature.INSTANCE.isPresentOn( project ) )
            {
                if( hasPlugin( project, "aQute.bnd.gradle.BndBuilderPlugin" ) ||
                    hasPlugin( project, "org.dm.gradle.plugins.bundle.BundlePlugin" ) )
                {
                    return new GradleBundleProject( project );
                }
            }
        }

        return retval;
    }

}
