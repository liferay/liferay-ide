/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.LiferayProjectCore;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.junit.Test;

/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
public class NewLiferayPluginProjectOp606Tests extends NewLiferayPluginProjectOpBase
{

    @Override
    protected IProject checkNewJsfAntProjectIvyFile( IProject jsfProject, String jsfSuite ) throws Exception
    {
        // ivy not supported in 6.0.6
        return jsfProject;
    }

    @Override
    protected IProject checkNewThemeAntProject( NewLiferayPluginProjectOp op, IProject project, String expectedBuildFile )
        throws Exception
    {
        final String themeParent = op.getThemeParent().content();
        final String themeFramework = op.getThemeFramework().content();
        final IFile buildXml = project.getFile( "build.xml" );

        final String buildXmlContent = CoreUtil.readStreamToString( buildXml.getContents() );

        if( expectedBuildFile == null )
        {
            expectedBuildFile = "build-theme-" + themeParent + "-" + themeFramework + ".xml";
        }

        final String expectedbuildXmlContent =
            CoreUtil.readStreamToString( this.getClass().getResourceAsStream( "files/" + expectedBuildFile ) );

        final String expectedContent = stripCarriageReturns( expectedbuildXmlContent ).replace( "<!DOCTYPE project>\n", "" );

        assertEquals( expectedContent, stripCarriageReturns( buildXmlContent ) );

        return project;
    }

    @Override
    protected IPath getLiferayPluginsSdkDir()
    {
        return LiferayProjectCore.getDefault().getStateLocation().append( "liferay-plugins-sdk-6.0.6" );
    }

    @Override
    protected IPath getLiferayPluginsSDKZip()
    {
        return tempDownloadsPath.append( "liferay-plugins-sdk-6.0.6.zip" );
    }

    @Override
    protected String getLiferayPluginsSDKZipUrl()
    {
        return "http://vm-32.liferay.com/files/liferay-plugins-sdk-6.0.6.zip";
    }

    @Override
    protected IPath getLiferayRuntimeDir()
    {
        return LiferayProjectCore.getDefault().getStateLocation().append( "liferay-portal-6.0.6" );
    }

    @Override
    protected IPath getLiferayRuntimeZip()
    {
        return tempDownloadsPath.append( "liferay-portal-tomcat-6.0.6-20110225.zip" );
    }

    @Override
    protected String getLiferayRuntimeZipUrl()
    {
        return "http://vm-32.liferay.com/files/liferay-portal-tomcat-6.0.6-20110225.zip";
    }

    @Override
    protected String getRuntimeId()
    {
        return "com.liferay.ide.eclipse.server.tomcat.runtime.60";
    }

    @Override
    protected String getRuntimeVersion()
    {
        return "6.0.6";
    }

    @Override
    public void testNewJsfAntProjects() throws Exception
    {
        // jsf projects not supported in 6.0.6
    }

    @Override
    public void testNewProjectCustomLocationPortlet() throws Exception
    {
        // not supported in 6.0.6
    }

    @Override
    public void testNewProjectCustomLocationWrongSuffix() throws Exception
    {
        // not supported in 6.0.6
    }

    @Override
    public void testNewSDKProjectCustomLocation() throws Exception
    {
        // not supported in 6.0.6
    }

    @Override
    public void testNewSDKProjectEclipseWorkspace() throws Exception
    {
        // not supported in 6.0.6
    }

    @Override
    public void testNewVaadinAntProject() throws Exception
    {
        // vaadin projets not supported in 6.0.6
    }

    @Test
    public void testPluginTypeListener() throws Exception
    {
        super.testPluginTypeListener();
    }

    @Test
    public void testUseDefaultLocationEnablement() throws Exception
    {
        super.testPluginTypeListener();
    }

    @Test
    public void testUseDefaultLocationListener() throws Exception
    {
        super.testUseDefaultLocationListener();
    }

}
