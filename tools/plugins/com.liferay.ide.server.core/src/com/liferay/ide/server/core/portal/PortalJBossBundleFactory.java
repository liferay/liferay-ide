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

import java.util.Map;

import org.eclipse.core.runtime.IPath;

/**
 * @author Simon Jiang
 */
public class PortalJBossBundleFactory extends AbstractPortalBundleFactory
{
    @Override
    public PortalBundle create( Map<String, String> appServerProperties )
    {
        return new PortalJBossBundle( appServerProperties );
    }

    @Override
    public PortalBundle create( IPath location )
    {
        return new PortalJBossBundle( location );
    }

    @Override
    protected boolean detectBundleDir( IPath path )
    {
        if( !path.toFile().exists() )
        {
            return false;
        }

        if( path.append( "bundles" ).toFile().exists() && path.append( "modules" ).toFile().exists() &&
            path.append( "standalone" ).toFile().exists() && path.append( "bin" ).toFile().exists() )
        {
            return true;
        }

        return false;
    }

}
