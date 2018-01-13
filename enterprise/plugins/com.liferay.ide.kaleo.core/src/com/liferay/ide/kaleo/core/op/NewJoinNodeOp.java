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

import com.liferay.ide.kaleo.core.model.internal.TransitionPossibleValuesService;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ImpliedElementProperty;
import org.eclipse.sapphire.Length;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Service;

/**
 * @author Gregory Amerson
 */
public interface NewJoinNodeOp extends NewNodeOp {

	public ElementType TYPE = new ElementType(NewJoinNodeOp.class);

	public Value<String> getExitTransitionName();

	public NewJoinNode getNewJoinNode();

	public void setExitTransitionName(String value);

	@Label(standard = "nodes to join")
	@Length(max = 2, min = 0)
	public ListProperty PROP_CONNECTED_NODES = new ListProperty(TYPE, NewNodeOp.PROP_CONNECTED_NODES);

	@Label(standard = "&exit transition name")
	@Service(impl = TransitionPossibleValuesService.class)
	public ValueProperty PROP_EXIT_TRANSITION_NAME = new ValueProperty(TYPE, "ExitTransitionName");

	@Label(standard = "new join node")
	@Type(base = NewJoinNode.class)
	public ImpliedElementProperty PROP_NEW_JOIN_NODE = new ImpliedElementProperty(TYPE, "NewJoinNode");

}