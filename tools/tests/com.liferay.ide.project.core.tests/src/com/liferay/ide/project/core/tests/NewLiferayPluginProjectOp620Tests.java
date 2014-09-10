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

package com.liferay.ide.project.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKManager;
import com.liferay.ide.sdk.core.SDKUtil;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.services.ValidationService;
import org.junit.Test;

/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
public class NewLiferayPluginProjectOp620Tests extends NewLiferayPluginProjectOpBase
{

    @Override
    protected IPath getLiferayPluginsSdkDir()
    {
        return ProjectCore.getDefault().getStateLocation().append( "liferay-plugins-sdk-6.2.0" );
    }

    @Override
    protected IPath getLiferayPluginsSDKZip()
    {
        return getLiferayBundlesPath().append( "liferay-plugins-sdk-6.2.0-ce-ga1-20131101192857659.zip" );
    }

    @Override
    protected String getLiferayPluginsSdkZipFolder()
    {
        return "liferay-plugins-sdk-6.2.0/";
    }

    @Override
    protected IPath getLiferayRuntimeDir()
    {
        return ProjectCore.getDefault().getStateLocation().append( "liferay-portal-6.2.0-ce-ga1/tomcat-7.0.42" );
    }

    @Override
    protected IPath getLiferayRuntimeZip()
    {
        return getLiferayBundlesPath().append( "liferay-portal-tomcat-6.2.0-ce-ga1-20131101192857659.zip" );
    }

    private IPath getLiferayPlugins612SdkDir()
    {
        return ProjectCore.getDefault().getStateLocation().append( "liferay-plugins-sdk-6.1.2" );
    }

    private IPath getLiferayPlugins612SDKZip()
    {
        return getLiferayBundlesPath().append( "liferay-plugins-sdk-6.1.1-20130816114619181.zip" );
    }

    private String getLiferayPlugins612SdkZipFolder()
    {
        return "liferay-plugins-sdk-6.1.1/";
    }

    @Override
    protected String getRuntimeId()
    {
        return "com.liferay.ide.eclipse.server.tomcat.runtime.70";
    }

    @Override
    public String getRuntimeVersion()
    {
        return "6.2.0";
    }

    @Override
    protected String getServiceXmlDoctype()
    {
        return "service-builder PUBLIC \"-//Liferay//DTD Service Builder 6.2.0//EN\" \"http://www.liferay.com/dtd/liferay-service-builder_6_2_0.dtd";
    }

    @Test
    public void testLocationListener() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        super.testLocationListener();
    }

    @Test
    public void testNewJsfRichfacesProjects() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        super.testNewJsfRichfacesProjects();
    }

    @Test
    public void testNewLayoutAntProject() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        super.testNewLayoutAntProject();
    }

    @Test
    public void testNewProjectCustomLocationPortlet() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        super.testNewProjectCustomLocationPortlet();
    }

    @Test
    public void testNewProjectCustomLocationWrongSuffix() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        super.testNewProjectCustomLocationWrongSuffix();
    }

    @Test
    public void testNewSDKProjectCustomLocation() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        super.testNewSDKProjectCustomLocation();
    }

    @Test
    public void testNewSDKProjectEclipseWorkspace() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        super.testNewSDKProjectEclipseWorkspace();
    }

    @Test
    public void testNewSDKProjectInSDK() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        super.testNewSDKProjectInSDK();
    }

    @Test
    public void testNewThemeProjects() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        super.testNewThemeProjects();
    }

    @Test
    public void testPluginTypeListener() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        super.testPluginTypeListener( true );
    }

    @Test
    public void testProjectNameValidation() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        setupPluginsSDK();
        setupPlugins612SDK();

        final NewLiferayPluginProjectOp op1 = newProjectOp( "" );

        op1.setUseDefaultLocation( true );

        PossibleValuesService ps = op1.getPluginsSDKName().service( PossibleValuesService.class );

        String[] sdkNames = (String[]) ps.data().toArray( new String[ps.data().size()] );
        
        op1.setPluginsSDKName( sdkNames[0] );
        ValidationService vs = op1.getProjectName().service( ValidationService.class );

        String validProjectName = "test1";

        op1.setProjectName( validProjectName );
        assertEquals( "ok", vs.validation().message() );
        assertEquals( "ok", op1.getProjectName().validation().message() );

        op1.setProjectName( validProjectName + "-portlet" );
        assertEquals( "ok", vs.validation().message() );
        assertEquals( "ok", op1.getProjectName().validation().message() );

        createProject( op1 );

        op1.setPluginsSDKName( sdkNames[1] );

        op1.setProjectName( validProjectName );
        assertEquals( true, !"ok".equals( vs.validation().message() ) );
        assertEquals( true, !"ok".equals( op1.getProjectName().validation().message() ) );

        op1.dispose();
    }

    @Test
    public void testUseSdkLocationListener() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        super.testUseSdkLocationListener();
    }
    



    /**
     * @throws Exception
     */
    private void setupPlugins612SDK() throws Exception
    {
        if( shouldSkipBundleTests() )
            return;

        final File liferayPluginsSdkDirFile = getLiferayPlugins612SdkDir().toFile();

        if( !liferayPluginsSdkDirFile.exists() )
        {
            final File liferayPluginsSdkZipFile = getLiferayPlugins612SDKZip().toFile();

            assertEquals(
                "Expected file to exist: " + liferayPluginsSdkZipFile.getAbsolutePath(), true,
                liferayPluginsSdkZipFile.exists() );

            liferayPluginsSdkDirFile.mkdirs();

            final String liferayPluginsSdkZipFolder = getLiferayPlugins612SdkZipFolder();

            if( CoreUtil.isNullOrEmpty( liferayPluginsSdkZipFolder ) )
            {
                ZipUtil.unzip( liferayPluginsSdkZipFile, liferayPluginsSdkDirFile );
            }
            else
            {
                ZipUtil.unzip(
                    liferayPluginsSdkZipFile, liferayPluginsSdkZipFolder, liferayPluginsSdkDirFile,
                    new NullProgressMonitor() );
            }
        }

        assertEquals( true, liferayPluginsSdkDirFile.exists() );

        final File ivyCacheDir = new File( liferayPluginsSdkDirFile, ".ivy" );

        if( !ivyCacheDir.exists() )
        {
            // setup ivy cache

            final File ivyCacheZipFile = getIvyCacheZip().toFile();

            assertEquals(
                "Expected ivy-cache.zip to be here: " + ivyCacheZipFile.getAbsolutePath(), true,
                ivyCacheZipFile.exists() );

            ZipUtil.unzip( ivyCacheZipFile, liferayPluginsSdkDirFile );
        }

        assertEquals( "Expected .ivy folder to be here: " + ivyCacheDir.getAbsolutePath(), true, ivyCacheDir.exists() );

        SDK sdk = null;

        final SDK existingSdk = SDKManager.getInstance().getSDK( getLiferayPlugins612SdkDir() );

        if( existingSdk == null )
        {
            sdk = SDKUtil.createSDKFromLocation( getLiferayPlugins612SdkDir() );
        }
        else
        {
            sdk = existingSdk;
        }

        assertNotNull( sdk );

        sdk.setDefault( true );

        SDKManager.getInstance().addSDK( sdk );

        final IPath customLocationBase = getCustomLocationBase();

        final File customBaseDir = customLocationBase.toFile();

        if( customBaseDir.exists() )
        {
            FileUtil.deleteDir( customBaseDir, true );

            if( customBaseDir.exists() )
            {
                for( File f : customBaseDir.listFiles() )
                {
                    System.out.println( f.getAbsolutePath() );
                }
            }

            assertEquals( "Unable to delete pre-existing customBaseDir", false, customBaseDir.exists() );
        }
    }

}
