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
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Terry Jia
 * @author Rui Wang
 */
public class NewPortletModuleGradleTests extends SwtbotBase {

	@Test
	public void createFreemarkerPortlet() {
		String projectName = "test-freemarker-portlet-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName, FREEMARKER_PORTLET);

		wizardAction.finish();

		Assert.assertTrue(viewAction.project.visibleFileTry(projectName));

		viewAction.project.closeAndDelete(projectName);
	}

	@Test
	public void createMvcPortlet() {
		String projectName = "test-mvc-portlet-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName, MVC_PORTLET);

		wizardAction.finish();

		Assert.assertTrue(viewAction.project.visibleFileTry(projectName));

		jobAction.waitForValidate(projectName);

		viewAction.project.closeAndDelete(projectName);
	}

	@Test
	public void createNpmAngularPortlet() {
		String projectName = "test-npm-angular-portlet-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName, NPM_ANGULAR_PORTLET);

		wizardAction.finish();

		Assert.assertTrue(viewAction.project.visibleFileTry(projectName));

		viewAction.project.closeAndDelete(projectName);
	}

	@Test
	public void createNpmBillboardjsPortlet() {
		String projectName = "test-npm-billboardjs-portlet-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName, NPM_BILLBOARDJS_PORLET);

		wizardAction.finish();

		Assert.assertTrue(viewAction.project.visibleFileTry(projectName));

		viewAction.project.closeAndDelete(projectName);
	}

	@Test
	public void createNpmIsomorphicPortlet() {
		String projectName = "test-npm-isomorphic-portlet-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName, NPM_ISOMORPHIC_PORTLET);

		wizardAction.finish();

		Assert.assertTrue(viewAction.project.visibleFileTry(projectName));

		viewAction.project.closeAndDelete(projectName);
	}

	@Test
	public void createNpmJqueryPortlet() {
		String projectName = "test-npm-jquery-portlet-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName, NPM_JQUERY_PORTLET);

		wizardAction.finish();

		Assert.assertTrue(viewAction.project.visibleFileTry(projectName));

		viewAction.project.closeAndDelete(projectName);
	}

	@Test
	public void createNpmMetaljsPortletGradle() {
		String projectName = "test-npm-metaljs-portlet-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName, NPM_METALJS_PORTLET);

		wizardAction.finish();

		Assert.assertTrue(viewAction.project.visibleFileTry(projectName));

		viewAction.project.closeAndDelete(projectName);
	}

	@Test
	public void createNpmPortlet() {
		String projectName = "test-npm-portlet-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName, NPM_PORTLET);

		wizardAction.finish();

		Assert.assertTrue(viewAction.project.visibleFileTry(projectName));

		viewAction.project.closeAndDelete(projectName);
	}

	@Test
	public void createNpmReactPortlet() {
		String projectName = "test-npm-react-portlet-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName, NPM_REACT_PORTLET);

		wizardAction.finish();

		Assert.assertTrue(viewAction.project.visibleFileTry(projectName));

		viewAction.project.closeAndDelete(projectName);
	}

	@Test
	public void createNpmVuejsPortlet() {
		String projectName = "test-npm-vuejs-portlet-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName, NPM_VUEJS_PORTLET);

		wizardAction.finish();

		Assert.assertTrue(viewAction.project.visibleFileTry(projectName));

		viewAction.project.closeAndDelete(projectName);
	}

	@Test
	public void createPortlet() {
		String projectName = "test-portlet-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName, PORTLET);

		wizardAction.finish();

		Assert.assertTrue(viewAction.project.visibleFileTry(projectName));

		jobAction.waitForValidate(projectName);

		viewAction.project.closeAndDelete(projectName);
	}

	@Ignore("ignore to wait IDE-3579 as it will take too long unexpected")
	@Test
	public void createSoyPortlet() {
		String projectName = "test-soy-portlet-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName, SOY_PORTLET);

		wizardAction.finish();

		Assert.assertTrue(viewAction.project.visibleFileTry(projectName));

		viewAction.project.closeAndDelete(projectName);
	}

	@Test
	public void createSpringMvcPortlet() {
		String projectName = "test-spring-mvc-portlet-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName, SPRING_MVC_PORTLET);

		wizardAction.finish();

		Assert.assertTrue(viewAction.project.visibleFileTry(projectName));

		viewAction.project.closeAndDelete(projectName);
	}

}