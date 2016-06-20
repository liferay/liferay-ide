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

import org.eclipse.core.runtime.IPath;
import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;

import com.liferay.ide.project.core.ProjectCore;

/**
 * @author Terry Jia
 */
public class NewLiferayPluginProjectOp701Tests extends NewLiferayPluginProjectOpBase
{

    protected IPath getLiferayPluginsSdkDir()
    {
        return ProjectCore.getDefault().getStateLocation().append( "com.liferay.portal.plugins.sdk-7.0" );
    }

    protected IPath getLiferayPluginsSDKZip()
    {
        return getLiferayBundlesPath().append( "com.liferay.portal.plugins.sdk-7.0-ga2-20160610113014153.zip" );
    }

    protected String getLiferayPluginsSdkZipFolder()
    {
        return "com.liferay.portal.plugins.sdk-7.0/";
    }

    @AfterClass
    public static void removePluginsSDK() throws Exception
    {
        deleteAllWorkspaceProjects();
    }

    protected IPath getLiferayRuntimeDir()
    {
        return ProjectCore.getDefault().getStateLocation().append( "liferay-ce-portal-7.0-ga2/tomcat-8.0.32" );
    }

    protected IPath getLiferayRuntimeZip()
    {
        return getLiferayBundlesPath().append( "liferay-ce-portal-tomcat-7.0-ga2-20160610113014153.zip" );
    }

    public String getRuntimeVersion()
    {
        return "7.0.1";
    }

    @Override
    protected String getServiceXmlDoctype()
    {
        return "service-builder PUBLIC \"-//Liferay//DTD Service Builder 7.0.0//EN\" \"http://www.liferay.com/dtd/liferay-service-builder_7_0_0.dtd";
    }

    @Override
    @Test
    @Ignore
    public void testLocationListener() throws Exception
    {
    }

    @Override
    @Test
    @Ignore
    public void testNewJsfRichfacesProjects() throws Exception
    {
    }

    @Override
    @Test
    @Ignore
    public void testNewLayoutAntProject() throws Exception
    {
    }

    @Test
    @Ignore
    public void testNewProjectCustomLocationPortlet() throws Exception
    {
    }

    @Test
    @Ignore
    public void testNewProjectCustomLocationWrongSuffix() throws Exception
    {
    }

    @Test
    @Ignore
    public void testNewSDKProjectCustomLocation() throws Exception
    {
    }

    @Test
    @Ignore
    public void testNewSDKProjectEclipseWorkspace() throws Exception
    {
    }

    @Override
    @Test
    @Ignore
    public void testNewSDKProjectInSDK() throws Exception
    {
    }

    @Override
    @Test
    public void testNewThemeProjects() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        super.testNewThemeProjects();
    }

    @Test
    @Ignore
    public void testNewJsfAntProjects() throws Exception
    {
    }

    @Override
    @Test
    @Ignore
    public void testPluginTypeListener() throws Exception
    {
    }

    @Test
    @Ignore
    public void testProjectNameValidation() throws Exception
    {
    }

    @Override
    @Test
    @Ignore
    public void testNewSDKProjects() throws Exception
    {
    }

    @Test
    @Ignore
    public void testNewWebAntProjectValidation() throws Exception
    {
    }

}
