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

package com.liferay.ide.functional.springmvc.portlet.tests;

import com.liferay.ide.functional.liferay.SwtbotBase;
import com.liferay.ide.functional.liferay.support.project.ProjectSupport;
import com.liferay.ide.functional.liferay.support.workspace.LiferayWorkspaceGradle72Support;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Rui Wang
 */
public class NewSpringMvcPortletProjectGradleTests extends SwtbotBase {

	@ClassRule
	public static LiferayWorkspaceGradle72Support liferayWorkspace = new LiferayWorkspaceGradle72Support(bot);

	@Test
	public void createPortletMVC4SpringEmbeddedJsp() {
		wizardAction.openNewLiferaySpringMvcPortletWizard();

		wizardAction.newSpringMvcPortlet.prepareProjectName(project.getName());

		wizardAction.next();

		wizardAction.newSpringMvcPortlet.prepareSelection(PORTLETMVC4SPRING, EMBEDDED, JSP_WITH_VIEW_TYPE);

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getModuleFiles(project.getName()));
	}

	@Test
	public void createPortletMVC4SpringEmbeddedThymeleaf() {
		wizardAction.openNewLiferaySpringMvcPortletWizard();

		wizardAction.newSpringMvcPortlet.prepareProjectName(project.getName());

		wizardAction.next();

		wizardAction.newSpringMvcPortlet.prepareSelection(PORTLETMVC4SPRING, EMBEDDED, THYMELEAF);

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getModuleFiles(project.getName()));
	}

	@Test
	public void createSpringMvcPortletEmbeddedJsp() {
		wizardAction.openNewLiferaySpringMvcPortletWizard();

		wizardAction.newSpringMvcPortlet.prepareProjectName(project.getName());

		wizardAction.next();

		wizardAction.newSpringMvcPortlet.prepareSelection(SPRING_PORTLET_MVC, EMBEDDED, JSP_WITH_VIEW_TYPE);

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getModuleFiles(project.getName()));
	}

	@Test
	public void createSpringMvcPortletEmbeddedThymeleaf() {
		wizardAction.openNewLiferaySpringMvcPortletWizard();

		wizardAction.newSpringMvcPortlet.prepareProjectName(project.getName());

		wizardAction.next();

		wizardAction.newSpringMvcPortlet.prepareSelection(SPRING_PORTLET_MVC, EMBEDDED, THYMELEAF);

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getModuleFiles(project.getName()));
	}

	@Test
	public void createSpringMvcPortletProvidedJsp() {
		wizardAction.openNewLiferaySpringMvcPortletWizard();

		wizardAction.newSpringMvcPortlet.prepareProjectName(project.getName());

		wizardAction.next();

		wizardAction.newSpringMvcPortlet.prepareSelection(SPRING_PORTLET_MVC, PROVIDED, JSP_WITH_VIEW_TYPE);

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getModuleFiles(project.getName()));
	}

	@Test
	public void createSpringMvcPortletProvidedThymeleaf() {
		wizardAction.openNewLiferaySpringMvcPortletWizard();

		wizardAction.newSpringMvcPortlet.prepareProjectName(project.getName());

		wizardAction.next();

		wizardAction.newSpringMvcPortlet.prepareSelection(SPRING_PORTLET_MVC, PROVIDED, THYMELEAF);

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getModuleFiles(project.getName()));
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

}