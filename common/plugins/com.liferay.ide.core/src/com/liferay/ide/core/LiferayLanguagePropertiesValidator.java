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

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.osgi.util.NLS;

/**
 * @author Kuo Zhang
 */
public class LiferayLanguagePropertiesValidator
{

    public final static String ID_LANGUAGE_PROPERTIES_ENCODING_NOT_DEFAULT = "language-properties-encoding-not-defalut";

    public final static String LIFERAY_LANGUAGE_PROPERTIES_MARKER_TYPE = "com.liferay.ide.core.LiferayLanguagePropertiesMarker";

    private static WeakHashMap<IFile, WeakReference<LiferayLanguagePropertiesValidator>> filesAndValidators =
        new WeakHashMap<IFile, WeakReference<LiferayLanguagePropertiesValidator>>();

    public static final String LOCATION_ENCODING= "Properties/Resource/Text file encoding";

    public final static String MESSAGE_LANGUAGE_PROPERTIES_ENCODING_NOT_DEFALUT = Msgs.languagePropertiesEncodingNotDefault;

    private IFile file;

    private Set<IMarker> markers = new HashSet<IMarker>();

    // This is for the case where the workspace is closed accidently, clear those alive but incorrect markers.
    public static void clearAbandonedMarkers()
    {
        try
        {
            final IMarker[] markers =
                CoreUtil.getWorkspaceRoot().findMarkers(
                    LIFERAY_LANGUAGE_PROPERTIES_MARKER_TYPE, true, IResource.DEPTH_INFINITE );

            for( IMarker marker : markers )
            {
                if( ! marker.getResource().exists() )
                {
                    marker.delete();
                }
                else
                {
                    if( marker.getResource().getType() == IResource.FILE )
                    {
                        getValidator( (IFile) marker.getResource() ).validateEncoding();
                    }
                }
            }
        }
        catch( CoreException e )
        {
        }
    }

    public static void clearUnusedValidatorsAndMarkers( IProject project )
    {
        synchronized( filesAndValidators )
        {
            try
            {
                Set<IFile> files = filesAndValidators.keySet();

                for( Iterator<IFile> iterator = files.iterator(); iterator.hasNext(); )
                {
                    IFile file = iterator.next();

                    if( ! PropertiesUtil.isLanguagePropertiesFile( file ) )
                    {
                        LiferayLanguagePropertiesValidator validator = filesAndValidators.get( file ).get();

                        validator.clearAllMarkers();

                        iterator.remove();
                    }
                }

                final IMarker[] markers =
                    project.getWorkspace().getRoot().findMarkers(
                        LIFERAY_LANGUAGE_PROPERTIES_MARKER_TYPE, true, IResource.DEPTH_INFINITE );

                for( IMarker marker : markers )
                {
                    if( ! marker.getResource().exists() )
                    {
                        marker.delete();
                    }
                    else
                    {
                        if( marker.getResource().getType() == IResource.FILE )
                        {
                            if( ! files.contains( (IFile) marker.getResource() ) )
                            {
                                marker.delete();
                            }
                        }
                    }
                }
            }
            catch( Exception e )
            {
            }
        }
    }

    public static LiferayLanguagePropertiesValidator getValidator( IFile file )
    {
        synchronized( filesAndValidators )
        {
            try
            {
                if( filesAndValidators.get( file ).get() != null )
                {
                    return filesAndValidators.get( file ).get();
                }
                else
                {
                    throw new NullPointerException();
                }
            }
            catch( NullPointerException e )
            {
                LiferayLanguagePropertiesValidator validator = new LiferayLanguagePropertiesValidator( file );

                filesAndValidators.put( file, new WeakReference<LiferayLanguagePropertiesValidator>( validator ) );

                return validator;
            }
        }
    }

    private LiferayLanguagePropertiesValidator( IFile file )
    {
        this.file = file;

        try
        {
            for( IMarker marker : file.findMarkers(
                LIFERAY_LANGUAGE_PROPERTIES_MARKER_TYPE, false, IResource.DEPTH_INFINITE ) )
            {
                if( ID_LANGUAGE_PROPERTIES_ENCODING_NOT_DEFAULT.equals( marker.getAttribute( IMarker.SOURCE_ID ) ) )
                {
                    markers.add( marker );
                }
            }
        }
        catch( CoreException e )
        {
            LiferayCore.logError( e );
        }
    }

    public void clearAllMarkers()
    {
        synchronized( markers )
        {
            for( IMarker marker : markers )
            {
                if( marker != null && marker.exists() )
                {
                    try
                    {
                        marker.delete();
                        markers.remove( marker );
                    }
                    catch( CoreException e )
                    {
                        LiferayCore.logError( e );
                    }
                }
            }
        }
    }

    private void clearMarker( String markerSourceId )
    {
        try
        {
            synchronized( markers )
            {
                for( IMarker marker : markers )
                {
                    if( marker != null && marker.exists() &&
                        markerSourceId.equals( marker.getAttribute( IMarker.SOURCE_ID ) ) )
                    {
                        markers.remove( marker );

                        marker.delete();
                    }
                }
            }
        }
        catch( CoreException e )
        {
            LiferayCore.logError( e );
        }
    }

    private void setMarker( String markerType, String markerSourceId, int markerSeverity, String location, String markerMsg )
        throws CoreException, InterruptedException
    {
        synchronized( markers )
        {
            for( IMarker marker : markers )
            {
                if( marker != null && marker.exists() &&
                    markerSourceId.equals( marker.getAttribute( IMarker.SOURCE_ID ) ) )
                {
                    return;
                }
            }

            IMarker marker = file.createMarker( markerType );

            marker.setAttribute( IMarker.SEVERITY, markerSeverity );
            marker.setAttribute( IMarker.MESSAGE, markerMsg );
            marker.setAttribute( IMarker.SOURCE_ID, markerSourceId );
            marker.setAttribute( IMarker.LOCATION, location );

            markers.add( marker );
        }

    }

    public void validateEncoding()
    {
        if( PropertiesUtil.isLanguagePropertiesFile( file ) )
        {
            try
            {
                if( ! ILiferayConstants.LANGUAGE_PROPERTIES_FILE_ENCODING_CHARSET.equals( file.getCharset() ) )
                {
                    setMarker(
                        LIFERAY_LANGUAGE_PROPERTIES_MARKER_TYPE, ID_LANGUAGE_PROPERTIES_ENCODING_NOT_DEFAULT,
                        IMarker.SEVERITY_WARNING, LOCATION_ENCODING,
                        NLS.bind( MESSAGE_LANGUAGE_PROPERTIES_ENCODING_NOT_DEFALUT, new Object[] { file.getName() } ) );
                }
                else
                {
                    clearMarker( ID_LANGUAGE_PROPERTIES_ENCODING_NOT_DEFAULT );
                }
            }
            catch( Exception e )
            {
                LiferayCore.logError( e );
            }
        }
        else
        {
            clearMarker( ID_LANGUAGE_PROPERTIES_ENCODING_NOT_DEFAULT );
        }
    }

    private static class Msgs extends NLS
    {

        public static String languagePropertiesEncodingNotDefault;

        static
        {
            initializeMessages( LiferayLanguagePropertiesValidator.class.getName(), Msgs.class );
        }
    }

}
