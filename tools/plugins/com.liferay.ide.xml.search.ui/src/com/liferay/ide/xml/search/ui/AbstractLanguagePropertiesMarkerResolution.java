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
import com.liferay.ide.xml.search.ui.validators.LiferayBaseValidator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
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

    public AbstractLanguagePropertiesMarkerResolution( IMarker marker )
    {
        this.currentMarker = marker;
    }

    public IMarker[] findOtherMarkers( IMarker[] markers )
    {
        final List<IMarker> otherMarkers = new ArrayList<IMarker>();

        for( IMarker marker : markers )
        {
            if( marker != null &&
                ( !marker.equals( currentMarker ) ) &&
                LiferayXMLConstants.RESOURCE_BUNDLE_QUERY_SPECIFICATION_ID.equals( marker.getAttribute(
                    LiferayBaseValidator.MARKER_QUERY_ID, "" ) ) )
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

    protected String getLanguageKey( IMarker marker )
    {
        if( marker == null )
        {
            return "";
        }

        return marker.getAttribute( LiferayXMLConstants.LANGUAGE_KEY, "" );
    }

    protected String getDefaultLanguageMessage( String languageKey )
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

    protected void openEditor( IFile file ) throws PartInitException
    {
        final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

        IDE.openEditor( page, file );
    }

    public void run( IMarker marker )
    {
        resolve( marker );

        CoreUtil.validateFile( (IFile) marker.getResource(), new NullProgressMonitor() );
    }

    public void run( IMarker[] markers, IProgressMonitor monitor )
    {
        final Set<IFile> files = new HashSet<IFile>();

        for( int i = 0; i < markers.length; i++ )
        {
            monitor.subTask( Util.getProperty( IMarker.MESSAGE, markers[i] ) );

            resolve( markers[i] );

            final IResource resource = markers[i].getResource();

            if( resource instanceof IFile && ( !files.contains( resource ) ) )
            {
                files.add( (IFile) resource );
            }
        }

        for( IFile file : files )
        {
            CoreUtil.validateFile( file, new NullProgressMonitor() );
        }
    }

    protected abstract void resolve( IMarker marker );

}
