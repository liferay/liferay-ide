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

import com.liferay.ide.portlet.core.lfportlet.model.internal.NumberValueValidationService;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Simon Jiang
 */
public interface SimpleTriggerValueTrigger extends ISimpleTrigger {

	public ElementType TYPE = new ElementType(SimpleTriggerValueTrigger.class);

	public Value<String> getSimpleTriggerValue();

	public void setSimpleTriggerValue(String value);

	@Label(standard = "Simple Trigger Value")
	@Required
	@Service(impl = NumberValueValidationService.class, params = {
		@Service.Param(name = "min", value = "1"), @Service.Param(name = "max", value = "")
	})
	@XmlBinding(path = "")
	public ValueProperty PROP_SIMPLE_TRIGGER_VALUE = new ValueProperty(TYPE, "SimpleTriggerValue");

}