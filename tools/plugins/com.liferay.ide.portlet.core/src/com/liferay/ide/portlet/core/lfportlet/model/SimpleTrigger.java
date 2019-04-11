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

package com.liferay.ide.portlet.core.lfportlet.model;

import org.eclipse.sapphire.ElementHandle;
import org.eclipse.sapphire.ElementProperty;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.PossibleValues;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlElementBinding;

/**
 * @author Simon Jiang
 */
public interface SimpleTrigger extends ISimpleTrigger {

	public ElementType TYPE = new ElementType(SimpleTrigger.class);

	public ElementHandle<ISimpleTrigger> getSimpleTrigger();

	public Value<String> getTimeUnit();

	public void setTimeUnit(String value);

	@Required
	@Type(base = ISimpleTrigger.class, possible = {PropertySimpleTrigger.class, SimpleTriggerValueTrigger.class})
	@XmlElementBinding(
		mappings = {
			@XmlElementBinding.Mapping(element = "property-key", type = PropertySimpleTrigger.class),
			@XmlElementBinding.Mapping(element = "simple-trigger-value", type = SimpleTriggerValueTrigger.class)
		},
		path = ""
	)
	public ElementProperty PROP_SIMPLE_TRIGGER = new ElementProperty(TYPE, "SimpleTrigger");

	@DefaultValue(text = "second")
	@Label(standard = "Time Unit")
	@PossibleValues(values = {"day", "hour", "minute", "second", "week"})
	@Required
	@XmlBinding(path = "time-unit")
	public ValueProperty PROP_TIME_UNIT = new ValueProperty(TYPE, "TimeUnit");

}