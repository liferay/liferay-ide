package com.liferay.ide.debug.core.fm;

import freemarker.debug.DebuggedEnvironment;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;


public class FMStackFrame extends FMDebugElement implements IStackFrame
{
    private FMThread thread;
    private IVariable[] variables;
    private int linePC;
    private String name;

    public FMStackFrame( FMThread thread, String name )
    {
        super( thread.getDebugTarget() );
        this.thread = thread;
        this.name = name;
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

    public boolean isStepping()
    {
        return getThread().isStepping();
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

    public boolean canResume()
    {
        return getThread().canResume();
    }

    public boolean canSuspend()
    {
        return getThread().canSuspend();
    }

    public boolean isSuspended()
    {
        return getThread().isSuspended();
    }

    public void resume() throws DebugException
    {
        getThread().resume();
    }

    public void suspend() throws DebugException
    {
        getThread().suspend();
    }

    public boolean canTerminate()
    {
        return getThread().canTerminate();
    }

    public boolean isTerminated()
    {
        return getThread().isTerminated();
    }

    public void terminate() throws DebugException
    {
        getThread().terminate();
    }

    public IThread getThread()
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

    public boolean hasVariables() throws DebugException
    {
        return this.variables != null && this.variables.length > 0;
    }

    public int getLineNumber() throws DebugException
    {
        return linePC;
    }

    public int getCharStart() throws DebugException
    {
        return -1;
    }

    public int getCharEnd() throws DebugException
    {
        return -1;
    }

    public String getName() throws DebugException
    {
        return this.name;
    }

    public IRegisterGroup[] getRegisterGroups() throws DebugException
    {
        return null;
    }

    public boolean hasRegisterGroups() throws DebugException
    {
        return false;
    }

}
