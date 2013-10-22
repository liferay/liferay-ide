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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.server.util.ServerUtil;

import java.util.Set;

import org.eclipse.sapphire.modeling.Status.Severity;
import org.eclipse.sapphire.services.PossibleValuesService;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeLifecycleListener;
import org.eclipse.wst.server.core.ServerCore;


/**
 * @author Gregory Amerson
 */
public class RuntimeNamePossibleValuesService extends PossibleValuesService implements IRuntimeLifecycleListener
{
    @Override
    protected void init()
    {
        super.init();

        ServerCore.addRuntimeLifecycleListener( this );
    }

    @Override
    protected void fillPossibleValues( Set<String> values )
    {
        IRuntime[] runtimes = ServerCore.getRuntimes();

        if( ! CoreUtil.isNullOrEmpty( runtimes ) )
        {
            for( IRuntime runtime : runtimes )
            {
                if( ServerUtil.isLiferayRuntime( runtime ) )
                {
                    values.add( runtime.getName() );
                }
            }
        }
    }

    @Override
    public Severity getInvalidValueSeverity( String invalidValue )
    {
        if( RuntimeNameDefaultValueService.NONE.equals( invalidValue ) ) //$NON-NLS-1$
        {
            return Severity.OK;
        }

        return super.getInvalidValueSeverity( invalidValue );
    }

    @Override
    public boolean ordered()
    {
        return true;
    }

    public void runtimeAdded( IRuntime runtime )
    {
        this.broadcast();
    }

    public void runtimeChanged( IRuntime runtime )
    {
        this.broadcast();
    }

    public void runtimeRemoved( IRuntime runtime )
    {
        this.broadcast();
    }

}
