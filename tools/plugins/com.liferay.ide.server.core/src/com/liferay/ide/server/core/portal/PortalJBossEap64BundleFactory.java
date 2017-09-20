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
public class PortalJBossEap64BundleFactory extends PortalJBossEapBundleFactory
{

    private static final String EAP_DIR_META_INF = "modules/system/layers/base/org/jboss/as/product/eap/dir/META-INF";

    @Override
    public PortalBundle create( Map<String, String> appServerProperties )
    {
        return new PortalJBossEap64Bundle( appServerProperties );
    }

    @Override
    public PortalBundle create( IPath location )
    {
        return new PortalJBossEap64Bundle( location );
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
            String eapVersion = getEAPVersion( path.toFile(), EAP_DIR_META_INF, new String[] { "6." }, "eap", "EAP" );
            return eapVersion != null;
        }

        return false;
    }
}
