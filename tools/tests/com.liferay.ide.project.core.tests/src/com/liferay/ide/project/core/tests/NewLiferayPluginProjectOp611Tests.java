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

import com.liferay.ide.project.core.LiferayProjectCore;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.junit.Test;

/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
public class NewLiferayPluginProjectOp611Tests extends NewLiferayPluginProjectOpBase
{

    @Override
    protected IProject checkNewJsfAntProjectIvyFile( IProject jsfProject, String jsfSuite ) throws Exception
    {
        // ivy not supported in 6.1.1
        return jsfProject;
    }

    @Override
    protected IPath getLiferayPluginsSdkDir()
    {
        return LiferayProjectCore.getDefault().getStateLocation().append( "liferay-plugins-sdk-6.1.1" );
    }

    @Override
    protected IPath getLiferayPluginsSDKZip()
    {
        return tempDownloadsPath.append( "liferay-plugins-sdk-6.1.1-ce-ga2-20121004092655026.zip" );
    }

    @Override
    protected String getLiferayPluginsSDKZipUrl()
    {
        return "http://vm-32.liferay.com/files/liferay-plugins-sdk-6.1.1-ce-ga2-20121004092655026.zip";
    }

    @Override
    protected IPath getLiferayRuntimeDir()
    {
        return LiferayProjectCore.getDefault().getStateLocation().append( "liferay-portal-6.1.1-ce-ga2" );
    }

    @Override
    protected IPath getLiferayRuntimeZip()
    {
        return tempDownloadsPath.append( "liferay-portal-tomcat-6.1.1-ce-ga2-20120731132656558.zip" );
    }

    @Override
    protected String getLiferayRuntimeZipUrl()
    {
        return "http://vm-32.liferay.com/files/liferay-portal-tomcat-6.1.1-ce-ga2-20120731132656558.zip";
    }

    @Override
    protected String getRuntimeId()
    {
        return "com.liferay.ide.eclipse.server.tomcat.runtime.70";
    }

    @Override
    protected String getRuntimeVersion()
    {
        return "6.1.1";
    }

    @Override
    public void testNewProjectCustomLocationPortlet() throws Exception
    {
        // not supported in 6.1.1
    }

    @Override
    public void testNewProjectCustomLocationWrongSuffix() throws Exception
    {
        // not supported in 6.1.1
    }

    @Override
    public void testNewSDKProjectCustomLocation() throws Exception
    {
        // not supported in 6.1.1
    }

    @Override
    public void testNewSDKProjectEclipseWorkspace() throws Exception
    {
        // not supported in 6.1.1
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
