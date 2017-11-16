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

package com.liferay.ide.portlet.core.model.lfrportlet.common;

import com.liferay.ide.core.model.xml.internal.ChoiceValueBinding;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author kamesh TODO: Update the XMlValueBinding to allow only existence of choice nodes
 */
@GenerateImpl
public interface ICron extends Element {

	public ElementType TYPE = new ElementType(ICron.class);

	// *** PropertyKey ***

	@Label(standard = "Property Key")
	@XmlBinding(path = "property-key")
	@Enablement(expr = "${CronTriggerValue=='CRON_EXPRESSION'}")
	@DefaultValue(text = "PROPERTY_KEY")
	@CustomXmlValueBinding(impl = ChoiceValueBinding.class, params = {"property-key", "property-key",
		"cron-trigger-value"
	})
	public ValueProperty PROP_PROPERTY_KEY = new ValueProperty(TYPE, "PropertyKey");

	public Value<String> getPropertyKey();

	public void setPropertyKey(String value);

	// *** CronTriggerValue ***

	@Label(standard = "Cron Trigger Value")
	@DefaultValue(text = "CRON_EXPRESSION")
	@Enablement(expr = "${PropertyKey=='PROPERTY_KEY'}")
	@XmlBinding(path = "cron-trigger-value")
	@CustomXmlValueBinding(impl = ChoiceValueBinding.class, params = {"cron-trigger-value", "property-key",
		"cron-trigger-value"
	})
	public ValueProperty PROP_CRON_TRIGGER_VALUE = new ValueProperty(TYPE, "CronTriggerValue");

	public Value<String> getCronTriggerValue();

	public void setCronTriggerValue(String value);

}