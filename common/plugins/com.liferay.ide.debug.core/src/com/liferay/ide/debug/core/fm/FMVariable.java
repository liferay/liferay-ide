package com.liferay.ide.debug.core.fm;

import freemarker.debug.DebugModel;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;


public class FMVariable extends FMDebugElement implements IVariable
{

    protected FMStackFrame stackFrame;
    private String name;
    protected DebugModel debugModel;
    private IValue value;

    public FMVariable( FMStackFrame stackFrame, String name, DebugModel debugModel )
    {
        super( stackFrame.getDebugTarget() );

        this.stackFrame = stackFrame;
        this.name = name;
        this.debugModel = debugModel;
    }

    public DebugModel getDebugModel()
    {
        return this.debugModel;
    }

    public void setValue( String expression ) throws DebugException
    {
    }

    public void setValue( IValue value ) throws DebugException
    {
    }

    public boolean supportsValueModification()
    {
        return false;
    }

    public boolean verifyValue( String expression ) throws DebugException
    {
        return false;
    }

    public boolean verifyValue( IValue value ) throws DebugException
    {
        return false;
    }

    public IValue getValue() throws DebugException
    {
        if( this.value == null )
        {
            this.value = new FMValue( this.stackFrame, this.debugModel );
        }

        return this.value;
    }

    public String getName() throws DebugException
    {
        return this.name;
    }

    public String getReferenceTypeName() throws DebugException
    {
        return getReferenceTypeName( debugModel );
    }

    public boolean hasValueChanged() throws DebugException
    {
        return false;
    }

}
