package com.liferay.ide.eclipse.service.core.model.internal;

import com.liferay.ide.eclipse.service.core.model.IFinder;
import com.liferay.ide.eclipse.service.core.model.IFinderColumn;
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

public final class Finder
    
    extends ModelElement
    implements IFinder
    
{
    private Value<Boolean> dbIndex;
    private ModelElementList<IFinderColumn> finderColumns;
    private Value<String> name;
    private Value<String> returnType;
    private Value<Boolean> unique;
    private Value<String> where;
    
    public Finder( final IModelParticle parent, final ModelProperty parentProperty, final Resource resource )
    {
        super( TYPE, parent, parentProperty, resource );
    }
    
    public Finder( final Resource resource )
    {
        super( TYPE, null, null, resource );
    }
    
    public Value<Boolean> isDbIndex()
    {
        synchronized( root() )
        {
            if( this.dbIndex == null )
            {
                refresh( PROP_DB_INDEX, true );
            }
            
            return this.dbIndex;
        }
    }
    
    public void setDbIndex( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( "" ) )
            {
                value = null;
            }
            
            value = PROP_DB_INDEX.decodeKeywords( value );
            value = service( PROP_DB_INDEX, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_DB_INDEX, true );
            
            if( ! equal( this.dbIndex.getText( false ), value ) )
            {
                resource().binding( PROP_DB_INDEX ).write( value );
                refresh( PROP_DB_INDEX, false );
            }
        }
    }
    
    public void setDbIndex( final Boolean value )
    {
        setDbIndex( value != null ? service( PROP_DB_INDEX, ValueSerializationService.class ).encode( value ) : null );
    }
    
    public ModelElementList<IFinderColumn> getFinderColumns()
    {
        synchronized( root() )
        {
            if( this.finderColumns == null )
            {
                refresh( PROP_FINDER_COLUMNS, true );
            }
            
            return this.finderColumns;
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
    
    public Value<String> getReturnType()
    {
        synchronized( root() )
        {
            if( this.returnType == null )
            {
                refresh( PROP_RETURN_TYPE, true );
            }
            
            return this.returnType;
        }
    }
    
    public void setReturnType( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( "" ) )
            {
                value = null;
            }
            
            value = PROP_RETURN_TYPE.decodeKeywords( value );
            value = service( PROP_RETURN_TYPE, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_RETURN_TYPE, true );
            
            if( ! equal( this.returnType.getText( false ), value ) )
            {
                resource().binding( PROP_RETURN_TYPE ).write( value );
                refresh( PROP_RETURN_TYPE, false );
            }
        }
    }
    
    public Value<Boolean> isUnique()
    {
        synchronized( root() )
        {
            if( this.unique == null )
            {
                refresh( PROP_UNIQUE, true );
            }
            
            return this.unique;
        }
    }
    
    public void setUnique( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( "" ) )
            {
                value = null;
            }
            
            value = PROP_UNIQUE.decodeKeywords( value );
            value = service( PROP_UNIQUE, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_UNIQUE, true );
            
            if( ! equal( this.unique.getText( false ), value ) )
            {
                resource().binding( PROP_UNIQUE ).write( value );
                refresh( PROP_UNIQUE, false );
            }
        }
    }
    
    public void setUnique( final Boolean value )
    {
        setUnique( value != null ? service( PROP_UNIQUE, ValueSerializationService.class ).encode( value ) : null );
    }
    
    public Value<String> getWhere()
    {
        synchronized( root() )
        {
            if( this.where == null )
            {
                refresh( PROP_WHERE, true );
            }
            
            return this.where;
        }
    }
    
    public void setWhere( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( "" ) )
            {
                value = null;
            }
            
            value = PROP_WHERE.decodeKeywords( value );
            value = service( PROP_WHERE, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_WHERE, true );
            
            if( ! equal( this.where.getText( false ), value ) )
            {
                resource().binding( PROP_WHERE ).write( value );
                refresh( PROP_WHERE, false );
            }
        }
    }
    
    protected void refreshProperty( ModelProperty property, final boolean force )
    {
        synchronized( root() )
        {
            property = property.refine( this );
            
            if( property == PROP_DB_INDEX )
            {
                if( this.dbIndex != null || force == true )
                {
                    final Value<Boolean> oldValue = this.dbIndex;
                    
                    final String val = resource().binding( PROP_DB_INDEX ).read();
                    
                    this.dbIndex = new Value<Boolean>( this, PROP_DB_INDEX, service( PROP_DB_INDEX, ValueNormalizationService.class ).normalize( PROP_DB_INDEX.encodeKeywords( val ) ) );
                    this.dbIndex.init();
                    
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_DB_INDEX );
                    
                    if( oldValue != null )
                    {
                        if( this.dbIndex.equals( oldValue ) )
                        {
                            this.dbIndex = oldValue;
                        }
                        
                        if( this.dbIndex != oldValue || propertyEnabledStatusChanged )
                        {
                            notifyPropertyChangeListeners( PROP_DB_INDEX );
                        }
                    }
                }
            }
            else if( property == PROP_FINDER_COLUMNS )
            {
                if( this.finderColumns == null )
                {
                    if( force == true )
                    {
                        this.finderColumns = new ModelElementList<IFinderColumn>( this, PROP_FINDER_COLUMNS );
                        final ListBindingImpl binding = resource().binding( PROP_FINDER_COLUMNS );
                        this.finderColumns.init( binding );
                        refreshPropertyEnabledStatus( PROP_FINDER_COLUMNS );
                    }
                }
                else
                {
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_FINDER_COLUMNS );
                    final boolean notified = this.finderColumns.refresh();
                    
                    if( ! notified && propertyEnabledStatusChanged )
                    {
                        notifyPropertyChangeListeners( PROP_FINDER_COLUMNS );
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
            else if( property == PROP_RETURN_TYPE )
            {
                if( this.returnType != null || force == true )
                {
                    final Value<String> oldValue = this.returnType;
                    
                    final String val = resource().binding( PROP_RETURN_TYPE ).read();
                    
                    this.returnType = new Value<String>( this, PROP_RETURN_TYPE, service( PROP_RETURN_TYPE, ValueNormalizationService.class ).normalize( PROP_RETURN_TYPE.encodeKeywords( val ) ) );
                    this.returnType.init();
                    
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_RETURN_TYPE );
                    
                    if( oldValue != null )
                    {
                        if( this.returnType.equals( oldValue ) )
                        {
                            this.returnType = oldValue;
                        }
                        
                        if( this.returnType != oldValue || propertyEnabledStatusChanged )
                        {
                            notifyPropertyChangeListeners( PROP_RETURN_TYPE );
                        }
                    }
                }
            }
            else if( property == PROP_UNIQUE )
            {
                if( this.unique != null || force == true )
                {
                    final Value<Boolean> oldValue = this.unique;
                    
                    final String val = resource().binding( PROP_UNIQUE ).read();
                    
                    this.unique = new Value<Boolean>( this, PROP_UNIQUE, service( PROP_UNIQUE, ValueNormalizationService.class ).normalize( PROP_UNIQUE.encodeKeywords( val ) ) );
                    this.unique.init();
                    
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_UNIQUE );
                    
                    if( oldValue != null )
                    {
                        if( this.unique.equals( oldValue ) )
                        {
                            this.unique = oldValue;
                        }
                        
                        if( this.unique != oldValue || propertyEnabledStatusChanged )
                        {
                            notifyPropertyChangeListeners( PROP_UNIQUE );
                        }
                    }
                }
            }
            else if( property == PROP_WHERE )
            {
                if( this.where != null || force == true )
                {
                    final Value<String> oldValue = this.where;
                    
                    final String val = resource().binding( PROP_WHERE ).read();
                    
                    this.where = new Value<String>( this, PROP_WHERE, service( PROP_WHERE, ValueNormalizationService.class ).normalize( PROP_WHERE.encodeKeywords( val ) ) );
                    this.where.init();
                    
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_WHERE );
                    
                    if( oldValue != null )
                    {
                        if( this.where.equals( oldValue ) )
                        {
                            this.where = oldValue;
                        }
                        
                        if( this.where != oldValue || propertyEnabledStatusChanged )
                        {
                            notifyPropertyChangeListeners( PROP_WHERE );
                        }
                    }
                }
            }
        }
    }
    
    public Object read( ModelProperty property )
    {
        property = property.refine( this );
        
        if( property == PROP_DB_INDEX )
        {
            return isDbIndex();
        }
        else if( property == PROP_FINDER_COLUMNS )
        {
            return getFinderColumns();
        }
        else if( property == PROP_NAME )
        {
            return getName();
        }
        else if( property == PROP_RETURN_TYPE )
        {
            return getReturnType();
        }
        else if( property == PROP_UNIQUE )
        {
            return isUnique();
        }
        else if( property == PROP_WHERE )
        {
            return getWhere();
        }
        
        return super.read( property );
    }
    
    public void write( ValueProperty property, final Object value )
    {
        property = (ValueProperty) property.refine( this );
        
        if( property == PROP_DB_INDEX )
        {
            if( ! ( value instanceof String ) )
            {
                setDbIndex( (Boolean) value );
            }
            else
            {
                setDbIndex( (String) value );
            }
            
            return;
        }
        else if( property == PROP_NAME )
        {
            setName( (String) value );
            return;
        }
        else if( property == PROP_RETURN_TYPE )
        {
            setReturnType( (String) value );
            return;
        }
        else if( property == PROP_UNIQUE )
        {
            if( ! ( value instanceof String ) )
            {
                setUnique( (Boolean) value );
            }
            else
            {
                setUnique( (String) value );
            }
            
            return;
        }
        else if( property == PROP_WHERE )
        {
            setWhere( (String) value );
            return;
        }
        
        super.write( property, value );
    }
    
}
