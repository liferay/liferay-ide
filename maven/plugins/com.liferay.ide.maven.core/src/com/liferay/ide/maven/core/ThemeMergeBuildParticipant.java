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

import com.liferay.ide.theme.core.ThemeCSSBuilder;

import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.osgi.util.NLS;


/**
 * @author Gregory Amerson
 */
public class ThemeMergeBuildParticipant extends ThemePluginBuildParticipant
{

    @Override
    public Set<IProject> build( int kind, IProgressMonitor monitor ) throws Exception
    {
        IProgressMonitor sub = new SubProgressMonitor( monitor, 100 );

        sub.beginTask( Msgs.mergingTheme, 100 );

        final Set<IProject> retval = super.build( kind, monitor );

        try
        {
            ThemeCSSBuilder.ensureLookAndFeelFileExists( getMavenProjectFacade().getProject() );
        }
        catch( CoreException e )
        {
            LiferayMavenCore.logError( "Unable to ensure look and feel file exists", e ); //$NON-NLS-1$
        }

        sub.done();

        return retval;
    }

    @Override
    protected String getGoal()
    {
        return ILiferayMavenConstants.PLUGIN_GOAL_THEME_MERGE;
    }

    protected boolean shouldBuild( int kind, IMavenProjectFacade facade )
    {
        boolean retval = false;

        final IResourceDelta delta = this.getDelta( facade.getProject() );

        //TODO don't hard code path of src/main/webapp/css
        if( delta != null && ( delta.findMember( new Path( "src/main/webapp" ) ) != null || //$NON-NLS-1$
            delta.findMember( new Path( "pom.xml" ) ) != null ) ) //$NON-NLS-1$
        {
            retval = true;
        }

        return retval;
    }

    private static class Msgs extends NLS
    {
        public static String mergingTheme;

        static
        {
            initializeMessages( ThemeMergeBuildParticipant.class.getName(), Msgs.class );
        }
    }

}
