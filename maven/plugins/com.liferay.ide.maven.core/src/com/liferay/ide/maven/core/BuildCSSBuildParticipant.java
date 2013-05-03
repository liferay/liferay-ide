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

import java.io.File;

import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.core.runtime.IPath;
import org.eclipse.m2e.core.project.IMavenProjectFacade;


/**
 * @author Gregory Amerson
 */
public class BuildCSSBuildParticipant extends ThemePluginBuildParticipant
{

    @Override
    protected void configureExecution( IMavenProjectFacade facade, Xpp3Dom config )
    {
        super.configureExecution( facade, config );

        final IPath m2eLiferayFolder =
                        MavenUtil.getM2eLiferayFolder( facade.getMavenProject(), facade.getProject() );
        final IPath themeResourcesFolder =
                        m2eLiferayFolder.append( ILiferayMavenConstants.THEME_RESOURCES_FOLDER );
        // Must use full path because sassDirNames is a string and not a "file"
        final File projectDir = new File( facade.getProject().getLocationURI() );
        final File themeResourcesDir = new File( projectDir, themeResourcesFolder.toOSString() );

        MavenUtil.setConfigValue(
            config, ILiferayMavenConstants.PLUGIN_CONFIG_SASS_DIR_NAMES, themeResourcesDir.getAbsolutePath() );
    }

    @Override
    protected String getGoal()
    {
        return ILiferayMavenConstants.PLUGIN_GOAL_BUILD_CSS;
    }

    @Override
    protected boolean shouldBuild( int kind, IMavenProjectFacade facade )
    {
        boolean retval = false;

//        final IResourceDelta delta = this.getDelta( facade.getProject() );
//
//        //TODO IDE-935 don't hard code path of src/main/webapp/css
//        if( delta != null && delta.findMember( new Path( "src/main/webapp/css" ) ) != null ) //$NON-NLS-1$
//        {
//            retval = true;
//        }

        return retval;
    }



}
