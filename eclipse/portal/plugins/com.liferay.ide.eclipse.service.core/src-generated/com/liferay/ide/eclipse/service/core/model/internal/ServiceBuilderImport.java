package com.liferay.ide.eclipse.service.core.model.internal;

import com.liferay.ide.eclipse.service.core.model.IServiceBuilderImport;
import org.eclipse.sapphire.modeling.IModelParticle;
import org.eclipse.sapphire.modeling.ModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Resource;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueNormalizationService;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.serialization.ValueSerializationService;

public final class ServiceBuilderImport
    
    extends ModelElement
    implements IServiceBuilderImport
    
{
    private Value<Path> file;
    
    public ServiceBuilderImport( final IModelParticle parent, final ModelProperty parentProperty, final Resource resource )
    {
        super( TYPE, parent, parentProperty, resource );
    }
    
    public ServiceBuilderImport( final Resource resource )
    {
        super( TYPE, null, null, resource );
    }
    
    public Value<Path> getFile()
    {
        synchronized( root() )
        {
            if( this.file == null )
            {
                refresh( PROP_FILE, true );
            }
            
            return this.file;
        }
    }
    
    public void setFile( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( "" ) )
            {
                value = null;
            }
            
            value = PROP_FILE.decodeKeywords( value );
            value = service( PROP_FILE, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_FILE, true );
            
            if( ! equal( this.file.getText( false ), value ) )
            {
                resource().binding( PROP_FILE ).write( value );
                refresh( PROP_FILE, false );
            }
        }
    }
    
    public void setFile( final Path value )
    {
        setFile( value != null ? service( PROP_FILE, ValueSerializationService.class ).encode( value ) : null );
    }
    
    protected void refreshProperty( ModelProperty property, final boolean force )
    {
        synchronized( root() )
        {
            property = property.refine( this );
            
            if( property == PROP_FILE )
            {
                if( this.file != null || force == true )
                {
                    final Value<Path> oldValue = this.file;
                    
                    final String val = resource().binding( PROP_FILE ).read();
                    
                    this.file = new Value<Path>( this, PROP_FILE, service( PROP_FILE, ValueNormalizationService.class ).normalize( PROP_FILE.encodeKeywords( val ) ) );
                    this.file.init();
                    
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_FILE );
                    
                    if( oldValue != null )
                    {
                        if( this.file.equals( oldValue ) )
                        {
                            this.file = oldValue;
                        }
                        
                        if( this.file != oldValue || propertyEnabledStatusChanged )
                        {
                            notifyPropertyChangeListeners( PROP_FILE );
                        }
                    }
                }
            }
        }
    }
    
    public Object read( ModelProperty property )
    {
        property = property.refine( this );
        
        if( property == PROP_FILE )
        {
            return getFile();
        }
        
        return super.read( property );
    }
    
    public void write( ValueProperty property, final Object value )
    {
        property = (ValueProperty) property.refine( this );
        
        if( property == PROP_FILE )
        {
            if( ! ( value instanceof String ) )
            {
                setFile( (Path) value );
            }
            else
            {
                setFile( (String) value );
            }
            
            return;
        }
        
        super.write( property, value );
    }
    
}
