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
package com.liferay.ide.debug.core.fm;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.debug.core.LiferayDebugCore;

import freemarker.debug.DebuggedEnvironment;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;


/**
 * @author Gregory Amerson
 */
public class FMStackFrame extends FMDebugElement implements IStackFrame
{
    private String name;
    private FMThread thread;
    private IVariable[] variables;

    public FMStackFrame( FMThread thread, String name )
    {
        super( thread.getDebugTarget() );
        this.thread = thread;
        this.name = name;
    }

    public boolean canResume()
    {
        return getThread().canResume();
    }

    public boolean canStepInto()
    {
        return getThread().canStepInto();
    }

    public boolean canStepOver()
    {
        return getThread().canStepOver();
    }

    public boolean canStepReturn()
    {
        return getThread().canStepReturn();
    }

    public boolean canSuspend()
    {
        return getThread().canSuspend();
    }

    public boolean canTerminate()
    {
        return getThread().canTerminate();
    }

    public int getCharEnd() throws DebugException
    {
        return -1;
    }

    public int getCharStart() throws DebugException
    {
        return -1;
    }

    public int getLineNumber() throws DebugException
    {
        int retval = -1;

        if( this.thread != null && this.thread.isSuspended() )
        {
            IBreakpoint[] lineBreakpoints = this.thread.getBreakpoints();

            if( ! CoreUtil.isNullOrEmpty( lineBreakpoints ) )
            {
                IBreakpoint bp = lineBreakpoints[0];

                if( bp instanceof ILineBreakpoint )
                {
                    ILineBreakpoint lineBp = (ILineBreakpoint) bp;

                    try
                    {
                        retval = lineBp.getLineNumber();
                    }
                    catch( CoreException e )
                    {
                        LiferayDebugCore.logError( "Could not get breakpoint charStart", e );
                    }
                }
            }
            else if ( this.thread.getStepBreakpoint() != null )
            {
                retval = this.thread.getStepBreakpoint().getLine();
            }
        }

        return retval;
    }

    public String getName() throws DebugException
    {
        return this.name;
    }

    public IRegisterGroup[] getRegisterGroups() throws DebugException
    {
        return null;
    }

    public FMThread getThread()
    {
        return this.thread;
    }

    public IVariable[] getVariables() throws DebugException
    {
        if( this.variables == null )
        {
            /*
             * Represents the debugger-side mirror of a debugged freemarker.core.Environment object in the remote VM.
             *
             * This interface extends DebugModel, and the properties of the Environment are exposed as hash keys on it.
             * Specifically, the following keys are supported: "currentNamespace", "dataModel", "globalNamespace",
             * "knownVariables", "mainNamespace", and "template".
             *
             * The debug model for the template supports keys
             * "configuration" and "name".
             *
             * The debug model for the configuration supports key "sharedVariables".
             * Additionally, all of the debug models for environment, template, and configuration also support all the
             * setting keys of freemarker.core.Configurable objects.
             */
            final DebuggedEnvironment env = this.thread.getEnvironment();

            try
            {
                this.variables = new IVariable[]
                {
                    new FMVariable( this, "currentNamespace", env.get("currentNamespace") ),
                    new FMVariable( this, "dataModel", env.get( "dataModel" ) ),
                    new FMVariable( this, "globalNamespace", env.get( "globalNamespace" ) ),
                    new FMVariable( this, "knownVariables", env.get( "knownVariables" ) ),
                    new FMVariable( this, "mainNamespace", env.get( "mainNamespace" ) ),
                    new FMVariable( this, "template", env.get( "template" ) )
                    {
                        @Override
                        public IValue getValue() throws DebugException
                        {
                            return new TemplateVMValue( stackFrame, debugModel );
                        }
                    },
                };
            }
            catch( Exception e )
            {
                e.printStackTrace();
            }
        }

        return this.variables;
    }

    public boolean hasRegisterGroups() throws DebugException
    {
        return false;
    }

    public boolean hasVariables() throws DebugException
    {
        return this.variables != null && this.variables.length > 0;
    }

    public boolean isStepping()
    {
        return getThread().isStepping();
    }

    public boolean isSuspended()
    {
        return getThread().isSuspended();
    }

    public boolean isTerminated()
    {
        return getThread().isTerminated();
    }

    public void resume() throws DebugException
    {
        getThread().resume();
    }

    public void stepInto() throws DebugException
    {
        getThread().stepInto();
    }

    public void stepOver() throws DebugException
    {
        getThread().stepOver();
    }

    public void stepReturn() throws DebugException
    {
        getThread().stepReturn();
    }

    public void suspend() throws DebugException
    {
        getThread().suspend();
    }

    public void suspendJavaThread() throws DebugException
    {
        getDebugTarget().suspendRelatedJavaThread( getThread().getThreadId() );
    }

    public void terminate() throws DebugException
    {
        getThread().terminate();
    }

}
