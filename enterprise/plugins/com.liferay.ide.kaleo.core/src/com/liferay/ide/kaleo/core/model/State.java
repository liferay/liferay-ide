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

package com.liferay.ide.kaleo.core.model;

import com.liferay.ide.kaleo.core.model.internal.StateEndValueBinding;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Gregory Amerson
 */
@Image(path = "images/initial_16x16.png")
public interface State extends ActionTimer, CanTransition {

	public ElementType TYPE = new ElementType(State.class);

	public Value<Boolean> isEnd();

	public Value<Boolean> isInitial();

	public void setEnd(Boolean value);

	public void setEnd(String value);

	public void setInitial(Boolean value);

	public void setInitial(String value);

	@CustomXmlValueBinding(impl = StateEndValueBinding.class)
	@Label(standard = "&end")
	@Type(base = Boolean.class)
	public ValueProperty PROP_END = new ValueProperty(TYPE, "End");

	@DefaultValue(text = "false")
	@Label(standard = "&initial")
	@Type(base = Boolean.class)
	@XmlBinding(path = "initial")
	public ValueProperty PROP_INITIAL = new ValueProperty(TYPE, "Initial");

}