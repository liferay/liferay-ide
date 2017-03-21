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

import freemarker.debug.Breakpoint;
import freemarker.debug.DebuggedEnvironment;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;


/**
 * @author Gregory Amerson
 */
public class FMThread extends FMDebugElement implements IThread
{

    /**
     * Breakpoints this thread is suspended at or <code>null</code>
     * if none.
     */
    private IBreakpoint[] breakpoints;

    private Breakpoint stepBreakpoint;

    /**
     * Whether this thread is stepping
     */
    private boolean stepping = false;

    private DebuggedEnvironment suspendedEnvironment;

    /**
     * Corresponds to the thread id of the originating freemarker engine thre
     */
    private long threadId = -1;


    /**
     * Constructs a new thread for the given target
     *
     * @param target VM
     */
    public FMThread( FMDebugTarget fmDebugTarget )
    {
        super( fmDebugTarget );
    }


    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.core.model.ISuspendResume#canResume()
     */
    public boolean canResume()
    {
        return isSuspended();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.core.model.IStep#canStepInto()
     */
    public boolean canStepInto()
    {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.core.model.IStep#canStepOver()
     */
    public boolean canStepOver()
    {
        return isSuspended();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.core.model.IStep#canStepReturn()
     */
    public boolean canStepReturn()
    {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.core.model.ISuspendResume#canSuspend()
     */
    public boolean canSuspend()
    {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.core.model.ITerminate#canTerminate()
     */
    public boolean canTerminate()
    {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.core.model.IThread#getBreakpoints()
     */
    public IBreakpoint[] getBreakpoints()
    {
        if( this.breakpoints == null )
        {
            return new IBreakpoint[0];
        }

        return this.breakpoints;
    }

    public DebuggedEnvironment getEnvironment()
    {
        return this.suspendedEnvironment;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.core.model.IThread#getName()
     */
    public String getName() throws DebugException
    {
        return "FM Engine";
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.core.model.IThread#getPriority()
     */
    public int getPriority() throws DebugException
    {
        return 0;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.core.model.IThread#getStackFrames()
     */
    public IStackFrame[] getStackFrames() throws DebugException
    {
        if( isSuspended() )
        {
            return ( (FMDebugTarget) getDebugTarget() ).getStackFrames();
        }
        else
        {
            return new IStackFrame[0];
        }
    }

    public Breakpoint getStepBreakpoint()
    {
        return this.stepBreakpoint;
    }

    public long getThreadId()
    {
        return this.threadId;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.core.model.IThread#getTopStackFrame()
     */
    public IStackFrame getTopStackFrame() throws DebugException
    {
        final IStackFrame[] frames = getStackFrames();

        if( frames.length > 0 )
        {
            return frames[0];
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.core.model.IThread#hasStackFrames()
     */
    public boolean hasStackFrames() throws DebugException
    {
        return isSuspended();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.core.model.IStep#isStepping()
     */
    public boolean isStepping()
    {
        return this.stepping;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.core.model.ISuspendResume#isSuspended()
     */
    public boolean isSuspended()
    {
        return getDebugTarget().isSuspended();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.core.model.ITerminate#isTerminated()
     */
    public boolean isTerminated()
    {
        return getDebugTarget().isTerminated();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.core.model.ISuspendResume#resume()
     */
    public void resume() throws DebugException
    {
        getDebugTarget().resume( this );
    }

    /**
     * Sets the breakpoints this thread is suspended at, or <code>null</code> if none.
     *
     * @param breakpoints
     *            the breakpoints this thread is suspended at, or <code>null</code> if none
     */
    protected void setBreakpoints( IBreakpoint[] breakpoints )
    {
        this.breakpoints = breakpoints;
    }

    public void setEnvironment( DebuggedEnvironment environment )
    {
        this.suspendedEnvironment = environment;
    }

    public void setStepBreakpoint( Breakpoint bp )
    {
        this.stepBreakpoint = bp;
    }

    /**
     * Sets whether this thread is stepping
     *
     * @param stepping
     *            whether stepping
     */
    protected void setStepping( boolean stepping )
    {
        this.stepping = stepping;
    }

    public void setThreadId( long threadId )
    {
        this.threadId = threadId;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.core.model.IStep#stepInto()
     */
    public void stepInto() throws DebugException
    {
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.core.model.IStep#stepOver()
     */
    public void stepOver() throws DebugException
    {
        ( (FMDebugTarget) getDebugTarget() ).step( this );
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.core.model.IStep#stepReturn()
     */
    public void stepReturn() throws DebugException
    {
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.core.model.ISuspendResume#suspend()
     */
    public void suspend() throws DebugException
    {
        getDebugTarget().suspend();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.core.model.ITerminate#terminate()
     */
    public void terminate() throws DebugException
    {
        getDebugTarget().terminate();
    }

}
