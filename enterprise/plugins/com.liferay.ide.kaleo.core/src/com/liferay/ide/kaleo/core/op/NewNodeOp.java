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

import com.liferay.ide.kaleo.core.model.Fork;
import com.liferay.ide.kaleo.core.model.Join;
import com.liferay.ide.kaleo.core.model.JoinXor;
import com.liferay.ide.kaleo.core.model.Node;
import com.liferay.ide.kaleo.core.model.Scriptable;
import com.liferay.ide.kaleo.core.model.State;
import com.liferay.ide.kaleo.core.model.Task;
import com.liferay.ide.kaleo.core.model.WorkflowDefinition;
import com.liferay.ide.kaleo.core.op.internal.DefaultOpMethods;
import com.liferay.ide.kaleo.core.op.internal.NewNodeOpAdapter;

import org.eclipse.sapphire.ElementHandle;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementProperty;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ExecutableElement;
import org.eclipse.sapphire.Length;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Service;

/**
 * @author Gregory Amerson
 */
@Service(impl = NewNodeOpAdapter.class)
public interface NewNodeOp extends ExecutableElement {

	@Image(path = "images/condition_16x16.png")
	@Label(standard = "new condition")
	public interface ConditionForOp extends Fork, Scriptable {

		public ElementType TYPE = new ElementType(ConditionForOp.class);

		@Length(min = 0)
		public ListProperty PROP_TRANSITIONS = new ListProperty(TYPE, Fork.PROP_TRANSITIONS);

	}

	@Label(standard = "new fork")
	public interface ForkForOp extends Fork {

		public ElementType TYPE = new ElementType(ForkForOp.class);

		@Length(min = 0)
		public ListProperty PROP_TRANSITIONS = new ListProperty(TYPE, Fork.PROP_TRANSITIONS);

	}

	@Label(standard = "new join")
	public interface JoinForOp extends Join {

		public ElementType TYPE = new ElementType(JoinForOp.class);

		@Length(min = 0)
		public ListProperty PROP_TRANSITIONS = new ListProperty(TYPE, Join.PROP_TRANSITIONS);

	}

	@Label(standard = "new join XOR")
	public interface JoinXorForOp extends JoinXor {

		public ElementType TYPE = new ElementType(JoinXorForOp.class);

		@Length(min = 0)
		public ListProperty PROP_TRANSITIONS = new ListProperty(TYPE, Join.PROP_TRANSITIONS);

	}

	@Label(standard = "new state")
	public interface StateForOp extends State {

		public ElementType TYPE = new ElementType(StateForOp.class);

		@Length(min = 0)
		public ListProperty PROP_TRANSITIONS = new ListProperty(TYPE, State.PROP_TRANSITIONS);

	}

	@Label(standard = "new task")
	public interface TaskForOp extends Task {

		public ElementType TYPE = new ElementType(TaskForOp.class);

		@Length(min = 0)
		public ListProperty PROP_TRANSITIONS = new ListProperty(TYPE, Task.PROP_TRANSITIONS);

	}

	public ElementType TYPE = new ElementType(NewNodeOp.class);

	@DelegateImplementation(DefaultOpMethods.class)
	public Status execute(ProgressMonitor monitor);

	public ElementList<Node> getConnectedNodes();

	public ElementHandle<WorkflowDefinition> getWorkflowDefinition();

	public Value<Boolean> isUseNodeWizards();

	public void setUseNodeWizards(Boolean value);

	public void setUseNodeWizards(String value);

	@Label(standard = "connected nodes")
	@Type(
		base = Node.class,
		possible = {
			ChooseDiagramNode.class, StateForOp.class, TaskForOp.class, ConditionForOp.class, ForkForOp.class,
			JoinForOp.class, JoinXorForOp.class
		}
	)
	public ListProperty PROP_CONNECTED_NODES = new ListProperty(TYPE, "ConnectedNodes");

	@DefaultValue(text = "true")
	@Label(standard = "Use Add wizards on drop from palette")
	@Type(base = Boolean.class)
	public ValueProperty PROP_USE_NODE_WIZARDS = new ValueProperty(TYPE, "UseNodeWizards");

	@Type(base = WorkflowDefinition.class)
	public ElementProperty PROP_WORKFLOW_DEFINITION = new ElementProperty(TYPE, "WorkflowDefinition");

}