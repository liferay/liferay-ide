/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.liferay.ide.server.core.portal;

import java.util.Properties;

import org.eclipse.core.runtime.IPath;

/**
 * @author Terry Jia
 */
public class EmptyPortalBundle implements PortalBundle
{

    private final String msg;

    public EmptyPortalBundle( String msg )
    {
        this.msg = msg;
    }

    @Override
    public IPath getAppServerDeployDir()
    {
        return null;
    }

    @Override
    public IPath getAppServerDir()
    {
        return null;
    }

    @Override
    public IPath getAppServerLibGlobalDir()
    {
        return null;
    }

    @Override
    public IPath getAppServerPortalDir()
    {
        return null;
    }

    @Override
    public IPath getAutoDeployPath()
    {
        return null;
    }

    @Override
    public IPath[] getBundleDependencyJars()
    {
        return null;
    }

    @Override
    public String getDisplayName()
    {
        return null;
    }

    @Override
    public String[] getHookSupportedProperties()
    {
        return null;
    }

    @Override
    public String getHttpPort()
    {
        return null;
    }

    @Override
    public int getJmxRemotePort()
    {
        return 0;
    }

    @Override
    public IPath getLiferayHome()
    {
        return null;
    }

    @Override
    public String getMainClass()
    {
        return null;
    }

    public String getMsg()
    {
        return msg;
    }

    @Override
    public IPath getModulesPath()
    {
        return null;
    }

    @Override
    public IPath getOSGiBundlesDir()
    {
        return null;
    }

    @Override
    public Properties getPortletCategories()
    {
        return null;
    }

    @Override
    public Properties getPortletEntryCategories()
    {
        return null;
    }

    @Override
    public IPath[] getRuntimeClasspath()
    {
        return null;
    }

    @Override
    public String[] getRuntimeStartProgArgs()
    {
        return null;
    }

    @Override
    public String[] getRuntimeStartVMArgs()
    {
        return null;
    }

    @Override
    public String[] getRuntimeStopProgArgs()
    {
        return null;
    }

    @Override
    public String[] getRuntimeStopVMArgs()
    {
        return null;
    }

    @Override
    public String getType()
    {
        return null;
    }

    @Override
    public IPath[] getUserLibs()
    {
        return null;
    }

    @Override
    public String getVersion()
    {
        return null;
    }

    @Override
    public void setHttpPort( String port )
    {
    }

}
