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
import com.liferay.ide.sdk.core.ISDKConstants;
import com.liferay.ide.server.core.portal.PortalBundle;
import com.liferay.ide.server.util.LiferayPortalValueLoader;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.IPath;


/**
 * @author Simon.Jiang
 */
public class PluginsSDKBundle implements ILiferayPortal
{
    private final PortalBundle portalBundle;

    public PluginsSDKBundle( PortalBundle portalBundle )
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

    
    private String getAppServerPropertyKey( String propertyAppServerDeployDir )
    {
        String retval = null;

        try
        {
            final String type = this.portalBundle.getType();

            retval = MessageFormat.format( propertyAppServerDeployDir, "." + type + "." ); //$NON-NLS-1$ //$NON-NLS-2$
        }
        catch( Exception e )
        {
        }
        finally
        {
            if( retval == null )
            {
                retval = MessageFormat.format( propertyAppServerDeployDir, "." ); //$NON-NLS-1$
            }
        }

        return retval;
    }
    
    @Override
    public Map<String, String> getRequiredProperties()
    {
        Map<String, String> properties = new HashMap<String, String>();

        String type = portalBundle.getType();

        String dir = portalBundle.getAppServerDir().toOSString();

        String deployDir = portalBundle.getAppServerDeployDir().toOSString();

        String libGlobalDir = portalBundle.getAppServerLibGlobalDir().toOSString();

        String parentDir = new File( dir ).getParent();

        String portalDir = portalBundle.getPortalDir().toOSString();

        properties.put( ISDKConstants.PROPERTY_APP_SERVER_TYPE, type );

        final String appServerDirKey =
            getAppServerPropertyKey( ISDKConstants.PROPERTY_APP_SERVER_DIR );
        final String appServerDeployDirKey =
            getAppServerPropertyKey( ISDKConstants.PROPERTY_APP_SERVER_DEPLOY_DIR );
        final String appServerLibGlobalDirKey =
            getAppServerPropertyKey( ISDKConstants.PROPERTY_APP_SERVER_LIB_GLOBAL_DIR );
        final String appServerPortalDirKey =
            getAppServerPropertyKey( ISDKConstants.PROPERTY_APP_SERVER_PORTAL_DIR );

        properties.put( appServerDirKey, dir );
        properties.put( appServerDeployDirKey, deployDir );
        properties.put( appServerLibGlobalDirKey, libGlobalDir );
        properties.put( ISDKConstants.PROPERTY_APP_SERVER_PARENT_DIR, parentDir );
        properties.put( appServerPortalDirKey, portalDir );

        return properties;
    }
}
