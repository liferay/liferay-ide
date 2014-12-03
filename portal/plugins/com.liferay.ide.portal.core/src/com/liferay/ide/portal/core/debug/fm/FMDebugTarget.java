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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.portal.core.PortalCore;
import com.liferay.ide.portal.core.debug.ILRDebugConstants;
import com.liferay.ide.server.util.ServerUtil;
import com.sun.jdi.Field;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;

import freemarker.debug.Breakpoint;
import freemarker.debug.DebuggedEnvironment;
import freemarker.debug.Debugger;
import freemarker.debug.DebuggerClient;
import freemarker.debug.DebuggerListener;
import freemarker.debug.EnvironmentSuspendedEvent;

import java.net.Inet4Address;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;
import org.eclipse.jdt.internal.debug.core.model.JDIThread;
import org.eclipse.osgi.util.NLS;


/**
 * @author Gregory Amerson
 * @author Cindy Li
 */
@SuppressWarnings( "restriction" )
public class FMDebugTarget extends FMDebugElement implements IDebugTarget, IDebugEventSetListener, IPreferenceChangeListener
{
    private static final FMStackFrame[] EMPTY_STACK_FRAMES = new FMStackFrame[0];

    public static final String FM_TEMPLATE_SERVLET_CONTEXT = "_SERVLET_CONTEXT_"; //$NON-NLS-1$

    private Debugger debuggerClient;
    private EventDispatchJob eventDispatchJob;
    private FMStackFrame[] fmStackFrames = EMPTY_STACK_FRAMES;
    private FMThread fmThread;
    private String host;
    private ILaunch launch;
    private String name;
    private IProcess process;
    private boolean suspended = false;
    private FMDebugTarget target;
    private boolean terminated = false;

    private IThread[] threads = new IThread[0];

    class EventDispatchJob extends Job implements DebuggerListener
    {
        private boolean setup;

        public EventDispatchJob()
        {
            super( "Freemarker Event Dispatch" );
            setSystem( true );
        }

        public void environmentSuspended( final EnvironmentSuspendedEvent event ) throws RemoteException
        {
            final int lineNumber = event.getLine();
            final ILineBreakpoint[] breakpoints = getEnabledLineBreakpoints();

            boolean foundBreakpoint = false;

            for( IBreakpoint breakpoint : breakpoints )
            {
                if( breakpoint instanceof ILineBreakpoint )
                {
                    ILineBreakpoint lineBreakpoint = (ILineBreakpoint) breakpoint;

                    try
                    {
                        final int bpLineNumber = lineBreakpoint.getLineNumber();
                        final String templateName = breakpoint.getMarker().getAttribute( ILRDebugConstants.FM_TEMPLATE_NAME, "" );
                        final String remoteTemplateName = event.getTemplateName().replaceAll( FM_TEMPLATE_SERVLET_CONTEXT, "" );

                        if( bpLineNumber == lineNumber && remoteTemplateName.equals( templateName ) )
                        {
                            final String frameName = templateName + " line: " + lineNumber;

                            fmThread.setEnvironment( event.getEnvironment() );
                            fmThread.setThreadId( event.getThreadId() );
                            fmThread.setBreakpoints( new IBreakpoint[] { breakpoint } );
                            fmStackFrames = new FMStackFrame[] { new FMStackFrame( fmThread, frameName ) };

                            foundBreakpoint = true;
                            break;
                        }
                    }
                    catch( CoreException e )
                    {
                        PortalCore.logError( "Unable to suspend at breakpoint", e );
                    }
                }
            }

            if( ! foundBreakpoint && fmThread.isStepping() )
            {
                final Breakpoint stepBp = fmThread.getStepBreakpoint();

                if( stepBp != null )
                {
                    String frameName = getDisplayableTemplateName( stepBp.getTemplateName() ) + " line: " + stepBp.getLine();

                    fmThread.setEnvironment( event.getEnvironment() );
                    fmThread.setBreakpoints( null );
                    fmThread.setStepping( false );
                    fmStackFrames = new FMStackFrame[] { new FMStackFrame( fmThread, frameName ) };

                    foundBreakpoint = true;
                }
            }

            if( foundBreakpoint )
            {
                suspended( DebugEvent.BREAKPOINT );
            }
            else
            {
                // lets not pause the remote environment if for some reason the breakpoints don't match.
                new Job( "resuming remote environment" )
                {
                    @SuppressWarnings( "rawtypes" )
                    @Override
                    protected IStatus run( IProgressMonitor monitor )
                    {
                        IStatus retval = Status.OK_STATUS;

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
                            retval = PortalCore.createErrorStatus( "Could not resume after missing breakpoint", e );
                        }

                        return retval;
                    }
                }.schedule();

                PortalCore.logError( "Could not find local breakpoint, resuming all remote environments" );
            }
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
                        wait();
                    }
                    catch( InterruptedException e )
                    {
                    }
                }
            }

            return Status.OK_STATUS;
        }

        private boolean setupDebugger( Debugger debugger )
        {
            try
            {
                debugger.addDebuggerListener( eventDispatchJob );

                FMDebugTarget.this.threads = new IThread[] { FMDebugTarget.this.fmThread };

                final IBreakpoint[] localBreakpoints = getEnabledLineBreakpoints();

                addRemoteBreakpoints( debugger, localBreakpoints );
            }
            catch( RemoteException e )
            {
                return false;
            }

            return true;
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
        DebugPlugin.getDefault().addDebugEventListener( this );
        PortalCore.getPrefs().addPreferenceChangeListener( this );
    }

    public void addRemoteBreakpoints( final Debugger debugger, final IBreakpoint bps[] ) throws RemoteException
    {
        final List<Breakpoint> remoteBps = new ArrayList<Breakpoint>();

        for( IBreakpoint bp : bps )
        {
            final int line = bp.getMarker().getAttribute( IMarker.LINE_NUMBER, -1 );
            final String templateName = bp.getMarker().getAttribute( ILRDebugConstants.FM_TEMPLATE_NAME, null );
            final String remoteTemplateName = createRemoteTemplateName( templateName );

            if( ! CoreUtil.isNullOrEmpty( remoteTemplateName ) && line > -1 )
            {
                remoteBps.add( new Breakpoint( remoteTemplateName, line ) );
            }
        }

        final Job job = new Job( "add remote breakpoints" )
        {
            @Override
            protected IStatus run( IProgressMonitor monitor )
            {
                IStatus retval = null;

                for( Breakpoint bp : remoteBps )
                {
                    try
                    {
                        debugger.addBreakpoint( bp );
                    }
                    catch( RemoteException e )
                    {
                        retval =
                            PortalCore.createErrorStatus(
                                NLS.bind(
                                    "Could not add remote breakpoint: {0}:{1}",
                                    new Object[] { bp.getTemplateName(), bp.getLine() } ), e );

                        if( retval != Status.OK_STATUS )
                        {
                            PortalCore.logError( retval.getMessage() );
                        }
                    }
                }

                return Status.OK_STATUS;
            }
        };

        job.schedule();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.core.IBreakpointListener#breakpointAdded(org.eclipse.debug.core.model.IBreakpoint)
     */
    public void breakpointAdded( IBreakpoint breakpoint )
    {
        if( supportsBreakpoint( breakpoint ) && !this.launch.isTerminated() )
        {
            try
            {
                Debugger debugger = getDebuggerClient();

                if( debugger != null && breakpoint.isEnabled() )
                {
                    addRemoteBreakpoints( debugger, new IBreakpoint[] { breakpoint } );
                }
            }
            catch( Exception e )
            {
                PortalCore.logError( "Error adding breakpoint.", e );
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.core.IBreakpointListener#breakpointChanged(org.eclipse.debug.core.model.IBreakpoint,
     * org.eclipse.core.resources.IMarkerDelta)
     */
    public void breakpointChanged( IBreakpoint breakpoint, IMarkerDelta delta )
    {
        if( supportsBreakpoint( breakpoint ) && ! this.launch.isTerminated() )
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
     * @see org.eclipse.debug.core.IBreakpointListener#breakpointRemoved(org.eclipse.debug.core.model.IBreakpoint,
     * org.eclipse.core.resources.IMarkerDelta)
     */
    public void breakpointRemoved( IBreakpoint breakpoint, IMarkerDelta delta )
    {
        if( supportsBreakpoint( breakpoint ) && ! this.launch.isTerminated() )
        {
            removeRemoteBreakpoints( new IBreakpoint[] { breakpoint } );
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

    public boolean canTerminate()
    {
        return false;
    }

    private void cleanup()
    {
        this.terminated = true;
        this.suspended = false;

        DebugPlugin.getDefault().getBreakpointManager().removeBreakpointListener(this);
        DebugPlugin.getDefault().removeDebugEventListener( this );
        PortalCore.getPrefs().removePreferenceChangeListener( this );
    }

    private String createRemoteTemplateName( String templateName )
    {
        String retval = null;

        if( ! CoreUtil.isNullOrEmpty( templateName ) )
        {
            final IPath templatePath = new Path( templateName );
            final String firstSegment = templatePath.segment( 0 );
            final IProject project = ServerUtil.findProjectByContextName( firstSegment );

            if( project != null )
            {
                /*
                 * need to add special uri to the end of first segment of template path in order to make it work with
                 * remote debugger
                 */
                final Object[] bindings = new Object[] { firstSegment, FM_TEMPLATE_SERVLET_CONTEXT,
                    templatePath.removeFirstSegments( 1 ) };

                retval = NLS.bind( "{0}{1}/{2}", bindings );
            }
            else
            {
                retval = templatePath.toPortableString();
            }
        }

        return retval;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.core.model.IDisconnect#disconnect()
     */
    public void disconnect() throws DebugException
    {
    }

    public Debugger getDebuggerClient()
    {
        if( this.debuggerClient == null )
        {
            try
            {
                this.debuggerClient =
                    DebuggerClient.getDebugger( Inet4Address.getByName( this.host ), getDebugPort(), getDebugPassword() );
            }
            catch( Exception e )
            {
            }
        }

        return this.debuggerClient;
    }

    private String getDebugPassword()
    {
        String debugPassword = launch.getAttribute( PortalCore.PREF_FM_DEBUG_PASSWORD );

        if( debugPassword != null )
        {
            return debugPassword;
        }

        return PortalCore.getPreference( PortalCore.PREF_FM_DEBUG_PASSWORD );
    }

    private int getDebugPort()
    {
        String debugPort = launch.getAttribute( PortalCore.PREF_FM_DEBUG_PORT );

        if( debugPort != null )
        {
            return Integer.parseInt( debugPort );
        }

        return Integer.parseInt( PortalCore.getPreference( PortalCore.PREF_FM_DEBUG_PORT ) );
    }

    public FMDebugTarget getDebugTarget()
    {
        return this.target;
    }

    private String getDisplayableTemplateName( String templateName )
    {
        return templateName.replaceAll( FM_TEMPLATE_SERVLET_CONTEXT, "" );
    }

    private ILineBreakpoint[] getEnabledLineBreakpoints()
    {
        List<ILineBreakpoint> breakpoints = new ArrayList<ILineBreakpoint>();

        final IBreakpointManager breakpointManager = DebugPlugin.getDefault().getBreakpointManager();

        if( breakpointManager.isEnabled() )
        {
            IBreakpoint[] fmBreakpoints = breakpointManager.getBreakpoints( getModelIdentifier() );


            for( IBreakpoint fmBreakpoint : fmBreakpoints )
            {
                try
                {
                    if( fmBreakpoint instanceof ILineBreakpoint && supportsBreakpoint( fmBreakpoint ) &&
                        fmBreakpoint.isEnabled() )
                    {
                        breakpoints.add( (ILineBreakpoint) fmBreakpoint );
                    }
                }
                catch( CoreException e )
                {
                }
            }

        }

        return breakpoints.toArray( new ILineBreakpoint[0] );
    }

    public ILaunch getLaunch()
    {
        return this.launch;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.core.model.IMemoryBlockRetrieval#getMemoryBlock(long, long)
     */
    public IMemoryBlock getMemoryBlock( long startAddress, long length ) throws DebugException
    {
        return null;
    }

    public String getName() throws DebugException
    {
        if( this.name == null )
        {
            this.name = "Freemarker Debugger at " + this.host + ":" + getDebugPort();
        }

        return this.name;
    }

    public IProcess getProcess()
    {
        return this.process;
    }

    FMStackFrame[] getStackFrames()
    {
        return this.fmStackFrames;
    }

    public IThread[] getThreads() throws DebugException
    {
        return this.threads;
    }

    public void handleDebugEvents( DebugEvent[] events )
    {
        for( DebugEvent event : events )
        {
            if( event.getKind() == DebugEvent.TERMINATE )
            {
                if( this.process.equals( event.getSource() ) )
                {
                    if( this.process.isTerminated() )
                    {
                        cleanup();
                    }
                }
            }
        }
    }

    public boolean hasThreads() throws DebugException
    {
        return this.threads != null && this.threads.length > 0;
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
     * @see org.eclipse.debug.core.model.ISuspendResume#isSuspended()
     */
    public boolean isSuspended()
    {
        return this.suspended;
    }

    public boolean isTerminated()
    {
        return this.terminated || getProcess().isTerminated();
    }

    public void preferenceChange( PreferenceChangeEvent event )
    {
        if( PortalCore.PREF_ADVANCED_VARIABLES_VIEW.equals( event.getKey() ) )
        {
            for( FMStackFrame stackFrame : getStackFrames() )
            {
                stackFrame.clearVariables();
            }
        }
    }

    private void removeRemoteBreakpoints( final IBreakpoint[] breakpoints )
    {
        final List<Breakpoint> remoteBreakpoints = new ArrayList<Breakpoint>();

        for( IBreakpoint bp : breakpoints )
        {
            final String templateName = bp.getMarker().getAttribute( ILRDebugConstants.FM_TEMPLATE_NAME, "" );

            final String remoteTemplateName =createRemoteTemplateName( templateName );

            final Breakpoint remoteBp =
                new Breakpoint( remoteTemplateName, bp.getMarker().getAttribute( IMarker.LINE_NUMBER, -1 ) );

            remoteBreakpoints.add( remoteBp );
        }

        final Job job = new Job( "remove remote breakpoints" )
        {
            @Override
            protected IStatus run( IProgressMonitor monitor )
            {
                IStatus retval = null;

                for( Breakpoint bp : remoteBreakpoints )
                {
                    try
                    {
                        getDebuggerClient().removeBreakpoint( bp );
                    }
                    catch( Exception e )
                    {
                        retval =
                            PortalCore.createErrorStatus(
                                NLS.bind(
                                    "Unable to get debug client to remove breakpoint: {0}:{1}",
                                    new Object[] { bp.getTemplateName(), bp.getLine() } ), e );

                        if( retval != Status.OK_STATUS )
                        {
                            PortalCore.logError( retval.getMessage() );
                        }
                    }
                }

                return Status.OK_STATUS;
            }
        };

        job.schedule();
    }

    @SuppressWarnings( "rawtypes" )
    public void resume()
    {
        final Job job = new Job("resume")
        {
            @Override
            protected IStatus run( IProgressMonitor monitor )
            {
                try
                {
                    // need to check to see if current thread is stepping and then remove the step breakpoint
                    Debugger debugger = getDebuggerClient();

                    if( debugger != null )
                    {
                        if( fmThread.isStepping() )
                        {
                            Breakpoint stepBp = fmThread.getStepBreakpoint();
                            debugger.removeBreakpoint( stepBp );
                        }

                        for( Iterator i = debugger.getSuspendedEnvironments().iterator(); i.hasNext(); )
                        {
                            DebuggedEnvironment env = (DebuggedEnvironment) i.next();

                            try
                            {
                                env.resume();
                            }
                            catch( Exception e )
                            {
                                PortalCore.logError( "Could not resume suspended environment", e );
                            }
                        }

                        fmStackFrames = EMPTY_STACK_FRAMES;

                        resumed( DebugEvent.CLIENT_REQUEST );
                    }
                }
                catch( RemoteException e )
                {
                    PortalCore.logError( "Could not fully resume suspended environments", e );
                }

                return Status.OK_STATUS;
            }
        };

        job.schedule();
    }

    public void resume( FMThread thread ) throws DebugException
    {
        try
        {
            Breakpoint stepBp = thread.getStepBreakpoint();

            if( stepBp != null )
            {
                getDebuggerClient().removeBreakpoint( stepBp );
                thread.setStepping( false );
                thread.setStepBreakpoint( null );
            }

            thread.getEnvironment().resume();

            this.fmStackFrames = EMPTY_STACK_FRAMES;

            resumed( DebugEvent.CLIENT_REQUEST );
        }
        catch( RemoteException e )
        {
            throw new DebugException( PortalCore.createErrorStatus( e ) );
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
        this.fmStackFrames = EMPTY_STACK_FRAMES;
        this.fmThread.fireResumeEvent( detail );
        this.fireResumeEvent( detail );
    }

    /*
     * Since current fm debugger doens't have native stepping we must emulate stepping with following steps.
     *
     * 1. Starting at the current stopped line, continue going down the template file to find a
     *    suitable line to stop, ie, a addBreakpoint() that doesn't throw an exception.
     * 2. For the next line if there is already a breakpoint, simply call resume(),
     * 3. If there is no breakpoint already installed, add another one to the next line if that line has a valid
     *    breakpoint location, then resume().
     * 4. Once the next breakpoint is hit, we need to remove the previously added step breakpoint
     */
    @SuppressWarnings( { "rawtypes" } )
    void step( FMThread thread ) throws DebugException
    {
        int currentLineNumber = -1;
        String templateName = null;
        Breakpoint existingStepBp = null;

        final IBreakpoint[] breakpoints = thread.getBreakpoints();


        if( breakpoints.length > 0 )
        {
            try
            {
                ILineBreakpoint bp = (ILineBreakpoint) breakpoints[0];
                currentLineNumber = bp.getLineNumber();
                templateName = bp.getMarker().getAttribute( ILRDebugConstants.FM_TEMPLATE_NAME, "" );
            }
            catch( CoreException e )
            {
                PortalCore.logError( "Could not get breakpoint information.", e );
            }
        }
        else
        {
            existingStepBp = thread.getStepBreakpoint();

            currentLineNumber = existingStepBp.getLine();
            templateName = existingStepBp.getTemplateName();
        }

        if( currentLineNumber > -1 && templateName != null )
        {
            final String remoteTemplateName = createRemoteTemplateName( templateName );
            int stepLine = currentLineNumber + 1;
            Breakpoint existingBp = null;

            Debugger debugCli = getDebuggerClient();

            try
            {
                List remoteBps = debugCli.getBreakpoints( remoteTemplateName );

                for( Iterator i = remoteBps.iterator(); i.hasNext(); )
                {
                    Breakpoint remoteBp = (Breakpoint) i.next();

                    if( remoteBp.getLine() == stepLine )
                    {
                        existingBp = remoteBp;
                        break;
                    }
                }

                if( existingBp == null )
                {
                    boolean addedRemote = false;

                    while( ! addedRemote )
                    {
                        Breakpoint newBp = new Breakpoint( remoteTemplateName, stepLine++ );

                        try
                        {
                            debugCli.addBreakpoint( newBp );
                        }
                        catch( RemoteException e )
                        {
                            // we except to get some remote exceptions if the next line is invalid breakpoint location
                        }

                        List updatedRemoteBps = debugCli.getBreakpoints( remoteTemplateName );

                        if( updatedRemoteBps.size() == remoteBps.size() + 1 ) // our new remote bp was sucessfully added
                        {
                            addedRemote = true;
                            thread.setStepBreakpoint( newBp );
                            thread.setStepping( true );

                            fireResumeEvent( DebugEvent.RESUME  );

                            if( existingStepBp != null)
                            {
                                debugCli.removeBreakpoint( existingStepBp );
                            }

                            thread.getEnvironment().resume();
                        }
                    }
                }
                else
                {
                    // the next line already has a remote breakpoint installed so lets clear our "step" breakpoint
                    thread.setStepBreakpoint( null );
                    thread.setStepping( false );

                    fireResumeEvent( DebugEvent.RESUME  );

                    if( existingStepBp != null)
                    {
                        debugCli.removeBreakpoint( existingStepBp );
                    }

                    thread.getEnvironment().resume();
                }
            }
            catch( RemoteException e )
            {
                PortalCore.logError( "Unable to check remote breakpoints", e );
            }
        }
        else
        {
            PortalCore.logError( "Unable to step because of missing lineNumber or templateName information." );
        }
    }

    public boolean supportsBreakpoint( IBreakpoint breakpoint )
    {
        if( breakpoint.getModelIdentifier().equals( ILRDebugConstants.ID_FM_DEBUG_MODEL ) )
        {
            try
            {
                return breakpoint.getMarker().getType().equals( PortalCore.ID_FM_BREAKPOINT_TYPE );
            }
            catch( CoreException e )
            {
            }
        }

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

    public void suspend() throws DebugException
    {
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

    boolean suspendRelatedJavaThread( final long remoteThreadId ) throws DebugException
    {
        boolean retval = false;

        for( IDebugTarget target : this.launch.getDebugTargets() )
        {
            if( target instanceof IJavaDebugTarget )
            {
                IJavaDebugTarget javaTarget = (IJavaDebugTarget) target;

                IThread[] threads = javaTarget.getThreads();

                for( final IThread thread : threads )
                {
                    if( thread instanceof JDIThread )
                    {
                        JDIThread jdiThread = (JDIThread) thread;
                        ThreadReference underlyingThread = jdiThread.getUnderlyingThread();
                        Field tidField = underlyingThread.referenceType().fieldByName( "tid" );
                        Value tidValue = underlyingThread.getValue( tidField );

                        long threadId = Long.parseLong( tidValue.toString() );

                        if( threadId == remoteThreadId )
                        {
                            thread.suspend();
                            break;
                        }
                    }
                }
            }
        }

        return retval;
    }

    public void terminate() throws DebugException
    {
        final IBreakpoint[] localBreakpoints = getEnabledLineBreakpoints();

        removeRemoteBreakpoints( localBreakpoints );

        resume();

        terminated();
    }

    /**
     * Called when this debug target terminates.
     */
    private void terminated()
    {
        cleanup();

        fireTerminateEvent();
    }

}
