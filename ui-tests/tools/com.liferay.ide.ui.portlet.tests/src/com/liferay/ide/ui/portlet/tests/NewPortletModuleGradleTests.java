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

package com.liferay.ide.ui.portlet.tests;

import com.liferay.ide.ui.liferay.SwtbotBase;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Terry Jia
 */
public class NewPortletModuleGradleTests extends SwtbotBase {

	@Test
	public void createMvcPortlet() {
		String projectName = "test-mvc-portlet-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle(projectName, MVC_PORTLET);

		wizardAction.finishToWait();

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectName));

		jobAction.waitForCancelIvy();

		jobAction.waitForCancelValidate(projectName);

		viewAction.closeProject(projectName);

		jobAction.waitForCloseProject();

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createPortlet() {
		String projectName = "test-portlet-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle(projectName, PORTLET);

		wizardAction.finishToWait();

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectName));

		jobAction.waitForCancelIvy();

		jobAction.waitForCancelValidate(projectName);

		viewAction.closeProject(projectName);

		jobAction.waitForCloseProject();

		viewAction.deleteProject(projectName);
	}

}