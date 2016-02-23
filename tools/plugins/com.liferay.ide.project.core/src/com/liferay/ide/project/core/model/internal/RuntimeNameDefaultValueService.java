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
package com.liferay.ide.project.core.model.internal;

import com.liferay.ide.project.core.model.HasLiferayRuntime;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeLifecycleListener;
import org.eclipse.wst.server.core.ServerCore;


/**
 * @author Gregory Amerson
 */
public class RuntimeNameDefaultValueService extends DefaultValueService implements IRuntimeLifecycleListener
{

    static final String NONE = "<None>";

    private static IRuntime newRuntime = null;

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
        String value = null;

        final RuntimeNamePossibleValuesService service =
            context( HasLiferayRuntime.class ).property( HasLiferayRuntime.PROP_RUNTIME_NAME ).service(
                RuntimeNamePossibleValuesService.class );

        final Set<String> values = new HashSet<String>();

        service.compute( values );

        if( values.size() > 0 )
        {
            final String[] vals = values.toArray( new String[0] );

            Arrays.sort( vals );

            if( newRuntime != null )
            {
                for( String runtimeName : values )
                {
                    if (runtimeName.equals( newRuntime.getName() )) {
                        value = newRuntime.getName();

                        break;
                    }
                }

                if( value == null )
                {
                    value = vals[vals.length - 1];
                }
            }
            else
            {
                value = vals[vals.length - 1];
            }
        }
        else
        {
            value = NONE; //$NON-NLS-1$
        }

        return value;
    }

    public void runtimeAdded( IRuntime runtime )
    {
        newRuntime = runtime;
        refresh();
        newRuntime = null;
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
