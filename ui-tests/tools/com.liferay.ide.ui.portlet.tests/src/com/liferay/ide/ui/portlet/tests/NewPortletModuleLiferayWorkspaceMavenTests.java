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
import com.liferay.ide.ui.liferay.base.LiferayWorkspaceMavenSupport;
import com.liferay.ide.ui.liferay.base.ProjectSupport;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Rui Wang
 */
public class NewPortletModuleLiferayWorkspaceMavenTests extends SwtbotBase {

	@ClassRule
	public static LiferayWorkspaceMavenSupport liferayWorkspace = new LiferayWorkspaceMavenSupport(bot);

	@Test
	public void createFreemarkerPortlet() {
		String projectName = project.getName("test-freemarker-portlet-maven");
		String packageName = "test.freemarker.portlet.maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(projectName, FREEMARKER_PORTLET);

		wizardAction.next();

		wizardAction.newModuleInfo.preparePackageName(packageName);

		wizardAction.finish();

		viewAction.project.openUpdateMavenProjectDialog(liferayWorkspace.getLiferayWorkspaceName());

		dialogAction.updateMavenProject.selectAll();

		dialogAction.confirm();

		jobAction.waitForUpdateMavenProject();

		String[] projectNames =
			{liferayWorkspace.getLiferayWorkspaceName(), liferayWorkspace.getModulesDirName(), projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDelete(projectNames);
	}

	@Test
	public void createMvcPortlet() {
		String projectName = project.getName("test-mvc-portlet-maven");
		String packageName = "test.mvc.portlet.maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(projectName, MVC_PORTLET);

		wizardAction.next();

		wizardAction.newModuleInfo.preparePackageName(packageName);

		wizardAction.finish();

		viewAction.project.openUpdateMavenProjectDialog(liferayWorkspace.getLiferayWorkspaceName());

		dialogAction.updateMavenProject.selectAll();

		dialogAction.confirm();

		jobAction.waitForUpdateMavenProject();

		String[] projectNames =
			{liferayWorkspace.getLiferayWorkspaceName(), liferayWorkspace.getModulesDirName(), projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		jobAction.waitForValidate(projectName);

		viewAction.project.closeAndDelete(projectNames);
	}

	@Test
	public void createNpmAngularPortlet() {
		String projectName = project.getName("test-npm-angular-portlet-maven");
		String packageName = "test.npm.angular.portlet.maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(projectName, NPM_ANGULAR_PORTLET);

		wizardAction.next();

		wizardAction.newModuleInfo.preparePackageName(packageName);

		wizardAction.finish();

		viewAction.project.openUpdateMavenProjectDialog(liferayWorkspace.getLiferayWorkspaceName());

		dialogAction.updateMavenProject.selectAll();

		dialogAction.confirm();

		jobAction.waitForUpdateMavenProject();

		String[] projectNames =
			{liferayWorkspace.getLiferayWorkspaceName(), liferayWorkspace.getModulesDirName(), projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDelete(projectNames);
	}

	@Test
	public void createNpmBillboardjsPortlet() {
		String projectName = project.getName("test-npm-billboardjs-portlet-maven");
		String packageName = "test.npm.billboardjs.portlet.maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(projectName, NPM_BILLBOARDJS_PORLET);

		wizardAction.next();

		wizardAction.newModuleInfo.preparePackageName(packageName);

		wizardAction.finish();

		viewAction.project.openUpdateMavenProjectDialog(liferayWorkspace.getLiferayWorkspaceName());

		dialogAction.updateMavenProject.selectAll();

		dialogAction.confirm();

		jobAction.waitForUpdateMavenProject();

		String[] projectNames =
			{liferayWorkspace.getLiferayWorkspaceName(), liferayWorkspace.getModulesDirName(), projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDelete(projectNames);
	}

	@Test
	public void createNpmIsomorphicPortlet() {
		String projectName = project.getName("test-npm-isomorphic-portlet-maven");
		String packageName = "test.npm.isomorphic.portlet.maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(projectName, NPM_ISOMORPHIC_PORTLET);

		wizardAction.next();

		wizardAction.newModuleInfo.preparePackageName(packageName);

		wizardAction.finish();

		viewAction.project.openUpdateMavenProjectDialog(liferayWorkspace.getLiferayWorkspaceName());

		dialogAction.updateMavenProject.selectAll();

		dialogAction.confirm();

		jobAction.waitForUpdateMavenProject();

		String[] projectNames =
			{liferayWorkspace.getLiferayWorkspaceName(), liferayWorkspace.getModulesDirName(), projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDelete(projectNames);
	}

	@Test
	public void createNpmJqueryPortlet() {
		String projectName = project.getName("test-npm-jquery-portlet-maven");
		String packageName = "test.npm.jquery.portlet.maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(projectName, NPM_JQUERY_PORTLET);

		wizardAction.next();

		wizardAction.newModuleInfo.preparePackageName(packageName);

		wizardAction.finish();

		viewAction.project.openUpdateMavenProjectDialog(liferayWorkspace.getLiferayWorkspaceName());

		dialogAction.updateMavenProject.selectAll();

		dialogAction.confirm();

		jobAction.waitForUpdateMavenProject();

		String[] projectNames =
			{liferayWorkspace.getLiferayWorkspaceName(), liferayWorkspace.getModulesDirName(), projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDelete(projectNames);
	}

	@Test
	public void createNpmMetaljsPortletMaven() {
		String projectName = project.getName("test-npm-metaljs-portlet-maven");
		String packageName = "test.npm.metaljs.portlet.maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(projectName, NPM_METALJS_PORTLET);

		wizardAction.next();

		wizardAction.newModuleInfo.preparePackageName(packageName);

		wizardAction.finish();

		viewAction.project.openUpdateMavenProjectDialog(liferayWorkspace.getLiferayWorkspaceName());

		dialogAction.updateMavenProject.selectAll();

		dialogAction.confirm();

		jobAction.waitForUpdateMavenProject();

		String[] projectNames =
			{liferayWorkspace.getLiferayWorkspaceName(), liferayWorkspace.getModulesDirName(), projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDelete(projectNames);
	}

	@Test
	public void createNpmPortlet() {
		String projectName = project.getName("test-npm-portlet-maven");
		String packageName = "test.npm.portlet.maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(projectName, NPM_PORTLET);

		wizardAction.next();

		wizardAction.newModuleInfo.preparePackageName(packageName);

		wizardAction.finish();

		viewAction.project.openUpdateMavenProjectDialog(liferayWorkspace.getLiferayWorkspaceName());

		dialogAction.updateMavenProject.selectAll();

		dialogAction.confirm();

		jobAction.waitForUpdateMavenProject();

		String[] projectNames =
			{liferayWorkspace.getLiferayWorkspaceName(), liferayWorkspace.getModulesDirName(), projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDelete(projectNames);
	}

	@Test
	public void createNpmReactPortlet() {
		String projectName = project.getName("test-npm-react-portlet-maven");
		String packageName = "test.npm.react.portlet.maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(projectName, NPM_REACT_PORTLET);

		wizardAction.next();

		wizardAction.newModuleInfo.preparePackageName(packageName);

		wizardAction.finish();

		viewAction.project.openUpdateMavenProjectDialog(liferayWorkspace.getLiferayWorkspaceName());

		dialogAction.updateMavenProject.selectAll();

		dialogAction.confirm();

		jobAction.waitForUpdateMavenProject();

		String[] projectNames =
			{liferayWorkspace.getLiferayWorkspaceName(), liferayWorkspace.getModulesDirName(), projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDelete(projectNames);
	}

	@Test
	public void createNpmVuejsPortlet() {
		String projectName = project.getName("test-npm-vuejs-portlet-maven");
		String packageName = "test.npm.vuejs.portlet.maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(projectName, NPM_VUEJS_PORTLET);

		wizardAction.next();

		wizardAction.newModuleInfo.preparePackageName(packageName);

		wizardAction.finish();

		viewAction.project.openUpdateMavenProjectDialog(liferayWorkspace.getLiferayWorkspaceName());

		dialogAction.updateMavenProject.selectAll();

		dialogAction.confirm();

		jobAction.waitForUpdateMavenProject();

		String[] projectNames =
			{liferayWorkspace.getLiferayWorkspaceName(), liferayWorkspace.getModulesDirName(), projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDelete(projectNames);
	}

	@Test
	public void createPortlet() {
		String projectName = project.getName("test-portlet-maven");
		String packageName = "test.portlet.maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(projectName, PORTLET);

		wizardAction.next();

		wizardAction.newModuleInfo.preparePackageName(packageName);

		wizardAction.finish();

		viewAction.project.openUpdateMavenProjectDialog(liferayWorkspace.getLiferayWorkspaceName());

		dialogAction.updateMavenProject.selectAll();

		dialogAction.confirm();

		jobAction.waitForUpdateMavenProject();

		String[] projectNames =
			{liferayWorkspace.getLiferayWorkspaceName(), liferayWorkspace.getModulesDirName(), projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDelete(projectNames);
	}

	@Ignore("ignore to wait IDE-3579 as it will take too long unexpected")
	@Test
	public void createSoyPortlet() {
		String projectName = project.getName("test-soy-portlet-maven");
		String packageName = "test.soy.portlet.maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(projectName, SOY_PORTLET);

		wizardAction.next();

		wizardAction.newModuleInfo.preparePackageName(packageName);

		wizardAction.finish();

		viewAction.project.openUpdateMavenProjectDialog(liferayWorkspace.getLiferayWorkspaceName());

		dialogAction.updateMavenProject.selectAll();

		dialogAction.confirm();

		jobAction.waitForUpdateMavenProject();

		Assert.assertTrue(viewAction.project.visibleFileTry(projectName));

		viewAction.project.closeAndDelete(projectName);
	}

	@Test
	public void createSpringMvcPortlet() {
		String projectName = project.getName("test-spring-mvc-portlet");
		String packageName = "test.spring.mvc.portlet.maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(projectName, SPRING_MVC_PORTLET);

		wizardAction.next();

		wizardAction.newModuleInfo.preparePackageName(packageName);

		wizardAction.finish();

		viewAction.project.openUpdateMavenProjectDialog(liferayWorkspace.getLiferayWorkspaceName());

		dialogAction.updateMavenProject.selectAll();

		dialogAction.confirm();

		jobAction.waitForUpdateMavenProject();

		String[] projectNames =
			{liferayWorkspace.getLiferayWorkspaceName(), liferayWorkspace.getWarsDirName(), projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDelete(projectNames);
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

}