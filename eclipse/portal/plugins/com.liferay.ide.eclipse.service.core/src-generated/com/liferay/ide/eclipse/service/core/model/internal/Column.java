package com.liferay.ide.eclipse.service.core.model.internal;

import com.liferay.ide.eclipse.service.core.model.IColumn;
import org.eclipse.sapphire.modeling.IModelParticle;
import org.eclipse.sapphire.modeling.ModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.Resource;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueNormalizationService;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.serialization.ValueSerializationService;

public final class Column
    
    extends ModelElement
    implements IColumn
    
{
    private Value<Boolean> convertNull;
    private Value<String> dbName;
    private Value<String> entity;
    private Value<Boolean> filterPrimary;
    private Value<String> idParam;
    private Value<String> idType;
    private Value<Boolean> jsonEnabled;
    private Value<Boolean> localized;
    private Value<String> mappingKey;
    private Value<String> mappingTable;
    private Value<String> name;
    private Value<Boolean> primary;
    private Value<String> type;
    
    public Column( final IModelParticle parent, final ModelProperty parentProperty, final Resource resource )
    {
        super( TYPE, parent, parentProperty, resource );
    }
    
    public Column( final Resource resource )
    {
        super( TYPE, null, null, resource );
    }
    
    public Value<Boolean> isConvertNull()
    {
        synchronized( root() )
        {
            if( this.convertNull == null )
            {
                refresh( PROP_CONVERT_NULL, true );
            }
            
            return this.convertNull;
        }
    }
    
    public void setConvertNull( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( "" ) )
            {
                value = null;
            }
            
            value = PROP_CONVERT_NULL.decodeKeywords( value );
            value = service( PROP_CONVERT_NULL, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_CONVERT_NULL, true );
            
            if( ! equal( this.convertNull.getText( false ), value ) )
            {
                resource().binding( PROP_CONVERT_NULL ).write( value );
                refresh( PROP_CONVERT_NULL, false );
            }
        }
    }
    
    public void setConvertNull( final Boolean value )
    {
        setConvertNull( value != null ? service( PROP_CONVERT_NULL, ValueSerializationService.class ).encode( value ) : null );
    }
    
    public Value<String> getDbName()
    {
        synchronized( root() )
        {
            if( this.dbName == null )
            {
                refresh( PROP_DB_NAME, true );
            }
            
            return this.dbName;
        }
    }
    
    public void setDbName( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( "" ) )
            {
                value = null;
            }
            
            value = PROP_DB_NAME.decodeKeywords( value );
            value = service( PROP_DB_NAME, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_DB_NAME, true );
            
            if( ! equal( this.dbName.getText( false ), value ) )
            {
                resource().binding( PROP_DB_NAME ).write( value );
                refresh( PROP_DB_NAME, false );
            }
        }
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
    
    public Value<Boolean> isFilterPrimary()
    {
        synchronized( root() )
        {
            if( this.filterPrimary == null )
            {
                refresh( PROP_FILTER_PRIMARY, true );
            }
            
            return this.filterPrimary;
        }
    }
    
    public void setFilterPrimary( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( "" ) )
            {
                value = null;
            }
            
            value = PROP_FILTER_PRIMARY.decodeKeywords( value );
            value = service( PROP_FILTER_PRIMARY, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_FILTER_PRIMARY, true );
            
            if( ! equal( this.filterPrimary.getText( false ), value ) )
            {
                resource().binding( PROP_FILTER_PRIMARY ).write( value );
                refresh( PROP_FILTER_PRIMARY, false );
            }
        }
    }
    
    public void setFilterPrimary( final Boolean value )
    {
        setFilterPrimary( value != null ? service( PROP_FILTER_PRIMARY, ValueSerializationService.class ).encode( value ) : null );
    }
    
    public Value<String> getIdParam()
    {
        synchronized( root() )
        {
            if( this.idParam == null )
            {
                refresh( PROP_ID_PARAM, true );
            }
            
            return this.idParam;
        }
    }
    
    public void setIdParam( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( "" ) )
            {
                value = null;
            }
            
            value = PROP_ID_PARAM.decodeKeywords( value );
            value = service( PROP_ID_PARAM, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_ID_PARAM, true );
            
            if( ! equal( this.idParam.getText( false ), value ) )
            {
                resource().binding( PROP_ID_PARAM ).write( value );
                refresh( PROP_ID_PARAM, false );
            }
        }
    }
    
    public Value<String> getIdType()
    {
        synchronized( root() )
        {
            if( this.idType == null )
            {
                refresh( PROP_ID_TYPE, true );
            }
            
            return this.idType;
        }
    }
    
    public void setIdType( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( "" ) )
            {
                value = null;
            }
            
            value = PROP_ID_TYPE.decodeKeywords( value );
            value = service( PROP_ID_TYPE, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_ID_TYPE, true );
            
            if( ! equal( this.idType.getText( false ), value ) )
            {
                resource().binding( PROP_ID_TYPE ).write( value );
                refresh( PROP_ID_TYPE, false );
            }
        }
    }
    
    public Value<Boolean> isJsonEnabled()
    {
        synchronized( root() )
        {
            if( this.jsonEnabled == null )
            {
                refresh( PROP_JSON_ENABLED, true );
            }
            
            return this.jsonEnabled;
        }
    }
    
    public void setJsonEnabled( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( "" ) )
            {
                value = null;
            }
            
            value = PROP_JSON_ENABLED.decodeKeywords( value );
            value = service( PROP_JSON_ENABLED, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_JSON_ENABLED, true );
            
            if( ! equal( this.jsonEnabled.getText( false ), value ) )
            {
                resource().binding( PROP_JSON_ENABLED ).write( value );
                refresh( PROP_JSON_ENABLED, false );
            }
        }
    }
    
    public void setJsonEnabled( final Boolean value )
    {
        setJsonEnabled( value != null ? service( PROP_JSON_ENABLED, ValueSerializationService.class ).encode( value ) : null );
    }
    
    public Value<Boolean> isLocalized()
    {
        synchronized( root() )
        {
            if( this.localized == null )
            {
                refresh( PROP_LOCALIZED, true );
            }
            
            return this.localized;
        }
    }
    
    public void setLocalized( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( "" ) )
            {
                value = null;
            }
            
            value = PROP_LOCALIZED.decodeKeywords( value );
            value = service( PROP_LOCALIZED, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_LOCALIZED, true );
            
            if( ! equal( this.localized.getText( false ), value ) )
            {
                resource().binding( PROP_LOCALIZED ).write( value );
                refresh( PROP_LOCALIZED, false );
            }
        }
    }
    
    public void setLocalized( final Boolean value )
    {
        setLocalized( value != null ? service( PROP_LOCALIZED, ValueSerializationService.class ).encode( value ) : null );
    }
    
    public Value<String> getMappingKey()
    {
        synchronized( root() )
        {
            if( this.mappingKey == null )
            {
                refresh( PROP_MAPPING_KEY, true );
            }
            
            return this.mappingKey;
        }
    }
    
    public void setMappingKey( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( "" ) )
            {
                value = null;
            }
            
            value = PROP_MAPPING_KEY.decodeKeywords( value );
            value = service( PROP_MAPPING_KEY, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_MAPPING_KEY, true );
            
            if( ! equal( this.mappingKey.getText( false ), value ) )
            {
                resource().binding( PROP_MAPPING_KEY ).write( value );
                refresh( PROP_MAPPING_KEY, false );
            }
        }
    }
    
    public Value<String> getMappingTable()
    {
        synchronized( root() )
        {
            if( this.mappingTable == null )
            {
                refresh( PROP_MAPPING_TABLE, true );
            }
            
            return this.mappingTable;
        }
    }
    
    public void setMappingTable( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( "" ) )
            {
                value = null;
            }
            
            value = PROP_MAPPING_TABLE.decodeKeywords( value );
            value = service( PROP_MAPPING_TABLE, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_MAPPING_TABLE, true );
            
            if( ! equal( this.mappingTable.getText( false ), value ) )
            {
                resource().binding( PROP_MAPPING_TABLE ).write( value );
                refresh( PROP_MAPPING_TABLE, false );
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
    
    public Value<Boolean> isPrimary()
    {
        synchronized( root() )
        {
            if( this.primary == null )
            {
                refresh( PROP_PRIMARY, true );
            }
            
            return this.primary;
        }
    }
    
    public void setPrimary( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( "" ) )
            {
                value = null;
            }
            
            value = PROP_PRIMARY.decodeKeywords( value );
            value = service( PROP_PRIMARY, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_PRIMARY, true );
            
            if( ! equal( this.primary.getText( false ), value ) )
            {
                resource().binding( PROP_PRIMARY ).write( value );
                refresh( PROP_PRIMARY, false );
            }
        }
    }
    
    public void setPrimary( final Boolean value )
    {
        setPrimary( value != null ? service( PROP_PRIMARY, ValueSerializationService.class ).encode( value ) : null );
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
    
    public void setType( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( "" ) )
            {
                value = null;
            }
            
            value = PROP_TYPE.decodeKeywords( value );
            value = service( PROP_TYPE, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_TYPE, true );
            
            if( ! equal( this.type.getText( false ), value ) )
            {
                resource().binding( PROP_TYPE ).write( value );
                refresh( PROP_TYPE, false );
            }
        }
    }
    
    protected void refreshProperty( ModelProperty property, final boolean force )
    {
        synchronized( root() )
        {
            property = property.refine( this );
            
            if( property == PROP_CONVERT_NULL )
            {
                if( this.convertNull != null || force == true )
                {
                    final Value<Boolean> oldValue = this.convertNull;
                    
                    final String val = resource().binding( PROP_CONVERT_NULL ).read();
                    
                    this.convertNull = new Value<Boolean>( this, PROP_CONVERT_NULL, service( PROP_CONVERT_NULL, ValueNormalizationService.class ).normalize( PROP_CONVERT_NULL.encodeKeywords( val ) ) );
                    this.convertNull.init();
                    
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_CONVERT_NULL );
                    
                    if( oldValue != null )
                    {
                        if( this.convertNull.equals( oldValue ) )
                        {
                            this.convertNull = oldValue;
                        }
                        
                        if( this.convertNull != oldValue || propertyEnabledStatusChanged )
                        {
                            notifyPropertyChangeListeners( PROP_CONVERT_NULL );
                        }
                    }
                }
            }
            else if( property == PROP_DB_NAME )
            {
                if( this.dbName != null || force == true )
                {
                    final Value<String> oldValue = this.dbName;
                    
                    final String val = resource().binding( PROP_DB_NAME ).read();
                    
                    this.dbName = new Value<String>( this, PROP_DB_NAME, service( PROP_DB_NAME, ValueNormalizationService.class ).normalize( PROP_DB_NAME.encodeKeywords( val ) ) );
                    this.dbName.init();
                    
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_DB_NAME );
                    
                    if( oldValue != null )
                    {
                        if( this.dbName.equals( oldValue ) )
                        {
                            this.dbName = oldValue;
                        }
                        
                        if( this.dbName != oldValue || propertyEnabledStatusChanged )
                        {
                            notifyPropertyChangeListeners( PROP_DB_NAME );
                        }
                    }
                }
            }
            else if( property == PROP_ENTITY )
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
            else if( property == PROP_FILTER_PRIMARY )
            {
                if( this.filterPrimary != null || force == true )
                {
                    final Value<Boolean> oldValue = this.filterPrimary;
                    
                    final String val = resource().binding( PROP_FILTER_PRIMARY ).read();
                    
                    this.filterPrimary = new Value<Boolean>( this, PROP_FILTER_PRIMARY, service( PROP_FILTER_PRIMARY, ValueNormalizationService.class ).normalize( PROP_FILTER_PRIMARY.encodeKeywords( val ) ) );
                    this.filterPrimary.init();
                    
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_FILTER_PRIMARY );
                    
                    if( oldValue != null )
                    {
                        if( this.filterPrimary.equals( oldValue ) )
                        {
                            this.filterPrimary = oldValue;
                        }
                        
                        if( this.filterPrimary != oldValue || propertyEnabledStatusChanged )
                        {
                            notifyPropertyChangeListeners( PROP_FILTER_PRIMARY );
                        }
                    }
                }
            }
            else if( property == PROP_ID_PARAM )
            {
                if( this.idParam != null || force == true )
                {
                    final Value<String> oldValue = this.idParam;
                    
                    final String val = resource().binding( PROP_ID_PARAM ).read();
                    
                    this.idParam = new Value<String>( this, PROP_ID_PARAM, service( PROP_ID_PARAM, ValueNormalizationService.class ).normalize( PROP_ID_PARAM.encodeKeywords( val ) ) );
                    this.idParam.init();
                    
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_ID_PARAM );
                    
                    if( oldValue != null )
                    {
                        if( this.idParam.equals( oldValue ) )
                        {
                            this.idParam = oldValue;
                        }
                        
                        if( this.idParam != oldValue || propertyEnabledStatusChanged )
                        {
                            notifyPropertyChangeListeners( PROP_ID_PARAM );
                        }
                    }
                }
            }
            else if( property == PROP_ID_TYPE )
            {
                if( this.idType != null || force == true )
                {
                    final Value<String> oldValue = this.idType;
                    
                    final String val = resource().binding( PROP_ID_TYPE ).read();
                    
                    this.idType = new Value<String>( this, PROP_ID_TYPE, service( PROP_ID_TYPE, ValueNormalizationService.class ).normalize( PROP_ID_TYPE.encodeKeywords( val ) ) );
                    this.idType.init();
                    
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_ID_TYPE );
                    
                    if( oldValue != null )
                    {
                        if( this.idType.equals( oldValue ) )
                        {
                            this.idType = oldValue;
                        }
                        
                        if( this.idType != oldValue || propertyEnabledStatusChanged )
                        {
                            notifyPropertyChangeListeners( PROP_ID_TYPE );
                        }
                    }
                }
            }
            else if( property == PROP_JSON_ENABLED )
            {
                if( this.jsonEnabled != null || force == true )
                {
                    final Value<Boolean> oldValue = this.jsonEnabled;
                    
                    final String val = resource().binding( PROP_JSON_ENABLED ).read();
                    
                    this.jsonEnabled = new Value<Boolean>( this, PROP_JSON_ENABLED, service( PROP_JSON_ENABLED, ValueNormalizationService.class ).normalize( PROP_JSON_ENABLED.encodeKeywords( val ) ) );
                    this.jsonEnabled.init();
                    
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_JSON_ENABLED );
                    
                    if( oldValue != null )
                    {
                        if( this.jsonEnabled.equals( oldValue ) )
                        {
                            this.jsonEnabled = oldValue;
                        }
                        
                        if( this.jsonEnabled != oldValue || propertyEnabledStatusChanged )
                        {
                            notifyPropertyChangeListeners( PROP_JSON_ENABLED );
                        }
                    }
                }
            }
            else if( property == PROP_LOCALIZED )
            {
                if( this.localized != null || force == true )
                {
                    final Value<Boolean> oldValue = this.localized;
                    
                    final String val = resource().binding( PROP_LOCALIZED ).read();
                    
                    this.localized = new Value<Boolean>( this, PROP_LOCALIZED, service( PROP_LOCALIZED, ValueNormalizationService.class ).normalize( PROP_LOCALIZED.encodeKeywords( val ) ) );
                    this.localized.init();
                    
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_LOCALIZED );
                    
                    if( oldValue != null )
                    {
                        if( this.localized.equals( oldValue ) )
                        {
                            this.localized = oldValue;
                        }
                        
                        if( this.localized != oldValue || propertyEnabledStatusChanged )
                        {
                            notifyPropertyChangeListeners( PROP_LOCALIZED );
                        }
                    }
                }
            }
            else if( property == PROP_MAPPING_KEY )
            {
                if( this.mappingKey != null || force == true )
                {
                    final Value<String> oldValue = this.mappingKey;
                    
                    final String val = resource().binding( PROP_MAPPING_KEY ).read();
                    
                    this.mappingKey = new Value<String>( this, PROP_MAPPING_KEY, service( PROP_MAPPING_KEY, ValueNormalizationService.class ).normalize( PROP_MAPPING_KEY.encodeKeywords( val ) ) );
                    this.mappingKey.init();
                    
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_MAPPING_KEY );
                    
                    if( oldValue != null )
                    {
                        if( this.mappingKey.equals( oldValue ) )
                        {
                            this.mappingKey = oldValue;
                        }
                        
                        if( this.mappingKey != oldValue || propertyEnabledStatusChanged )
                        {
                            notifyPropertyChangeListeners( PROP_MAPPING_KEY );
                        }
                    }
                }
            }
            else if( property == PROP_MAPPING_TABLE )
            {
                if( this.mappingTable != null || force == true )
                {
                    final Value<String> oldValue = this.mappingTable;
                    
                    final String val = resource().binding( PROP_MAPPING_TABLE ).read();
                    
                    this.mappingTable = new Value<String>( this, PROP_MAPPING_TABLE, service( PROP_MAPPING_TABLE, ValueNormalizationService.class ).normalize( PROP_MAPPING_TABLE.encodeKeywords( val ) ) );
                    this.mappingTable.init();
                    
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_MAPPING_TABLE );
                    
                    if( oldValue != null )
                    {
                        if( this.mappingTable.equals( oldValue ) )
                        {
                            this.mappingTable = oldValue;
                        }
                        
                        if( this.mappingTable != oldValue || propertyEnabledStatusChanged )
                        {
                            notifyPropertyChangeListeners( PROP_MAPPING_TABLE );
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
            else if( property == PROP_PRIMARY )
            {
                if( this.primary != null || force == true )
                {
                    final Value<Boolean> oldValue = this.primary;
                    
                    final String val = resource().binding( PROP_PRIMARY ).read();
                    
                    this.primary = new Value<Boolean>( this, PROP_PRIMARY, service( PROP_PRIMARY, ValueNormalizationService.class ).normalize( PROP_PRIMARY.encodeKeywords( val ) ) );
                    this.primary.init();
                    
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_PRIMARY );
                    
                    if( oldValue != null )
                    {
                        if( this.primary.equals( oldValue ) )
                        {
                            this.primary = oldValue;
                        }
                        
                        if( this.primary != oldValue || propertyEnabledStatusChanged )
                        {
                            notifyPropertyChangeListeners( PROP_PRIMARY );
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
        }
    }
    
    public Object read( ModelProperty property )
    {
        property = property.refine( this );
        
        if( property == PROP_CONVERT_NULL )
        {
            return isConvertNull();
        }
        else if( property == PROP_DB_NAME )
        {
            return getDbName();
        }
        else if( property == PROP_ENTITY )
        {
            return getEntity();
        }
        else if( property == PROP_FILTER_PRIMARY )
        {
            return isFilterPrimary();
        }
        else if( property == PROP_ID_PARAM )
        {
            return getIdParam();
        }
        else if( property == PROP_ID_TYPE )
        {
            return getIdType();
        }
        else if( property == PROP_JSON_ENABLED )
        {
            return isJsonEnabled();
        }
        else if( property == PROP_LOCALIZED )
        {
            return isLocalized();
        }
        else if( property == PROP_MAPPING_KEY )
        {
            return getMappingKey();
        }
        else if( property == PROP_MAPPING_TABLE )
        {
            return getMappingTable();
        }
        else if( property == PROP_NAME )
        {
            return getName();
        }
        else if( property == PROP_PRIMARY )
        {
            return isPrimary();
        }
        else if( property == PROP_TYPE )
        {
            return getType();
        }
        
        return super.read( property );
    }
    
    public void write( ValueProperty property, final Object value )
    {
        property = (ValueProperty) property.refine( this );
        
        if( property == PROP_CONVERT_NULL )
        {
            if( ! ( value instanceof String ) )
            {
                setConvertNull( (Boolean) value );
            }
            else
            {
                setConvertNull( (String) value );
            }
            
            return;
        }
        else if( property == PROP_DB_NAME )
        {
            setDbName( (String) value );
            return;
        }
        else if( property == PROP_ENTITY )
        {
            setEntity( (String) value );
            return;
        }
        else if( property == PROP_FILTER_PRIMARY )
        {
            if( ! ( value instanceof String ) )
            {
                setFilterPrimary( (Boolean) value );
            }
            else
            {
                setFilterPrimary( (String) value );
            }
            
            return;
        }
        else if( property == PROP_ID_PARAM )
        {
            setIdParam( (String) value );
            return;
        }
        else if( property == PROP_ID_TYPE )
        {
            setIdType( (String) value );
            return;
        }
        else if( property == PROP_JSON_ENABLED )
        {
            if( ! ( value instanceof String ) )
            {
                setJsonEnabled( (Boolean) value );
            }
            else
            {
                setJsonEnabled( (String) value );
            }
            
            return;
        }
        else if( property == PROP_LOCALIZED )
        {
            if( ! ( value instanceof String ) )
            {
                setLocalized( (Boolean) value );
            }
            else
            {
                setLocalized( (String) value );
            }
            
            return;
        }
        else if( property == PROP_MAPPING_KEY )
        {
            setMappingKey( (String) value );
            return;
        }
        else if( property == PROP_MAPPING_TABLE )
        {
            setMappingTable( (String) value );
            return;
        }
        else if( property == PROP_NAME )
        {
            setName( (String) value );
            return;
        }
        else if( property == PROP_PRIMARY )
        {
            if( ! ( value instanceof String ) )
            {
                setPrimary( (Boolean) value );
            }
            else
            {
                setPrimary( (String) value );
            }
            
            return;
        }
        else if( property == PROP_TYPE )
        {
            setType( (String) value );
            return;
        }
        
        super.write( property, value );
    }
    
}
