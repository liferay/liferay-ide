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

package com.liferay.ide.xml.search.ui;

import com.liferay.ide.core.util.CoreUtil;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.views.markers.WorkbenchMarkerResolution;
import org.eclipse.ui.views.markers.internal.Util;

/**
 * @author Terry Jia
 */
@SuppressWarnings( "restriction" )
public abstract class AbstractLanguagePropertiesMarkerResolution extends WorkbenchMarkerResolution
{

    private IMarker currentMarker = null;

    private int count = 0;

    public AbstractLanguagePropertiesMarkerResolution( IMarker marker )
    {
        this.currentMarker = marker;
    }

    public IMarker[] findOtherMarkers( IMarker[] markers )
    {
        final List<IMarker> otherMarkers = new ArrayList<IMarker>();

        for( IMarker marker : markers )
        {
            if( ( marker != null ) &&
                !marker.equals( currentMarker ) &&
                marker.getAttribute( LiferayXMLConstants.MARKER_CATEGORY, "" ).equals(
                    LiferayXMLConstants.MARKER_CATEGORY_RESOURCE_BUNDLE ) )
            {
                otherMarkers.add( marker );
            }
        }

        return otherMarkers.toArray( new IMarker[0] );
    }

    public String getDescription()
    {
        return getLabel();
    }

    protected String getLanguageKey( String markerMessage )
    {
        if( CoreUtil.isNullOrEmpty( markerMessage ) )
        {
            return "";
        }

        String languageKey = "";

        int firstCharAt = markerMessage.indexOf( "\"" );

        int secondCharAt = markerMessage.lastIndexOf( "\"" );

        if( firstCharAt > 0 && secondCharAt > 0 )
        {
            languageKey = markerMessage.substring( firstCharAt + 1, secondCharAt );
        }

        return languageKey;
    }

    protected String getLanguageMessage( String languageKey )
    {
        if( CoreUtil.isNullOrEmpty( languageKey ) )
        {
            return "";
        }

        final String[] words = languageKey.split( "-" );

        final StringBuffer sb = new StringBuffer();

        for( int i = 0; i < words.length; i++ )
        {
            String word = words[i];

            if( i == 0 )
            {
                word = word.replaceFirst( word.substring( 0, 1 ), word.substring( 0, 1 ).toUpperCase() );
            }

            sb.append( word );

            sb.append( " " );
        }

        return sb.toString().trim();
    }

    public Image getImage()
    {
        final URL url = LiferayXMLSearchUI.getDefault().getBundle().getEntry( "/icons/portlet.png" );

        return ImageDescriptor.createFromURL( url ).createImage();
    }

    protected void openEditor( IFile file ) throws PartInitException
    {
        final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

        IDE.openEditor( page, file );
    }

    public void run( IMarker marker )
    {
        promptUser( marker );

        count--;

        if( count == 0 )
        {
            CoreUtil.validateFile( (IFile) marker.getResource(), new NullProgressMonitor() );
        }
    }

    public void run( IMarker[] markers, IProgressMonitor monitor )
    {
        count = markers.length;

        for( int i = 0; i < markers.length; i++ )
        {
            monitor.subTask( Util.getProperty( IMarker.MESSAGE, markers[i] ) );

            run( markers[i] );
        }
    }

    protected abstract void promptUser( IMarker marker );

}
