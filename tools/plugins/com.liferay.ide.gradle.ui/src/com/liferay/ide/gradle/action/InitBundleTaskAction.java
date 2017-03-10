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

package com.liferay.ide.gradle.action;

import com.liferay.ide.gradle.core.GradleCore;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;
import com.liferay.ide.server.util.ServerUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * @author Terry Jia
 */
public class InitBundleTaskAction extends GradleTaskAction
{

    protected void afterTask()
    {
        IProject project = LiferayWorkspaceUtil.getLiferayWorkspaceProject();

        IPath bundlesLocation = LiferayWorkspaceUtil.loadConfiguredHomePath( project );

        String serverName = bundlesLocation.lastSegment();

        try
        {
            if( bundlesLocation.toFile().exists() )
            {
                ServerUtil.addPortalRuntimeAndServer( serverName, bundlesLocation, new NullProgressMonitor() );
            }
        }
        catch( CoreException e )
        {
            GradleCore.logError( "Adding server failed", e );
        }
    }

    @Override
    protected String getGradleTask()
    {
        return "initBundle";
    }

}
