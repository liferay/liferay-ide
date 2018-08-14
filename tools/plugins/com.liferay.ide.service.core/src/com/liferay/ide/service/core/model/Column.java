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

import com.liferay.ide.service.core.model.internal.ColumnImageService;
import com.liferay.ide.service.core.model.internal.TypePossibleValuesService;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.PossibleValues;
import org.eclipse.sapphire.Since;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Documentation;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 */
@Service(impl = ColumnImageService.class)
public interface Column extends Element {

	public ElementType TYPE = new ElementType(Column.class);

	public Value<Boolean> getAccessor();

	// *** Name ***

	public Value<String> getDbName();

	public Value<String> getEntity();

	public Value<String> getIdParam();

	// *** Db Name ***

	public Value<String> getIdType();

	public Value<String> getMappingKey();

	public Value<String> getMappingTable();

	// *** Type ***

	public Value<String> getName();

	public Value<String> getType();

	public Value<Boolean> isConvertNull();

	// *** Primary ***

	public Value<Boolean> isFilterPrimary();

	public Value<Boolean> isJsonEnabled();

	public Value<Boolean> isLazy();

	public Value<Boolean> isLocalized();

	// *** Filter Primary ***

	public Value<Boolean> isPrimary();

	public void setAccessor(Boolean value);

	public void setAccessor(String value);

	public void setConvertNull(Boolean value);

	// *** Entity ***

	public void setConvertNull(String value);

	public void setDbName(String value);

	public void setEntity(String value);

	// *** Mapping Key ***

	public void setFilterPrimary(Boolean value);

	public void setFilterPrimary(String value);

	public void setIdParam(String value);

	// *** Mapping Table ***

	public void setIdType(String value);

	public void setJsonEnabled(Boolean value);

	public void setJsonEnabled(String value);

	// ** Id Type ***

	public void setLazy(Boolean value);

	public void setLazy(String value);

	public void setLocalized(Boolean value);

	// *** Id Param ***

	public void setLocalized(String value);

	public void setMappingKey(String value);

	public void setMappingTable(String value);

	// *** Convert Null ***

	public void setName(String value);

	public void setPrimary(Boolean value);

	public void setPrimary(String value);

	public void setType(String value);

	@DefaultValue(text = "false")
	@Documentation(
		content = "This [b]accessor[/b] value specifies whether or not to generate an accessor for this column.This accessor will provide a fast and type-safe way to access column value."
	)
	@Label(standard = "&accessor")
	@Since("6.1")
	@Type(base = Boolean.class)
	@XmlBinding(path = "@accessor")
	public ValueProperty PROP_ACCESSOR = new ValueProperty(TYPE, "Accessor");

	@DefaultValue(text = "true")
	@Label(standard = "&convert null")
	@Type(base = Boolean.class)
	@XmlBinding(path = "@convert-null")
	public ValueProperty PROP_CONVERT_NULL = new ValueProperty(TYPE, "ConvertNull");

	@Label(standard = "&db name")
	@XmlBinding(path = "@db-name")
	public ValueProperty PROP_DB_NAME = new ValueProperty(TYPE, "DbName");

	@Label(standard = "&entity")
	@XmlBinding(path = "@entity")
	public ValueProperty PROP_ENTITY = new ValueProperty(TYPE, "Entity");

	// *** Localized ***

	@DefaultValue(text = "false")
	@Label(standard = "&filter primary")
	@Type(base = Boolean.class)
	@XmlBinding(path = "@filter-primary")
	public ValueProperty PROP_FILTER_PRIMARY = new ValueProperty(TYPE, "FilterPrimary");

	@Label(standard = "&id param")
	@XmlBinding(path = "@id-param")
	public ValueProperty PROP_ID_PARAM = new ValueProperty(TYPE, "IdParam");

	@Label(standard = "id type")
	@PossibleValues(
		invalidValueMessage = "{0} is not a valid ID type.", values = {"class", "increment", "identity", "sequence"}
	)
	@XmlBinding(path = "@id-type")
	public ValueProperty PROP_ID_TYPE = new ValueProperty(TYPE, "IdType");

	@DefaultValue(text = "true")
	@Label(standard = "&JSON enabled")
	@Type(base = Boolean.class)
	@XmlBinding(path = "@json-enabled")
	public ValueProperty PROP_JSON_ENABLED = new ValueProperty(TYPE, "JsonEnabled");

	// *** Json Enabled

	@DefaultValue(text = "true")
	@Label(standard = "&lazy")
	@Type(base = Boolean.class)
	@XmlBinding(path = "@lazy")
	public ValueProperty PROP_LAZY = new ValueProperty(TYPE, "Lazy");

	@DefaultValue(text = "false")
	@Label(standard = "&localized")
	@Type(base = Boolean.class)
	@XmlBinding(path = "@localized")
	public ValueProperty PROP_LOCALIZED = new ValueProperty(TYPE, "Localized");

	@Label(standard = "&mapping key")
	@XmlBinding(path = "@mapping-key")
	public ValueProperty PROP_MAPPING_KEY = new ValueProperty(TYPE, "MappingKey");

	@Label(standard = "&mapping table")
	@XmlBinding(path = "@mapping-table")
	public ValueProperty PROP_MAPPING_TABLE = new ValueProperty(TYPE, "MappingTable");

	// *** Accessor ***

	@Label(standard = "&name")
	@Required
	@XmlBinding(path = "@name")
	public ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");

	@DefaultValue(text = "false")
	@Label(standard = "&primary")
	@Type(base = Boolean.class)
	@XmlBinding(path = "@primary")
	public ValueProperty PROP_PRIMARY = new ValueProperty(TYPE, "Primary");

	@Label(standard = "type")
	@Required
	@Service(impl = TypePossibleValuesService.class)
	@XmlBinding(path = "@type")
	public ValueProperty PROP_TYPE = new ValueProperty(TYPE, "Type");

}