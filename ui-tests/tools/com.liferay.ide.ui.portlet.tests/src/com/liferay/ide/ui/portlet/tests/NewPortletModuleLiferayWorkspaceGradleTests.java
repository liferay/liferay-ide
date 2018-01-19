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
import com.liferay.ide.ui.liferay.base.LiferayWorkspaceGradleSupport;
import com.liferay.ide.ui.liferay.base.TimestampSupport;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Rui Wang
 */
public class NewPortletModuleLiferayWorkspaceGradleTests extends SwtbotBase {

	@ClassRule
	public static LiferayWorkspaceGradleSupport liferayWorkspace = new LiferayWorkspaceGradleSupport(bot);

	@Test
	public void createFreemarkerPortlet() {
		String projectName = timestamp.getName("test-freemarker-portlet-gradle");
		String packageName = "test.freemarker.portlet.gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName, FREEMARKER_PORTLET);

		wizardAction.next();

		wizardAction.newModuleInfo.preparePackageName(packageName);

		wizardAction.finish();

		String[] projectNames = {liferayWorkspace.getLiferayWorkspaceName(), "modules", projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createMvcPortlet() {
		String projectName = timestamp.getName("test-mvc-portlet-gradle");
		String packageName = "test.mvc.portlet.gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName, MVC_PORTLET);

		wizardAction.next();

		wizardAction.newModuleInfo.preparePackageName(packageName);

		wizardAction.finish();

		String[] projectNames = {liferayWorkspace.getLiferayWorkspaceName(), "modules", projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		jobAction.waitForValidate(projectName);

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createNpmAngularPortlet() {
		String projectName = timestamp.getName("test-npm-angular-portlet-gradle");
		String packageName = "test.npm.angular.portlet.gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName, NPM_ANGULAR_PORTLET);

		wizardAction.next();

		wizardAction.newModuleInfo.preparePackageName(packageName);

		wizardAction.finish();

		String[] projectNames = {liferayWorkspace.getLiferayWorkspaceName(), "modules", projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createNpmBillboardjsPortlet() {
		String projectName = timestamp.getName("test-npm-billboardjs-portlet-gradle");
		String packageName = "test.npm.billboardjs.portlet.gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName, NPM_BILLBOARDJS_PORLET);

		wizardAction.next();

		wizardAction.newModuleInfo.preparePackageName(packageName);

		wizardAction.finish();

		String[] projectNames = {liferayWorkspace.getLiferayWorkspaceName(), "modules", projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createNpmIsomorphicPortlet() {
		String projectName = timestamp.getName("test-npm-isomorphic-portlet-gradle");
		String packageName = "test.npm.isomorphic.portlet.gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName, NPM_ISOMORPHIC_PORTLET);

		wizardAction.next();

		wizardAction.newModuleInfo.preparePackageName(packageName);

		wizardAction.finish();

		String[] projectNames = {liferayWorkspace.getLiferayWorkspaceName(), "modules", projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createNpmJqueryPortlet() {
		String projectName = timestamp.getName("test-npm-jquery-portlet-gradle");
		String packageName = "test.npm.jquery.portlet.gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName, NPM_JQUERY_PORTLET);

		wizardAction.next();

		wizardAction.newModuleInfo.preparePackageName(packageName);

		wizardAction.finish();

		String[] projectNames = {liferayWorkspace.getLiferayWorkspaceName(), "modules", projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createNpmMetaljsPortletGradle() {
		String projectName = timestamp.getName("test-npm-metaljs-portlet-gradle");
		String packageName = "test.npm.metaljs.portlet.gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName, NPM_METALJS_PORTLET);

		wizardAction.next();

		wizardAction.newModuleInfo.preparePackageName(packageName);

		wizardAction.finish();

		String[] projectNames = {liferayWorkspace.getLiferayWorkspaceName(), "modules", projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createNpmPortlet() {
		String projectName = timestamp.getName("test-npm-portlet-gradle");
		String packageName = "test.npm.portlet.gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName, NPM_PORTLET);

		wizardAction.next();

		wizardAction.newModuleInfo.preparePackageName(packageName);

		wizardAction.finish();

		String[] projectNames = {liferayWorkspace.getLiferayWorkspaceName(), "modules", projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createNpmReactPortlet() {
		String projectName = timestamp.getName("test-npm-react-portlet-gradle");
		String packageName = "test.npm.react.portlet.gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName, NPM_REACT_PORTLET);

		wizardAction.next();

		wizardAction.newModuleInfo.preparePackageName(packageName);

		wizardAction.finish();

		String[] projectNames = {liferayWorkspace.getLiferayWorkspaceName(), "modules", projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createNpmVuejsPortlet() {
		String projectName = timestamp.getName("test-npm-vuejs-portlet-gradle");
		String packageName = "test.npm.vuejs.portlet.gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName, NPM_VUEJS_PORTLET);

		wizardAction.next();

		wizardAction.newModuleInfo.preparePackageName(packageName);

		wizardAction.finish();

		String[] projectNames = {liferayWorkspace.getLiferayWorkspaceName(), "modules", projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createPortlet() {
		String projectName = timestamp.getName("test-npm-angular-portlet-gradle");
		String packageName = "test.npm.angular.portlet.gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName, PORTLET);

		wizardAction.next();

		wizardAction.newModuleInfo.preparePackageName(packageName);

		wizardAction.finish();

		String[] projectNames = {liferayWorkspace.getLiferayWorkspaceName(), "modules", projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Ignore("ignore to wait IDE-3579 as it will take too long unexpected")
	@Test
	public void createSoyPortlet() {
		String projectName = timestamp.getName("test-soy-portlet-gradle");
		String packageName = "test.soy.portlet.gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName, SOY_PORTLET);

		wizardAction.next();

		wizardAction.newModuleInfo.preparePackageName(packageName);

		wizardAction.finish();

		String[] projectNames = {liferayWorkspace.getLiferayWorkspaceName(), "modules", projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Ignore("Ignore to make it run ok on Jenkins temporary")
	@Test
	public void createSpringMvcPortlet() {
		String projectName = timestamp.getName("test-spring-mvc-portlet-gradle");
		String packageName = "test.spring.mvc.portlet.gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName, SPRING_MVC_PORTLET);

		wizardAction.next();

		wizardAction.newModuleInfo.preparePackageName(packageName);

		wizardAction.finish();

		String[] projectNames = {liferayWorkspace.getLiferayWorkspaceName(), "wars", projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Rule
	public TimestampSupport timestamp = new TimestampSupport(bot);

}