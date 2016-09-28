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

package com.liferay.ide.project.ui.upgrade.animated;

import com.liferay.ide.project.core.ProjectCore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.eclipse.core.runtime.IPath;

/**
 * @author Andy Wu
 */
public class UpgradeSettingsUtil
{

    private static File codeUpgradeFile;
    private static Properties codeUpgradeProperties;

    public static String getProperty( String key )
    {
        return codeUpgradeProperties.getProperty( key );
    }

    public static String getProperty( String key, String defaultValue )
    {
        return codeUpgradeProperties.getProperty( key, defaultValue );
    }

    public static void init()
    {
        final IPath stateLocation = ProjectCore.getDefault().getStateLocation();

        File stateDir = stateLocation.toFile();

        codeUpgradeFile = new File( stateDir, "liferay-code-upgrade.properties" );

        if( !codeUpgradeFile.exists() )
        {
            try
            {
                codeUpgradeFile.createNewFile();
            }
            catch( IOException e1 )
            {
            }
        }

        if( codeUpgradeProperties == null )
        {
            codeUpgradeProperties = new Properties();

            try(InputStream in = new FileInputStream( codeUpgradeFile ))
            {
                codeUpgradeProperties.load( in );
            }
            catch( Exception e )
            {
            }
        }
    }

    public static void resetStoreProperties()
    {
        if( codeUpgradeProperties != null )
        {
            codeUpgradeProperties.clear();

            storeProperty( null, null );
        }
    }

    public static void storeProperty( Object key, Object value )
    {
        if( key != null )
        {
            codeUpgradeProperties.setProperty( String.valueOf( key ), String.valueOf( value ) );
        }

        try(OutputStream out = new FileOutputStream( codeUpgradeFile ))
        {
            codeUpgradeProperties.store( out, "" );
        }
        catch( Exception e )
        {
        }
    }
}
