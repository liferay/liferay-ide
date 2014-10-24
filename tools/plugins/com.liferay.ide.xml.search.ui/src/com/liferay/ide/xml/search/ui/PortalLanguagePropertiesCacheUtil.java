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

import com.liferay.ide.core.ILiferayProject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.WeakHashMap;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.eclipse.core.runtime.IPath;

/**
 * @author Terry Jia
 */
public class PortalLanguagePropertiesCacheUtil
{

    private static final WeakHashMap<IPath, Properties> languagePortalMap = new WeakHashMap<IPath, Properties>();

    public static Properties getPortalLanguageProperties( ILiferayProject project )
    {
        if( project == null )
        {
            return new Properties();
        }

        Properties retval = null;

        JarFile jar = null;

        InputStream in = null;

        try
        {
            final IPath appServerPortalDir = project.getAppServerPortalDir();

            retval = languagePortalMap.get( appServerPortalDir );

            if( retval == null )
            {
                if( appServerPortalDir != null && appServerPortalDir.toFile().exists() )
                {
                    jar = new JarFile( appServerPortalDir.append( "WEB-INF/lib/portal-impl.jar" ).toFile() );
                    final ZipEntry lang = jar.getEntry( "content/Language.properties" );

                    retval = new Properties();

                    in = jar.getInputStream( lang );

                    retval.load( in );
                }

                languagePortalMap.put( appServerPortalDir, retval );
            }
        }
        catch( Exception e )
        {
            LiferayXMLSearchUI.logError( "Unable to find portal language properties", e );
        }
        finally
        {
            try
            {
                if( in != null )
                {
                    in.close();
                }

                if( jar != null )
                {
                    jar.close();
                }
            }
            catch( IOException e )
            {
                // no errors this is best effort
            }
        }

        return retval;
    }

}
