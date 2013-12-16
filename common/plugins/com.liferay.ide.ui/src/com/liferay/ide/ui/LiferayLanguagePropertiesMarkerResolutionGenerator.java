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

package com.liferay.ide.ui;

import com.liferay.ide.core.LiferayLanguagePropertiesValidator;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.PropertiesUtil;
import com.liferay.ide.ui.util.UIUtil;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.IMarkerResolutionGenerator2;

/**
 * @author Kuo Zhang
 */
public class LiferayLanguagePropertiesMarkerResolutionGenerator implements IMarkerResolutionGenerator2
{

    public IMarkerResolution[] getResolutions( IMarker marker )
    {
        List<IMarkerResolution> resolutions = new ArrayList<IMarkerResolution>();

        try
        {
            if( LiferayLanguagePropertiesValidator.ID_LANGUAGE_PROPERTIES_ENCODING_NOT_DEFAULT.equals( marker.getAttribute( IMarker.SOURCE_ID ) ) )
            {
                resolutions.add( new EncodeAllFilesToDefaultResolution() );
                resolutions.add( new EncodeOneFileToDefaultResolution() );
            }
        }
        catch( CoreException e )
        {
            LiferayUIPlugin.logError( e );
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
            LiferayUIPlugin.logError( e );
        }

        return false;
    }

    private class EncodeAllFilesToDefaultResolution implements IMarkerResolution2
    {

        public String getDescription()
        {
            return getLabel();
        }

        public Image getImage()
        {
            final URL url = LiferayUIPlugin.getDefault().getBundle().getEntry( "/icons/e16/encode.png" );
            return ImageDescriptor.createFromURL( url ).createImage();
        }

        public String getLabel()
        {
            return Msgs.encodeAllFilesToDefault;
        }

        public void run( IMarker marker )
        {
            final IProject proj = CoreUtil.getLiferayProject( marker.getResource() );

            if( proj != null && proj.exists() )
            {
                try
                {
                    new ProgressMonitorDialog( UIUtil.getActiveShell() ).run( true, false, new IRunnableWithProgress()
                    {

                        public void run( IProgressMonitor monitor ) throws InvocationTargetException,
                            InterruptedException
                        {
                            monitor.beginTask(
                                "Encoding All Liferay Language Properties Files of this Project to Default (UTF-8)... ",
                                10 );

                            PropertiesUtil.encodeLanguagePropertiesFilesToDefault( proj, monitor );

                            monitor.done();
                        }
                    } );
                }
                catch( Exception e )
                {
                    LiferayUIPlugin.logError( e );
                }
            }
        }
    }

    private class EncodeOneFileToDefaultResolution implements IMarkerResolution2
    {

        public String getDescription()
        {
            return getLabel();
        }

        public Image getImage()
        {
            final URL url = LiferayUIPlugin.getDefault().getBundle().getEntry( "/icons/e16/encode.png" );
            return ImageDescriptor.createFromURL( url ).createImage();
        }

        public String getLabel()
        {
            return Msgs.encodeThisFileToDefault;
        }

        public void run( IMarker marker )
        {
            if( marker.getResource().getType() == IResource.FILE )
            {
                final IFile file = (IFile) marker.getResource();

                try
                {
                    new ProgressMonitorDialog( UIUtil.getActiveShell() ).run( true, false, new IRunnableWithProgress()
                    {

                        public void run( IProgressMonitor monitor ) throws InvocationTargetException,
                            InterruptedException
                        {
                            monitor.beginTask( "Encoding Liferay Language Properties File to Default (UTF-8)... ", 10 );

                            PropertiesUtil.encodeLanguagePropertiesFilesToDefault( file, monitor );

                            monitor.done();
                        }
                    } );
                }
                catch( Exception e )
                {
                    LiferayUIPlugin.logError( e );
                }
            }
        }

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
