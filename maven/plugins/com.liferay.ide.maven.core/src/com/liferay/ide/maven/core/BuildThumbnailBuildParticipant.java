/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.Path;
import org.eclipse.m2e.core.project.IMavenProjectFacade;


/**
 * @author Gregory Amerson
 */
public class BuildThumbnailBuildParticipant extends ThemePluginBuildParticipant
{

    @Override
    protected void configureExecution( IMavenProjectFacade facade, Xpp3Dom config )
    {
        // dont call super.configure() because we don't need the webappDir
    }

    @Override
    protected String getGoal()
    {
        return ILiferayMavenConstants.PLUGIN_GOAL_BUILD_THUMBNAIL;
    }

    @Override
    protected boolean shouldBuild( int kind, IMavenProjectFacade facade )
    {
        boolean retval = false;

        final IResourceDelta delta = this.getDelta( facade.getProject() );

        //TODO IDE-935 don't hard code path of src/main/webapp/
        if( delta != null && delta.findMember( new Path( "src/main/webapp/images/screenshot.png" ) ) != null ) //$NON-NLS-1$
        {
            retval = true;
        }

        return retval;
    }
}
