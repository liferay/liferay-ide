package com.liferay.ide.eclipse.service.core.model.internal;

import com.liferay.ide.eclipse.service.core.model.IEntity;
import com.liferay.ide.eclipse.service.core.model.IRelationship;
import org.eclipse.sapphire.modeling.IModelParticle;
import org.eclipse.sapphire.modeling.ModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ReferenceValue;
import org.eclipse.sapphire.modeling.Resource;
import org.eclipse.sapphire.modeling.ValueNormalizationService;
import org.eclipse.sapphire.modeling.ValueProperty;

public final class Relationship
    
    extends ModelElement
    implements IRelationship
    
{
    private ReferenceValue<String,IEntity> name;
    
    public Relationship( final IModelParticle parent, final ModelProperty parentProperty, final Resource resource )
    {
        super( TYPE, parent, parentProperty, resource );
    }
    
    public Relationship( final Resource resource )
    {
        super( TYPE, null, null, resource );
    }
    
    public ReferenceValue<String,IEntity> getName()
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
            
            if( property == PROP_NAME )
            {
                if( this.name != null || force == true )
                {
                    final ReferenceValue<String,IEntity> oldValue = this.name;
                    
                    final String val = resource().binding( PROP_NAME ).read();
                    
                    this.name = new ReferenceValue<String,IEntity>( this, PROP_NAME, service( PROP_NAME, ValueNormalizationService.class ).normalize( PROP_NAME.encodeKeywords( val ) ) );
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
        
        if( property == PROP_NAME )
        {
            return getName();
        }
        
        return super.read( property );
    }
    
    public void write( ValueProperty property, final Object value )
    {
        property = (ValueProperty) property.refine( this );
        
        if( property == PROP_NAME )
        {
            setName( (String) value );
            return;
        }
        
        super.write( property, value );
    }
    
}
