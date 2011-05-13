package com.liferay.ide.eclipse.service.core.model.internal;

import com.liferay.ide.eclipse.service.core.model.IFinderColumn;
import org.eclipse.sapphire.modeling.IModelParticle;
import org.eclipse.sapphire.modeling.ModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.Resource;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueNormalizationService;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.serialization.ValueSerializationService;

public final class FinderColumn
    
    extends ModelElement
    implements IFinderColumn
    
{
    private Value<String> arrayableOperator;
    private Value<Boolean> caseSensitive;
    private Value<String> comparator;
    private Value<String> name;
    
    public FinderColumn( final IModelParticle parent, final ModelProperty parentProperty, final Resource resource )
    {
        super( TYPE, parent, parentProperty, resource );
    }
    
    public FinderColumn( final Resource resource )
    {
        super( TYPE, null, null, resource );
    }
    
    public Value<String> getArrayableOperator()
    {
        synchronized( root() )
        {
            if( this.arrayableOperator == null )
            {
                refresh( PROP_ARRAYABLE_OPERATOR, true );
            }
            
            return this.arrayableOperator;
        }
    }
    
    public void setArrayableOperator( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( "" ) )
            {
                value = null;
            }
            
            value = PROP_ARRAYABLE_OPERATOR.decodeKeywords( value );
            value = service( PROP_ARRAYABLE_OPERATOR, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_ARRAYABLE_OPERATOR, true );
            
            if( ! equal( this.arrayableOperator.getText( false ), value ) )
            {
                resource().binding( PROP_ARRAYABLE_OPERATOR ).write( value );
                refresh( PROP_ARRAYABLE_OPERATOR, false );
            }
        }
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
    
    public Value<String> getComparator()
    {
        synchronized( root() )
        {
            if( this.comparator == null )
            {
                refresh( PROP_COMPARATOR, true );
            }
            
            return this.comparator;
        }
    }
    
    public void setComparator( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( "" ) )
            {
                value = null;
            }
            
            value = PROP_COMPARATOR.decodeKeywords( value );
            value = service( PROP_COMPARATOR, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_COMPARATOR, true );
            
            if( ! equal( this.comparator.getText( false ), value ) )
            {
                resource().binding( PROP_COMPARATOR ).write( value );
                refresh( PROP_COMPARATOR, false );
            }
        }
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
    
    protected void refreshProperty( ModelProperty property, final boolean force )
    {
        synchronized( root() )
        {
            property = property.refine( this );
            
            if( property == PROP_ARRAYABLE_OPERATOR )
            {
                if( this.arrayableOperator != null || force == true )
                {
                    final Value<String> oldValue = this.arrayableOperator;
                    
                    final String val = resource().binding( PROP_ARRAYABLE_OPERATOR ).read();
                    
                    this.arrayableOperator = new Value<String>( this, PROP_ARRAYABLE_OPERATOR, service( PROP_ARRAYABLE_OPERATOR, ValueNormalizationService.class ).normalize( PROP_ARRAYABLE_OPERATOR.encodeKeywords( val ) ) );
                    this.arrayableOperator.init();
                    
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_ARRAYABLE_OPERATOR );
                    
                    if( oldValue != null )
                    {
                        if( this.arrayableOperator.equals( oldValue ) )
                        {
                            this.arrayableOperator = oldValue;
                        }
                        
                        if( this.arrayableOperator != oldValue || propertyEnabledStatusChanged )
                        {
                            notifyPropertyChangeListeners( PROP_ARRAYABLE_OPERATOR );
                        }
                    }
                }
            }
            else if( property == PROP_CASE_SENSITIVE )
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
            else if( property == PROP_COMPARATOR )
            {
                if( this.comparator != null || force == true )
                {
                    final Value<String> oldValue = this.comparator;
                    
                    final String val = resource().binding( PROP_COMPARATOR ).read();
                    
                    this.comparator = new Value<String>( this, PROP_COMPARATOR, service( PROP_COMPARATOR, ValueNormalizationService.class ).normalize( PROP_COMPARATOR.encodeKeywords( val ) ) );
                    this.comparator.init();
                    
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_COMPARATOR );
                    
                    if( oldValue != null )
                    {
                        if( this.comparator.equals( oldValue ) )
                        {
                            this.comparator = oldValue;
                        }
                        
                        if( this.comparator != oldValue || propertyEnabledStatusChanged )
                        {
                            notifyPropertyChangeListeners( PROP_COMPARATOR );
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
        }
    }
    
    public Object read( ModelProperty property )
    {
        property = property.refine( this );
        
        if( property == PROP_ARRAYABLE_OPERATOR )
        {
            return getArrayableOperator();
        }
        else if( property == PROP_CASE_SENSITIVE )
        {
            return isCaseSensitive();
        }
        else if( property == PROP_COMPARATOR )
        {
            return getComparator();
        }
        else if( property == PROP_NAME )
        {
            return getName();
        }
        
        return super.read( property );
    }
    
    public void write( ValueProperty property, final Object value )
    {
        property = (ValueProperty) property.refine( this );
        
        if( property == PROP_ARRAYABLE_OPERATOR )
        {
            setArrayableOperator( (String) value );
            return;
        }
        else if( property == PROP_CASE_SENSITIVE )
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
        else if( property == PROP_COMPARATOR )
        {
            setComparator( (String) value );
            return;
        }
        else if( property == PROP_NAME )
        {
            setName( (String) value );
            return;
        }
        
        super.write( property, value );
    }
    
}
