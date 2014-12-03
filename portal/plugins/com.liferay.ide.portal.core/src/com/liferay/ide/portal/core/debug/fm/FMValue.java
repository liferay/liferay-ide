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

import freemarker.debug.DebugModel;
import freemarker.template.TemplateModelException;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;


/**
 * @author Gregory Amerson
 */
public class FMValue extends FMDebugElement implements IValue
{
    private static final int VALID_VARIBLE_TYPES = DebugModel.TYPE_BOOLEAN | DebugModel.TYPE_COLLECTION |
        DebugModel.TYPE_CONFIGURATION | DebugModel.TYPE_DATE | DebugModel.TYPE_HASH | DebugModel.TYPE_HASH_EX |
        DebugModel.TYPE_NUMBER | DebugModel.TYPE_SCALAR | DebugModel.TYPE_SEQUENCE | DebugModel.TYPE_TEMPLATE;

    protected DebugModel debugModel;
    protected FMStackFrame stackFrame;
    private IVariable[] variables;

    public FMValue( FMStackFrame stackFrame, DebugModel debugModel )
    {
        super( stackFrame.getDebugTarget() );

        this.stackFrame = stackFrame;
        this.debugModel = debugModel;
    }

    public String getValueString() throws DebugException
    {
        String retval = null;

        try
        {
            int types = this.debugModel.getModelTypes();

            if( ( DebugModel.TYPE_BOOLEAN & types ) > 0 )
            {
                retval = Boolean.toString( this.debugModel.getAsBoolean() );
            }

            if( ( DebugModel.TYPE_COLLECTION & types ) > 0 )
            {
                retval = "Collection";
            }

            if( ( DebugModel.TYPE_CONFIGURATION & types ) > 0 )
            {
                retval = "Configuration";
            }

            if( ( DebugModel.TYPE_DATE & types ) > 0 )
            {
                retval = this.debugModel.getAsDate().toString();
            }

            if( ( DebugModel.TYPE_ENVIRONMENT & types ) > 0 )
            {
                retval = "Environment";
            }

            if( ( DebugModel.TYPE_HASH & types ) > 0 )
            {
                retval = "Hash";
            }

            if( ( DebugModel.TYPE_HASH_EX & types ) > 0 )
            {
                retval = "HashEx";
            }

            if( ( DebugModel.TYPE_NUMBER & types ) > 0 )
            {
                retval = this.debugModel.getAsNumber().toString();
            }

            if( ( DebugModel.TYPE_SCALAR & types ) > 0 )
            {
                retval = this.debugModel.getAsString();
            }

            if( ( DebugModel.TYPE_SEQUENCE & types ) > 0 )
            {
                retval = "Sequence";
            }

            if( ( DebugModel.TYPE_TEMPLATE & types ) > 0 )
            {
                retval = "Template";
            }

            if( ( DebugModel.TYPE_TRANSFORM & types ) > 0 )
            {
                retval = "Transform";
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        if( retval == null )
        {
            retval = "";
        }

        return retval;
    }

    private String getCollectionDetailString( DebugModel model )
    {
        StringBuilder sb = new StringBuilder();
        sb.append( '[' );

        try
        {
            for( int i = 0; i < model.size(); i++ )
            {
                final DebugModel val = model.get( i );
                final String value = getModelDetailString( val );

                if( value != null )
                {
                    sb.append( value );
                    sb.append(',');
                }
            }
        }
        catch( Exception e )
        {
            sb.append( e.getMessage() );
        }

        String value = sb.toString();

        return value.endsWith( "," ) ? value.replaceFirst( ",$", "]" ) : value;
    }

    private String getSequenceDetailString( DebugModel model )
    {
        StringBuilder sb = new StringBuilder();
        sb.append( '[' );

        try
        {
            for( int i = 0; i < model.size(); i++ )
            {
                final DebugModel val = model.get( i );
                final String value = getModelDetailString( val );

                if( value != null )
                {
                    sb.append( value );
                    sb.append(',');
                }
            }
        }
        catch( Exception e )
        {
            sb.append( e.getMessage() );
        }

        String value = sb.toString();

        return value.endsWith( "," ) ? value.replaceFirst( ",$", "]" ) : value;
    }

    private String getModelDetailString( DebugModel model ) throws RemoteException, TemplateModelException
    {
        String value = null;

        final int modelTypes = model.getModelTypes();

        if( isStringType( modelTypes ) && !isHashType( modelTypes ) )
        {
            value = model.getAsString();
        }
        else if( isNumberType( modelTypes ) )
        {
            value = model.getAsNumber().toString();
        }
        else if( isDateType( modelTypes ) )
        {
            value = model.getAsDate().toString();
        }
        else if( isBooleanType( modelTypes ) )
        {
            value = Boolean.toString( model.getAsBoolean() );
        }
        else if( isHashType( modelTypes ) )
        {
//            value = getHashDetailString( model );
            value = "Hash";
        }
        else if( isCollectionType( modelTypes ) )
        {
//            value = getHashDetailString( model );
            value = "Collection";
        }
        else if( isSequenceType( modelTypes ) )
        {
//            value = getSequenceDetailString( model );
            value = "Sequence";
        }
        else if( isMethodType( modelTypes) || isTransformType( modelTypes ) || modelTypes == 0 )
        {
            value = null;
        }
        else
        {
            System.out.println("unsupported detail model type: " + modelTypes );
        }

        return value;
    }

    public String getDetailString()
    {
        String retval = null;

        try
        {
            int types = this.debugModel.getModelTypes();

            if( ( DebugModel.TYPE_BOOLEAN & types ) > 0 )
            {
                retval = Boolean.toString( this.debugModel.getAsBoolean() );
            }

            if( ( DebugModel.TYPE_COLLECTION & types ) > 0 )
            {
                retval = getCollectionDetailString( this.debugModel );
            }

            if( ( DebugModel.TYPE_CONFIGURATION & types ) > 0 )
            {
                retval = "Configuration";
            }

            if( ( DebugModel.TYPE_DATE & types ) > 0 )
            {
                retval = this.debugModel.getAsDate().toString();
            }

            if( ( DebugModel.TYPE_ENVIRONMENT & types ) > 0 )
            {
                retval = "Environment";
            }

            if( ( DebugModel.TYPE_HASH & types ) > 0 )
            {
                retval = "Hash";
            }

            if( ( DebugModel.TYPE_HASH_EX & types ) > 0 )
            {
                retval = getHashDetailString( this.debugModel );
            }

            if( ( DebugModel.TYPE_NUMBER & types ) > 0 )
            {
                retval = this.debugModel.getAsNumber().toString();
            }

            if( ( DebugModel.TYPE_SCALAR & types ) > 0 )
            {
                retval = this.debugModel.getAsString();
            }

            if( ( DebugModel.TYPE_SEQUENCE & types ) > 0 )
            {
                retval = getSequenceDetailString( this.debugModel );
            }

            if( ( DebugModel.TYPE_TEMPLATE & types ) > 0 )
            {
                retval = "Template";
            }

            if( ( DebugModel.TYPE_TRANSFORM & types ) > 0 )
            {
                retval = "Transform";
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        if( retval == null )
        {
            retval = "";
        }

        return retval;
    }

    private String getHashDetailString( DebugModel model )
    {
        StringBuilder sb = new StringBuilder();
        sb.append( '{' );

        try
        {
            for( String key : model.keys() )
            {
                final DebugModel val = model.get( key );
                final String value = getModelDetailString( val );

                if( value != null )
                {
                    sb.append( key );
                    sb.append('=');
                    sb.append( value );
                    sb.append(',');
                }
            }
        }
        catch( Exception e )
        {
            sb.append( e.getMessage() );
        }

        String value = sb.toString();

        return value.endsWith( "," ) ? value.replaceFirst( ",$", "}" ) : value;
    }

    public boolean isAllocated() throws DebugException
    {
        return true;
    }

    public IVariable[] getVariables() throws DebugException
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

        if( this.variables == null )
        {
            List<IVariable> vars = new ArrayList<IVariable>();

            try
            {
                int types = this.debugModel.getModelTypes();

                if( isHashType( types) )
                {
                    try
                    {
                        String[] keys = this.debugModel.keys();

                        DebugModel[] vals = this.debugModel.get( keys );

                        for( int i = 0; i < keys.length; i++ )
                        {
                            DebugModel hashValue = vals[i];

                            if( isValidVariable( hashValue ) )
                            {
                                vars.add( new FMVariable( stackFrame, keys[i] , hashValue ) );
                            }
                        }
                    }
                    catch( ClassCastException cce )
                    {
                        // ignore IDE-1082
                    }
                }
                else if( isCollectionType( types ) )
                {
//                    String[] keys = this.debugModel.keys();

//                    if( isValidVariable( hashValue ) )
//                    {
//                        vars.add( new FMVariable( stackFrame, key , debugModel ) );
//                    }
                }
                else if( isSequenceType( types ) && isValidSequence( this.debugModel ) )
                {
                    int length = this.debugModel.size();

                    DebugModel[] vals = this.debugModel.get( 0, length );

                    for( int i = 0; i < length; i++ )
                    {
                        if( isValidVariable( vals[i] ) )
                        {
                            vars.add( new FMVariable( stackFrame, Integer.toString( i ), vals[i] ) );
                        }
                    }
                }
                else if( isStringType( types ) || isNumberType( types ) || isBooleanType( types ) || isDateType( types )  )
                {
                    // no variables
                }
                else
                {
                    System.out.println( "Unknown value: " + getReferenceTypeName( this.debugModel ) );
                }
            }
            catch( Exception e )
            {
                e.printStackTrace();
            }

            this.variables = vars.toArray( new IVariable[vars.size()] );

            sortVariables( this.variables );
        }

        return this.variables;
    }

    private boolean isValidSequence( DebugModel model )
    {
        try
        {
            return model != null && model.size() > 0;
        }
        catch( Exception e )
        {
            return false;
        }
    }

    private boolean isHashType( int types )
    {
        return ( DebugModel.TYPE_HASH & types ) > 0 || ( DebugModel.TYPE_HASH_EX & types ) > 0;
    }

    private boolean isMethodType( int types )
    {
        return ( DebugModel.TYPE_METHOD & types ) > 0 || ( DebugModel.TYPE_METHOD_EX & types ) > 0;
    }

    private boolean isTransformType( int types )
    {
        return ( DebugModel.TYPE_TRANSFORM & types ) > 0;
    }

    private boolean isStringType( int types )
    {
        return ( DebugModel.TYPE_SCALAR & types ) > 0;
    }

    private boolean isNumberType( int types )
    {
        return ( DebugModel.TYPE_NUMBER & types ) > 0;
    }

    private boolean isDateType( int types )
    {
        return ( DebugModel.TYPE_DATE & types ) > 0;
    }

    private boolean isBooleanType( int types )
    {
        return ( DebugModel.TYPE_BOOLEAN & types ) > 0;
    }

    private boolean isCollectionType( int types )
    {
        return ( DebugModel.TYPE_COLLECTION & types ) > 0;
    }

    private boolean isSequenceType( int types )
    {
        return ( DebugModel.TYPE_SEQUENCE & types ) > 0;
    }

    private boolean isValidVariable( DebugModel model )
    {
        boolean retval = false;

        if( model != null )
        {
            try
            {
                int types = model.getModelTypes();

                retval = ( VALID_VARIBLE_TYPES & types ) > 0;

                if( retval && isSequenceType( types ) && ! isValidSequence( model ) )
                {
                    retval = false;
                }
            }
            catch( RemoteException e )
            {
                e.printStackTrace();
            }
        }

        return retval;
    }

    public boolean hasVariables() throws DebugException
    {
        try{
            return getVariables().length > 0;
        }
        catch (NullPointerException e)
        {
            return false;
        }

    }

    public String getReferenceTypeName() throws DebugException
    {
        return getReferenceTypeName( this.debugModel );
    }

}
