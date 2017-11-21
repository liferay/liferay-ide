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

import org.eclipse.sapphire.ElementHandle;
import org.eclipse.sapphire.ElementProperty;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Gregory Amerson
 */
public interface Timer extends WorkflowNode {

	public ElementType TYPE = new ElementType(Timer.class);

	public ElementHandle<TimeDelay> getDelay();

	public ElementHandle<TimeDelay> getRecurrence();

	@Type(base = TimeDelay.class)
	@Label(standard = "delay")
	@Required
	/**
	 * @XmlElementBinding( mappings = @XmlElementBinding.Mapping( element =
	 * "delay", type = ITimeDelay.class ) )
	 */
	@XmlBinding(path = "delay")
	public ElementProperty PROP_DELAY = new ElementProperty(TYPE, "Delay");

	@Type(base = TimeDelay.class)
	@Label(standard = "recurrence")
	/**
	 * @XmlElementBinding( mappings = @XmlElementBinding.Mapping( element =
	 * "recurrence", type = ITimeDelay.class ) )
	 */
	@XmlBinding(path = "recurrence")
	public ElementProperty PROP_RECURRENCE = new ElementProperty(TYPE, "Recurrence");

}