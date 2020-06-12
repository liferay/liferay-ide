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

package com.liferay.ide.functional.layouttpl.tests;

import com.liferay.ide.functional.liferay.SwtbotBase;
import com.liferay.ide.functional.liferay.support.project.ProjectSupport;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Ying Xu
 */
@Ignore("ignore because blade 3.10.0 does not support the creation of gradle standalone")
public class NewLayoutTemplateModuleWizardGradleTests extends SwtbotBase {

	@Test
	public void createLayoutTemplate() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), LAYOUT_TEMPLATE);

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		Assert.assertTrue(viewAction.project.visibleFileTry(project.getName()));

		viewAction.project.closeAndDelete(project.getName());
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

}