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

import com.liferay.ide.server.util.ServerUtil;

import java.util.List;
import java.util.Set;

import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.wst.server.core.IRuntime;

/**
 * @author Terry Jia
 * @author Andy Wu
 */
public class HostOSGiBundlePossibleValuesService extends PossibleValuesService
{

    private List<String> bundles = null;

    private FilteredListener<PropertyContentEvent> listener;

    @Override
    protected void initPossibleValuesService()
    {
        super.initPossibleValuesService();

        this.listener = new FilteredListener<PropertyContentEvent>()
        {

            @Override
            protected void handleTypedEvent( PropertyContentEvent event )
            {
                bundles = null;
                refresh();
            }
        };

        op().property( NewModuleFragmentOp.PROP_LIFERAY_RUNTIME_NAME ).attach( this.listener );
    }

    @Override
    protected void compute( Set<String> values )
    {
        if( this.bundles != null )
        {
            values.addAll( this.bundles );
        }
        else
        {
            final NewModuleFragmentOp op = op();

            if( !op.disposed() )
            {
                final String runtimeName = op.getLiferayRuntimeName().content();

                IRuntime runtime = ServerUtil.getRuntime( runtimeName );

                bundles = ServerUtil.getModuleFileListFrom70Server(runtime);
            }

            values.addAll( bundles );
        }
    }

    private NewModuleFragmentOp op()
    {
        return context( NewModuleFragmentOp.class );
    }

    @Override
    public boolean ordered()
    {
        return true;
    }
}
