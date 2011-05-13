package com.liferay.ide.eclipse.taglib.ui.model.internal;

import com.liferay.ide.eclipse.taglib.ui.model.IAttribute;
import org.eclipse.sapphire.modeling.IModelParticle;
import org.eclipse.sapphire.modeling.ModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.Resource;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueNormalizationService;
import org.eclipse.sapphire.modeling.ValueProperty;

public final class Attribute
    
    extends ModelElement
    implements IAttribute
    
{
    private Value<String> defaultValue;
    private Value<String> description;
    private Value<String> name;
    private Value<Boolean> required;
    private Value<String> type;
    private Value<String> value;
    
    public Attribute( final IModelParticle parent, final ModelProperty parentProperty, final Resource resource )
    {
        super( TYPE, parent, parentProperty, resource );
    }
    
    public Attribute( final Resource resource )
    {
        super( TYPE, null, null, resource );
    }
    
    public Value<String> getDefaultValue()
    {
        synchronized( root() )
        {
            if( this.defaultValue == null )
            {
                refresh( PROP_DEFAULT_VALUE, true );
            }
            
            return this.defaultValue;
        }
    }
    
    public Value<String> getDescription()
    {
        synchronized( root() )
        {
            if( this.description == null )
            {
                refresh( PROP_DESCRIPTION, true );
            }
            
            return this.description;
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
    
    public Value<Boolean> isRequired()
    {
        synchronized( root() )
        {
            if( this.required == null )
            {
                refresh( PROP_REQUIRED, true );
            }
            
            return this.required;
        }
    }
    
    public Value<String> getType()
    {
        synchronized( root() )
        {
            if( this.type == null )
            {
                refresh( PROP_TYPE, true );
            }
            
            return this.type;
        }
    }
    
    public Value<String> getValue()
    {
        synchronized( root() )
        {
            if( this.value == null )
            {
                refresh( PROP_VALUE, true );
            }
            
            return this.value;
        }
    }
    
    public void setValue( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( "" ) )
            {
                value = null;
            }
            
            value = PROP_VALUE.decodeKeywords( value );
            value = service( PROP_VALUE, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_VALUE, true );
            
            if( ! equal( this.value.getText( false ), value ) )
            {
                resource().binding( PROP_VALUE ).write( value );
                refresh( PROP_VALUE, false );
            }
        }
    }
    
    protected void refreshProperty( ModelProperty property, final boolean force )
    {
        synchronized( root() )
        {
            property = property.refine( this );
            
            if( property == PROP_DEFAULT_VALUE )
            {
                if( this.defaultValue != null || force == true )
                {
                    final Value<String> oldValue = this.defaultValue;
                    
                    final String val = resource().binding( PROP_DEFAULT_VALUE ).read();
                    
                    this.defaultValue = new Value<String>( this, PROP_DEFAULT_VALUE, service( PROP_DEFAULT_VALUE, ValueNormalizationService.class ).normalize( PROP_DEFAULT_VALUE.encodeKeywords( val ) ) );
                    this.defaultValue.init();
                    
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_DEFAULT_VALUE );
                    
                    if( oldValue != null )
                    {
                        if( this.defaultValue.equals( oldValue ) )
                        {
                            this.defaultValue = oldValue;
                        }
                        
                        if( this.defaultValue != oldValue || propertyEnabledStatusChanged )
                        {
                            notifyPropertyChangeListeners( PROP_DEFAULT_VALUE );
                        }
                    }
                }
            }
            else if( property == PROP_DESCRIPTION )
            {
                if( this.description != null || force == true )
                {
                    final Value<String> oldValue = this.description;
                    
                    final String val = resource().binding( PROP_DESCRIPTION ).read();
                    
                    this.description = new Value<String>( this, PROP_DESCRIPTION, service( PROP_DESCRIPTION, ValueNormalizationService.class ).normalize( PROP_DESCRIPTION.encodeKeywords( val ) ) );
                    this.description.init();
                    
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_DESCRIPTION );
                    
                    if( oldValue != null )
                    {
                        if( this.description.equals( oldValue ) )
                        {
                            this.description = oldValue;
                        }
                        
                        if( this.description != oldValue || propertyEnabledStatusChanged )
                        {
                            notifyPropertyChangeListeners( PROP_DESCRIPTION );
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
            else if( property == PROP_REQUIRED )
            {
                if( this.required != null || force == true )
                {
                    final Value<Boolean> oldValue = this.required;
                    
                    final String val = resource().binding( PROP_REQUIRED ).read();
                    
                    this.required = new Value<Boolean>( this, PROP_REQUIRED, service( PROP_REQUIRED, ValueNormalizationService.class ).normalize( PROP_REQUIRED.encodeKeywords( val ) ) );
                    this.required.init();
                    
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_REQUIRED );
                    
                    if( oldValue != null )
                    {
                        if( this.required.equals( oldValue ) )
                        {
                            this.required = oldValue;
                        }
                        
                        if( this.required != oldValue || propertyEnabledStatusChanged )
                        {
                            notifyPropertyChangeListeners( PROP_REQUIRED );
                        }
                    }
                }
            }
            else if( property == PROP_TYPE )
            {
                if( this.type != null || force == true )
                {
                    final Value<String> oldValue = this.type;
                    
                    final String val = resource().binding( PROP_TYPE ).read();
                    
                    this.type = new Value<String>( this, PROP_TYPE, service( PROP_TYPE, ValueNormalizationService.class ).normalize( PROP_TYPE.encodeKeywords( val ) ) );
                    this.type.init();
                    
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_TYPE );
                    
                    if( oldValue != null )
                    {
                        if( this.type.equals( oldValue ) )
                        {
                            this.type = oldValue;
                        }
                        
                        if( this.type != oldValue || propertyEnabledStatusChanged )
                        {
                            notifyPropertyChangeListeners( PROP_TYPE );
                        }
                    }
                }
            }
            else if( property == PROP_VALUE )
            {
                if( this.value != null || force == true )
                {
                    final Value<String> oldValue = this.value;
                    
                    final String val = resource().binding( PROP_VALUE ).read();
                    
                    this.value = new Value<String>( this, PROP_VALUE, service( PROP_VALUE, ValueNormalizationService.class ).normalize( PROP_VALUE.encodeKeywords( val ) ) );
                    this.value.init();
                    
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_VALUE );
                    
                    if( oldValue != null )
                    {
                        if( this.value.equals( oldValue ) )
                        {
                            this.value = oldValue;
                        }
                        
                        if( this.value != oldValue || propertyEnabledStatusChanged )
                        {
                            notifyPropertyChangeListeners( PROP_VALUE );
                        }
                    }
                }
            }
        }
    }
    
    public Object read( ModelProperty property )
    {
        property = property.refine( this );
        
        if( property == PROP_DEFAULT_VALUE )
        {
            return getDefaultValue();
        }
        else if( property == PROP_DESCRIPTION )
        {
            return getDescription();
        }
        else if( property == PROP_NAME )
        {
            return getName();
        }
        else if( property == PROP_REQUIRED )
        {
            return isRequired();
        }
        else if( property == PROP_TYPE )
        {
            return getType();
        }
        else if( property == PROP_VALUE )
        {
            return getValue();
        }
        
        return super.read( property );
    }
    
    public void write( ValueProperty property, final Object value )
    {
        property = (ValueProperty) property.refine( this );
        
        if( property == PROP_VALUE )
        {
            setValue( (String) value );
            return;
        }
        
        super.write( property, value );
    }
    
}
