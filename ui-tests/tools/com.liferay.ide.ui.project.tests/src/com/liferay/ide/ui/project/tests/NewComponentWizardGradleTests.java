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

package com.liferay.ide.ui.project.tests;

import com.liferay.ide.ui.liferay.SwtbotBase;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Ying Xu
 * @author Ashley Yuan
 */
public class NewComponentWizardGradleTests extends SwtbotBase {

	@Test
	public void createComponentModelListener() {
	}

	@Test
	public void createComponentPortlet() {
		String projectName = "test-component-portlet-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle(projectName);

		wizardAction.finishToWait();

		wizardAction.openNewLiferayComponentClassWizard();

		String className = "MyPortlet";
		String packageName = "com.liferay.ide.test";
		String template = PORTLET_UPCASE;

		wizardAction.prepareComponentClass(projectName, template, className, packageName);

		wizardAction.finishToWait();

		Assert.assertTrue(
			viewAction.visibleProjectFileTry(projectName, "src/main/java", packageName, className + ".java"));

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createComponentServiceWrapper() {
		String projectName = "test-component-service-wrapper-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle(projectName);

		wizardAction.finishToWait();

		wizardAction.openNewLiferayComponentClassWizard();

		String className = "MyServiceWrapper";
		String packageName = "com.liferay.ide.test";
		String template = SERVICE_WRAPPER_UPCASE;

		wizardAction.prepareComponentClass(projectName, template, className, packageName);

		wizardAction.openSelectModelClassAndServiceDialog();

		dialogAction.prepareText("*bookmarksEntryLocal");

		dialogAction.confirm();

		wizardAction.finishToWait();

		Assert.assertTrue(
			viewAction.visibleProjectFileTry(projectName, "src/main/java", packageName, className + ".java"));

		viewAction.deleteProject(projectName);
	}

}