/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 *******************************************************************************/
package com.liferay.ide.service.core.model;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementHandle;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementProperty;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Since;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Documentation;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlElementBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

@Image(path = "images/Entity_16x16.gif")
public interface Entity extends Element
{
    ElementType TYPE = new ElementType( Entity.class );

    // *** Name ***

    @XmlBinding( path = "@name" )
    @Label( standard = "&name" )
    @Required
    ValueProperty PROP_NAME = new ValueProperty( TYPE, "Name" ); //$NON-NLS-1$

    Value<String> getName();

    void setName( String value );

    // *** Human Name ***

    @XmlBinding( path = "@human-name" )
    @Label( standard = "&human name" )
    ValueProperty PROP_HUMAN_NAME = new ValueProperty( TYPE, "HumanName" ); //$NON-NLS-1$

    Value<String> getHumanName();

    void setHumanName( String value );

    // *** Table ***

    @XmlBinding( path = "@table" )
    @Label( standard = "&table" )
    ValueProperty PROP_TABLE = new ValueProperty( TYPE, "Table" ); //$NON-NLS-1$

    Value<String> getTable();

    void setTable( String value );

    // *** UUID ***

    @Type( base = Boolean.class )
    @XmlBinding( path = "@uuid" )
    @Label( standard = "&uuid" )
    @DefaultValue( text = "false" )
    ValueProperty PROP_UUID = new ValueProperty( TYPE, "Uuid" ); //$NON-NLS-1$

    Value<Boolean> getUuid();

    void setUuid( String value );

    void setUuid( Boolean value );

    // *** UUID Accessor ***

    @Type( base = Boolean.class )
    @XmlBinding( path = "@uuid-accessor" )
    @Label( standard = "&uuid accessor" )
    @DefaultValue( text = "false" )
    @Documentation( content = "If the [b]uuid-accessor[/b] value is true, then the service will generate a UUID column accessor for the service. This accessor will provide a fast and type-safe way to access entity's UUID." )
    @Since( "6.1" )
    ValueProperty PROP_UUID_ACCESSOR = new ValueProperty( TYPE, "UuidAccessor" ); //$NON-NLS-1$

    Value<Boolean> getUuidAccessor();

    void setUuidAccessor( String value );

    void setUuidAccessor( Boolean value );

    // *** LocalService

    @Type( base = Boolean.class )
    @Label( standard = "&local service" )
    @DefaultValue( text = "false" )
    @XmlBinding( path = "@local-service" )
    ValueProperty PROP_LOCAL_SERVICE = new ValueProperty( TYPE, "LocalService" ); //$NON-NLS-1$

    Value<Boolean> isLocalService();

    void setLocalService( String value );

    void setLocalService( Boolean value );

    // *** RemoteService

    @Type( base = Boolean.class )
    @Label( standard = "&remote service" )
    @DefaultValue( text = "true" )
    @XmlBinding( path = "@remote-service" )
    ValueProperty PROP_REMOTE_SERVICE = new ValueProperty( TYPE, "RemoteService" ); //$NON-NLS-1$

    Value<Boolean> isRemoteService();

    void setRemoteService( String value );

    void setRemoteService( Boolean value );

    // *** Persistence Class ***

    @XmlBinding( path = "@persistence-class" )
    @Label( standard = "&persistence class" )
    ValueProperty PROP_PERSISTENCE_CLASS = new ValueProperty( TYPE, "PersistenceClass" ); //$NON-NLS-1$

    Value<String> getPersistenceClass();

    void setPersistenceClass( String value );

    // *** Data Source ***

    @XmlBinding( path = "@data-source" )
    @Label( standard = "&data source" )
    ValueProperty PROP_DATA_SOURCE = new ValueProperty( TYPE, "DataSource" ); //$NON-NLS-1$

    Value<String> getDataSource();

    void setDataSource( String value );

    // *** Session Factory ***

    @XmlBinding( path = "@session-factory" )
    @Label( standard = "&session factory" )
    ValueProperty PROP_SESSION_FACTORY = new ValueProperty( TYPE, "SessionFactory" ); //$NON-NLS-1$

    Value<String> getSessionFactory();

    void setSessionFactory( String value );

    // *** Tx manager ***

    @XmlBinding( path = "@tx-manager" )
    @Label( standard = "tx &manager" )
    ValueProperty PROP_TX_MANAGER = new ValueProperty( TYPE, "TxManager" ); //$NON-NLS-1$

    Value<String> getTxManager();

    void setTxManager( String value );

    // *** Cache Enabled

    @Type( base = Boolean.class )
    @Label( standard = "&cache enabled" )
    @XmlBinding( path = "@cache-enabled" )
    @DefaultValue( text = "true" )
    ValueProperty PROP_CACHE_ENABLED = new ValueProperty( TYPE, "CacheEnabled" ); //$NON-NLS-1$

    Value<Boolean> isCacheEnabled();

    void setCacheEnabled( String value );

    void setCacheEnabled( Boolean value );

    // *** Json Enabled

    @Type( base = Boolean.class )
    @Label( standard = "&JSON enabled" )
    @XmlBinding( path = "@json-enabled" )
    @DefaultValue( text = "true" )
    ValueProperty PROP_JSON_ENABLED = new ValueProperty( TYPE, "JsonEnabled" ); //$NON-NLS-1$

    Value<Boolean> isJsonEnabled();

    void setJsonEnabled( String value );

    void setJsonEnabled( Boolean value );
    @Type( base = Column.class )
    @Label( standard = "column" )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "column", type = Column.class ) )
    ListProperty PROP_COLUMNS = new ListProperty( TYPE, "Columns" ); //$NON-NLS-1$

    ElementList<Column> getColumns();

    @Type( base = Order.class )
    @Label( standard = "order" )
    @XmlElementBinding( mappings = @XmlElementBinding.Mapping( element = "order", type = Order.class ) )
    ElementProperty PROP_ORDER = new ElementProperty( TYPE, "Order" ); //$NON-NLS-1$

    ElementHandle<Order> getOrder();

    // @XmlElementBinding(path = "order")
    // ImpliedElementProperty PROP_ORDER = new ImpliedElementProperty(TYPE, "Order");

    // IOrder getOrder();

    @Type( base = Finder.class )
    @Label( standard = "finder" )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "finder", type = Finder.class ) )
    ListProperty PROP_FINDERS = new ListProperty( TYPE, "Finders" ); //$NON-NLS-1$

    ElementList<Finder> getFinders();

    // *** References ***

    @Type( base = Reference.class )
    @Label( standard = "references" )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "reference", type = Reference.class ) )
    ListProperty PROP_REFERENCES = new ListProperty( TYPE, "References" ); //$NON-NLS-1$

    ElementList<Reference> getReferences();

    // *** TxRequireds ***

    @Type( base = TxRequired.class )
    @Label( standard = "tx requireds" )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "tx-required", type = TxRequired.class ) )
    ListProperty PROP_TX_REQUIREDS = new ListProperty( TYPE, "TxRequireds" ); //$NON-NLS-1$

    ElementList<TxRequired> getTxRequireds();

}
