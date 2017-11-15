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

package com.liferay.ide.core.properties;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;

/**
 * @author Gregory Amerson
 */
public interface PluginPackageProperties extends Element {

	public ElementType TYPE = new ElementType(PluginPackageProperties.class);

	public Value<String> getAuthor();

	public Value<String> getChangeLog();

	public Value<String> getLongDescription();

	public Value<String> getShortDescription();

	public Value<Boolean> getSpeedFilters();

	public void setAuthor(String value);

	public void setChangeLog(String value);

	public void setLongDescription(String value);

	public void setShortDescription(String value);

	public void setSpeedFilters(Boolean value);

	public void setSpeedFilters(String value);

	@Label(standard = "&Author")
	@PropertyBinding(key = "author")
	public ValueProperty PROP_AUTHOR = new ValueProperty(TYPE, "Author");

	@Label(standard = "&Change Log")
	@PropertyBinding(key = "change-log")
	public ValueProperty PROP_CHANGE_LOG = new ValueProperty(TYPE, "ChangeLog");

	@Label(standard = "Long &Description")
	@PropertyBinding(key = "long-description")
	public ValueProperty PROP_LONG_DESCRIPTION = new ValueProperty(TYPE, "LongDescription");

	@Label(standard = "Short &Description")
	@PropertyBinding(key = "short-description")
	public ValueProperty PROP_SHORT_DESCRIPTION = new ValueProperty(TYPE, "ShortDescription");

	@Label(standard = "Speed &Filters")
	@PropertyBinding(key = "speed-filters-enabled")
	@Type(base = Boolean.class)
	public ValueProperty PROP_SPEED_FILTERS = new ValueProperty(TYPE, "SpeedFilters");

}