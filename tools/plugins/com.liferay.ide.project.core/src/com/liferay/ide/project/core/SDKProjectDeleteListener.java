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

package com.liferay.ide.project.core;

import com.liferay.ide.project.core.util.ResourceFilterUtil;
import com.liferay.ide.sdk.core.SDKUtil;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;

/**
 * @author Andy Wu
 */
public class SDKProjectDeleteListener implements IResourceChangeListener
{

    @Override
    public void resourceChanged( IResourceChangeEvent event )
    {
        try
        {
            if( event.getType() == IResourceChangeEvent.PRE_DELETE )
            {
                IProject project = (IProject) event.getResource();

                if( SDKUtil.isSDKProject( project ) )
                {
                    String parentName = project.getLocation().toFile().getParentFile().getName();
                    IProject sdkProject = SDKUtil.getWorkspaceSDKProject();

                    if( sdkProject != null )
                    {
                        IFolder parentFolder = sdkProject.getFolder( parentName );

                        ResourceFilterUtil.deleteResourceFilter( parentFolder, project.getName() );
                    }
                }

                return;
            }
        }
        catch( Exception e )
        {
            ProjectCore.logError( "delete project resource filter error", e );
        }

        return;
    }
}
