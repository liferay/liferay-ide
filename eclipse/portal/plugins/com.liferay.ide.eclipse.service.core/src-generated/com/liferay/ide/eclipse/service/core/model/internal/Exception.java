package com.liferay.ide.eclipse.service.core.model.internal;

import com.liferay.ide.eclipse.service.core.model.IException;
import org.eclipse.sapphire.modeling.IModelParticle;
import org.eclipse.sapphire.modeling.ModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.Resource;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;

public final class Exception
    
    extends ModelElement
    implements IException
    
{
    private Value<String> exception;
    
    public Exception( final IModelParticle parent, final ModelProperty parentProperty, final Resource resource )
    {
        super( TYPE, parent, parentProperty, resource );
    }
    
    public Exception( final Resource resource )
    {
        super( TYPE, null, null, resource );
    }
    
    public Value<String> getException()
    {
        synchronized( root() )
        {
            if( this.exception == null )
            {
                refresh( PROP_EXCEPTION, true );
            }
            
            return this.exception;
        }
    }
    
    public void setException( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( "" ) )
            {
                value = null;
            }
            
            value = PROP_EXCEPTION.decodeKeywords( value );
            
            refresh( PROP_EXCEPTION, true );
            
            if( ! equal( this.exception.getText( false ), value ) )
            {
                resource().binding( PROP_EXCEPTION ).write( value );
                refresh( PROP_EXCEPTION, false );
            }
        }
    }
    
    protected void refreshProperty( ModelProperty property, final boolean force )
    {
        synchronized( root() )
        {
            property = property.refine( this );
            
            if( property == PROP_EXCEPTION )
            {
                if( this.exception != null || force == true )
                {
                    final Value<String> oldValue = this.exception;
                    
                    final String val = resource().binding( PROP_EXCEPTION ).read();
                    
                    this.exception = new Value<String>( this, PROP_EXCEPTION, PROP_EXCEPTION.encodeKeywords( val ) );
                    this.exception.init();
                    
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_EXCEPTION );
                    
                    if( oldValue != null )
                    {
                        if( this.exception.equals( oldValue ) )
                        {
                            this.exception = oldValue;
                        }
                        
                        if( this.exception != oldValue || propertyEnabledStatusChanged )
                        {
                            notifyPropertyChangeListeners( PROP_EXCEPTION );
                        }
                    }
                }
            }
        }
    }
    
    public Object read( ModelProperty property )
    {
        property = property.refine( this );
        
        if( property == PROP_EXCEPTION )
        {
            return getException();
        }
        
        return super.read( property );
    }
    
    public void write( ValueProperty property, final Object value )
    {
        property = (ValueProperty) property.refine( this );
        
        if( property == PROP_EXCEPTION )
        {
            setException( (String) value );
            return;
        }
        
        super.write( property, value );
    }
    
}
