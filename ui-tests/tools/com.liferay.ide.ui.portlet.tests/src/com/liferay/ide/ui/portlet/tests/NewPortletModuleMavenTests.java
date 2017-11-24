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
public class NewPortletModuleMavenTests extends SwtbotBase {

	@Test
	public void createFreemarkerPortlet() {
		String projectName = "test-freemarker-portlet-maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleMaven(projectName, FREEMARKER_PORTLET);

		wizardAction.finish();

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectName));

		viewAction.closeAndDeleteProject(projectName);
	}

	@Test
	public void createMvcPortlet() {
		String projectName = "test-mvc-portlet-maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleMaven(projectName, MVC_PORTLET);

		wizardAction.finish();

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectName));

		viewAction.closeAndDeleteProject(projectName);
	}

	@Test
	public void createPortlet() {
		String projectName = "test-portlet-maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleMaven(projectName, PORTLET);

		wizardAction.finish();

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectName));

		viewAction.closeAndDeleteProject(projectName);
	}

	@Test
	public void createSoyPortlet() {
		String projectName = "test-soy-portlet-maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleMaven(projectName, SOY_PORTLET);

		wizardAction.finish();

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectName));

		viewAction.closeAndDeleteProject(projectName);
	}

	@Test
	public void createSpringMvcPortlet() {
		String projectName = "test-spring-mvc-portlet-maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleMaven(projectName, SPRING_MVC_PORTLET);

		wizardAction.finish();

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectName));

		viewAction.closeAndDeleteProject(projectName);
	}

}