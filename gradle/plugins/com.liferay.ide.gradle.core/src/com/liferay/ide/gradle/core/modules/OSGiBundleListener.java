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

package com.liferay.ide.gradle.core.modules;

import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.portal.PortalBundle;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;

import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.wst.server.core.IRuntime;

/**
 * @author Terry Jia
 */
public class OSGiBundleListener extends FilteredListener<PropertyContentEvent>
{

    @Override
    protected void handleTypedEvent( PropertyContentEvent event )
    {
        NewModuleFragmentOp op = op( event );

        op.getCustomFiles().clear();

        String osgiBundle = op.getCustomOSGiBundle().content();

        final String runtimeName = op.getBundleName().content();

        IRuntime runtime = ServerUtil.getRuntime( runtimeName );

        PortalBundle portalBundle = LiferayServerCore.newPortalBundle( runtime.getLocation() );

        if( portalBundle != null )
        {
            File module = portalBundle.getOSGiBundlesDir().append( "modules" ).append( osgiBundle ).toFile();

            if( module.exists() )
            {
                op.setRealOSGiBundleFile( module.getPath() );
            }
        }
    }

    protected NewModuleFragmentOp op( PropertyContentEvent event )
    {
        return event.property().element().nearest( NewModuleFragmentOp.class );
    }

}
