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

import com.liferay.ide.kaleo.core.model.internal.TransitionPossibleValuesService;
import com.liferay.ide.kaleo.core.model.internal.TransitionReferenceService;
import com.liferay.ide.kaleo.core.model.internal.TransitionTargetListener;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ReferenceValue;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Listeners;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Services;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Gregory Amerson
 */
@Image(path = "images/arrow_16x16.png")

// @Listeners( value = {TransitionTargetListener.class} )
public interface Transition extends Element {

	public ElementType TYPE = new ElementType(Transition.class);

	public Value<String> getName();

	public ReferenceValue<String, Node> getTarget();

	public Value<Boolean> isDefaultTransition();

	public void setDefaultTransition(Boolean value);

	public void setDefaultTransition(String value);

	public void setName(String value);

	public void setTarget(String value);

	@DefaultValue(text = "false")
	@Label(standard = "&default")
	@Type(base = Boolean.class)
	@XmlBinding(path = "default")
	public ValueProperty PROP_DEFAULT_TRANSITION = new ValueProperty(TYPE, "DefaultTransition");

	@DefaultValue(text = "${Target}")
	@Label(standard = "&name")
	@Required
	@XmlBinding(path = "name")
	public ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");

	@Label(standard = "&target")
	@Listeners(value = TransitionTargetListener.class)
	@Reference(target = Node.class)
	@Required
	@Services(
		{@Service(impl = TransitionReferenceService.class), @Service(impl = TransitionPossibleValuesService.class)}
	)
	@XmlBinding(path = "target")
	public ValueProperty PROP_TARGET = new ValueProperty(TYPE, "Target");

}