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
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.modeling.annotations.Label;

/**
 * @author Gregory Amerson
 */
public interface NewConditionNodeOp extends NewNodeOp {

	public ElementType TYPE = new ElementType(NewConditionNodeOp.class);

	public NewConditionNode getNewConditionNode();

	@Label(standard = "condition transitions")
	public ListProperty PROP_CONNECTED_NODES = new ListProperty(TYPE, NewNodeOp.PROP_CONNECTED_NODES);

	@Label(standard = "new condition node")
	@Type(base = NewConditionNode.class)
	public ImpliedElementProperty PROP_NEW_CONDITION_NODE = new ImpliedElementProperty(TYPE, "NewConditionNode");

}