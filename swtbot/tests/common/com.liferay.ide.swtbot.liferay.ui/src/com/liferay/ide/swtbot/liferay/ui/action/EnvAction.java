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

package com.liferay.ide.swtbot.liferay.ui.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.liferay.ide.swtbot.liferay.ui.UIAction;
import com.liferay.ide.swtbot.liferay.ui.util.BundleInfo;
import com.liferay.ide.swtbot.liferay.ui.util.CSVReader;
import com.liferay.ide.swtbot.liferay.ui.util.CoreUtil;
import com.liferay.ide.swtbot.liferay.ui.util.FileUtil;
import com.liferay.ide.swtbot.liferay.ui.util.ZipUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.utils.FileUtils;

/**
 * @author Terry Jia
 */
public class EnvAction extends UIAction
{

    private BundleInfo[] bundleInfos;
    private String liferayBundlesDir = System.getProperty( "liferay.bundles.dir" );
    private IPath liferayBundlesPath;
    private String PLUGINS_SDK_DIR = "com.liferay.portal.plugins.sdk-1.0.11-withdependencies";
    private String PLUGINS_SDK_ZIP =
        "com.liferay.portal.plugins.sdk-1.0.11-withdependencies-20170613175008905.zip";

    public EnvAction( SWTWorkbenchBot bot )
    {
        super( bot );

        bundleInfos = getBundleInfos();
    }

    public BundleInfo[] getBundleInfos()
    {
        File bundleCSV = getLiferayBundlesPath().append( "bundles.csv" ).toFile();

        assertTrue( bundleCSV.exists() );

        String[][] infos = CSVReader.readCSV( bundleCSV );

        BundleInfo[] bundleInfos = new BundleInfo[infos.length];

        for( int i = 0; i < infos.length; i++ )
        {
            bundleInfos[i] = new BundleInfo();

            String[] columns = infos[i];

            for( int t = 0; t < columns.length; t++ )
            {
                if( t == 0 )
                {
                    bundleInfos[i].setBundleZip( columns[t] );
                }
                else if( t == 1 )
                {
                    bundleInfos[i].setBundleDir( columns[t] );
                }
                else if( t == 2 )
                {
                    bundleInfos[i].setTomcatDir( columns[t] );
                }
                else if( t == 3 )
                {
                    bundleInfos[i].setType( columns[t] );
                }
                else if( t == 4 )
                {
                    bundleInfos[i].setVersion( columns[t] );
                }
            }
        }

        return bundleInfos;
    }

    public IPath getLiferayBundlesPath()
    {
        if( liferayBundlesPath == null )
        {
            if( liferayBundlesDir == null || liferayBundlesDir.equals( "" ) )
            {
                URL rootUrl = Platform.getBundle( "com.liferay.ide.swtbot.liferay.ui" ).getEntry( "/" );

                try
                {
                    liferayBundlesPath =
                        new Path( FileLocator.toFileURL( rootUrl ).getFile() ).removeLastSegments( 4 ).append(
                            "tests-resources" );
                }
                catch( IOException e )
                {
                }
            }
            else
            {
                liferayBundlesPath = new Path( liferayBundlesDir );
            }
        }

        assertTrue( liferayBundlesPath.toFile().exists() );

        return liferayBundlesPath;
    }

    public String getLiferayPluginServerName()
    {
        return bundleInfos[0].getTomcatDir();
    }

    public IPath getLiferayPluginsSdkDir()
    {
        return getLiferayBundlesPath().append( "bundles" ).append( PLUGINS_SDK_DIR );
    }

    public String getLiferayPluginsSdkName()
    {
        return PLUGINS_SDK_DIR;
    }

    public IPath getLiferayPluginsSDKZip()
    {
        return getLiferayBundlesPath().append( PLUGINS_SDK_ZIP );
    }

    public IPath getLiferayServerDir()
    {
        return getLiferayBundlesPath().append( "bundles" ).append( bundleInfos[0].getBundleDir() );
    }

    public IPath getLiferayServerFullDir()
    {
        return getLiferayServerDir().append( getLiferayPluginServerName() );
    }

    public IPath getLiferayServerZip()
    {
        return getLiferayBundlesPath().append( bundleInfos[0].getBundleZip() );
    }

    public String getLiferayServerZipFolder()
    {
        return bundleInfos[0].getBundleDir();
    }

    public File getProjectZip( String bundleId, String projectName ) throws IOException
    {
        URL projectZipUrl = Platform.getBundle( bundleId ).getEntry( "projects/" + projectName + ".zip" );

        File projectZipFile = new File( FileLocator.toFileURL( projectZipUrl ).getFile() );

        return projectZipFile;
    }

    public File getValidationFolder()
    {
        return getLiferayBundlesPath().append( "validation" ).toFile();
    }

    public void killGradleProcess() throws IOException
    {
        String jpsCmd = "jps";

        Process proc = Runtime.getRuntime().exec( jpsCmd );

        BufferedReader in = new BufferedReader( new InputStreamReader( proc.getInputStream() ) );

        String temp = in.readLine();

        List<String> result = new ArrayList<String>();

        while( temp != null )
        {
            temp = in.readLine();

            if( temp != null && temp.contains( "GradleDaemon" ) )
            {
                result.add( temp );
            }
        }

        try
        {
            for( String pid : result )
            {
                String allGradleProcess[] = pid.split( " " );

                Runtime.getRuntime().exec( "taskkill /F /PID " + allGradleProcess[0] );
            }
        }
        catch( Exception e )
        {
        }
    }

    public void prepareGeoFile()
    {
        String filename = "com.liferay.ip.geocoder.internal.IPGeocoderConfiguration.cfg";

        File source = getLiferayBundlesPath().append( filename ).toFile();
        File dest = getLiferayServerDir().append( "osgi" ).append( "configs" ).append( filename ).toFile();

        try
        {
            FileUtil.copyFile( source, dest );

            String content = "filePath=" + getLiferayBundlesPath().toPortableString() + "/GeoLiteCity.dat";

            FileUtils.write( content, dest );
        }
        catch( Exception e )
        {
        }
    }

    public void preparePortalExtFile()
    {
        String filename = "portal-ext.properties";

        File source = getLiferayBundlesPath().append( filename ).toFile();
        File dest = getLiferayServerDir().append( filename ).toFile();

        try
        {
            FileUtil.copyFile( source, dest );
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }

    public void preparePortalSetupWizardFile()
    {
        String filename = "portal-setup-wizard.properties";

        File source = getLiferayBundlesPath().append( filename ).toFile();
        File dest = getLiferayServerDir().append( filename ).toFile();

        try
        {
            FileUtil.copyFile( source, dest );
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }

    public void unzipPluginsSDK() throws IOException
    {
        FileUtil.deleteDir( getLiferayPluginsSdkDir().toFile(), true );

        assertEquals(
            "Expected file to be not exist:" + getLiferayPluginsSdkDir().toPortableString(), false,
            getLiferayPluginsSdkDir().toFile().exists() );

        File liferayPluginsSdkZipFile = getLiferayPluginsSDKZip().toFile();

        assertEquals(
            "Expected file to exist: " + liferayPluginsSdkZipFile.getAbsolutePath(), true,
            liferayPluginsSdkZipFile.exists() );

        File liferayPluginsSdkDirFile = getLiferayPluginsSdkDir().toFile();

        liferayPluginsSdkDirFile.mkdirs();

        if( CoreUtil.isNullOrEmpty( PLUGINS_SDK_DIR ) )
        {
            ZipUtil.unzip( liferayPluginsSdkZipFile, liferayPluginsSdkDirFile );
        }
        else
        {
            ZipUtil.unzip(
                liferayPluginsSdkZipFile, PLUGINS_SDK_DIR, liferayPluginsSdkDirFile, new NullProgressMonitor() );
        }

        assertEquals( true, liferayPluginsSdkDirFile.exists() );

        Properties evnMap = System.getProperties();

        String username = evnMap.getProperty( "USERNAME" );

        File userBuildFile = new File( liferayPluginsSdkDirFile, "build." + username + ".properties" );

        if( !userBuildFile.exists() )
        {
            userBuildFile.createNewFile();

            String appServerParentDir =
                "app.server.parent.dir=" + getLiferayServerDir().toFile().getPath().replace( "\\", "/" );
            try
            {
                FileWriter writer = new FileWriter( userBuildFile.getPath(), true );

                writer.write( appServerParentDir );
                writer.close();
            }
            catch( IOException e )
            {
            }
        }
    }

    public void unzipServer() throws IOException
    {
        FileUtil.deleteDir( getLiferayServerDir().toFile(), true );

        assertEquals(
            "Expected file to be not exist:" + getLiferayServerDir().toPortableString(), false,
            getLiferayServerDir().toFile().exists() );

        File liferayServerZipFile = getLiferayServerZip().toFile();

        assertEquals(
            "Expected file to exist: " + liferayServerZipFile.getAbsolutePath(), true, liferayServerZipFile.exists() );

        File liferayServerDirFile = getLiferayServerDir().toFile();

        liferayServerDirFile.mkdirs();

        String liferayServerZipFolder = getLiferayServerZipFolder();

        if( CoreUtil.isNullOrEmpty( liferayServerZipFolder ) )
        {
            ZipUtil.unzip( liferayServerZipFile, liferayServerDirFile );
        }
        else
        {
            ZipUtil.unzip(
                liferayServerZipFile, liferayServerZipFolder, liferayServerDirFile, new NullProgressMonitor() );
        }

    }

}
