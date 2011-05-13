package com.liferay.ide.eclipse.service.core.model.internal;

import com.liferay.ide.eclipse.service.core.model.IEntity;
import com.liferay.ide.eclipse.service.core.model.IException;
import com.liferay.ide.eclipse.service.core.model.IServiceBuilder;
import org.eclipse.sapphire.modeling.IModelParticle;
import org.eclipse.sapphire.modeling.ListBindingImpl;
import org.eclipse.sapphire.modeling.ModelElement;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.Resource;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueNormalizationService;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.serialization.ValueSerializationService;

public final class ServiceBuilder
    
    extends ModelElement
    implements IServiceBuilder
    
{
    private Value<String> author;
    private Value<Boolean> autoNamespaceTables;
    private ModelElementList<IEntity> entities;
    private ModelElementList<IException> exceptions;
    private Value<String> namespace;
    private Value<String> packagePath;
    
    public ServiceBuilder( final IModelParticle parent, final ModelProperty parentProperty, final Resource resource )
    {
        super( TYPE, parent, parentProperty, resource );
    }
    
    public ServiceBuilder( final Resource resource )
    {
        super( TYPE, null, null, resource );
    }
    
    public Value<String> getAuthor()
    {
        synchronized( root() )
        {
            if( this.author == null )
            {
                refresh( PROP_AUTHOR, true );
            }
            
            return this.author;
        }
    }
    
    public void setAuthor( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( "" ) )
            {
                value = null;
            }
            
            value = PROP_AUTHOR.decodeKeywords( value );
            value = service( PROP_AUTHOR, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_AUTHOR, true );
            
            if( ! equal( this.author.getText( false ), value ) )
            {
                resource().binding( PROP_AUTHOR ).write( value );
                refresh( PROP_AUTHOR, false );
            }
        }
    }
    
    public Value<Boolean> isAutoNamespaceTables()
    {
        synchronized( root() )
        {
            if( this.autoNamespaceTables == null )
            {
                refresh( PROP_AUTO_NAMESPACE_TABLES, true );
            }
            
            return this.autoNamespaceTables;
        }
    }
    
    public void setAutoNamespaceTables( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( "" ) )
            {
                value = null;
            }
            
            value = PROP_AUTO_NAMESPACE_TABLES.decodeKeywords( value );
            value = service( PROP_AUTO_NAMESPACE_TABLES, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_AUTO_NAMESPACE_TABLES, true );
            
            if( ! equal( this.autoNamespaceTables.getText( false ), value ) )
            {
                resource().binding( PROP_AUTO_NAMESPACE_TABLES ).write( value );
                refresh( PROP_AUTO_NAMESPACE_TABLES, false );
            }
        }
    }
    
    public void setAutoNamespaceTables( final Boolean value )
    {
        setAutoNamespaceTables( value != null ? service( PROP_AUTO_NAMESPACE_TABLES, ValueSerializationService.class ).encode( value ) : null );
    }
    
    public ModelElementList<IEntity> getEntities()
    {
        synchronized( root() )
        {
            if( this.entities == null )
            {
                refresh( PROP_ENTITIES, true );
            }
            
            return this.entities;
        }
    }
    
    public ModelElementList<IException> getExceptions()
    {
        synchronized( root() )
        {
            if( this.exceptions == null )
            {
                refresh( PROP_EXCEPTIONS, true );
            }
            
            return this.exceptions;
        }
    }
    
    public Value<String> getNamespace()
    {
        synchronized( root() )
        {
            if( this.namespace == null )
            {
                refresh( PROP_NAMESPACE, true );
            }
            
            return this.namespace;
        }
    }
    
    public void setNamespace( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( "" ) )
            {
                value = null;
            }
            
            value = PROP_NAMESPACE.decodeKeywords( value );
            value = service( PROP_NAMESPACE, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_NAMESPACE, true );
            
            if( ! equal( this.namespace.getText( false ), value ) )
            {
                resource().binding( PROP_NAMESPACE ).write( value );
                refresh( PROP_NAMESPACE, false );
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
            
            if( property == PROP_AUTHOR )
            {
                if( this.author != null || force == true )
                {
                    final Value<String> oldValue = this.author;
                    
                    final String val = resource().binding( PROP_AUTHOR ).read();
                    
                    this.author = new Value<String>( this, PROP_AUTHOR, service( PROP_AUTHOR, ValueNormalizationService.class ).normalize( PROP_AUTHOR.encodeKeywords( val ) ) );
                    this.author.init();
                    
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_AUTHOR );
                    
                    if( oldValue != null )
                    {
                        if( this.author.equals( oldValue ) )
                        {
                            this.author = oldValue;
                        }
                        
                        if( this.author != oldValue || propertyEnabledStatusChanged )
                        {
                            notifyPropertyChangeListeners( PROP_AUTHOR );
                        }
                    }
                }
            }
            else if( property == PROP_AUTO_NAMESPACE_TABLES )
            {
                if( this.autoNamespaceTables != null || force == true )
                {
                    final Value<Boolean> oldValue = this.autoNamespaceTables;
                    
                    final String val = resource().binding( PROP_AUTO_NAMESPACE_TABLES ).read();
                    
                    this.autoNamespaceTables = new Value<Boolean>( this, PROP_AUTO_NAMESPACE_TABLES, service( PROP_AUTO_NAMESPACE_TABLES, ValueNormalizationService.class ).normalize( PROP_AUTO_NAMESPACE_TABLES.encodeKeywords( val ) ) );
                    this.autoNamespaceTables.init();
                    
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_AUTO_NAMESPACE_TABLES );
                    
                    if( oldValue != null )
                    {
                        if( this.autoNamespaceTables.equals( oldValue ) )
                        {
                            this.autoNamespaceTables = oldValue;
                        }
                        
                        if( this.autoNamespaceTables != oldValue || propertyEnabledStatusChanged )
                        {
                            notifyPropertyChangeListeners( PROP_AUTO_NAMESPACE_TABLES );
                        }
                    }
                }
            }
            else if( property == PROP_ENTITIES )
            {
                if( this.entities == null )
                {
                    if( force == true )
                    {
                        this.entities = new ModelElementList<IEntity>( this, PROP_ENTITIES );
                        final ListBindingImpl binding = resource().binding( PROP_ENTITIES );
                        this.entities.init( binding );
                        refreshPropertyEnabledStatus( PROP_ENTITIES );
                    }
                }
                else
                {
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_ENTITIES );
                    final boolean notified = this.entities.refresh();
                    
                    if( ! notified && propertyEnabledStatusChanged )
                    {
                        notifyPropertyChangeListeners( PROP_ENTITIES );
                    }
                }
            }
            else if( property == PROP_EXCEPTIONS )
            {
                if( this.exceptions == null )
                {
                    if( force == true )
                    {
                        this.exceptions = new ModelElementList<IException>( this, PROP_EXCEPTIONS );
                        final ListBindingImpl binding = resource().binding( PROP_EXCEPTIONS );
                        this.exceptions.init( binding );
                        refreshPropertyEnabledStatus( PROP_EXCEPTIONS );
                    }
                }
                else
                {
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_EXCEPTIONS );
                    final boolean notified = this.exceptions.refresh();
                    
                    if( ! notified && propertyEnabledStatusChanged )
                    {
                        notifyPropertyChangeListeners( PROP_EXCEPTIONS );
                    }
                }
            }
            else if( property == PROP_NAMESPACE )
            {
                if( this.namespace != null || force == true )
                {
                    final Value<String> oldValue = this.namespace;
                    
                    final String val = resource().binding( PROP_NAMESPACE ).read();
                    
                    this.namespace = new Value<String>( this, PROP_NAMESPACE, service( PROP_NAMESPACE, ValueNormalizationService.class ).normalize( PROP_NAMESPACE.encodeKeywords( val ) ) );
                    this.namespace.init();
                    
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_NAMESPACE );
                    
                    if( oldValue != null )
                    {
                        if( this.namespace.equals( oldValue ) )
                        {
                            this.namespace = oldValue;
                        }
                        
                        if( this.namespace != oldValue || propertyEnabledStatusChanged )
                        {
                            notifyPropertyChangeListeners( PROP_NAMESPACE );
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
        
        if( property == PROP_AUTHOR )
        {
            return getAuthor();
        }
        else if( property == PROP_AUTO_NAMESPACE_TABLES )
        {
            return isAutoNamespaceTables();
        }
        else if( property == PROP_ENTITIES )
        {
            return getEntities();
        }
        else if( property == PROP_EXCEPTIONS )
        {
            return getExceptions();
        }
        else if( property == PROP_NAMESPACE )
        {
            return getNamespace();
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
        
        if( property == PROP_AUTHOR )
        {
            setAuthor( (String) value );
            return;
        }
        else if( property == PROP_AUTO_NAMESPACE_TABLES )
        {
            if( ! ( value instanceof String ) )
            {
                setAutoNamespaceTables( (Boolean) value );
            }
            else
            {
                setAutoNamespaceTables( (String) value );
            }
            
            return;
        }
        else if( property == PROP_NAMESPACE )
        {
            setNamespace( (String) value );
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
