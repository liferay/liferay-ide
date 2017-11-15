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
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Length;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author Gregory Amerson
 */
@Image(path = "images/finder_16x16.gif")
public interface Finder extends Element {

	public ElementType TYPE = new ElementType(Finder.class);

	// *** Name ***

	public ElementList<FinderColumn> getFinderColumns();

	public Value<String> getName();

	public Value<String> getReturnType();

	// *** Return Type ***

	public Value<String> getWhere();

	public Value<Boolean> isDbIndex();

	public Value<Boolean> isUnique();

	// *** Unique ***

	public void setDbIndex(Boolean value);

	public void setDbIndex(String value);

	public void setName(String value);

	public void setReturnType(String value);

	// *** Where ***

	public void setUnique(Boolean value);

	public void setUnique(String value);

	public void setWhere(String value);

	// *** DB Index ***

	@DefaultValue(text = "true")
	@Label(standard = "&db index")
	@Type(base = Boolean.class)
	@XmlBinding(path = "@db-index")
	public ValueProperty PROP_DB_INDEX = new ValueProperty(TYPE, "DbIndex");

	@Label(standard = "finder columns")
	@Length(min = 1)
	@Type(base = FinderColumn.class)
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "finder-column", type = FinderColumn.class))
	public ListProperty PROP_FINDER_COLUMNS = new ListProperty(TYPE, "FinderColumns");

	@Label(standard = "&name")
	@Required
	@XmlBinding(path = "@name")
	public ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");

	@Label(standard = "&return type")
	@Required
	@XmlBinding(path = "@return-type")
	public ValueProperty PROP_RETURN_TYPE = new ValueProperty(TYPE, "ReturnType");

	// *** Finder Columns ***

	@DefaultValue(text = "false")
	@Label(standard = "&unique")
	@Type(base = Boolean.class)
	@XmlBinding(path = "@unique")
	public ValueProperty PROP_UNIQUE = new ValueProperty(TYPE, "Unique");

	@Label(standard = "&where")
	@XmlBinding(path = "@where")
	public ValueProperty PROP_WHERE = new ValueProperty(TYPE, "Where");

}