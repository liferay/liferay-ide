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

import com.liferay.ide.server.util.JavaUtil;

import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * @author Simon Jiang
 */
public class PortalJBossBundleFactory extends AbstractPortalBundleFactory
{

    private static final String JBAS7_RELEASE_VERSION = "JBossAS-Release-Version";

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
            final String mainFolder = new Path( "modules/org/jboss/as/server/main" ).toOSString();

            return JavaUtil.scanFolderJarsForManifestProp( path.toFile(), mainFolder, JBAS7_RELEASE_VERSION, "7." );
        }

        return false;
    }

}
