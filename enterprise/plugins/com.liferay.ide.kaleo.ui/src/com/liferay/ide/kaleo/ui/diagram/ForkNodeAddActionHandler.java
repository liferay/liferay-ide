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

package com.liferay.ide.kaleo.ui.diagram;

import com.liferay.ide.kaleo.core.model.CanTransition;
import com.liferay.ide.kaleo.core.model.Fork;
import com.liferay.ide.kaleo.core.model.Join;
import com.liferay.ide.kaleo.core.model.Node;
import com.liferay.ide.kaleo.core.model.Transition;
import com.liferay.ide.kaleo.core.model.WorkflowDefinition;
import com.liferay.ide.kaleo.core.model.WorkflowNode;
import com.liferay.ide.kaleo.core.op.NewForkNode;
import com.liferay.ide.kaleo.core.op.NewForkNodeOp;
import com.liferay.ide.kaleo.core.op.NewNodeOp;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeTemplate;

/**
 * @author Gregory Amerson
 */
public class ForkNodeAddActionHandler extends NewNodeAddActionHandler {

	public ForkNodeAddActionHandler(DiagramNodeTemplate nodeTemplate) {
		super(nodeTemplate);
	}

	@Override
	public void postDiagramNodePartAdded(NewNodeOp op, CanTransition newNodeFromWizard, CanTransition newNode) {
		NewForkNodeOp newForkOp = op.nearest(NewForkNodeOp.class);
		NewForkNode newForkNodeFromWizard = newNodeFromWizard.nearest(NewForkNode.class);
		Fork newFork = newNode.nearest(Fork.class);

		WorkflowDefinition workflowDefinition = newFork.nearest(WorkflowDefinition.class);

		if (newForkOp.isAddJoin().content(true)) {
			Join newJoin = workflowDefinition.getJoins().insert();
			String newJoinName = newForkNodeFromWizard.getName().content() + " Join";

			newJoin.setName(newJoinName);

			for (Node connectedNode : op.getConnectedNodes()) {
				for (WorkflowNode diagramNode : workflowDefinition.getDiagramNodes()) {
					Value<String> nodeName = connectedNode.getName();

					if (nodeName.content().equals(diagramNode.getName().content())) {
						CanTransition canTransition = diagramNode.nearest(CanTransition.class);

						ElementList<Transition> transitions = canTransition.getTransitions();

						Transition newTransition = transitions.insert();

						newTransition.setName(newJoinName);
						newTransition.setTarget(newJoinName);
					}
				}
			}
		}
	}

	@Override
	protected NewNodeOp createOp(Presentation context) {
		return NewForkNodeOp.TYPE.instantiate();
	}

	@Override
	protected String getWizardId() {
		return _WIZARD_ID;
	}

	private static final String _WIZARD_ID = "newForkNodeWizard";

}