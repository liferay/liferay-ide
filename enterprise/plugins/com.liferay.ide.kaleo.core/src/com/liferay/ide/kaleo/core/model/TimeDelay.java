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

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Gregory Amerson
 */
public interface TimeDelay extends Element {

	public ElementType TYPE = new ElementType(TimeDelay.class);

	public Value<Double> getDuration();

	public Value<TimeScaleType> getScale();

	public void setDuration(Double val);

	public void setDuration(String val);

	public void setScale(String scaleType);

	public void setScale(TimeScaleType scaleType);

	@Label(standard = "&duration")
	@Type(base = Double.class)
	@XmlBinding(path = "duration")
	public ValueProperty PROP_DURATION = new ValueProperty(TYPE, "Duration");

	@Label(standard = "scale")
	@Type(base = TimeScaleType.class)
	@XmlBinding(path = "scale")
	public ValueProperty PROP_SCALE = new ValueProperty(TYPE, "Scale");

}