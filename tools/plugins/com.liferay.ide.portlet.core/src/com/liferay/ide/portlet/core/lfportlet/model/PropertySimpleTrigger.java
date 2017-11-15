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

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Simon Jiang
 */
public interface PropertySimpleTrigger extends ISimpleTrigger {

	public ElementType TYPE = new ElementType(PropertySimpleTrigger.class);

	// *** Property Key ***

	@Label(standard = "Property Key")
	@Required
	@XmlBinding(path = "")
	public ValueProperty PROP_PROPERTY_KEY = new ValueProperty(TYPE, "PropertyKey");

	public Value<String> getPropertyKey();

	public void setPropertyKey(String value);

}