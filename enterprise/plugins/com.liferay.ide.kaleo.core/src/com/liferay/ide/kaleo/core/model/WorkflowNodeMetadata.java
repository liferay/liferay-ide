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
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ImpliedElementProperty;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;

/**
 * @author Gregory Amerson
 */
public interface WorkflowNodeMetadata extends Element {

	public ElementType TYPE = new ElementType(WorkflowNodeMetadata.class);

	public Position getPosition();

	public ElementList<TransitionMetadata> getTransitionsMetadata();

	public Value<Boolean> isTerminal();

	public void setTerminal(Boolean terminal);

	public void setTerminal(String value);

	@Type(base = Position.class)
	public ImpliedElementProperty PROP_POSITION = new ImpliedElementProperty(TYPE, "Position");

	@Type(base = Boolean.class)
	public ValueProperty PROP_TERMINAL = new ValueProperty(TYPE, "Terminal");

	@Type(base = TransitionMetadata.class)
	public ListProperty PROP_TRANSITIONS_METADATA = new ListProperty(TYPE, "TransitionsMetadata");

}