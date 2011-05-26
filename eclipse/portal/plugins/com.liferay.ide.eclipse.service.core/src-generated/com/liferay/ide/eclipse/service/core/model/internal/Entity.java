package com.liferay.ide.eclipse.service.core.model.internal;

import com.liferay.ide.eclipse.service.core.model.IColumn;
import com.liferay.ide.eclipse.service.core.model.IEntity;
import com.liferay.ide.eclipse.service.core.model.IFinder;
import com.liferay.ide.eclipse.service.core.model.IOrder;
import com.liferay.ide.eclipse.service.core.model.IReference;
import com.liferay.ide.eclipse.service.core.model.IRelationship;
import com.liferay.ide.eclipse.service.core.model.ITxRequired;
import org.eclipse.sapphire.modeling.IModelParticle;
import org.eclipse.sapphire.modeling.ListBindingImpl;
import org.eclipse.sapphire.modeling.ModelElement;
import org.eclipse.sapphire.modeling.ModelElementHandle;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.Resource;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueNormalizationService;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.serialization.ValueSerializationService;

public final class Entity
    
    extends ModelElement
    implements IEntity
    
{
    private Value<Boolean> cacheEnabled;
    private ModelElementList<IColumn> columns;
    private Value<String> dataSource;
    private ModelElementList<IFinder> finders;
    private Value<String> humanName;
    private Value<Boolean> jsonEnabled;
    private Value<Boolean> localService;
    private Value<String> name;
    private ModelElementHandle<IOrder> order;
    private Value<String> persistenceClass;
    private ModelElementList<IReference> references;
    private ModelElementList<IRelationship> relationships;
    private Value<Boolean> remoteService;
    private Value<String> sessionFactory;
    private Value<String> table;
    private Value<String> txManager;
    private ModelElementList<ITxRequired> txRequireds;
    private Value<Boolean> uuid;
    
    public Entity( final IModelParticle parent, final ModelProperty parentProperty, final Resource resource )
    {
        super( TYPE, parent, parentProperty, resource );
    }
    
    public Entity( final Resource resource )
    {
        super( TYPE, null, null, resource );
    }
    
    public Value<Boolean> isCacheEnabled()
    {
        synchronized( root() )
        {
            if( this.cacheEnabled == null )
            {
                refresh( PROP_CACHE_ENABLED, true );
            }
            
            return this.cacheEnabled;
        }
    }
    
    public void setCacheEnabled( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( "" ) )
            {
                value = null;
            }
            
            value = PROP_CACHE_ENABLED.decodeKeywords( value );
            value = service( PROP_CACHE_ENABLED, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_CACHE_ENABLED, true );
            
            if( ! equal( this.cacheEnabled.getText( false ), value ) )
            {
                resource().binding( PROP_CACHE_ENABLED ).write( value );
                refresh( PROP_CACHE_ENABLED, false );
            }
        }
    }
    
    public void setCacheEnabled( final Boolean value )
    {
        setCacheEnabled( value != null ? service( PROP_CACHE_ENABLED, ValueSerializationService.class ).encode( value ) : null );
    }
    
    public ModelElementList<IColumn> getColumns()
    {
        synchronized( root() )
        {
            if( this.columns == null )
            {
                refresh( PROP_COLUMNS, true );
            }
            
            return this.columns;
        }
    }
    
    public Value<String> getDataSource()
    {
        synchronized( root() )
        {
            if( this.dataSource == null )
            {
                refresh( PROP_DATA_SOURCE, true );
            }
            
            return this.dataSource;
        }
    }
    
    public void setDataSource( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( "" ) )
            {
                value = null;
            }
            
            value = PROP_DATA_SOURCE.decodeKeywords( value );
            value = service( PROP_DATA_SOURCE, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_DATA_SOURCE, true );
            
            if( ! equal( this.dataSource.getText( false ), value ) )
            {
                resource().binding( PROP_DATA_SOURCE ).write( value );
                refresh( PROP_DATA_SOURCE, false );
            }
        }
    }
    
    public ModelElementList<IFinder> getFinders()
    {
        synchronized( root() )
        {
            if( this.finders == null )
            {
                refresh( PROP_FINDERS, true );
            }
            
            return this.finders;
        }
    }
    
    public Value<String> getHumanName()
    {
        synchronized( root() )
        {
            if( this.humanName == null )
            {
                refresh( PROP_HUMAN_NAME, true );
            }
            
            return this.humanName;
        }
    }
    
    public void setHumanName( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( "" ) )
            {
                value = null;
            }
            
            value = PROP_HUMAN_NAME.decodeKeywords( value );
            value = service( PROP_HUMAN_NAME, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_HUMAN_NAME, true );
            
            if( ! equal( this.humanName.getText( false ), value ) )
            {
                resource().binding( PROP_HUMAN_NAME ).write( value );
                refresh( PROP_HUMAN_NAME, false );
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
    
    public Value<Boolean> isLocalService()
    {
        synchronized( root() )
        {
            if( this.localService == null )
            {
                refresh( PROP_LOCAL_SERVICE, true );
            }
            
            return this.localService;
        }
    }
    
    public void setLocalService( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( "" ) )
            {
                value = null;
            }
            
            value = PROP_LOCAL_SERVICE.decodeKeywords( value );
            value = service( PROP_LOCAL_SERVICE, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_LOCAL_SERVICE, true );
            
            if( ! equal( this.localService.getText( false ), value ) )
            {
                resource().binding( PROP_LOCAL_SERVICE ).write( value );
                refresh( PROP_LOCAL_SERVICE, false );
            }
        }
    }
    
    public void setLocalService( final Boolean value )
    {
        setLocalService( value != null ? service( PROP_LOCAL_SERVICE, ValueSerializationService.class ).encode( value ) : null );
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
    
    public ModelElementHandle<IOrder> getOrder()
    {
        synchronized( root() )
        {
            if( this.order == null )
            {
                refresh( PROP_ORDER, true );
            }
            
            return this.order;
        }
    }
    
    public Value<String> getPersistenceClass()
    {
        synchronized( root() )
        {
            if( this.persistenceClass == null )
            {
                refresh( PROP_PERSISTENCE_CLASS, true );
            }
            
            return this.persistenceClass;
        }
    }
    
    public void setPersistenceClass( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( "" ) )
            {
                value = null;
            }
            
            value = PROP_PERSISTENCE_CLASS.decodeKeywords( value );
            value = service( PROP_PERSISTENCE_CLASS, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_PERSISTENCE_CLASS, true );
            
            if( ! equal( this.persistenceClass.getText( false ), value ) )
            {
                resource().binding( PROP_PERSISTENCE_CLASS ).write( value );
                refresh( PROP_PERSISTENCE_CLASS, false );
            }
        }
    }
    
    public ModelElementList<IReference> getReferences()
    {
        synchronized( root() )
        {
            if( this.references == null )
            {
                refresh( PROP_REFERENCES, true );
            }
            
            return this.references;
        }
    }
    
    public ModelElementList<IRelationship> getRelationships()
    {
        synchronized( root() )
        {
            if( this.relationships == null )
            {
                refresh( PROP_RELATIONSHIPS, true );
            }
            
            return this.relationships;
        }
    }
    
    public Value<Boolean> isRemoteService()
    {
        synchronized( root() )
        {
            if( this.remoteService == null )
            {
                refresh( PROP_REMOTE_SERVICE, true );
            }
            
            return this.remoteService;
        }
    }
    
    public void setRemoteService( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( "" ) )
            {
                value = null;
            }
            
            value = PROP_REMOTE_SERVICE.decodeKeywords( value );
            value = service( PROP_REMOTE_SERVICE, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_REMOTE_SERVICE, true );
            
            if( ! equal( this.remoteService.getText( false ), value ) )
            {
                resource().binding( PROP_REMOTE_SERVICE ).write( value );
                refresh( PROP_REMOTE_SERVICE, false );
            }
        }
    }
    
    public void setRemoteService( final Boolean value )
    {
        setRemoteService( value != null ? service( PROP_REMOTE_SERVICE, ValueSerializationService.class ).encode( value ) : null );
    }
    
    public Value<String> getSessionFactory()
    {
        synchronized( root() )
        {
            if( this.sessionFactory == null )
            {
                refresh( PROP_SESSION_FACTORY, true );
            }
            
            return this.sessionFactory;
        }
    }
    
    public void setSessionFactory( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( "" ) )
            {
                value = null;
            }
            
            value = PROP_SESSION_FACTORY.decodeKeywords( value );
            value = service( PROP_SESSION_FACTORY, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_SESSION_FACTORY, true );
            
            if( ! equal( this.sessionFactory.getText( false ), value ) )
            {
                resource().binding( PROP_SESSION_FACTORY ).write( value );
                refresh( PROP_SESSION_FACTORY, false );
            }
        }
    }
    
    public Value<String> getTable()
    {
        synchronized( root() )
        {
            if( this.table == null )
            {
                refresh( PROP_TABLE, true );
            }
            
            return this.table;
        }
    }
    
    public void setTable( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( "" ) )
            {
                value = null;
            }
            
            value = PROP_TABLE.decodeKeywords( value );
            value = service( PROP_TABLE, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_TABLE, true );
            
            if( ! equal( this.table.getText( false ), value ) )
            {
                resource().binding( PROP_TABLE ).write( value );
                refresh( PROP_TABLE, false );
            }
        }
    }
    
    public Value<String> getTxManager()
    {
        synchronized( root() )
        {
            if( this.txManager == null )
            {
                refresh( PROP_TX_MANAGER, true );
            }
            
            return this.txManager;
        }
    }
    
    public void setTxManager( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( "" ) )
            {
                value = null;
            }
            
            value = PROP_TX_MANAGER.decodeKeywords( value );
            value = service( PROP_TX_MANAGER, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_TX_MANAGER, true );
            
            if( ! equal( this.txManager.getText( false ), value ) )
            {
                resource().binding( PROP_TX_MANAGER ).write( value );
                refresh( PROP_TX_MANAGER, false );
            }
        }
    }
    
    public ModelElementList<ITxRequired> getTxRequireds()
    {
        synchronized( root() )
        {
            if( this.txRequireds == null )
            {
                refresh( PROP_TX_REQUIREDS, true );
            }
            
            return this.txRequireds;
        }
    }
    
    public Value<Boolean> getUuid()
    {
        synchronized( root() )
        {
            if( this.uuid == null )
            {
                refresh( PROP_UUID, true );
            }
            
            return this.uuid;
        }
    }
    
    public void setUuid( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( "" ) )
            {
                value = null;
            }
            
            value = PROP_UUID.decodeKeywords( value );
            value = service( PROP_UUID, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_UUID, true );
            
            if( ! equal( this.uuid.getText( false ), value ) )
            {
                resource().binding( PROP_UUID ).write( value );
                refresh( PROP_UUID, false );
            }
        }
    }
    
    public void setUuid( final Boolean value )
    {
        setUuid( value != null ? service( PROP_UUID, ValueSerializationService.class ).encode( value ) : null );
    }
    
    protected void refreshProperty( ModelProperty property, final boolean force )
    {
        synchronized( root() )
        {
            property = property.refine( this );
            
            if( property == PROP_CACHE_ENABLED )
            {
                if( this.cacheEnabled != null || force == true )
                {
                    final Value<Boolean> oldValue = this.cacheEnabled;
                    
                    final String val = resource().binding( PROP_CACHE_ENABLED ).read();
                    
                    this.cacheEnabled = new Value<Boolean>( this, PROP_CACHE_ENABLED, service( PROP_CACHE_ENABLED, ValueNormalizationService.class ).normalize( PROP_CACHE_ENABLED.encodeKeywords( val ) ) );
                    this.cacheEnabled.init();
                    
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_CACHE_ENABLED );
                    
                    if( oldValue != null )
                    {
                        if( this.cacheEnabled.equals( oldValue ) )
                        {
                            this.cacheEnabled = oldValue;
                        }
                        
                        if( this.cacheEnabled != oldValue || propertyEnabledStatusChanged )
                        {
                            notifyPropertyChangeListeners( PROP_CACHE_ENABLED );
                        }
                    }
                }
            }
            else if( property == PROP_COLUMNS )
            {
                if( this.columns == null )
                {
                    if( force == true )
                    {
                        this.columns = new ModelElementList<IColumn>( this, PROP_COLUMNS );
                        final ListBindingImpl binding = resource().binding( PROP_COLUMNS );
                        this.columns.init( binding );
                        refreshPropertyEnabledStatus( PROP_COLUMNS );
                    }
                }
                else
                {
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_COLUMNS );
                    final boolean notified = this.columns.refresh();
                    
                    if( ! notified && propertyEnabledStatusChanged )
                    {
                        notifyPropertyChangeListeners( PROP_COLUMNS );
                    }
                }
            }
            else if( property == PROP_DATA_SOURCE )
            {
                if( this.dataSource != null || force == true )
                {
                    final Value<String> oldValue = this.dataSource;
                    
                    final String val = resource().binding( PROP_DATA_SOURCE ).read();
                    
                    this.dataSource = new Value<String>( this, PROP_DATA_SOURCE, service( PROP_DATA_SOURCE, ValueNormalizationService.class ).normalize( PROP_DATA_SOURCE.encodeKeywords( val ) ) );
                    this.dataSource.init();
                    
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_DATA_SOURCE );
                    
                    if( oldValue != null )
                    {
                        if( this.dataSource.equals( oldValue ) )
                        {
                            this.dataSource = oldValue;
                        }
                        
                        if( this.dataSource != oldValue || propertyEnabledStatusChanged )
                        {
                            notifyPropertyChangeListeners( PROP_DATA_SOURCE );
                        }
                    }
                }
            }
            else if( property == PROP_FINDERS )
            {
                if( this.finders == null )
                {
                    if( force == true )
                    {
                        this.finders = new ModelElementList<IFinder>( this, PROP_FINDERS );
                        final ListBindingImpl binding = resource().binding( PROP_FINDERS );
                        this.finders.init( binding );
                        refreshPropertyEnabledStatus( PROP_FINDERS );
                    }
                }
                else
                {
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_FINDERS );
                    final boolean notified = this.finders.refresh();
                    
                    if( ! notified && propertyEnabledStatusChanged )
                    {
                        notifyPropertyChangeListeners( PROP_FINDERS );
                    }
                }
            }
            else if( property == PROP_HUMAN_NAME )
            {
                if( this.humanName != null || force == true )
                {
                    final Value<String> oldValue = this.humanName;
                    
                    final String val = resource().binding( PROP_HUMAN_NAME ).read();
                    
                    this.humanName = new Value<String>( this, PROP_HUMAN_NAME, service( PROP_HUMAN_NAME, ValueNormalizationService.class ).normalize( PROP_HUMAN_NAME.encodeKeywords( val ) ) );
                    this.humanName.init();
                    
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_HUMAN_NAME );
                    
                    if( oldValue != null )
                    {
                        if( this.humanName.equals( oldValue ) )
                        {
                            this.humanName = oldValue;
                        }
                        
                        if( this.humanName != oldValue || propertyEnabledStatusChanged )
                        {
                            notifyPropertyChangeListeners( PROP_HUMAN_NAME );
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
            else if( property == PROP_LOCAL_SERVICE )
            {
                if( this.localService != null || force == true )
                {
                    final Value<Boolean> oldValue = this.localService;
                    
                    final String val = resource().binding( PROP_LOCAL_SERVICE ).read();
                    
                    this.localService = new Value<Boolean>( this, PROP_LOCAL_SERVICE, service( PROP_LOCAL_SERVICE, ValueNormalizationService.class ).normalize( PROP_LOCAL_SERVICE.encodeKeywords( val ) ) );
                    this.localService.init();
                    
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_LOCAL_SERVICE );
                    
                    if( oldValue != null )
                    {
                        if( this.localService.equals( oldValue ) )
                        {
                            this.localService = oldValue;
                        }
                        
                        if( this.localService != oldValue || propertyEnabledStatusChanged )
                        {
                            notifyPropertyChangeListeners( PROP_LOCAL_SERVICE );
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
            else if( property == PROP_ORDER )
            {
                if( this.order == null )
                {
                    if( force == true )
                    {
                        this.order = new ModelElementHandle<IOrder>( this, PROP_ORDER );
                        this.order.init();
                        refreshPropertyEnabledStatus( PROP_ORDER );
                    }
                }
                else
                {
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_ORDER );
                    final boolean notified = this.order.refresh();
                    
                    if( ! notified && propertyEnabledStatusChanged )
                    {
                        notifyPropertyChangeListeners( PROP_ORDER );
                    }
                }
            }
            else if( property == PROP_PERSISTENCE_CLASS )
            {
                if( this.persistenceClass != null || force == true )
                {
                    final Value<String> oldValue = this.persistenceClass;
                    
                    final String val = resource().binding( PROP_PERSISTENCE_CLASS ).read();
                    
                    this.persistenceClass = new Value<String>( this, PROP_PERSISTENCE_CLASS, service( PROP_PERSISTENCE_CLASS, ValueNormalizationService.class ).normalize( PROP_PERSISTENCE_CLASS.encodeKeywords( val ) ) );
                    this.persistenceClass.init();
                    
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_PERSISTENCE_CLASS );
                    
                    if( oldValue != null )
                    {
                        if( this.persistenceClass.equals( oldValue ) )
                        {
                            this.persistenceClass = oldValue;
                        }
                        
                        if( this.persistenceClass != oldValue || propertyEnabledStatusChanged )
                        {
                            notifyPropertyChangeListeners( PROP_PERSISTENCE_CLASS );
                        }
                    }
                }
            }
            else if( property == PROP_REFERENCES )
            {
                if( this.references == null )
                {
                    if( force == true )
                    {
                        this.references = new ModelElementList<IReference>( this, PROP_REFERENCES );
                        final ListBindingImpl binding = resource().binding( PROP_REFERENCES );
                        this.references.init( binding );
                        refreshPropertyEnabledStatus( PROP_REFERENCES );
                    }
                }
                else
                {
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_REFERENCES );
                    final boolean notified = this.references.refresh();
                    
                    if( ! notified && propertyEnabledStatusChanged )
                    {
                        notifyPropertyChangeListeners( PROP_REFERENCES );
                    }
                }
            }
            else if( property == PROP_RELATIONSHIPS )
            {
                if( this.relationships == null )
                {
                    if( force == true )
                    {
                        this.relationships = new ModelElementList<IRelationship>( this, PROP_RELATIONSHIPS );
                        final ListBindingImpl binding = resource().binding( PROP_RELATIONSHIPS );
                        this.relationships.init( binding );
                        refreshPropertyEnabledStatus( PROP_RELATIONSHIPS );
                    }
                }
                else
                {
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_RELATIONSHIPS );
                    final boolean notified = this.relationships.refresh();
                    
                    if( ! notified && propertyEnabledStatusChanged )
                    {
                        notifyPropertyChangeListeners( PROP_RELATIONSHIPS );
                    }
                }
            }
            else if( property == PROP_REMOTE_SERVICE )
            {
                if( this.remoteService != null || force == true )
                {
                    final Value<Boolean> oldValue = this.remoteService;
                    
                    final String val = resource().binding( PROP_REMOTE_SERVICE ).read();
                    
                    this.remoteService = new Value<Boolean>( this, PROP_REMOTE_SERVICE, service( PROP_REMOTE_SERVICE, ValueNormalizationService.class ).normalize( PROP_REMOTE_SERVICE.encodeKeywords( val ) ) );
                    this.remoteService.init();
                    
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_REMOTE_SERVICE );
                    
                    if( oldValue != null )
                    {
                        if( this.remoteService.equals( oldValue ) )
                        {
                            this.remoteService = oldValue;
                        }
                        
                        if( this.remoteService != oldValue || propertyEnabledStatusChanged )
                        {
                            notifyPropertyChangeListeners( PROP_REMOTE_SERVICE );
                        }
                    }
                }
            }
            else if( property == PROP_SESSION_FACTORY )
            {
                if( this.sessionFactory != null || force == true )
                {
                    final Value<String> oldValue = this.sessionFactory;
                    
                    final String val = resource().binding( PROP_SESSION_FACTORY ).read();
                    
                    this.sessionFactory = new Value<String>( this, PROP_SESSION_FACTORY, service( PROP_SESSION_FACTORY, ValueNormalizationService.class ).normalize( PROP_SESSION_FACTORY.encodeKeywords( val ) ) );
                    this.sessionFactory.init();
                    
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_SESSION_FACTORY );
                    
                    if( oldValue != null )
                    {
                        if( this.sessionFactory.equals( oldValue ) )
                        {
                            this.sessionFactory = oldValue;
                        }
                        
                        if( this.sessionFactory != oldValue || propertyEnabledStatusChanged )
                        {
                            notifyPropertyChangeListeners( PROP_SESSION_FACTORY );
                        }
                    }
                }
            }
            else if( property == PROP_TABLE )
            {
                if( this.table != null || force == true )
                {
                    final Value<String> oldValue = this.table;
                    
                    final String val = resource().binding( PROP_TABLE ).read();
                    
                    this.table = new Value<String>( this, PROP_TABLE, service( PROP_TABLE, ValueNormalizationService.class ).normalize( PROP_TABLE.encodeKeywords( val ) ) );
                    this.table.init();
                    
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_TABLE );
                    
                    if( oldValue != null )
                    {
                        if( this.table.equals( oldValue ) )
                        {
                            this.table = oldValue;
                        }
                        
                        if( this.table != oldValue || propertyEnabledStatusChanged )
                        {
                            notifyPropertyChangeListeners( PROP_TABLE );
                        }
                    }
                }
            }
            else if( property == PROP_TX_MANAGER )
            {
                if( this.txManager != null || force == true )
                {
                    final Value<String> oldValue = this.txManager;
                    
                    final String val = resource().binding( PROP_TX_MANAGER ).read();
                    
                    this.txManager = new Value<String>( this, PROP_TX_MANAGER, service( PROP_TX_MANAGER, ValueNormalizationService.class ).normalize( PROP_TX_MANAGER.encodeKeywords( val ) ) );
                    this.txManager.init();
                    
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_TX_MANAGER );
                    
                    if( oldValue != null )
                    {
                        if( this.txManager.equals( oldValue ) )
                        {
                            this.txManager = oldValue;
                        }
                        
                        if( this.txManager != oldValue || propertyEnabledStatusChanged )
                        {
                            notifyPropertyChangeListeners( PROP_TX_MANAGER );
                        }
                    }
                }
            }
            else if( property == PROP_TX_REQUIREDS )
            {
                if( this.txRequireds == null )
                {
                    if( force == true )
                    {
                        this.txRequireds = new ModelElementList<ITxRequired>( this, PROP_TX_REQUIREDS );
                        final ListBindingImpl binding = resource().binding( PROP_TX_REQUIREDS );
                        this.txRequireds.init( binding );
                        refreshPropertyEnabledStatus( PROP_TX_REQUIREDS );
                    }
                }
                else
                {
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_TX_REQUIREDS );
                    final boolean notified = this.txRequireds.refresh();
                    
                    if( ! notified && propertyEnabledStatusChanged )
                    {
                        notifyPropertyChangeListeners( PROP_TX_REQUIREDS );
                    }
                }
            }
            else if( property == PROP_UUID )
            {
                if( this.uuid != null || force == true )
                {
                    final Value<Boolean> oldValue = this.uuid;
                    
                    final String val = resource().binding( PROP_UUID ).read();
                    
                    this.uuid = new Value<Boolean>( this, PROP_UUID, service( PROP_UUID, ValueNormalizationService.class ).normalize( PROP_UUID.encodeKeywords( val ) ) );
                    this.uuid.init();
                    
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_UUID );
                    
                    if( oldValue != null )
                    {
                        if( this.uuid.equals( oldValue ) )
                        {
                            this.uuid = oldValue;
                        }
                        
                        if( this.uuid != oldValue || propertyEnabledStatusChanged )
                        {
                            notifyPropertyChangeListeners( PROP_UUID );
                        }
                    }
                }
            }
        }
    }
    
    public Object read( ModelProperty property )
    {
        property = property.refine( this );
        
        if( property == PROP_CACHE_ENABLED )
        {
            return isCacheEnabled();
        }
        else if( property == PROP_COLUMNS )
        {
            return getColumns();
        }
        else if( property == PROP_DATA_SOURCE )
        {
            return getDataSource();
        }
        else if( property == PROP_FINDERS )
        {
            return getFinders();
        }
        else if( property == PROP_HUMAN_NAME )
        {
            return getHumanName();
        }
        else if( property == PROP_JSON_ENABLED )
        {
            return isJsonEnabled();
        }
        else if( property == PROP_LOCAL_SERVICE )
        {
            return isLocalService();
        }
        else if( property == PROP_NAME )
        {
            return getName();
        }
        else if( property == PROP_ORDER )
        {
            return getOrder();
        }
        else if( property == PROP_PERSISTENCE_CLASS )
        {
            return getPersistenceClass();
        }
        else if( property == PROP_REFERENCES )
        {
            return getReferences();
        }
        else if( property == PROP_RELATIONSHIPS )
        {
            return getRelationships();
        }
        else if( property == PROP_REMOTE_SERVICE )
        {
            return isRemoteService();
        }
        else if( property == PROP_SESSION_FACTORY )
        {
            return getSessionFactory();
        }
        else if( property == PROP_TABLE )
        {
            return getTable();
        }
        else if( property == PROP_TX_MANAGER )
        {
            return getTxManager();
        }
        else if( property == PROP_TX_REQUIREDS )
        {
            return getTxRequireds();
        }
        else if( property == PROP_UUID )
        {
            return getUuid();
        }
        
        return super.read( property );
    }
    
    public void write( ValueProperty property, final Object value )
    {
        property = (ValueProperty) property.refine( this );
        
        if( property == PROP_CACHE_ENABLED )
        {
            if( ! ( value instanceof String ) )
            {
                setCacheEnabled( (Boolean) value );
            }
            else
            {
                setCacheEnabled( (String) value );
            }
            
            return;
        }
        else if( property == PROP_DATA_SOURCE )
        {
            setDataSource( (String) value );
            return;
        }
        else if( property == PROP_HUMAN_NAME )
        {
            setHumanName( (String) value );
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
        else if( property == PROP_LOCAL_SERVICE )
        {
            if( ! ( value instanceof String ) )
            {
                setLocalService( (Boolean) value );
            }
            else
            {
                setLocalService( (String) value );
            }
            
            return;
        }
        else if( property == PROP_NAME )
        {
            setName( (String) value );
            return;
        }
        else if( property == PROP_PERSISTENCE_CLASS )
        {
            setPersistenceClass( (String) value );
            return;
        }
        else if( property == PROP_REMOTE_SERVICE )
        {
            if( ! ( value instanceof String ) )
            {
                setRemoteService( (Boolean) value );
            }
            else
            {
                setRemoteService( (String) value );
            }
            
            return;
        }
        else if( property == PROP_SESSION_FACTORY )
        {
            setSessionFactory( (String) value );
            return;
        }
        else if( property == PROP_TABLE )
        {
            setTable( (String) value );
            return;
        }
        else if( property == PROP_TX_MANAGER )
        {
            setTxManager( (String) value );
            return;
        }
        else if( property == PROP_UUID )
        {
            if( ! ( value instanceof String ) )
            {
                setUuid( (Boolean) value );
            }
            else
            {
                setUuid( (String) value );
            }
            
            return;
        }
        
        super.write( property, value );
    }
    
}
