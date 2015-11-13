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

import com.liferay.ide.gradle.core.GradleCore;
import com.liferay.ide.server.util.ServerUtil;

import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.wst.server.core.IRuntime;

/**
 * @author Terry Jia
 * @author Andy Wu
 */
public class OSGiBundleListener extends FilteredListener<PropertyContentEvent>
{

    @Override
    protected void handleTypedEvent( PropertyContentEvent event )
    {
        NewModuleFragmentOp op = op( event );

        final IPath temp = GradleCore.getDefault().getStateLocation();

        final String runtimeName = op.getLiferayRuntimeName().content();
        final String hostOsgiBundle = op.getHostOsgiBundle().content();

        IRuntime runtime = ServerUtil.getRuntime( runtimeName );

        ServerUtil.getModuleFileFrom70Server( runtime, hostOsgiBundle, temp );

        op.getOverrideFiles().clear();
    }

    protected NewModuleFragmentOp op( PropertyContentEvent event )
    {
        return event.property().element().nearest( NewModuleFragmentOp.class );
    }
}
