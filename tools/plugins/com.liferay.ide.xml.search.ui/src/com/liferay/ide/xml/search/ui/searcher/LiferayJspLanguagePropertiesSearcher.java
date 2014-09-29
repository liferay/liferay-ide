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

package com.liferay.ide.xml.search.ui.searcher;

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.PropertiesUtil;
import com.liferay.ide.xml.search.ui.PortalLanguagePropertiesCacheUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.xml.search.editor.references.IXMLReferenceTo;
import org.eclipse.wst.xml.search.editor.references.IXMLReferenceToProperty;
import org.eclipse.wst.xml.search.editor.searchers.properties.XMLSearcherForProperties;

/**
 * @author Terry Jia
 */
public class LiferayJspLanguagePropertiesSearcher extends XMLSearcherForProperties
{

    private static final String HOVER = "\"{0}\" in {1}";

    public String searchForTextHover(
        Object selectedNode, int offset, String mathingString, int startIndex, int endIndex, IFile file,
        IXMLReferenceTo referenceTo )
    {
        StringBuffer sb = new StringBuffer();

        if( referenceTo instanceof IXMLReferenceToProperty )
        {
            final IProject project = file.getProject();
            final List<IFile> languagePropertiesFiles =
                PropertiesUtil.getDefaultLanguagePropertiesFromProject( project );

            for( IFile languagePropertiesFile : languagePropertiesFiles )
            {
                final Properties properties = new Properties();

                InputStream contents = null;

                try
                {
                    contents = languagePropertiesFile.getContents();

                    properties.load( contents );

                    Object key = properties.get( mathingString );

                    if( key != null )
                    {
                        sb.append( NLS.bind( HOVER, key, languagePropertiesFile.getFullPath().toString() ) );

                        sb.append( "<br/>" );
                    }
                    else
                    {
                        continue;
                    }
                }
                catch( Exception e )
                {
                }
                finally
                {
                    if( contents != null )
                    {
                        try
                        {
                            contents.close();
                        }
                        catch( IOException e )
                        {
                        }
                    }
                }
            }

            if( CoreUtil.isNullOrEmpty( sb.toString() ) )
            {
                final Properties portalProperties =
                    PortalLanguagePropertiesCacheUtil.getPortalLanguageProperties( LiferayCore.create( project ) );

                Object key = portalProperties.get( mathingString );

                if( key != null )
                {
                    sb.append( NLS.bind( HOVER, key, "Liferay Portal Language.properties" ) );
                }
            }
        }

        return sb.toString();
    }

}
