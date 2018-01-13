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
import com.liferay.ide.kaleo.core.model.JoinXor;
import com.liferay.ide.kaleo.core.model.Node;
import com.liferay.ide.kaleo.core.model.Transition;
import com.liferay.ide.kaleo.core.model.WorkflowDefinition;
import com.liferay.ide.kaleo.core.model.WorkflowNode;
import com.liferay.ide.kaleo.core.op.NewJoinXorNodeOp;
import com.liferay.ide.kaleo.core.op.NewNodeOp;

import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeTemplate;

/**
 * @author Gregory Amerson
 */
public class JoinXorNodeAddActionHandler extends NewNodeAddActionHandler {

	public JoinXorNodeAddActionHandler(DiagramNodeTemplate nodeTemplate) {
		super(nodeTemplate);
	}

	@Override
	public void postDiagramNodePartAdded(NewNodeOp op, CanTransition newNodeFromWizard, CanTransition newNode) {
		NewJoinXorNodeOp newJoinXorNodeOp = op.nearest(NewJoinXorNodeOp.class);

		JoinXor newJoinXor = newNode.nearest(JoinXor.class);

		WorkflowDefinition workflowDefinition = newJoinXor.nearest(WorkflowDefinition.class);

		for (Node nodeName : newJoinXorNodeOp.getConnectedNodes()) {
			for (WorkflowNode diagramNode : workflowDefinition.getDiagramNodes()) {
				if (nodeName.getName().content() != null) {
					String name = nodeName.getName().content();

					if (name.equals(diagramNode.getName().content())) {
						CanTransition canTransition = diagramNode.nearest(CanTransition.class);

						Transition newTransition = canTransition.getTransitions().insert();

						newTransition.setName(newJoinXor.getName().content());
						newTransition.setTarget(newJoinXor.getName().content());
					}
				}
			}
		}

		if (newJoinXorNodeOp.getExitTransitionName().content() != null) {
			Transition newTransition = newJoinXor.getTransitions().insert();

			newTransition.setTarget(newJoinXorNodeOp.getExitTransitionName().content());
			newTransition.setName(newJoinXorNodeOp.getExitTransitionName().content());
		}
	}

	@Override
	protected NewNodeOp createOp(Presentation context) {
		return NewJoinXorNodeOp.TYPE.instantiate();
	}

	@Override
	protected String getWizardId() {
		return _WIZARD_ID;
	}

	private static final String _WIZARD_ID = "newJoinXorNodeWizard";

}