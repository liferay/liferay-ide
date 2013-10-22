/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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
package com.liferay.ide.project.core.model.internal;

import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.sapphire.services.DefaultValueService;
import org.eclipse.sapphire.services.DefaultValueServiceData;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeLifecycleListener;
import org.eclipse.wst.server.core.ServerCore;


/**
 * @author Gregory Amerson
 */
public class RuntimeNameDefaultValueService extends DefaultValueService implements IRuntimeLifecycleListener
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
    protected DefaultValueServiceData compute()
    {
        String value = null;

        final RuntimeNamePossibleValuesService service =
            context( NewLiferayPluginProjectOp.class ).property( NewLiferayPluginProjectOp.PROP_RUNTIME_NAME ).service(
                RuntimeNamePossibleValuesService.class );

        final Set<String> values = new HashSet<String>();

        service.fillPossibleValues( values );

        if( values.size() > 0 )
        {
            value = values.iterator().next();
        }
        else
        {
            value = NONE; //$NON-NLS-1$
        }

        return new DefaultValueServiceData( value );
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
