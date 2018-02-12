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

package com.liferay.ide.ui.kaleo.tests;

import com.liferay.ide.ui.liferay.SwtbotBase;
import com.liferay.ide.ui.liferay.base.ProjectSupport;

import org.junit.Rule;
import org.junit.Test;

/**
 * @author Haoyi Sun
 */
public class NewKaleoProjectWizardSdkTests extends SwtbotBase {

	@Test
	public void createKaleoWorkflowAssignCreatorOnProject() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepare(project.getName());

		wizardAction.finish();

		viewAction.switchKaleoDesignerPerspective();

		wizardAction.openNewLiferayKaleoWorkflowWizard();

		wizardAction.newKaleoWorkflow.openSelectProjectDialog();

		dialogAction.prepareText(project.getName());

		dialogAction.confirm();

		wizardAction.next();

		wizardAction.next();

		wizardAction.finish();

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void createKaleoWorkflowAssignResourceActionOnProject() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepare(project.getName());

		wizardAction.finish();

		viewAction.switchKaleoDesignerPerspective();

		wizardAction.openNewLiferayKaleoWorkflowWizard();

		wizardAction.newKaleoWorkflow.openSelectProjectDialog();

		dialogAction.prepareText(project.getName());

		dialogAction.confirm();

		wizardAction.next();

		wizardAction.chooseAssignmentType.selectAssignResourceActions();

		wizardAction.next();

		wizardAction.makeTaskAssignAction.addResourceAction("test-action");

		wizardAction.finish();

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void createKaleoWorkflowAssignRoleByIdOnProject() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepare(project.getName());

		wizardAction.finish();

		viewAction.switchKaleoDesignerPerspective();

		wizardAction.openNewLiferayKaleoWorkflowWizard();

		wizardAction.newKaleoWorkflow.openSelectProjectDialog();

		dialogAction.prepareText(project.getName());

		dialogAction.confirm();

		wizardAction.next();

		wizardAction.chooseAssignmentType.selectAssignRoleById();

		wizardAction.next();

		wizardAction.makeTaskAssignRoleById.prepareRoleId("123");

		wizardAction.finish();

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void createKaleoWorkflowAssignRoleTypeOnProject() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepare(project.getName());

		wizardAction.finish();

		viewAction.switchKaleoDesignerPerspective();

		wizardAction.openNewLiferayKaleoWorkflowWizard();

		wizardAction.newKaleoWorkflow.openSelectProjectDialog();

		dialogAction.prepareText(project.getName());

		dialogAction.confirm();

		wizardAction.next();

		wizardAction.chooseAssignmentType.selectAssignRoleType();

		wizardAction.next();

		wizardAction.makeTaskAssignRoleType.addRole();

		wizardAction.makeTaskAssignRoleType.deleteRole();

		wizardAction.finish();

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void createKaleoWorkflowAssignScriptOnProject() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepare(project.getName());

		wizardAction.finish();

		viewAction.switchKaleoDesignerPerspective();

		wizardAction.openNewLiferayKaleoWorkflowWizard();

		wizardAction.newKaleoWorkflow.openSelectProjectDialog();

		dialogAction.prepareText(project.getName());

		dialogAction.confirm();

		wizardAction.next();

		wizardAction.chooseAssignmentType.selectAssignScriptedAssignment();

		wizardAction.next();

		wizardAction.makeTaskAssignScript.prepareScriptLanguage("Groovy");

		wizardAction.finish();

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void createKaleoWorkflowAssignUserOnProject() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepare(project.getName());

		wizardAction.finish();

		viewAction.switchKaleoDesignerPerspective();

		wizardAction.openNewLiferayKaleoWorkflowWizard();

		wizardAction.newKaleoWorkflow.openSelectProjectDialog();

		dialogAction.prepareText(project.getName());

		dialogAction.confirm();

		wizardAction.next();

		wizardAction.chooseAssignmentType.selectAssignUser();

		wizardAction.next();

		wizardAction.makeTaskAssignUser.prepareEmailAddress("test@liferay.com");

		wizardAction.finish();

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void createKaleoWorkflowOnProject() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepare(project.getName());

		wizardAction.finish();

		viewAction.switchKaleoDesignerPerspective();

		wizardAction.openNewLiferayKaleoWorkflowWizard();

		wizardAction.newKaleoWorkflow.openSelectProjectDialog();

		dialogAction.prepareText(project.getName());

		dialogAction.confirm();

		wizardAction.finish();

		viewAction.project.closeAndDelete(project.getName());
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

}