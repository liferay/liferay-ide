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
import com.liferay.ide.ui.liferay.support.project.ProjectSupport;
import com.liferay.ide.ui.liferay.support.workspace.LiferayWorkspaceMavenSupport;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Rui Wang
 */
@Ignore("ignore as the problem of deleting Liferay workspace")
public class NewPortletModuleLiferayWorkspaceMavenTests extends SwtbotBase {

	@ClassRule
	public static LiferayWorkspaceMavenSupport liferayWorkspace = new LiferayWorkspaceMavenSupport(bot);

	@Test
	public void createFreemarkerPortlet() {
		String packageName = "test.freemarker.portlet.maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(project.getName(), FREEMARKER_PORTLET);

		wizardAction.next();

		wizardAction.newModuleInfo.preparePackageName(packageName);

		wizardAction.finish();

		viewAction.project.openUpdateMavenProjectDialog(liferayWorkspace.getName());

		dialogAction.updateMavenProject.selectAll();

		dialogAction.confirm();

		jobAction.waitForUpdateMavenProject();

		String[] projectNames = {liferayWorkspace.getName(), liferayWorkspace.getModulesDirName(), project.getName()};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDelete(projectNames);
	}

	@Test
	public void createMvcPortlet() {
		String packageName = "test.mvc.portlet.maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(project.getName(), MVC_PORTLET);

		wizardAction.next();

		wizardAction.newModuleInfo.preparePackageName(packageName);

		wizardAction.finish();

		viewAction.project.openUpdateMavenProjectDialog(liferayWorkspace.getName());

		dialogAction.updateMavenProject.selectAll();

		dialogAction.confirm();

		jobAction.waitForUpdateMavenProject();

		String[] projectNames = {liferayWorkspace.getName(), liferayWorkspace.getModulesDirName(), project.getName()};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		jobAction.waitForValidate(project.getName());

		viewAction.project.closeAndDelete(projectNames);
	}

	@Test
	public void createNpmAngularPortlet() {
		String packageName = "test.npm.angular.portlet.maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(project.getName(), NPM_ANGULAR_PORTLET);

		wizardAction.next();

		wizardAction.newModuleInfo.preparePackageName(packageName);

		wizardAction.finish();

		viewAction.project.openUpdateMavenProjectDialog(liferayWorkspace.getName());

		dialogAction.updateMavenProject.selectAll();

		dialogAction.confirm();

		jobAction.waitForUpdateMavenProject();

		String[] projectNames = {liferayWorkspace.getName(), liferayWorkspace.getModulesDirName(), project.getName()};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDelete(projectNames);
	}

	@Test
	public void createNpmReactPortlet() {
		String packageName = "test.npm.react.portlet.maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(project.getName(), NPM_REACT_PORTLET);

		wizardAction.next();

		wizardAction.newModuleInfo.preparePackageName(packageName);

		wizardAction.finish();

		viewAction.project.openUpdateMavenProjectDialog(liferayWorkspace.getName());

		dialogAction.updateMavenProject.selectAll();

		dialogAction.confirm();

		jobAction.waitForUpdateMavenProject();

		String[] projectNames = {liferayWorkspace.getName(), liferayWorkspace.getModulesDirName(), project.getName()};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDelete(projectNames);
	}

	@Test
	public void createNpmVuejsPortlet() {
		String packageName = "test.npm.vuejs.portlet.maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(project.getName(), NPM_VUEJS_PORTLET);

		wizardAction.next();

		wizardAction.newModuleInfo.preparePackageName(packageName);

		wizardAction.finish();

		viewAction.project.openUpdateMavenProjectDialog(liferayWorkspace.getName());

		dialogAction.updateMavenProject.selectAll();

		dialogAction.confirm();

		jobAction.waitForUpdateMavenProject();

		String[] projectNames = {liferayWorkspace.getName(), liferayWorkspace.getModulesDirName(), project.getName()};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDelete(projectNames);
	}

	@Test
	public void createPortlet() {
		String packageName = "test.portlet.maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(project.getName(), PORTLET);

		wizardAction.next();

		wizardAction.newModuleInfo.preparePackageName(packageName);

		wizardAction.finish();

		viewAction.project.openUpdateMavenProjectDialog(liferayWorkspace.getName());

		dialogAction.updateMavenProject.selectAll();

		dialogAction.confirm();

		jobAction.waitForUpdateMavenProject();

		String[] projectNames = {liferayWorkspace.getName(), liferayWorkspace.getModulesDirName(), project.getName()};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDelete(projectNames);
	}

	@Ignore("ignore to wait IDE-3579 as it will take too long unexpected")
	@Test
	public void createSoyPortlet() {
		String packageName = "test.soy.portlet.maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(project.getName(), SOY_PORTLET);

		wizardAction.next();

		wizardAction.newModuleInfo.preparePackageName(packageName);

		wizardAction.finish();

		viewAction.project.openUpdateMavenProjectDialog(liferayWorkspace.getName());

		dialogAction.updateMavenProject.selectAll();

		dialogAction.confirm();

		jobAction.waitForUpdateMavenProject();

		Assert.assertTrue(viewAction.project.visibleFileTry(project.getName()));

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void createSpringMvcPortlet() {
		String packageName = "test.spring.mvc.portlet.maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(project.getName(), SPRING_MVC_PORTLET);

		wizardAction.next();

		wizardAction.newModuleInfo.preparePackageName(packageName);

		wizardAction.finish();

		viewAction.project.openUpdateMavenProjectDialog(liferayWorkspace.getName());

		dialogAction.updateMavenProject.selectAll();

		dialogAction.confirm();

		jobAction.waitForUpdateMavenProject();

		String[] projectNames = {liferayWorkspace.getName(), liferayWorkspace.getWarsDirName(), project.getName()};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDelete(projectNames);
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

}