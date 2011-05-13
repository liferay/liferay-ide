package com.liferay.ide.eclipse.service.core.model.internal;

import com.liferay.ide.eclipse.service.core.model.ITxRequired;
import org.eclipse.sapphire.modeling.IModelParticle;
import org.eclipse.sapphire.modeling.ModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.Resource;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueNormalizationService;
import org.eclipse.sapphire.modeling.ValueProperty;

public final class TxRequired
    
    extends ModelElement
    implements ITxRequired
    
{
    private Value<String> txRequired;
    
    public TxRequired( final IModelParticle parent, final ModelProperty parentProperty, final Resource resource )
    {
        super( TYPE, parent, parentProperty, resource );
    }
    
    public TxRequired( final Resource resource )
    {
        super( TYPE, null, null, resource );
    }
    
    public Value<String> getTxRequired()
    {
        synchronized( root() )
        {
            if( this.txRequired == null )
            {
                refresh( PROP_TX_REQUIRED, true );
            }
            
            return this.txRequired;
        }
    }
    
    public void setTxRequired( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( "" ) )
            {
                value = null;
            }
            
            value = PROP_TX_REQUIRED.decodeKeywords( value );
            value = service( PROP_TX_REQUIRED, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_TX_REQUIRED, true );
            
            if( ! equal( this.txRequired.getText( false ), value ) )
            {
                resource().binding( PROP_TX_REQUIRED ).write( value );
                refresh( PROP_TX_REQUIRED, false );
            }
        }
    }
    
    protected void refreshProperty( ModelProperty property, final boolean force )
    {
        synchronized( root() )
        {
            property = property.refine( this );
            
            if( property == PROP_TX_REQUIRED )
            {
                if( this.txRequired != null || force == true )
                {
                    final Value<String> oldValue = this.txRequired;
                    
                    final String val = resource().binding( PROP_TX_REQUIRED ).read();
                    
                    this.txRequired = new Value<String>( this, PROP_TX_REQUIRED, service( PROP_TX_REQUIRED, ValueNormalizationService.class ).normalize( PROP_TX_REQUIRED.encodeKeywords( val ) ) );
                    this.txRequired.init();
                    
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_TX_REQUIRED );
                    
                    if( oldValue != null )
                    {
                        if( this.txRequired.equals( oldValue ) )
                        {
                            this.txRequired = oldValue;
                        }
                        
                        if( this.txRequired != oldValue || propertyEnabledStatusChanged )
                        {
                            notifyPropertyChangeListeners( PROP_TX_REQUIRED );
                        }
                    }
                }
            }
        }
    }
    
    public Object read( ModelProperty property )
    {
        property = property.refine( this );
        
        if( property == PROP_TX_REQUIRED )
        {
            return getTxRequired();
        }
        
        return super.read( property );
    }
    
    public void write( ValueProperty property, final Object value )
    {
        property = (ValueProperty) property.refine( this );
        
        if( property == PROP_TX_REQUIRED )
        {
            setTxRequired( (String) value );
            return;
        }
        
        super.write( property, value );
    }
    
}
