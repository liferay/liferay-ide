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

package com.liferay.ide.functional.jsf.tests;

import com.liferay.ide.functional.liferay.SwtbotBase;
import com.liferay.ide.functional.liferay.support.project.ProjectSupport;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Ying Xu
 */
public class NewJsfProjectWizardGradleTests extends SwtbotBase {

	@Test
	public void createICEFaces() {
		wizardAction.openNewLiferayJsfProjectWizard();

		wizardAction.newLiferayJsf.prepareGradle(project.getName(), ICEFACES);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		Assert.assertTrue(viewAction.project.visibleFileTry(project.getName()));

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void createJSFStandard() {
		wizardAction.openNewLiferayJsfProjectWizard();

		wizardAction.newLiferayJsf.prepareGradle(project.getName(), JSF_STANDARD);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		Assert.assertTrue(viewAction.project.visibleFileTry(project.getName()));

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void createLiferayFacesAlloy() {
		wizardAction.openNewLiferayJsfProjectWizard();

		wizardAction.newLiferayJsf.prepareGradle(project.getName(), LIFERAY_FACES_ALLOY);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		Assert.assertTrue(viewAction.project.visibleFileTry(project.getName()));

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void createPrimeFaces() {
		wizardAction.openNewLiferayJsfProjectWizard();

		wizardAction.newLiferayJsf.prepareGradle(project.getName(), PRIMEFACES);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		Assert.assertTrue(viewAction.project.visibleFileTry(project.getName()));

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void createRichFaces() {
		wizardAction.openNewLiferayJsfProjectWizard();

		wizardAction.newLiferayJsf.prepareGradle(project.getName(), RICHFACES);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		Assert.assertTrue(viewAction.project.visibleFileTry(project.getName()));

		viewAction.project.closeAndDelete(project.getName());
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

}