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

package com.liferay.ide.functional.module.tests;

import com.liferay.ide.functional.liferay.SwtbotBase;
import com.liferay.ide.functional.liferay.support.project.ProjectsSupport;

import org.junit.Rule;
import org.junit.Test;

/**
 * @author Lily Li
 */
public class ModuleEditorQuickFixTests extends SwtbotBase {

	@Test
	public void quickFixInServiceWrapper() {
		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareGradleWithIndexSources(projects.getName(0), "7.2");

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projects.getName(1), SERVICE_WRAPPER, "7.2");

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		String projectName = projects.getName(1);

		String firletter = projectName.substring(0, 1);

		viewAction.project.openFile(
			projects.getName(0), "modules", projects.getName(1), "src/main/java", projects.getName(1),
			firletter.toUpperCase() + projectName.substring(1) + ".java");

		editorAction.selectText("com.liferay.marketplace");

		editorAction.openContextMenu(QUICK_FIX);

		keyboardAction.pressKeyEnter();

		jobAction.waitForNoRunningJobs();

		editorAction.close();

		viewAction.project.openFile(projects.getName(0), "modules", projects.getName(1), "build.gradle");

		validationAction.assertContains("com.liferay.marketplace", editorAction.getContent());

		editorAction.close();

		viewAction.project.closeAndDelete(projects.getName(0), "modules", projects.getName(1));

		viewAction.project.closeAndDelete(projects.getName(0), "modules");

		viewAction.project.closeAndDelete(projects.getName(0));
	}

	@Rule
	public ProjectsSupport projects = new ProjectsSupport(bot);

}