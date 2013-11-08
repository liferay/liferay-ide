
package com.liferay.ide.project.core.tests;

import com.liferay.ide.project.core.LiferayProjectCore;

import org.eclipse.core.runtime.IPath;
import org.junit.Test;

/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
public class NewLiferayPluginProjectOp612Tests extends NewLiferayPluginProjectOpBase
{

    @Override
    protected IPath getLiferayPluginsSdkDir()
    {
        return LiferayProjectCore.getDefault().getStateLocation().append( "liferay-plugins-sdk-6.1.2" );
    }

    @Override
    protected IPath getLiferayPluginsSDKZip()
    {
        return tempDownloadsPath.append( "liferay-plugins-sdk-6.1.2-with-ivy-cache.zip" );
    }

    @Override
    protected String getLiferayPluginsSDKZipUrl()
    {
        return "http://vm-32.liferay.com/files/liferay-plugins-sdk-6.1.2-with-ivy-cache.zip";
    }

    @Override
    protected IPath getLiferayRuntimeDir()
    {
        return LiferayProjectCore.getDefault().getStateLocation().append( "liferay-portal-6.1.2-ce-ga3" );
    }

    @Override
    protected IPath getLiferayRuntimeZip()
    {
        return tempDownloadsPath.append( "liferay-portal-tomcat-6.1.2-ce-ga3-20130816114619181.zip" );
    }

    @Override
    protected String getLiferayRuntimeZipUrl()
    {
        return "http://vm-32.liferay.com/files/liferay-portal-tomcat-6.1.2-ce-ga3-20130816114619181.zip";
    }

    @Override
    protected String getRuntimeId()
    {
        return "com.liferay.ide.eclipse.server.tomcat.runtime.70";
    }

    @Override
    protected String getRuntimeVersion()
    {
        return "6.1.2";
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
