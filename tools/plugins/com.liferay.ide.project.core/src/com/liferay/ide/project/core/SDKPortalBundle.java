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
import com.liferay.ide.server.core.portal.PortalBundle;
import com.liferay.ide.server.util.LiferayPortalValueLoader;
import com.liferay.ide.server.util.ServerUtil;

import java.util.Properties;

import org.eclipse.core.runtime.IPath;


/**
 * @author Simon.Jiang
 */
public class SDKPortalBundle implements ILiferayPortal
{
    private final PortalBundle portalBundle;

    public SDKPortalBundle( PortalBundle portalBundle )
    {
        this.portalBundle = portalBundle;
    }

    @Override
    public IPath getAppServerPortalDir()
    {
        return this.portalBundle.getPortalDir();
    }

    @Override
    public String[] getHookSupportedProperties()
    {
        IPath portalDir = portalBundle.getPortalDir();
        IPath[] extraLibs = portalBundle.getBundleDependencyJars();
        return new LiferayPortalValueLoader( portalDir, extraLibs ).loadHookPropertiesFromClass();
    }

    @Override
    public Properties getPortletCategories()
    {
        return ServerUtil.getPortletCategories( getAppServerPortalDir() );
    }

    @Override
    public Properties getPortletEntryCategories()
    {
        return ServerUtil.getPortletCategories( getAppServerPortalDir() );
    }

    @Override
    public String getVersion()
    {
        return portalBundle.getVersion();
    }
}
