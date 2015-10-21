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

import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;

import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.platform.PathBridge;
import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Terry Jia
 */
public class NewLiferayPluginProjectOp623Tests extends NewLiferayPluginProjectOpBase
{

    @AfterClass
    public static void removePluginsSDK() throws Exception
    {
        deleteAllWorkspaceProjects();
    }

    @Override
    protected IPath getLiferayPluginsSdkDir()
    {
        return ProjectCore.getDefault().getStateLocation().append( "liferay-plugins-sdk-6.2" );
    }

    @Override
    protected IPath getLiferayPluginsSDKZip()
    {
        return getLiferayBundlesPath().append( "liferay-plugins-sdk-6.2-ce-ga4-20150416163831865.zip" );
    }

    @Override
    protected String getLiferayPluginsSdkZipFolder()
    {
        return "liferay-plugins-sdk-6.2/";
    }

    @Override
    protected IPath getLiferayRuntimeDir()
    {
        return ProjectCore.getDefault().getStateLocation().append( "liferay-portal-6.2-ce-ga4/tomcat-7.0.42" );
    }

    @Override
    protected IPath getLiferayRuntimeZip()
    {
        return getLiferayBundlesPath().append( "liferay-portal-tomcat-6.2-ce-ga4-20150416163831865.zip" );
    }

    @Override
    protected String getRuntimeId()
    {
        return "com.liferay.ide.eclipse.server.tomcat.runtime.70";
    }

    @Override
    public String getRuntimeVersion()
    {
        return "6.2.3";
    }

    @Override
    protected String getServiceXmlDoctype()
    {
        return "service-builder PUBLIC \"-//Liferay//DTD Service Builder 6.2.0//EN\" \"http://www.liferay.com/dtd/liferay-service-builder_6_2_0.dtd";
    }

    @Override
    @Test
    @Ignore
    public void testLocationListener() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        super.testLocationListener();
    }

    @Override
    @Test
    public void testNewJsfRichfacesProjects() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        super.testNewJsfRichfacesProjects();
    }

    @Override
    @Test
    public void testNewLayoutAntProject() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        super.testNewLayoutAntProject();
    }

    @Test
    @Ignore
    public void testNewProjectCustomLocationPortlet() throws Exception
    {
        // not supported in 6.2.3
    }

    @Test
    @Ignore
    public void testNewProjectCustomLocationWrongSuffix() throws Exception
    {
        // not supported in 6.2.3
    }

    @Test
    @Ignore
    public void testNewSDKProjectCustomLocation() throws Exception
    {
        // not supported in 6.2.3
    }

    @Test
    @Ignore
    public void testNewSDKProjectEclipseWorkspace() throws Exception
    {
        // not supported in 6.2.3
    }

    @Override
    @Test
    public void testNewSDKProjectInSDK() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        super.testNewSDKProjectInSDK();
    }

    @Override
    @Test
    public void testNewThemeProjects() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        super.testNewThemeProjects();
    }

    @Override
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

        super.testProjectNameValidation( "project-name-validation-623" );
    }

    @Override
    @Test
    public void testNewSDKProjects() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        createAntProject( newProjectOp( "test-name-1" ) );
        createAntProject( newProjectOp( "test_name_1" ) );
        createAntProject( newProjectOp( "-portlet-portlet" ) );
        createAntProject( newProjectOp( "-portlet-hook" ) );

        final NewLiferayPluginProjectOp op = newProjectOp( "-hook-hook" );
        op.setPluginType( PluginType.hook );
        createAntProject( op );
    }

    @Test
    public void testNewWebAntProjectValidation() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String projectName = "test-web-project-sdk";

        final NewLiferayPluginProjectOp op = newProjectOp( projectName );

        op.setPluginType( PluginType.web );

        op.setSdkLocation( PathBridge.create( getLiferayPluginsSdkDir() ) );

        assertEquals(
            "The selected Plugins SDK does not support creating new web type plugins.  Please configure version 7.0.0 or greater.",
            op.getSdkLocation().validation().message() );
    }

}
