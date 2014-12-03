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
package com.liferay.ide.project.core;

import com.liferay.ide.core.ILiferayPortal;
import com.liferay.ide.server.core.ILiferayRuntime;

import java.util.Properties;

import org.eclipse.core.runtime.IPath;


/**
 * @author Gregory Amerson
 */
public class PluginsSDKPortal implements ILiferayPortal
{
    private final ILiferayRuntime runtime;

    public PluginsSDKPortal( ILiferayRuntime runtime )
    {
        this.runtime = runtime;
    }

    @Override
    public IPath getAppServerPortalDir()
    {
        return this.runtime.getAppServerPortalDir();
    }

    @Override
    public String[] getHookSupportedProperties()
    {
        return this.runtime.getHookSupportedProperties();
    }

    @Override
    public Properties getPortletCategories()
    {
        return this.runtime.getPortletCategories();
    }

    @Override
    public Properties getPortletEntryCategories()
    {
        return this.runtime.getPortletEntryCategories();
    }

    @Override
    public String getVersion()
    {
        return this.runtime.getPortalVersion();
    }
}
