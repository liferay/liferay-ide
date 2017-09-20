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

package com.liferay.ide.server.core.portal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;

/**
 * @author Simon Jiang
 */
public class PortalJBossEap64Bundle extends PortalJBossEapBundle
{

    public PortalJBossEap64Bundle( IPath path )
    {
        super( path );
    }

    public PortalJBossEap64Bundle( Map<String, String> appServerProperties )
    {
        super( appServerProperties );
    }

    @Override
    public String getDisplayName()
    {
        return "JBoss EAP 6.4";
    }

    @Override
    public String[] getRuntimeStopProgArgs()
    {
        final List<String> args = new ArrayList<String>();

        args.add( "-mp \"" + this.bundlePath.toPortableString() + "/modules" + "\"" );
        args.add( "org.jboss.as.cli" );

        PortalBundleConfiguration bundleConfiguration = initBundleConfiguration();
        int managetPort = 9999;

        if( bundleConfiguration != null )
        {
            List<LiferayServerPort> configuredServerPorts = bundleConfiguration.getConfiguredServerPorts();

            for( LiferayServerPort serverPort : configuredServerPorts )
            {
                if( serverPort.getProtocol().toLowerCase().equals( "management-native" ) )
                {
                    managetPort = serverPort.getPort();
                    break;
                }
            }
        }

        args.add( "--controller=localhost:" + managetPort );
        args.add( "--connect" );
        args.add( "--command=:shutdown" );

        return args.toArray( new String[0] );
    }
}
