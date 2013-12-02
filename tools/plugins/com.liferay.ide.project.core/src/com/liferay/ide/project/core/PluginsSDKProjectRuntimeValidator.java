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

package com.liferay.ide.project.core;

import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.server.util.ServerUtil;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectValidator;
import org.eclipse.wst.common.project.facet.core.runtime.internal.BridgedRuntime;

/**
 * @author Kuo Zhang
 */
@SuppressWarnings( "restriction" )
public class PluginsSDKProjectRuntimeValidator implements IFacetedProjectValidator
{
    public static final String LOCATION_TARGETED_RUNTIMES = "Targeted Runtimes";

    public static final String ID_PRIMARY_RUNTIME_NOT_SET = "primary-runtime-not-set";
    public static final String ID_PRIMARY_RUNTIME_NOT_LIFERAY_RUNTIME = "primary-runtime-not-liferay-runtime";

    public static final String MSG_PRIMARY_RUNTIME_NOT_SET = Msgs.primaryRuntimeNotSet;
    public static final String MSG_PRIMARY_RUNTIME_NOT_LIFERAY_RUNTIME = Msgs.primaryRuntimeNotLiferayRuntime;

    public void validate( IFacetedProject fproj ) throws CoreException
    {
        final IProject proj = fproj.getProject();

        clearMarkers( proj );

        if( SDKUtil.isSDKProject( fproj.getProject() ) )
        {
            if( fproj.getPrimaryRuntime() == null )
            {
                setMarker(
                    proj, LiferayProjectCore.LIFERAY_PROJECT_MARKR_TYPE, IMarker.SEVERITY_ERROR,
                    MSG_PRIMARY_RUNTIME_NOT_SET, LOCATION_TARGETED_RUNTIMES, ID_PRIMARY_RUNTIME_NOT_SET );
            }
            else
            {
                if( ! ServerUtil.isLiferayRuntime( (BridgedRuntime) fproj.getPrimaryRuntime() ) )
                {
                    setMarker(
                        proj, LiferayProjectCore.LIFERAY_PROJECT_MARKR_TYPE, IMarker.SEVERITY_ERROR,
                        MSG_PRIMARY_RUNTIME_NOT_LIFERAY_RUNTIME, LOCATION_TARGETED_RUNTIMES,
                        ID_PRIMARY_RUNTIME_NOT_LIFERAY_RUNTIME );
                }
            }
        }
    }

    private void clearMarkers( IProject proj )
    {
        try
        {
            if( proj.isOpen() )
            {
                IMarker[] markers =
                    proj.findMarkers( LiferayProjectCore.LIFERAY_PROJECT_MARKR_TYPE, true, IResource.DEPTH_INFINITE );

                for( IMarker marker : markers )
                {
                    for( String id : getMarkerSourceIds() )
                    {
                        if( marker.getAttribute( IMarker.SOURCE_ID ).equals( id ) )
                        {
                            marker.delete();
                        }
                    }
                }
            }
        }
        catch( CoreException e )
        {
            LiferayProjectCore.logError( e );
        }
    }

    private String[] getMarkerSourceIds()
    {
        String[] retval = {ID_PRIMARY_RUNTIME_NOT_LIFERAY_RUNTIME,ID_PRIMARY_RUNTIME_NOT_SET};

        return retval;
    }

    private void setMarker(
        IProject proj, String markerType, int markerSeverity, String markerMsg, String markerLocation,
        String markerSourceId ) throws CoreException
    {
        IMarker marker = proj.createMarker( markerType );

        marker.setAttribute( IMarker.SEVERITY, markerSeverity );
        marker.setAttribute( IMarker.MESSAGE, markerMsg );
        marker.setAttribute( IMarker.LOCATION, markerLocation );
        marker.setAttribute( IMarker.SOURCE_ID, markerSourceId );
    }

    private static class Msgs extends NLS
    {
        public static String primaryRuntimeNotSet;
        public static String primaryRuntimeNotLiferayRuntime;

        static
        {
            initializeMessages( PluginsSDKProjectRuntimeValidator.class.getName(), Msgs.class );
        }
    }

}
