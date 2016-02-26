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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.server.core.LiferayServerCore;

import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeLifecycleListener;
import org.eclipse.wst.server.core.ServerCore;

/**
 * @author Terry Jia
 */
public class LiferayRuntimeNameDefaultValueService extends DefaultValueService implements IRuntimeLifecycleListener
{

    static final String NONE = "<None>";

    @Override
    protected void initDefaultValueService()
    {
        super.initDefaultValueService();

        ServerCore.addRuntimeLifecycleListener( this );
    }

    @Override
    public void dispose()
    {
        ServerCore.removeRuntimeLifecycleListener( this );

        super.dispose();
    }

    @Override
    protected String compute()
    {
        IRuntime[] runtimes = ServerCore.getRuntimes();

        String value = NONE;

        if( !CoreUtil.isNullOrEmpty( runtimes ) )
        {
            for( IRuntime runtime : runtimes )
            {
                if( LiferayServerCore.newPortalBundle( runtime.getLocation() ) != null )
                {
                    value = runtime.getName();

                    break;
                }
            }
        }

        return value;
    }

    public void runtimeAdded( IRuntime runtime )
    {
        refresh();
    }

    public void runtimeChanged( IRuntime runtime )
    {
        refresh();
    }

    public void runtimeRemoved( IRuntime runtime )
    {
        refresh();
    }

}
