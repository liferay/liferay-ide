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

package com.liferay.ide.core;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.PropertiesUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator2;

/**
 * @author Kuo Zhang
 */
public class LiferayLanguagePropertiesMarkerResolutionGenerator implements IMarkerResolutionGenerator2
{

    // Questions: In the quick fix, I have two resolutions, how can I decide which one is the first one.
    public IMarkerResolution[] getResolutions( IMarker marker )
    {
        List<IMarkerResolution> resolutions = new ArrayList<IMarkerResolution>();

        try
        {
            if( LiferayLanguagePropertiesValidator.ID_LANGUAGE_PROPERTIES_ENCODING_NOT_DEFAULT.equals( marker.getAttribute( IMarker.SOURCE_ID ) ) )
            {
                IMarkerResolution encodeThisFileToDefault = new IMarkerResolution()
                {

                    public String getLabel()
                    {
                        return Msgs.encodeThisFileToDefault;
                    }

                    public void run( IMarker marker )
                    {
                        if( marker.getResource().getType() == IResource.FILE )
                        {
                            final IFile file = (IFile) marker.getResource();

                            PropertiesUtil.encodeLanguagePropertiesFilesToDefault( file, new NullProgressMonitor() );
                        }
                    }
                };

                IMarkerResolution encodeAllFilesToDefault = new IMarkerResolution()
                {

                    public String getLabel()
                    {
                        return Msgs.encodeAllFilesToDefault;
                    }

                    public void run( IMarker marker )
                    {
                        IProject proj = CoreUtil.getLiferayProject( marker.getResource() );

                        if( proj != null && proj.exists() )
                        {
                            PropertiesUtil.encodeLanguagePropertiesFilesToDefault( proj, new NullProgressMonitor() );
                        }
                    }
                };

                resolutions.add( encodeThisFileToDefault );
                resolutions.add( encodeAllFilesToDefault );
            }
        }
        catch( CoreException e )
        {
            LiferayCore.logError( e );
        }

        return resolutions.toArray( new IMarkerResolution[0] );
    }

    public boolean hasResolutions( IMarker marker )
    {
        try
        {
            if( LiferayLanguagePropertiesValidator.LIFERAY_LANGUAGE_PROPERTIES_MARKER_TYPE.equals( marker.getType() ) &&
                LiferayLanguagePropertiesValidator.ID_LANGUAGE_PROPERTIES_ENCODING_NOT_DEFAULT.equals( marker.getAttribute( IMarker.SOURCE_ID ) ) )
            {
                return true;
            }
        }
        catch( CoreException e )
        {
            LiferayCore.logError( e );
        }

        return false;
    }

    private static class Msgs extends NLS
    {
        public static String encodeThisFileToDefault;
        public static String encodeAllFilesToDefault;

        static
        {
            initializeMessages( LiferayLanguagePropertiesMarkerResolutionGenerator.class.getName(), Msgs.class );
        }
    }

}
