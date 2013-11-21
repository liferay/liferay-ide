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

import org.eclipse.core.runtime.IPath;
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
        return LiferayProjectCore.getDefault().getStateLocation().append( "liferay-plugins-sdk-6.2.0" );
    }

    @Override
    protected IPath getLiferayPluginsSDKZip()
    {
        return tempDownloadsPath.append( "liferay-plugins-sdk-6.2.0-ce-rc5-with-ivy-cache.zip" );
    }

    @Override
    protected String getLiferayPluginsSDKZipUrl()
    {
        return "http://vm-32.liferay.com/files/liferay-plugins-sdk-6.2.0-ce-rc5-with-ivy-cache.zip";
    }

    @Override
    protected IPath getLiferayRuntimeDir()
    {
        return LiferayProjectCore.getDefault().getStateLocation().append( "liferay-portal-6.2.0-ce-rc5/tomcat-7.0.40" );
    }

    @Override
    protected IPath getLiferayRuntimeZip()
    {
        return tempDownloadsPath.append( "liferay-portal-tomcat-6.2.0-ce-rc5-20131017114004875.zip" );
    }

    @Override
    protected String getLiferayRuntimeZipUrl()
    {
        return "http://vm-32.liferay.com/files/liferay-portal-tomcat-6.2.0-ce-rc5-20131017114004875.zip";
    }

    @Override
    protected String getRuntimeId()
    {
        return "com.liferay.ide.eclipse.server.tomcat.runtime.70";
    }

    @Override
    protected String getRuntimeVersion()
    {
        return "6.2.0";
    }

    @Test
    public void testLocationListener() throws Exception
    {
        super.testLocationListener();
    }

    @Test
    public void testNewProjectCustomLocationPortlet() throws Exception
    {
        super.testNewProjectCustomLocationPortlet();
    }

    @Test
    public void testNewProjectCustomLocationWrongSuffix() throws Exception
    {
        super.testNewProjectCustomLocationWrongSuffix();
    }

    @Test
    public void testNewSDKProjectCustomLocation() throws Exception
    {
        super.testNewSDKProjectCustomLocation();
    }

    @Test
    public void testNewSDKProjectEclipseWorkspace() throws Exception
    {
        super.testNewSDKProjectEclipseWorkspace();
    }

    @Test
    public void testPluginTypeListener() throws Exception
    {
        super.testPluginTypeListener( true );
    }

    @Test
    public void testUseDefaultLocationEnablement() throws Exception
    {
        super.testUseDefaultLocationEnablement( true );
    }

    @Test
    public void testUseDefaultLocationListener() throws Exception
    {
        super.testUseDefaultLocationListener( true );
    }

    @Test
    public void testUseSdkLocationListener() throws Exception
    {
        super.testUseSdkLocationListener();
    }

    @Override
    protected String getServiceXmlDoctype()
    {
        return "service-builder PUBLIC \"-//Liferay//DTD Service Builder 6.2.0//EN\" \"http://www.liferay.com/dtd/liferay-service-builder_6_2_0.dtd";
    }
}
