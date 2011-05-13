package com.liferay.ide.eclipse.service.core.model.internal;

import com.liferay.ide.eclipse.service.core.model.IOrderColumn;
import org.eclipse.sapphire.modeling.IModelParticle;
import org.eclipse.sapphire.modeling.ModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.Resource;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueNormalizationService;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.serialization.ValueSerializationService;

public final class OrderColumn
    
    extends ModelElement
    implements IOrderColumn
    
{
    private Value<Boolean> caseSensitive;
    private Value<String> name;
    private Value<String> orderBy;
    
    public OrderColumn( final IModelParticle parent, final ModelProperty parentProperty, final Resource resource )
    {
        super( TYPE, parent, parentProperty, resource );
    }
    
    public OrderColumn( final Resource resource )
    {
        super( TYPE, null, null, resource );
    }
    
    public Value<Boolean> isCaseSensitive()
    {
        synchronized( root() )
        {
            if( this.caseSensitive == null )
            {
                refresh( PROP_CASE_SENSITIVE, true );
            }
            
            return this.caseSensitive;
        }
    }
    
    public void setCaseSensitive( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( "" ) )
            {
                value = null;
            }
            
            value = PROP_CASE_SENSITIVE.decodeKeywords( value );
            value = service( PROP_CASE_SENSITIVE, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_CASE_SENSITIVE, true );
            
            if( ! equal( this.caseSensitive.getText( false ), value ) )
            {
                resource().binding( PROP_CASE_SENSITIVE ).write( value );
                refresh( PROP_CASE_SENSITIVE, false );
            }
        }
    }
    
    public void setCaseSensitive( final Boolean value )
    {
        setCaseSensitive( value != null ? service( PROP_CASE_SENSITIVE, ValueSerializationService.class ).encode( value ) : null );
    }
    
    public Value<String> getName()
    {
        synchronized( root() )
        {
            if( this.name == null )
            {
                refresh( PROP_NAME, true );
            }
            
            return this.name;
        }
    }
    
    public void setName( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( "" ) )
            {
                value = null;
            }
            
            value = PROP_NAME.decodeKeywords( value );
            value = service( PROP_NAME, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_NAME, true );
            
            if( ! equal( this.name.getText( false ), value ) )
            {
                resource().binding( PROP_NAME ).write( value );
                refresh( PROP_NAME, false );
            }
        }
    }
    
    public Value<String> getOrderBy()
    {
        synchronized( root() )
        {
            if( this.orderBy == null )
            {
                refresh( PROP_ORDER_BY, true );
            }
            
            return this.orderBy;
        }
    }
    
    public void setOrderBy( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( "" ) )
            {
                value = null;
            }
            
            value = PROP_ORDER_BY.decodeKeywords( value );
            value = service( PROP_ORDER_BY, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_ORDER_BY, true );
            
            if( ! equal( this.orderBy.getText( false ), value ) )
            {
                resource().binding( PROP_ORDER_BY ).write( value );
                refresh( PROP_ORDER_BY, false );
            }
        }
    }
    
    protected void refreshProperty( ModelProperty property, final boolean force )
    {
        synchronized( root() )
        {
            property = property.refine( this );
            
            if( property == PROP_CASE_SENSITIVE )
            {
                if( this.caseSensitive != null || force == true )
                {
                    final Value<Boolean> oldValue = this.caseSensitive;
                    
                    final String val = resource().binding( PROP_CASE_SENSITIVE ).read();
                    
                    this.caseSensitive = new Value<Boolean>( this, PROP_CASE_SENSITIVE, service( PROP_CASE_SENSITIVE, ValueNormalizationService.class ).normalize( PROP_CASE_SENSITIVE.encodeKeywords( val ) ) );
                    this.caseSensitive.init();
                    
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_CASE_SENSITIVE );
                    
                    if( oldValue != null )
                    {
                        if( this.caseSensitive.equals( oldValue ) )
                        {
                            this.caseSensitive = oldValue;
                        }
                        
                        if( this.caseSensitive != oldValue || propertyEnabledStatusChanged )
                        {
                            notifyPropertyChangeListeners( PROP_CASE_SENSITIVE );
                        }
                    }
                }
            }
            else if( property == PROP_NAME )
            {
                if( this.name != null || force == true )
                {
                    final Value<String> oldValue = this.name;
                    
                    final String val = resource().binding( PROP_NAME ).read();
                    
                    this.name = new Value<String>( this, PROP_NAME, service( PROP_NAME, ValueNormalizationService.class ).normalize( PROP_NAME.encodeKeywords( val ) ) );
                    this.name.init();
                    
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_NAME );
                    
                    if( oldValue != null )
                    {
                        if( this.name.equals( oldValue ) )
                        {
                            this.name = oldValue;
                        }
                        
                        if( this.name != oldValue || propertyEnabledStatusChanged )
                        {
                            notifyPropertyChangeListeners( PROP_NAME );
                        }
                    }
                }
            }
            else if( property == PROP_ORDER_BY )
            {
                if( this.orderBy != null || force == true )
                {
                    final Value<String> oldValue = this.orderBy;
                    
                    final String val = resource().binding( PROP_ORDER_BY ).read();
                    
                    this.orderBy = new Value<String>( this, PROP_ORDER_BY, service( PROP_ORDER_BY, ValueNormalizationService.class ).normalize( PROP_ORDER_BY.encodeKeywords( val ) ) );
                    this.orderBy.init();
                    
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_ORDER_BY );
                    
                    if( oldValue != null )
                    {
                        if( this.orderBy.equals( oldValue ) )
                        {
                            this.orderBy = oldValue;
                        }
                        
                        if( this.orderBy != oldValue || propertyEnabledStatusChanged )
                        {
                            notifyPropertyChangeListeners( PROP_ORDER_BY );
                        }
                    }
                }
            }
        }
    }
    
    public Object read( ModelProperty property )
    {
        property = property.refine( this );
        
        if( property == PROP_CASE_SENSITIVE )
        {
            return isCaseSensitive();
        }
        else if( property == PROP_NAME )
        {
            return getName();
        }
        else if( property == PROP_ORDER_BY )
        {
            return getOrderBy();
        }
        
        return super.read( property );
    }
    
    public void write( ValueProperty property, final Object value )
    {
        property = (ValueProperty) property.refine( this );
        
        if( property == PROP_CASE_SENSITIVE )
        {
            if( ! ( value instanceof String ) )
            {
                setCaseSensitive( (Boolean) value );
            }
            else
            {
                setCaseSensitive( (String) value );
            }
            
            return;
        }
        else if( property == PROP_NAME )
        {
            setName( (String) value );
            return;
        }
        else if( property == PROP_ORDER_BY )
        {
            setOrderBy( (String) value );
            return;
        }
        
        super.write( property, value );
    }
    
}
