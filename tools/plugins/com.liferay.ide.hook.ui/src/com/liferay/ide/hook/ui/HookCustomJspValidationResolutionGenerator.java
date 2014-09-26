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

package com.liferay.ide.hook.ui;

import com.liferay.ide.hook.core.HookCore;
import com.liferay.ide.hook.core.util.HookUtil;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator2;

/**
 * @author Simon Jiang
 */
public class HookCustomJspValidationResolutionGenerator implements IMarkerResolutionGenerator2
{

    public IMarkerResolution[] getResolutions( IMarker marker )
    {
        return new IMarkerResolution[] { new HookCustomJspValidationResolution() };
    }

    public boolean hasResolutions( IMarker marker )
    {
        boolean hasResolution = false;
        try
        {
            if( marker.getAttribute( IMarker.SEVERITY ).equals( IMarker.SEVERITY_ERROR ) )
            {
                final IProject project = marker.getResource().getProject();

                final IPath customJspPath = HookUtil.getCustomJspPath( project );

                if( customJspPath != null )
                {
                    final IPath jspPath = marker.getResource().getProjectRelativePath();
                    final IPath relativeCustomJspPath = customJspPath.makeRelativeTo( project.getFullPath() );

                    if( relativeCustomJspPath.isPrefixOf( jspPath ) )
                    {
                        hasResolution = true;
                    }
                }
            }
        }
        catch( Exception e )
        {
            HookCore.logError( "Get Marker attribute error. ", e );
        }

        return hasResolution;
    }
}
