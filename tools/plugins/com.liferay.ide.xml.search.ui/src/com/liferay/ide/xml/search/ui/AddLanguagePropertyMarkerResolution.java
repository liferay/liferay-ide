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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * @author Terry Jia
 */
public class AddLanguagePropertyMarkerResolution extends AbstractLanguagePropertiesMarkerResolution
{

    private IFile languageFile = null;
    private boolean openEditor = false;

    public AddLanguagePropertyMarkerResolution( IMarker marker, IFile languageFile, boolean openEditor )
    {
        super( marker );

        this.languageFile = languageFile;
        this.openEditor = openEditor;
    }

    public String getLabel()
    {
        final StringBuffer sb = new StringBuffer();

        sb.append( "Add the language key to " );
        sb.append( languageFile.getName() );

        if( openEditor )
        {
            sb.append( " and open the properties editor" );
        }

        return sb.toString();
    }

    protected void promptUser( final IMarker marker )
    {
        final String message = marker.getAttribute( IMarker.MESSAGE, "" );

        if( ( message == null ) || ( languageFile == null ) )
        {
            return;
        }

        InputStream is = null;

        try
        {
            is = languageFile.getContents();

            final String languageKey = getLanguageKey( message );

            if( CoreUtil.isNullOrEmpty( languageKey ) )
            {
                return;
            }

            final Properties properties = new Properties();

            properties.load( is );

            if( properties.get( languageKey ) != null )
            {
                is.close();

                return;
            }

            final String languageMessage = getLanguageMessage( languageKey );

            final String languagePropertyLine = languageKey + "=" + languageMessage;

            final String contents = CoreUtil.readStreamToString( languageFile.getContents() );

            final StringBuffer contentSb = new StringBuffer();

            contentSb.append( contents );

            if( !contents.endsWith( "\n" ) )
            {
                contentSb.append( "\n" );
            }

            contentSb.append( languagePropertyLine );

            languageFile.setContents(
                new ByteArrayInputStream( contentSb.toString().trim().getBytes( "UTF-8" ) ), IResource.FORCE,
                new NullProgressMonitor() );

            if( openEditor )
            {
                openEditor( languageFile );
            }

            is.close();
        }
        catch( IOException e )
        {
            LiferayXMLSearchUI.logError( e );
        }
        catch( CoreException e )
        {
            LiferayXMLSearchUI.logError( e );
        }
    }

}
