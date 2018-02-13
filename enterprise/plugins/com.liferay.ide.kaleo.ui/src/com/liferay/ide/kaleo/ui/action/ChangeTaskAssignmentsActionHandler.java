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

package com.liferay.ide.kaleo.ui.action;

import static com.liferay.ide.core.util.CoreUtil.isNullOrEmpty;

import com.liferay.ide.kaleo.core.model.Assignable;
import com.liferay.ide.kaleo.core.model.ResourceAction;
import com.liferay.ide.kaleo.core.model.Role;
import com.liferay.ide.kaleo.core.model.RoleName;
import com.liferay.ide.kaleo.core.model.Scriptable;
import com.liferay.ide.kaleo.core.model.Task;
import com.liferay.ide.kaleo.core.model.User;
import com.liferay.ide.kaleo.core.model.WorkflowDefinition;
import com.liferay.ide.kaleo.core.model.WorkflowNode;
import com.liferay.ide.kaleo.core.op.ChangeTaskAssignmentsOp;
import com.liferay.ide.kaleo.core.util.KaleoModelUtil;
import com.liferay.ide.kaleo.ui.wizard.NewWorkflowDefinitionWizard;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementHandle;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizard;
import org.eclipse.sapphire.ui.forms.swt.SwtPresentation;

/**
 * @author Gregory Amerson
 */
public class ChangeTaskAssignmentsActionHandler extends SapphireActionHandler {

	@Override
	protected Object run(Presentation context) {
		Task task = _task(context);

		ChangeTaskAssignmentsOp op = ChangeTaskAssignmentsOp.TYPE.instantiate();

		for (WorkflowNode node : task.nearest(WorkflowDefinition.class).getDiagramNodes()) {
			Assignable assignable = node.nearest(Assignable.class);

			if (assignable != null) {
				for (Role role : assignable.getRoles()) {
					String name = role.getName().content(false);

					if (!isNullOrEmpty(name)) {
						ElementList<RoleName> roleName = op.getRoleNames();

						roleName.insert().setName(name);
					}
				}
			}
		}

		User existingUser = task.getUser().content(false);
		ElementList<Role> existingRoles = task.getRoles();
		ElementList<ResourceAction> existingActions = task.getResourceActions();
		Scriptable scriptedAssignment = task.getScriptedAssignment().content(false);

		if (existingUser != null) {
			op.getImpliedUser().copy(existingUser);
		}
		else if (!existingRoles.isEmpty()) {
			op.getImpliedRole().copy(existingRoles.get(0));

			for (Role role : existingRoles) {
				Role newRole = op.getRoles().insert();

				newRole.copy(role);

				Boolean autoCreate = role.getAutoCreate().content(false);

				if (autoCreate != null) {
					newRole.setAutoCreate(role.getAutoCreate().content());
				}
			}
		}
		else if (!existingActions.isEmpty()) {
			for (ResourceAction action : existingActions) {
				ElementList<ResourceAction> resourceActions = op.getResourceActions();

				ResourceAction resourceAction = resourceActions.insert();

				resourceAction.copy(action);
			}
		}
		else if (scriptedAssignment != null) {
			ElementHandle<Scriptable> scriptable = op.getScriptedAssignment();

			Scriptable content = scriptable.content(true);

			content.copy(scriptedAssignment);
		}

		DefinitionLoader loader = DefinitionLoader.context(NewWorkflowDefinitionWizard.class);

		DefinitionLoader loaderSdef = loader.sdef("WorkflowDefinitionWizards");

		SapphireWizard<ChangeTaskAssignmentsOp> wizard = new SapphireWizard<>(
			op, loaderSdef.wizard("changeTaskAssignmentsWizard"));

		int returnCode = new WizardDialog(((SwtPresentation)context).shell(), wizard).open();

		if (returnCode == IDialogConstants.OK_ID) {
			KaleoModelUtil.changeTaskAssignments(_task(context), op);
		}

		return null;
	}

	private Task _task(Presentation context) {
		SapphirePart spPart = context.part();

		Element element = spPart.getLocalModelElement();

		return element.nearest(Task.class);
	}

}