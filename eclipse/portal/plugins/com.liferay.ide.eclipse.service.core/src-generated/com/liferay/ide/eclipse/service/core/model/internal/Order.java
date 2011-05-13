package com.liferay.ide.eclipse.service.core.model.internal;

import com.liferay.ide.eclipse.service.core.model.IOrder;
import com.liferay.ide.eclipse.service.core.model.IOrderColumn;
import org.eclipse.sapphire.modeling.IModelParticle;
import org.eclipse.sapphire.modeling.ListBindingImpl;
import org.eclipse.sapphire.modeling.ModelElement;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.Resource;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueNormalizationService;
import org.eclipse.sapphire.modeling.ValueProperty;

public final class Order
    
    extends ModelElement
    implements IOrder
    
{
    private Value<String> by;
    private ModelElementList<IOrderColumn> orderColumns;
    
    public Order( final IModelParticle parent, final ModelProperty parentProperty, final Resource resource )
    {
        super( TYPE, parent, parentProperty, resource );
    }
    
    public Order( final Resource resource )
    {
        super( TYPE, null, null, resource );
    }
    
    public Value<String> getBy()
    {
        synchronized( root() )
        {
            if( this.by == null )
            {
                refresh( PROP_BY, true );
            }
            
            return this.by;
        }
    }
    
    public void setBy( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( "" ) )
            {
                value = null;
            }
            
            value = PROP_BY.decodeKeywords( value );
            value = service( PROP_BY, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_BY, true );
            
            if( ! equal( this.by.getText( false ), value ) )
            {
                resource().binding( PROP_BY ).write( value );
                refresh( PROP_BY, false );
            }
        }
    }
    
    public ModelElementList<IOrderColumn> getOrderColumns()
    {
        synchronized( root() )
        {
            if( this.orderColumns == null )
            {
                refresh( PROP_ORDER_COLUMNS, true );
            }
            
            return this.orderColumns;
        }
    }
    
    protected void refreshProperty( ModelProperty property, final boolean force )
    {
        synchronized( root() )
        {
            property = property.refine( this );
            
            if( property == PROP_BY )
            {
                if( this.by != null || force == true )
                {
                    final Value<String> oldValue = this.by;
                    
                    final String val = resource().binding( PROP_BY ).read();
                    
                    this.by = new Value<String>( this, PROP_BY, service( PROP_BY, ValueNormalizationService.class ).normalize( PROP_BY.encodeKeywords( val ) ) );
                    this.by.init();
                    
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_BY );
                    
                    if( oldValue != null )
                    {
                        if( this.by.equals( oldValue ) )
                        {
                            this.by = oldValue;
                        }
                        
                        if( this.by != oldValue || propertyEnabledStatusChanged )
                        {
                            notifyPropertyChangeListeners( PROP_BY );
                        }
                    }
                }
            }
            else if( property == PROP_ORDER_COLUMNS )
            {
                if( this.orderColumns == null )
                {
                    if( force == true )
                    {
                        this.orderColumns = new ModelElementList<IOrderColumn>( this, PROP_ORDER_COLUMNS );
                        final ListBindingImpl binding = resource().binding( PROP_ORDER_COLUMNS );
                        this.orderColumns.init( binding );
                        refreshPropertyEnabledStatus( PROP_ORDER_COLUMNS );
                    }
                }
                else
                {
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_ORDER_COLUMNS );
                    final boolean notified = this.orderColumns.refresh();
                    
                    if( ! notified && propertyEnabledStatusChanged )
                    {
                        notifyPropertyChangeListeners( PROP_ORDER_COLUMNS );
                    }
                }
            }
        }
    }
    
    public Object read( ModelProperty property )
    {
        property = property.refine( this );
        
        if( property == PROP_BY )
        {
            return getBy();
        }
        else if( property == PROP_ORDER_COLUMNS )
        {
            return getOrderColumns();
        }
        
        return super.read( property );
    }
    
    public void write( ValueProperty property, final Object value )
    {
        property = (ValueProperty) property.refine( this );
        
        if( property == PROP_BY )
        {
            setBy( (String) value );
            return;
        }
        
        super.write( property, value );
    }
    
}
