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

package com.liferay.ide.kaleo.core.op;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ImpliedElementProperty;
import org.eclipse.sapphire.Length;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Label;

/**
 * @author Gregory Amerson
 */
public interface NewForkNodeOp extends NewNodeOp {

	public ElementType TYPE = new ElementType(NewForkNodeOp.class);

	public NewForkNode getNewForkNode();

	public Value<Boolean> isAddJoin();

	public void setAddJoin(Boolean value);

	public void setAddJoin(String value);

	@DefaultValue(text = "true")
	@Label(standard = "Automatically add join node")
	@Type(base = Boolean.class)
	public ValueProperty PROP_ADD_JOIN = new ValueProperty(TYPE, "AddJoin");

	@Label(standard = "fork nodes")
	@Length(max = 2, min = 0)
	public ListProperty PROP_CONNECTED_NODES = new ListProperty(TYPE, NewNodeOp.PROP_CONNECTED_NODES);

	@Label(standard = "new fork node")
	@Type(base = NewForkNode.class)
	public ImpliedElementProperty PROP_NEW_FORK_NODE = new ImpliedElementProperty(TYPE, "NewForkNode");

}