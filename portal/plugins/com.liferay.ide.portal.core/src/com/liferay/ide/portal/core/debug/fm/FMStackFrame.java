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

import freemarker.debug.DebuggedEnvironment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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
    final static Pattern freemarkerClasses = Pattern.compile( "^freemarker\\..*\\$.*$" );
    final static Pattern liferayClasses = Pattern.compile( "^com\\.liferay\\.portal\\.freemarker\\.LiferayObjectWrapper$" );
    // matches values like: com.liferay.portal.service.permission.UserGroupPermissionImpl@1cd9f974
    final static Pattern liferaySpringClasses = Pattern.compile( "^com\\.liferay\\..*@[a-z0-9]+$" );
    final static Pattern classDefs = Pattern.compile( "^public void com\\.liferay.*$" );

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

    void clearVariables()
    {
        this.variables = null;
    }

    private boolean filter( IVariable var )
    {
        try
        {
            final String name = var.getName();

            if( filterVariableName( name ) )
            {
                final String valueString = var.getValue().getValueString();

                if( filterVariableValueString( valueString) )
                {
                    return true;
                }
            }
        }
        catch( DebugException e )
        {
        }

        return false;
    }

    private boolean filterVariableName( String variableName )
    {
        boolean retval = true;

        // System.out.println(variableName);

        return retval;
    }

    private IVariable[] filterVariables( IVariable[] variables )
    {
        List<IVariable> filtered = new ArrayList<IVariable>();

        for( IVariable var : variables )
        {
            if( filter( var ) )
            {
                filtered.add( var );
            }
        }

        IVariable[] retval = filtered.toArray( new IVariable[0] );

        return retval;
    }

    private boolean filterVariableValueString( String valueString )
    {
        if( liferaySpringClasses.matcher( valueString ).matches() || freemarkerClasses.matcher( valueString ).matches() ||
            liferayClasses.matcher( valueString ).matches() || classDefs.matcher( valueString ).matches() )
        {
            return false;
        }

        return true;
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
                        PortalCore.logError( "Could not get breakpoint charStart", e );
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

            boolean advancedView =
                PortalCore.getPrefs().getBoolean( PortalCore.PREF_ADVANCED_VARIABLES_VIEW, false );

            final DebuggedEnvironment env = this.thread.getEnvironment();

            FMVariable[] topLevelVars = null;

            try
            {
                topLevelVars = new FMVariable[]
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
                PortalCore.logError( "Unable to create freemarker variables", e );
            }

            if( topLevelVars != null )
            {
                if( advancedView )
                {
                    this.variables = topLevelVars;
                }
                else
                {
                    // collapse all the variables into one list and remove duplicates
                    Map<String, IVariable> vars = new HashMap<String, IVariable>();

                    for( FMVariable topLevelVar : topLevelVars )
                    {
                        for( IVariable nestedVar : topLevelVar.getValue().getVariables() )
                        {
                            IVariable existingVar = vars.get( nestedVar.getName() );

                            if( existingVar == null )
                            {
                                vars.put( nestedVar.getName(), nestedVar );
                            }
                        }
                    }

                    this.variables = filterVariables( vars.values().toArray( new IVariable[0] ) );

                    sortVariables( this.variables );
                }
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
