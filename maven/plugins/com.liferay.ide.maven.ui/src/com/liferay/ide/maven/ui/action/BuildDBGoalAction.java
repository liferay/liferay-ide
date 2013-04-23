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
package com.liferay.ide.maven.ui.action;

import com.liferay.ide.maven.core.ILiferayMavenConstants;
import com.liferay.ide.maven.ui.LiferayMavenUI;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;


/**
 * @author Gregory Amerson
 */
public class BuildDBGoalAction extends MavenGoalAction
{

    @Override
    protected String getMavelGoal()
    {
        return ILiferayMavenConstants.PLUGIN_GOAL_BUILD_DB;
    }

    @Override
    protected void updateProject( IProject p, IProgressMonitor monitor )
    {
        try
        {
            p.refreshLocal( IResource.DEPTH_INFINITE, monitor );
        }
        catch( CoreException e )
        {
            LiferayMavenUI.logError( "Error refreshing project after liferay:build-db", e );
        }
    }

}
