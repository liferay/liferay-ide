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
import com.liferay.ide.debug.core.ILRDebugConstants;
import com.liferay.ide.debug.core.LiferayDebugCore;

import freemarker.debug.Breakpoint;
import freemarker.debug.DebuggedEnvironment;
import freemarker.debug.Debugger;
import freemarker.debug.DebuggerClient;
import freemarker.debug.DebuggerListener;
import freemarker.debug.EnvironmentSuspendedEvent;

import java.net.Inet4Address;
import java.rmi.RemoteException;
import java.util.Iterator;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;


/**
 * @author Gregory Amerson
 */
public class FMDebugTarget extends FMDebugElement implements IDebugTarget
{

    private String name;
    private ILaunch launch;
    private FMDebugTarget target;
    private IProcess process;
    private FMThread fmThread;
    private IThread[] threads = new IThread[0];
    private EventDispatchJob eventDispatchJob;
//    private Map<Long, DebuggedEnvironment> envs = new HashMap<Long, DebuggedEnvironment>();

    // suspend state
    private boolean suspended = false;

    // terminated state
    private boolean terminated = false;
    private Debugger debuggerClient;
    private IStackFrame[] fmStackFrames = new IStackFrame[0];
    private String host;

    class EventDispatchJob extends Job implements DebuggerListener
    {
        private boolean setup;

        public EventDispatchJob()
        {
            super( "Freemarker Event Dispatch" );
            setSystem( true );
        }

        @Override
        protected IStatus run( IProgressMonitor monitor )
        {
            while( ! isTerminated() )
            {
                // try to connect to debugger
                Debugger debugger = getDebuggerClient();

                if( debugger == null )
                {
                    try
                    {
                        Thread.sleep( 1000 );
                    }
                    catch( InterruptedException e )
                    {
                    }

                    continue;
                }

                if( !setup )
                {
                    setup = setupDebugger(debugger);
                }

                synchronized( eventDispatchJob )
                {
                    try
                    {
                        System.out.println("EVENT DISPATCH JOB WAIT()");
                        wait();
                    }
                    catch( InterruptedException e )
                    {
                    }
                }
            }

            return Status.OK_STATUS;
        }

        private boolean setupDebugger(Debugger debugger)
        {
            try
            {
                debugger.addDebuggerListener( eventDispatchJob );

                FMDebugTarget.this.threads = new IThread[] { FMDebugTarget.this.fmThread };

                final IBreakpoint[] localBreakpoints = DebugPlugin.getDefault().getBreakpointManager().getBreakpoints( getModelIdentifier() );

                for( IBreakpoint localBreakpoint : localBreakpoints )
                {
                    addRemoteBreakpoint( debugger, localBreakpoint );
                }
            }
            catch( RemoteException e )
            {
                return false;
            }

            return true;
        }

        public void environmentSuspended( EnvironmentSuspendedEvent event ) throws RemoteException
        {
            int lineNumber = event.getLine();
            IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager().getBreakpoints( getModelIdentifier() );

            boolean suspended = false;

            for( IBreakpoint breakpoint : breakpoints )
            {
                if( supportsBreakpoint( breakpoint ) )
                {
                    if( breakpoint instanceof ILineBreakpoint )
                    {
                        ILineBreakpoint lineBreakpoint = (ILineBreakpoint) breakpoint;

                        try
                        {
                            final int bpLineNumber = lineBreakpoint.getLineNumber();
                            final String templateName =
                                breakpoint.getMarker().getAttribute( ILRDebugConstants.FM_TEMPLATE_NAME, "" );
                            final String remoteTemplateName = event.getName();

                            if( bpLineNumber == lineNumber && remoteTemplateName.equals( templateName ) )
                            {
//                                envs.put( environment.getId(), environment );
                                fmThread.setEnvironment( event.getEnvironment() );
                                fmThread.setBreakpoints( new IBreakpoint[] { breakpoint } );

                                String frameName = templateName + " line: " + lineNumber;
                                fmStackFrames = new FMStackFrame[] { new FMStackFrame( fmThread, frameName ) };

                                suspended = true;
                                break;
                            }
                        }
                        catch( CoreException e )
                        {
                        }
                    }
                }
            }

            if( suspended )
            {
                suspended( DebugEvent.BREAKPOINT );
            }
            else
            {
                // lets not pause the remote environment if for some reason the breakpoints don't match.
                event.getEnvironment().resume();
            }
        }
    }

    /**
     * Constructs a new debug target in the given launch for
     * the associated FM debugger
     * @param server
     *
     * @param launch containing launch
     * @param process Portal VM
     */
    public FMDebugTarget( String host, ILaunch launch, IProcess process )
    {
        super( null );

        this.target = this;
        this.host = host;
        this.launch = launch;
        this.process = process;

        this.fmThread = new FMThread( this.target );
        this.eventDispatchJob = new EventDispatchJob();
        this.eventDispatchJob.schedule();

        DebugPlugin.getDefault().getBreakpointManager().addBreakpointListener( this );
    }

    public void addRemoteBreakpoint( Debugger debugger, IBreakpoint localBreakpoint ) throws RemoteException
    {
        String templateName = localBreakpoint.getMarker().getAttribute( FMLineBreakpoint.ATTR_TEMPLATE_NAME, null );
        int line = localBreakpoint.getMarker().getAttribute( IMarker.LINE_NUMBER, -1 );

        if( ! CoreUtil.isNullOrEmpty( templateName ) && line > -1 )
        {
            Breakpoint remoteBreakpoint = new Breakpoint( templateName, line );
            debugger.addBreakpoint( remoteBreakpoint );
        }
    }

    public Debugger getDebuggerClient()
    {
        if( this.debuggerClient == null )
        {
            try
            {
                this.debuggerClient =
                    DebuggerClient.getDebugger(
                        Inet4Address.getByName( this.host ), ILRDebugConstants.FM_DEBUG_PORT,
                        ILRDebugConstants.FM_DEBUG_PASSWORD );
            }
            catch( Exception e )
            {
            }
        }

        return this.debuggerClient;
    }

    public FMDebugTarget getDebugTarget()
    {
        return this.target;
    }

    public ILaunch getLaunch()
    {
        return this.launch;
    }

    public boolean canTerminate()
    {
        return getProcess().canTerminate();
    }

    public boolean isTerminated()
    {
        return this.terminated || getProcess().isTerminated();
    }

    @SuppressWarnings( "rawtypes" )
    public void terminate() throws DebugException
    {
        final IBreakpoint[] localBreakpoints = DebugPlugin.getDefault().getBreakpointManager().getBreakpoints( getModelIdentifier() );

        for( IBreakpoint localBreakpoint : localBreakpoints )
        {
            try
            {
                removeBreakpoint( localBreakpoint );
            }
            catch( RemoteException e )
            {
                LiferayDebugCore.logError( "Unable to remove remote breakpoint", e );
            }
        }

        try
        {
            for( Iterator i = getDebuggerClient().getSuspendedEnvironments().iterator(); i.hasNext(); )
            {
                DebuggedEnvironment e = (DebuggedEnvironment) i.next();
                e.resume();
            }
        }
        catch( RemoteException e )
        {
            LiferayDebugCore.logError( "Could not resume all suspended environments", e );
        }

        terminated();
    }

    public boolean canResume()
    {
        return !isTerminated() && isSuspended();
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
     * @see org.eclipse.debug.core.model.ISuspendResume#isSuspended()
     */
    public boolean isSuspended()
    {
        return this.suspended;
    }

    @SuppressWarnings( "rawtypes" )
    public void resume()
    {
        try
        {
            for( Iterator i = this.debuggerClient.getSuspendedEnvironments().iterator(); i.hasNext(); )
            {
                DebuggedEnvironment debuged = (DebuggedEnvironment) i.next();

                debuged.resume();
            }

            this.fmStackFrames = null;

            resumed( DebugEvent.CLIENT_REQUEST );
        }
        catch( RemoteException e )
        {
        }
    }

    public void resume( FMThread thread ) throws DebugException
    {
        try
        {
            thread.getEnvironment().resume();

            this.fmStackFrames = null;

            resumed( DebugEvent.CLIENT_REQUEST );
        }
        catch( RemoteException e )
        {
            throw new DebugException( LiferayDebugCore.createErrorStatus( e ) );
        }
    }

    /**
     * Notification the target has resumed for the given reason
     *
     * @param detail
     *            reason for the resume
     */
    private void resumed( int detail )
    {
        this.suspended = false;
        this.fmStackFrames = new IStackFrame[0];
        this.fmThread.fireResumeEvent( detail );
        this.fireResumeEvent( detail );
    }

    /**
     * Notification the target has suspended for the given reason
     *
     * @param detail
     *            reason for the suspend
     */
    private void suspended( int detail )
    {
        this.suspended = true;
        this.fmThread.fireSuspendEvent( detail );
    }

    public void suspend() throws DebugException
    {
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.core.IBreakpointListener#breakpointAdded(org.eclipse.debug.core.model.IBreakpoint)
     */
    public void breakpointAdded( IBreakpoint breakpoint )
    {
        if( supportsBreakpoint( breakpoint ) )
        {
            try
            {
                if( breakpoint.isEnabled() )
                {
                    addRemoteBreakpoint( getDebuggerClient(), breakpoint );
                }
            }
            catch( Exception e )
            {
                LiferayDebugCore.logError( "Error adding breakpoint.", e );
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.core.IBreakpointListener#breakpointRemoved(org.eclipse.debug.core.model.IBreakpoint,
     * org.eclipse.core.resources.IMarkerDelta)
     */
    public void breakpointRemoved( IBreakpoint breakpoint, IMarkerDelta delta )
    {
        if( supportsBreakpoint( breakpoint ) )
        {
            try
            {
                removeBreakpoint( breakpoint );
            }
            catch( RemoteException e )
            {
                LiferayDebugCore.logError( "Unable to remove remote breakpoint", e );
            }
        }
    }

    private void removeBreakpoint( IBreakpoint breakpoint ) throws RemoteException
    {
        String templateName = breakpoint.getMarker().getAttribute( ILRDebugConstants.FM_TEMPLATE_NAME, "" );
        final Breakpoint bp = new Breakpoint( templateName, breakpoint.getMarker().getAttribute( IMarker.LINE_NUMBER, -1 ) );

        Debugger client = getDebuggerClient();

        if( client != null )
        {
            client.removeBreakpoint( bp );
        }
        else
        {
            LiferayDebugCore.logError( "Unable to get debug client to remove breakpoint." );
        }
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.core.IBreakpointListener#breakpointChanged(org.eclipse.debug.core.model.IBreakpoint,
     * org.eclipse.core.resources.IMarkerDelta)
     */
    public void breakpointChanged( IBreakpoint breakpoint, IMarkerDelta delta )
    {
        if( supportsBreakpoint( breakpoint ) )
        {
            try
            {
                if( breakpoint.isEnabled() )
                {
                    breakpointAdded( breakpoint );
                }
                else
                {
                    breakpointRemoved( breakpoint, null );
                }
            }
            catch( CoreException e )
            {
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.core.model.IDisconnect#canDisconnect()
     */
    public boolean canDisconnect()
    {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.core.model.IDisconnect#disconnect()
     */
    public void disconnect() throws DebugException
    {
        System.out.println("disconnect()");
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.core.model.IDisconnect#isDisconnected()
     */
    public boolean isDisconnected()
    {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.core.model.IMemoryBlockRetrieval#supportsStorageRetrieval()
     */
    public boolean supportsStorageRetrieval()
    {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.core.model.IMemoryBlockRetrieval#getMemoryBlock(long, long)
     */
    public IMemoryBlock getMemoryBlock( long startAddress, long length ) throws DebugException
    {
        return null;
    }

    public IProcess getProcess()
    {
        return this.process;
    }

    public IThread[] getThreads() throws DebugException
    {
        return this.threads;
    }

    public boolean hasThreads() throws DebugException
    {
        return this.threads != null && this.threads.length > 0;
    }

    public String getName() throws DebugException
    {
        if( this.name == null )
        {
            this.name = "Freemarker Debugger";
        }

        return this.name;
    }

    public boolean supportsBreakpoint( IBreakpoint breakpoint )
    {
        if( breakpoint.getModelIdentifier().equals( ILRDebugConstants.ID_FM_DEBUG_MODEL ) )
        {
            try
            {
                return breakpoint.getMarker().getType().equals( LiferayDebugCore.ID_FM_BREAKPOINT_TYPE );
            }
            catch( CoreException e )
            {
                e.printStackTrace();
            }
        }

        return false;
    }

    protected void step( FMThread thread ) throws DebugException
    {
        //TODO step()
    }

    /**
     * Called when this debug target terminates.
     */
    private void terminated()
    {
        this.terminated = true;
        this.suspended = false;

        DebugPlugin.getDefault().getBreakpointManager().removeBreakpointListener(this);

        fireTerminateEvent();
    }

    protected IStackFrame[] getStackFrames()
    {
        return this.fmStackFrames;
    }

}
