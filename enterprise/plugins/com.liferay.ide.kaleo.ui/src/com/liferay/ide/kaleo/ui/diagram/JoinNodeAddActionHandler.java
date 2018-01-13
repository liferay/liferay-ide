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
import com.liferay.ide.kaleo.core.model.Join;
import com.liferay.ide.kaleo.core.model.Node;
import com.liferay.ide.kaleo.core.model.Transition;
import com.liferay.ide.kaleo.core.model.WorkflowDefinition;
import com.liferay.ide.kaleo.core.model.WorkflowNode;
import com.liferay.ide.kaleo.core.op.NewJoinNodeOp;
import com.liferay.ide.kaleo.core.op.NewNodeOp;

import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeTemplate;

/**
 * @author Gregory Amerson
 */
public class JoinNodeAddActionHandler extends NewNodeAddActionHandler {

	public JoinNodeAddActionHandler(DiagramNodeTemplate nodeTemplate) {
		super(nodeTemplate);
	}

	@Override
	public void postDiagramNodePartAdded(NewNodeOp op, CanTransition newNodeFromWizard, CanTransition newNode) {
		NewJoinNodeOp newJoinNodeOp = op.nearest(NewJoinNodeOp.class);

		Join newJoin = newNode.nearest(Join.class);

		WorkflowDefinition workflowDefinition = newJoin.nearest(WorkflowDefinition.class);

		for (Node nodeName : newJoinNodeOp.getConnectedNodes()) {
			for (WorkflowNode diagramNode : workflowDefinition.getDiagramNodes()) {
				if (nodeName.getName().content() != null) {
					String name = nodeName.getName().content();

					if (name.equals(diagramNode.getName().content())) {
						CanTransition canTransition = diagramNode.nearest(CanTransition.class);

						Transition newTransition = canTransition.getTransitions().insert();

						newTransition.setName(newJoin.getName().content());
						newTransition.setTarget(newJoin.getName().content());
					}
				}
			}
		}

		if (newJoinNodeOp.getExitTransitionName().content() != null) {
			Transition newTransition = newJoin.getTransitions().insert();

			newTransition.setTarget(newJoinNodeOp.getExitTransitionName().content());
			newTransition.setName(newJoinNodeOp.getExitTransitionName().content());
		}
	}

	@Override
	protected NewNodeOp createOp(Presentation context) {
		return NewJoinNodeOp.TYPE.instantiate();
	}

	@Override
	protected String getWizardId() {
		return _WIZARD_ID;
	}

	private static final String _WIZARD_ID = "newJoinNodeWizard";

}