/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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
 */

package com.liferay.ide.service.core.model;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementHandle;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementProperty;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Since;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Unique;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Documentation;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlElementBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author Gregory Amerson
 */
@Image(path = "images/Entity_16x16.gif")
public interface Entity extends Element {

	public ElementType TYPE = new ElementType(Entity.class);

	public ElementList<Column> getColumns();

	// *** Name ***

	public Value<String> getDataSource();

	public Value<Boolean> getDeprecated();

	public ElementList<Finder> getFinders();

	// *** Human Name ***

	public Value<String> getHumanName();

	public Value<String> getName();

	public ElementHandle<Order> getOrder();

	// *** Table ***

	public Value<String> getPersistenceClass();

	public ElementList<Reference> getReferences();

	public Value<String> getSessionFactory();

	// *** UUID ***

	public Value<String> getTable();

	public Value<Boolean> getTrashEnabled();

	public Value<String> getTxManager();

	public ElementList<TxRequired> getTxRequireds();

	// *** UUID Accessor ***

	public Value<Boolean> getUuid();

	public Value<Boolean> getUuidAccessor();

	public Value<Boolean> isCacheEnabled();

	public Value<Boolean> isJsonEnabled();

	// *** LocalService ***

	public Value<Boolean> isLocalService();

	public Value<Boolean> isRemoteService();

	public void setCacheEnabled(Boolean value);

	public void setCacheEnabled(String value);

	// *** RemoteService ***

	public void setDataSource(String value);

	public void setDeprecated(Boolean value);

	public void setDeprecated(String value);

	public void setHumanName(String value);

	// *** Persistence Class ***

	public void setJsonEnabled(Boolean value);

	public void setJsonEnabled(String value);

	public void setLocalService(Boolean value);

	// *** Data Source ***

	public void setLocalService(String value);

	public void setName(String value);

	public void setPersistenceClass(String value);

	// *** Session Factory ***

	public void setRemoteService(Boolean value);

	public void setRemoteService(String value);

	public void setSessionFactory(String value);

	// *** Tx manager ***

	public void setTable(String value);

	public void setTrashEnabled(Boolean value);

	public void setTrashEnabled(String value);

	// *** Cache Enabled ***

	public void setTxManager(String value);

	public void setUuid(Boolean value);

	public void setUuid(String value);

	public void setUuidAccessor(Boolean value);

	// *** Json Enabled ***

	public void setUuidAccessor(String value);

	@DefaultValue(text = "true")
	@Label(standard = "&cache enabled")
	@Type(base = Boolean.class)
	@XmlBinding(path = "@cache-enabled")
	public ValueProperty PROP_CACHE_ENABLED = new ValueProperty(TYPE, "CacheEnabled");

	@Label(standard = "column")
	@Type(base = Column.class)
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "column", type = Column.class))
	public ListProperty PROP_COLUMNS = new ListProperty(TYPE, "Columns");

	@Label(standard = "&data source")
	@XmlBinding(path = "@data-source")
	public ValueProperty PROP_DATA_SOURCE = new ValueProperty(TYPE, "DataSource");

	// *** Trash Enabled ***

	@DefaultValue(text = "false")
	@Label(standard = "&deprecated")
	@Since("6.2")
	@Type(base = Boolean.class)
	@XmlBinding(path = "@deprecated")
	public ValueProperty PROP_DEPRECATED = new ValueProperty(TYPE, "Deprecated");

	@Label(standard = "finder")
	@Type(base = Finder.class)
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "finder", type = Finder.class))
	public ListProperty PROP_FINDERS = new ListProperty(TYPE, "Finders");

	@Label(standard = "&human name")
	@XmlBinding(path = "@human-name")
	public ValueProperty PROP_HUMAN_NAME = new ValueProperty(TYPE, "HumanName");

	@DefaultValue(text = "true")
	@Label(standard = "&JSON enabled")
	@Type(base = Boolean.class)
	@XmlBinding(path = "@json-enabled")
	public ValueProperty PROP_JSON_ENABLED = new ValueProperty(TYPE, "JsonEnabled");

	// *** Deprecated ***

	@DefaultValue(text = "false")
	@Label(standard = "&local service")
	@Type(base = Boolean.class)
	@XmlBinding(path = "@local-service")
	public ValueProperty PROP_LOCAL_SERVICE = new ValueProperty(TYPE, "LocalService");

	@Label(standard = "&name")
	@Required
	@Unique
	@XmlBinding(path = "@name")
	public ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");

	@Label(standard = "order")
	@Type(base = Order.class)
	@XmlElementBinding(mappings = @XmlElementBinding.Mapping(element = "order", type = Order.class))
	public ElementProperty PROP_ORDER = new ElementProperty(TYPE, "Order");

	@Label(standard = "&persistence class")
	@XmlBinding(path = "@persistence-class")
	public ValueProperty PROP_PERSISTENCE_CLASS = new ValueProperty(TYPE, "PersistenceClass");

	// *** Columns ***

	@Label(standard = "references")
	@Type(base = Reference.class)
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "reference", type = Reference.class))
	public ListProperty PROP_REFERENCES = new ListProperty(TYPE, "References");

	@DefaultValue(text = "true")
	@Label(standard = "&remote service")
	@Type(base = Boolean.class)
	@XmlBinding(path = "@remote-service")
	public ValueProperty PROP_REMOTE_SERVICE = new ValueProperty(TYPE, "RemoteService");

	@Label(standard = "&session factory")
	@XmlBinding(path = "@session-factory")
	public ValueProperty PROP_SESSION_FACTORY = new ValueProperty(TYPE, "SessionFactory");

	@Label(standard = "&table")
	@XmlBinding(path = "@table")
	public ValueProperty PROP_TABLE = new ValueProperty(TYPE, "Table");

	// IOrder getOrder();

	@DefaultValue(text = "false")
	@Label(standard = "&Trash Enabled")
	@Since("6.2")
	@Type(base = Boolean.class)
	@XmlBinding(path = "@trash-enabled")
	public ValueProperty PROP_TRASH_ENABLED = new ValueProperty(TYPE, "TrashEnabled");

	@Label(standard = "tx &manager")
	@XmlBinding(path = "@tx-manager")
	public ValueProperty PROP_TX_MANAGER = new ValueProperty(TYPE, "TxManager");

	// *** References ***

	@Label(standard = "tx requireds")
	@Type(base = TxRequired.class)
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "tx-required", type = TxRequired.class))
	public ListProperty PROP_TX_REQUIREDS = new ListProperty(TYPE, "TxRequireds");

	@DefaultValue(text = "false")
	@Label(standard = "&uuid")
	@Type(base = Boolean.class)
	@XmlBinding(path = "@uuid")
	public ValueProperty PROP_UUID = new ValueProperty(TYPE, "Uuid");

	// *** TxRequireds ***

	@DefaultValue(text = "false")
	@Documentation(
		content = "If the [b]uuid-accessor[/b] value is true, then the service will generate a UUID column accessor for the service. This accessor will provide a fast and type-safe way to access entity's UUID."
	)
	@Label(standard = "&uuid accessor")
	@Since("6.1")
	@Type(base = Boolean.class)
	@XmlBinding(path = "@uuid-accessor")
	public ValueProperty PROP_UUID_ACCESSOR = new ValueProperty(TYPE, "UuidAccessor");

}