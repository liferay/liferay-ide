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

package com.liferay.ide.hook.core.model;

import org.eclipse.sapphire.Collation;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.PossibleValues;
import org.eclipse.sapphire.Unique;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Kamesh Sampath
 */
public interface Dispatcher extends Element {

	public ElementType TYPE = new ElementType(Dispatcher.class);

	public Value<String> getDispatcher();

	public void setDispatcher(String name);

	@Collation(ignoreCaseDifferences = "true")
	@Label(standard = "Dispatcher")
	@PossibleValues(values = {"FORWARD", "REQUEST", "INCLUDE", "ERROR"})
	@Unique
	@XmlBinding(path = "")
	public ValueProperty PROP_DISPATCHER = new ValueProperty(TYPE, "Dispatcher");

}