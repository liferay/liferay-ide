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

package com.liferay.ide.server.util;

import com.liferay.ide.core.util.CoreUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.jdt.launching.IVMInstallType;

/**
 * @author Greg Amerson
 * @author Simon Jiang
 */
public class JavaUtil
{
    public static final String EXT_JAR = ".jar";

    public static String createUniqueId( IVMInstallType vmType )
    {
        String id = null;

        do
        {
            id = String.valueOf( System.currentTimeMillis() );
        }
        while( vmType.findVMInstall( id ) != null );

        return id;
    }

    public static String getJarProperty( final File systemJarFile, final String propertyName )
    {
        if( systemJarFile.canRead() )
        {
            try( ZipFile jar = new ZipFile( systemJarFile ) )
            {
                ZipEntry manifest = jar.getEntry( "META-INF/MANIFEST.MF" );//$NON-NLS-1$

                Properties props = new Properties();
                props.load( jar.getInputStream( manifest ) );
                String value = (String) props.get( propertyName );

                return value;
            }
            catch( IOException e )
            {
                return null;
            }
        }

        return null;
    }

    public static String getManifestProperty( File manifestFile, String propertyName )
    {
        try
        {
            String contents = CoreUtil.readStreamToString( Files.newInputStream( manifestFile.toPath() ) );

            if( contents != null )
            {
                Manifest mf = new Manifest( new ByteArrayInputStream( contents.getBytes() ) );
                Attributes a = mf.getMainAttributes();
                String val = a.getValue( propertyName );

                return val;
            }
        }
        catch( IOException ioe )
        {
        }

        return null;
    }

    public static String getManifestPropFromFolderJars(
        final File location, final String mainFolder, final String property )
    {
        File f = new File( location, mainFolder );

        if( f.exists() )
        {
            File[] children = f.listFiles();

            for( int i = 0; i < children.length; i++ )
            {
                if( children[i].getName().endsWith( EXT_JAR ) )
                {
                    return getJarProperty( children[i], property );
                }
            }
        }

        return null;
    }

    public static boolean scanFolderJarsForManifestProp(
        final File location, final String mainFolder, final String property, final String propPrefix )
    {
        String value = getManifestPropFromFolderJars( location, mainFolder, property );

        if( value != null && value.trim().startsWith( propPrefix ) )
        {
            return true;
        }

        return false;
    }

}
