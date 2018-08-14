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

	public ElementList<FinderColumn> getFinderColumns();

	// *** Name ***

	public Value<String> getName();

	public Value<String> getReturnType();

	public Value<String> getWhere();

	// *** Return Type ***

	public Value<Boolean> isDbIndex();

	public Value<Boolean> isUnique();

	public void setDbIndex(Boolean value);

	// *** Unique ***

	public void setDbIndex(String value);

	public void setName(String value);

	public void setReturnType(String value);

	public void setUnique(Boolean value);

	// *** Where ***

	public void setUnique(String value);

	public void setWhere(String value);

	@DefaultValue(text = "true")
	@Label(standard = "&db index")
	@Type(base = Boolean.class)
	@XmlBinding(path = "@db-index")
	public ValueProperty PROP_DB_INDEX = new ValueProperty(TYPE, "DbIndex");

	// *** DB Index ***

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

	@DefaultValue(text = "false")
	@Label(standard = "&unique")
	@Type(base = Boolean.class)
	@XmlBinding(path = "@unique")
	public ValueProperty PROP_UNIQUE = new ValueProperty(TYPE, "Unique");

	// *** Finder Columns ***

	@Label(standard = "&where")
	@XmlBinding(path = "@where")
	public ValueProperty PROP_WHERE = new ValueProperty(TYPE, "Where");

}