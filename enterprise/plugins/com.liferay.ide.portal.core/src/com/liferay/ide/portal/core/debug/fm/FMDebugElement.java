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
package com.liferay.ide.portal.core.debug.fm;

import com.liferay.ide.portal.core.PortalCore;
import com.liferay.ide.portal.core.debug.ILRDebugConstants;

import freemarker.debug.DebugModel;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IVariable;


/**
 * @author Gregory Amerson
 */
public class FMDebugElement extends PlatformObject implements IDebugElement
{

    private FMDebugTarget target;

    public FMDebugElement( FMDebugTarget target )
    {
        this.target = target;
    }

    protected void abort( String message, Throwable e ) throws DebugException
    {
        throw new DebugException( new Status(
            IStatus.ERROR, PortalCore.getDefault().getBundle().getSymbolicName(),
            DebugPlugin.INTERNAL_ERROR, message, e ) );
    }

    /**
     * Fires a <code>CREATE</code> event for this element.
     */
    protected void fireCreationEvent()
    {
        fireEvent( new DebugEvent( this, DebugEvent.CREATE ) );
    }

    /**
     * Fires a debug event
     *
     * @param event
     *            the event to be fired
     */
    protected void fireEvent( DebugEvent event )
    {
        DebugPlugin.getDefault().fireDebugEventSet( new DebugEvent[] { event } );
    }

    /**
     * Fires a <code>RESUME</code> event for this element with the given detail.
     *
     * @param detail
     *            event detail code
     */
    public void fireResumeEvent( int detail )
    {
        fireEvent( new DebugEvent( this, DebugEvent.RESUME, detail ) );
    }

    /**
     * Fires a <code>SUSPEND</code> event for this element with the given detail.
     *
     * @param detail
     *            event detail code
     */
    public void fireSuspendEvent( int detail )
    {
        fireEvent( new DebugEvent( this, DebugEvent.SUSPEND, detail ) );
    }

    /**
     * Fires a <code>TERMINATE</code> event for this element.
     */
    protected void fireTerminateEvent()
    {
        fireEvent( new DebugEvent( this, DebugEvent.TERMINATE ) );
    }

    @SuppressWarnings( "rawtypes" )
    public Object getAdapter( Class adapter )
    {
        if( adapter == IDebugElement.class )
        {
            return this;
        }
        else if( adapter == ILaunch.class )
        {
            return getDebugTarget().getLaunch();
        }

        return super.getAdapter( adapter );
    }

    public FMDebugTarget getDebugTarget()
    {
        return this.target;
    }

    public ILaunch getLaunch()
    {
        return getDebugTarget().getLaunch();
    }

    public String getModelIdentifier()
    {
        return ILRDebugConstants.ID_FM_DEBUG_MODEL;
    }

    protected String getReferenceTypeName( DebugModel model ) throws DebugException
    {
        try
        {
            switch( model.getModelTypes() )
            {
                case DebugModel.TYPE_BOOLEAN:
                    return "boolean";
                case DebugModel.TYPE_COLLECTION:
                    return "collection";
                case DebugModel.TYPE_CONFIGURATION:
                    return "configuration";
                case DebugModel.TYPE_DATE:
                    return "date";
                case DebugModel.TYPE_ENVIRONMENT:
                    return "environment";
                case DebugModel.TYPE_HASH:
                    return "hash";
                case DebugModel.TYPE_HASH_EX:
                    return "hash_ex";
                case DebugModel.TYPE_METHOD:
                    return "method";
                case DebugModel.TYPE_METHOD_EX:
                    return "method_ex";
                case DebugModel.TYPE_NUMBER:
                    return "number";
                case DebugModel.TYPE_SCALAR:
                    return "scalar";
                case DebugModel.TYPE_SEQUENCE:
                    return "sequence";
                case DebugModel.TYPE_TEMPLATE:
                    return "template";
                case DebugModel.TYPE_TRANSFORM:
                    return "transform";
            }
        }
        catch( RemoteException e )
        {
            e.printStackTrace();
        }

        return "var";
    }

    protected void sortVariables( IVariable[] variables )
    {
        Arrays.sort
        (
            variables,
            new Comparator<IVariable>()
            {
                public int compare( IVariable var1, IVariable var2 )
                {
                    try
                    {
                        return var1.getName().compareTo( var2.getName() );
                    }
                    catch( DebugException e )
                    {
                    }

                    return 0;
                }
            }
        );
    }
}
