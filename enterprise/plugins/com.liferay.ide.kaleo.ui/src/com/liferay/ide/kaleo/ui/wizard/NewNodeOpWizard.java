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

package com.liferay.ide.kaleo.ui.wizard;

import static com.liferay.ide.ui.util.UIUtil.sync;

import com.liferay.ide.kaleo.core.model.CanTransition;
import com.liferay.ide.kaleo.core.model.Condition;
import com.liferay.ide.kaleo.core.model.Fork;
import com.liferay.ide.kaleo.core.model.Join;
import com.liferay.ide.kaleo.core.model.JoinXor;
import com.liferay.ide.kaleo.core.model.Node;
import com.liferay.ide.kaleo.core.model.State;
import com.liferay.ide.kaleo.core.model.Task;
import com.liferay.ide.kaleo.core.model.Transition;
import com.liferay.ide.kaleo.core.model.WorkflowDefinition;
import com.liferay.ide.kaleo.core.op.ChooseDiagramNode;
import com.liferay.ide.kaleo.core.op.NewNodeOp;
import com.liferay.ide.kaleo.core.op.NewNodeOp.ConditionForOp;
import com.liferay.ide.kaleo.core.op.NewNodeOp.ForkForOp;
import com.liferay.ide.kaleo.core.op.NewNodeOp.JoinForOp;
import com.liferay.ide.kaleo.core.op.NewNodeOp.JoinXorForOp;
import com.liferay.ide.kaleo.core.op.NewNodeOp.StateForOp;
import com.liferay.ide.kaleo.core.op.NewNodeOp.TaskForOp;
import com.liferay.ide.kaleo.ui.diagram.NewNodeAddActionHandler;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizard;

/**
 * @author Gregory Amerson
 */
public class NewNodeOpWizard extends SapphireWizard<NewNodeOp> {

	public NewNodeOpWizard(
		NewNodeOp modelElement, String wizardId, NewNodeAddActionHandler actionHandler, Presentation context) {

		super(modelElement, _loaderSdef.wizard(wizardId));

		_actionHandler = actionHandler;
		_context = context;
	}

	@Override
	protected Status performFinish(ProgressMonitor monitor) {
		Object[] diagramNode = new Object[1];

		monitor.beginTask("Creating new node", 2);

		sync(
			new Runnable() {

				public void run() {
					diagramNode[0] = NewNodeOpWizard.this._actionHandler.insertDiagramPart(
						NewNodeOpWizard.this._context, false);
				}

			});

		monitor.worked(1);

		if (diagramNode[0] instanceof DiagramNodePart) {
			_diagramNodePart = (DiagramNodePart)diagramNode[0];

			sync(
				new Runnable() {

					public void run() {
						performPostDiagramNodeAdded();
					}

				});

			monitor.worked(1);
		}

		return Status.createOkStatus();
	}

	protected void performPostDiagramNodeAdded() {
		NewNodeOp newNodeOp = element().nearest(NewNodeOp.class);

		CanTransition newNode = newNodeOp.adapt(CanTransition.class);

		CanTransition node = _diagramNodePart.getLocalModelElement().nearest(CanTransition.class);

		WorkflowDefinition workflowDefinition = node.nearest(WorkflowDefinition.class);

		if ((newNode != null) && (node != null)) {
			Value<String> newNodeName = newNode.getName();

			node.setName(newNodeName.content());

			if (newNodeOp.getConnectedNodes().size() > 0) {
				for (Node diagramNode : newNodeOp.getConnectedNodes()) {
					Value<String> nodeName = diagramNode.getName();

					String diagramNodeName = nodeName.content(true);

					if (diagramNode instanceof ConditionForOp) {
						ElementList<Condition> conditions = workflowDefinition.getConditions();

						Condition insertCondition = conditions.insert();

						insertCondition.setName(diagramNodeName);
					}
					else if (diagramNode instanceof ForkForOp) {
						ElementList<Fork> forks = workflowDefinition.getForks();

						Fork insertFork = forks.insert();

						insertFork.setName(diagramNodeName);
					}
					else if (diagramNode instanceof JoinForOp) {
						ElementList<Join> joins = workflowDefinition.getJoins();

						Join insertJoin = joins.insert();

						insertJoin.setName(diagramNodeName);
					}
					else if (diagramNode instanceof JoinXorForOp) {
						ElementList<JoinXor> joinXors = workflowDefinition.getJoinXors();

						JoinXor insertJoinXor = joinXors.insert();

						insertJoinXor.setName(diagramNodeName);
					}
					else if (diagramNode instanceof StateForOp) {
						ElementList<State> states = workflowDefinition.getStates();

						State insertState = states.insert();

						insertState.setName(diagramNodeName);
					}
					else if (diagramNode instanceof TaskForOp) {
						ElementList<Task> tasks = workflowDefinition.getTasks();

						Task insertTask = tasks.insert();

						insertTask.setName(diagramNodeName);
					}

					if (diagramNode instanceof ChooseDiagramNode || node instanceof Condition ||
						node instanceof Fork || node instanceof State || node instanceof Task) {

						Transition newTransition = node.getTransitions().insert();

						newTransition.setName(diagramNodeName);
						newTransition.setTarget(diagramNodeName);
					}
					else {
						CanTransition canTransition = diagramNode.nearest(CanTransition.class);

						ElementList<Transition> transition = canTransition.getTransitions();

						Transition joinTransition = transition.insert();

						joinTransition.setName(node.getName().content());
						joinTransition.setTarget(node.getName().content());
					}
				}
			}
		}

		_actionHandler.postDiagramNodePartAdded(element(), newNode, node);
	}

	private static DefinitionLoader _loader = DefinitionLoader.context(NewNodeOpWizard.class);
	private static DefinitionLoader _loaderSdef = _loader.sdef("WorkflowDefinitionWizards");

	private NewNodeAddActionHandler _actionHandler;
	private Presentation _context;
	private DiagramNodePart _diagramNodePart;

}