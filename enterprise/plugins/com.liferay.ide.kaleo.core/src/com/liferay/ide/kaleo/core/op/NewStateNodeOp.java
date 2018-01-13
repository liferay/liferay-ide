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
import org.eclipse.sapphire.PossibleValues;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Service;

/**
 * @author Gregory Amerson
 */
public interface NewStateNodeOp extends NewNodeOp {

	public ElementType TYPE = new ElementType(NewStateNodeOp.class);

	public Value<String> getExitTransitionName();

	public NewStateNode getNewStateNode();

	public Value<NewStateType> getType();

	public Value<String> getWorkflowStatus();

	public void setExitTransitionName(String value);

	public void setType(NewStateType type);

	public void setType(String type);

	public void setWorkflowStatus(String value);

	@Enablement(expr = "${Type != \"end\"}")
	@Label(standard = "&exit transition node")
	@Service(impl = TransitionPossibleValuesService.class)
	public ValueProperty PROP_EXIT_TRANSITION_NAME = new ValueProperty(TYPE, "ExitTransitionName");

	@Type(base = NewStateNode.class)
	public ImpliedElementProperty PROP_NEW_STATE_NODE = new ImpliedElementProperty(TYPE, "NewStateNode");

	@DefaultValue(text = "default")
	@Label(standard = "state &type")
	@Type(base = NewStateType.class)
	public ValueProperty PROP_TYPE = new ValueProperty(TYPE, "Type");

	@Label(standard = "&workflow status")
	@PossibleValues(values = {"approved", "denied", "draft", "expired", "inactive", "incomplete", "pending"})
	public ValueProperty PROP_WORKFLOW_STATUS = new ValueProperty(TYPE, "WorkflowStatus");

}