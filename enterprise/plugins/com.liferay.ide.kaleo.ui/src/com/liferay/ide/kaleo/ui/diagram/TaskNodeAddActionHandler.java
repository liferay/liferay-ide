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

import com.liferay.ide.kaleo.core.KaleoCore;
import com.liferay.ide.kaleo.core.model.Action;
import com.liferay.ide.kaleo.core.model.ActionNotification;
import com.liferay.ide.kaleo.core.model.CanTransition;
import com.liferay.ide.kaleo.core.model.ScriptLanguageType;
import com.liferay.ide.kaleo.core.model.Task;
import com.liferay.ide.kaleo.core.op.AssignableOp;
import com.liferay.ide.kaleo.core.op.NewNodeOp;
import com.liferay.ide.kaleo.core.op.NewTaskNode;
import com.liferay.ide.kaleo.core.op.NewTaskNode.INewTaskNotification;
import com.liferay.ide.kaleo.core.op.NewTaskNodeOp;
import com.liferay.ide.kaleo.core.util.KaleoModelUtil;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeTemplate;

/**
 * @author Gregory Amerson
 */
public class TaskNodeAddActionHandler extends NewNodeAddActionHandler {

	public TaskNodeAddActionHandler(DiagramNodeTemplate nodeTemplate) {
		super(nodeTemplate);
	}

	@Override
	public void postDiagramNodePartAdded(NewNodeOp op, CanTransition newNodeFromWizard, CanTransition newNode) {
		Task newTask = newNode.nearest(Task.class);
		NewTaskNode newTaskFromWizard = newNodeFromWizard.nearest(NewTaskNode.class);

		KaleoModelUtil.changeTaskAssignments(newTask, op.nearest(AssignableOp.class));

		for (Action taskAction : newTaskFromWizard.getTaskActions()) {
			ElementList<Action> action = newTask.getTaskActions();

			Action insertAction = action.insert();

			insertAction.copy(taskAction);
		}

		for (INewTaskNotification taskNotification : newTaskFromWizard.getNewTaskNotifications()) {
			ActionNotification newTaskNotification = newTask.getTaskNotifications().insert();

			newTaskNotification.setName(taskNotification.getName().content());
			newTaskNotification.setExecutionType(taskNotification.getExecutionType().content());
			newTaskNotification.setTemplateLanguage(taskNotification.getTemplateLanguage().content());
		}
	}

	@Override
	protected NewNodeOp createOp(Presentation context) {
		NewTaskNodeOp op = NewTaskNodeOp.TYPE.instantiate();

		op.getImpliedScriptable().setScriptLanguage(
			KaleoModelUtil.getDefaultValue(
				getModelElement(), KaleoCore.DEFAULT_SCRIPT_LANGUAGE_KEY, ScriptLanguageType.GROOVY));

		return op;
	}

	@Override
	protected String getWizardId() {
		return _WIZARD_ID;
	}

	private static final String _WIZARD_ID = "newTaskNodeWizard";

}