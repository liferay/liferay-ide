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

import static org.junit.Assert.assertFalse;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.ProjectCore;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Terry Jia
 */
public class NewLiferayPluginProjectOp701Tests extends NewLiferayPluginProjectOpBase
{

    @Override
    protected IPath getLiferayPluginsSdkDir()
    {
        return ProjectCore.getDefault().getStateLocation().append(
            "com.liferay.portal.plugins.sdk-1.0.11-withdependencies" );
    }

    @Override
    protected IPath getLiferayPluginsSDKZip()
    {
        return getLiferayBundlesPath().append(
            "com.liferay.portal.plugins.sdk-1.0.11-withdependencies-20170613175008905.zip" );
    }

    @Override
    protected String getLiferayPluginsSdkZipFolder()
    {
        return "com.liferay.portal.plugins.sdk-1.0.11-withdependencies/";
    }

    @AfterClass
    public static void removePluginsSDK() throws Exception
    {
        deleteAllWorkspaceProjects();
    }

    protected IPath getLiferayRuntimeDir()
    {
        return ProjectCore.getDefault().getStateLocation().append( "liferay-ce-portal-7.0-ga4/tomcat-8.0.32" );
    }

    protected IPath getLiferayRuntimeZip()
    {
        return getLiferayBundlesPath().append( "liferay-ce-portal-tomcat-7.0-ga4-20170613175008905.zip" );
    }

    public String getRuntimeVersion()
    {
        return "7.0.2";
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
        if( shouldSkipBundleTests() )
            return;

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

    @BeforeClass
    public static void removeAllProjects() throws Exception
    {
        IProgressMonitor monitor = new NullProgressMonitor();

        for( IProject project : CoreUtil.getAllProjects() )
        {
            project.delete( true, monitor );

            assertFalse( project.exists() );
        }
    }

}
