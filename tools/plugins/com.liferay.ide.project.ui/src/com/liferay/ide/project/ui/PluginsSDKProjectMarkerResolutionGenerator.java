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

package com.liferay.ide.project.ui;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator2;

import com.liferay.ide.project.core.PluginsSDKProjectValidator;

/**
 * @author Kuo Zhang
 */
public class PluginsSDKProjectMarkerResolutionGenerator implements IMarkerResolutionGenerator2
{

    public IMarkerResolution[] getResolutions( IMarker marker )
    {
        IMarkerResolution resolution = null;

        try
        {
            final String markerSourceId = (String) marker.getAttribute( IMarker.SOURCE_ID );

            if( markerSourceId.equals( PluginsSDKProjectValidator.ID_PRIMARY_RUNTIME_NOT_SET ) )
            {
                resolution = new PrimaryRuntimeNotSetResolution();
            }
            else if( markerSourceId.equals( PluginsSDKProjectValidator.ID_PRIMARY_RUNTIME_NOT_LIFERAY_RUNTIME ) )
            {
                resolution = new PrimaryRuntimeNotLiferayRuntimeResolution();
            }

        }
        catch( CoreException e )
        {
            ProjectUIPlugin.logError( "Marker cannot be found.", e ); //$NON-NLS-1$
        }

        return new IMarkerResolution[] { resolution };
    }

    public boolean hasResolutions( IMarker marker )
    {
        try
        {
            return marker.getType().equals( PluginsSDKProjectValidator.MARKER_TYPE );
        }
        catch( CoreException e )
        {
            ProjectUIPlugin.logError( "The marker doesn't exist.", e ); //$NON-NLS-1$
        }

        return false;
    }

}
