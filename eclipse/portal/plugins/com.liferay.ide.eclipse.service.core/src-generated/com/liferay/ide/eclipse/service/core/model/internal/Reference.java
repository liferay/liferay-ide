package com.liferay.ide.eclipse.service.core.model.internal;

import com.liferay.ide.eclipse.service.core.model.IReference;
import org.eclipse.sapphire.modeling.IModelParticle;
import org.eclipse.sapphire.modeling.ModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.Resource;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueNormalizationService;
import org.eclipse.sapphire.modeling.ValueProperty;

public final class Reference
    
    extends ModelElement
    implements IReference
    
{
    private Value<String> entity;
    private Value<String> packagePath;
    
    public Reference( final IModelParticle parent, final ModelProperty parentProperty, final Resource resource )
    {
        super( TYPE, parent, parentProperty, resource );
    }
    
    public Reference( final Resource resource )
    {
        super( TYPE, null, null, resource );
    }
    
    public Value<String> getEntity()
    {
        synchronized( root() )
        {
            if( this.entity == null )
            {
                refresh( PROP_ENTITY, true );
            }
            
            return this.entity;
        }
    }
    
    public void setEntity( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( "" ) )
            {
                value = null;
            }
            
            value = PROP_ENTITY.decodeKeywords( value );
            value = service( PROP_ENTITY, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_ENTITY, true );
            
            if( ! equal( this.entity.getText( false ), value ) )
            {
                resource().binding( PROP_ENTITY ).write( value );
                refresh( PROP_ENTITY, false );
            }
        }
    }
    
    public Value<String> getPackagePath()
    {
        synchronized( root() )
        {
            if( this.packagePath == null )
            {
                refresh( PROP_PACKAGE_PATH, true );
            }
            
            return this.packagePath;
        }
    }
    
    public void setPackagePath( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( "" ) )
            {
                value = null;
            }
            
            value = PROP_PACKAGE_PATH.decodeKeywords( value );
            value = service( PROP_PACKAGE_PATH, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_PACKAGE_PATH, true );
            
            if( ! equal( this.packagePath.getText( false ), value ) )
            {
                resource().binding( PROP_PACKAGE_PATH ).write( value );
                refresh( PROP_PACKAGE_PATH, false );
            }
        }
    }
    
    protected void refreshProperty( ModelProperty property, final boolean force )
    {
        synchronized( root() )
        {
            property = property.refine( this );
            
            if( property == PROP_ENTITY )
            {
                if( this.entity != null || force == true )
                {
                    final Value<String> oldValue = this.entity;
                    
                    final String val = resource().binding( PROP_ENTITY ).read();
                    
                    this.entity = new Value<String>( this, PROP_ENTITY, service( PROP_ENTITY, ValueNormalizationService.class ).normalize( PROP_ENTITY.encodeKeywords( val ) ) );
                    this.entity.init();
                    
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_ENTITY );
                    
                    if( oldValue != null )
                    {
                        if( this.entity.equals( oldValue ) )
                        {
                            this.entity = oldValue;
                        }
                        
                        if( this.entity != oldValue || propertyEnabledStatusChanged )
                        {
                            notifyPropertyChangeListeners( PROP_ENTITY );
                        }
                    }
                }
            }
            else if( property == PROP_PACKAGE_PATH )
            {
                if( this.packagePath != null || force == true )
                {
                    final Value<String> oldValue = this.packagePath;
                    
                    final String val = resource().binding( PROP_PACKAGE_PATH ).read();
                    
                    this.packagePath = new Value<String>( this, PROP_PACKAGE_PATH, service( PROP_PACKAGE_PATH, ValueNormalizationService.class ).normalize( PROP_PACKAGE_PATH.encodeKeywords( val ) ) );
                    this.packagePath.init();
                    
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_PACKAGE_PATH );
                    
                    if( oldValue != null )
                    {
                        if( this.packagePath.equals( oldValue ) )
                        {
                            this.packagePath = oldValue;
                        }
                        
                        if( this.packagePath != oldValue || propertyEnabledStatusChanged )
                        {
                            notifyPropertyChangeListeners( PROP_PACKAGE_PATH );
                        }
                    }
                }
            }
        }
    }
    
    public Object read( ModelProperty property )
    {
        property = property.refine( this );
        
        if( property == PROP_ENTITY )
        {
            return getEntity();
        }
        else if( property == PROP_PACKAGE_PATH )
        {
            return getPackagePath();
        }
        
        return super.read( property );
    }
    
    public void write( ValueProperty property, final Object value )
    {
        property = (ValueProperty) property.refine( this );
        
        if( property == PROP_ENTITY )
        {
            setEntity( (String) value );
            return;
        }
        else if( property == PROP_PACKAGE_PATH )
        {
            setPackagePath( (String) value );
            return;
        }
        
        super.write( property, value );
    }
    
}
