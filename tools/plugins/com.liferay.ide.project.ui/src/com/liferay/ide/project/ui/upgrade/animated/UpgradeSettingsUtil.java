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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.ProjectCore;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
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

    public static void init( LiferayUpgradeDataModel dataModel )
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

            try( InputStream in = Files.newInputStream( codeUpgradeFile.toPath() ) )
            {
                codeUpgradeProperties.load( in );
            }
            catch( Exception e )
            {
            }
        }

        retrieveProperties( dataModel );
    }

    public static void resetStoreProperties()
    {
        if( codeUpgradeProperties != null )
        {
            codeUpgradeProperties.clear();

            storeProperty( null, null );
        }
    }

    public static void retrieveProperties( LiferayUpgradeDataModel dataModel )
    {
        String liferay70ServerName = UpgradeSettingsUtil.getProperty( "Liferay70ServerName" );
        String liferay62ServerLocation = UpgradeSettingsUtil.getProperty( "Liferay62ServerLocation" );

        if( !CoreUtil.isNullOrEmpty( liferay62ServerLocation ) )
        {
            dataModel.setLiferay62ServerLocation( liferay62ServerLocation );
        }

        if( !CoreUtil.isNullOrEmpty( liferay70ServerName ) )
        {
            dataModel.setLiferay70ServerName( liferay70ServerName );
        }

        dataModel.setHasMavenProject( Boolean.parseBoolean( getProperty( "HasMavenProject", "false" ) ) );
        dataModel.setHasPortlet( Boolean.parseBoolean( getProperty( "HasPortlet", "false" ) ) );
        dataModel.setHasServiceBuilder( Boolean.parseBoolean( getProperty( "HasServiceBuilder", "false" ) ) );
        dataModel.setHasHook( Boolean.parseBoolean( getProperty( "HasHook", "false" ) ) );
        dataModel.setHasLayout( Boolean.parseBoolean( getProperty( "HasLayout", "false" ) ) );
        dataModel.setHasTheme( Boolean.parseBoolean( getProperty( "HasTheme", "false" ) ) );
        dataModel.setHasExt( Boolean.parseBoolean( getProperty( "HasExt", "false" ) ) );
        dataModel.setHasWeb( Boolean.parseBoolean( getProperty( "HasWeb", "false" ) ) );
        dataModel.setConvertLiferayWorkspace( Boolean.parseBoolean( getProperty( "ConvertLiferayWorkspace", "false" ) ) );
        dataModel.setImportFinished( Boolean.parseBoolean( getProperty( "ImportFinished", "false" ) ) );
    }

    public static void storeProperty( Object key, Object value )
    {
        if( key != null )
        {
            codeUpgradeProperties.setProperty( String.valueOf( key ), String.valueOf( value ) );
        }

        try(OutputStream out = Files.newOutputStream( codeUpgradeFile.toPath() ))
        {
            codeUpgradeProperties.store( out, "" );
        }
        catch( Exception e )
        {
        }
    }
}
