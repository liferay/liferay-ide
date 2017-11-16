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

package com.liferay.ide.ui.jsf.tests;

import com.liferay.ide.ui.liferay.SwtbotBase;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Ying Xu
 */
public class NewJsfProjectWizardMavenTests extends SwtbotBase {

	@Test
	public void createICEFaces() {
		String projectName = "test-ice-faces-maven";

		wizardAction.openNewLiferayJsfProjectWizard();

		wizardAction.prepareJsfProjectMaven(projectName, ICEFACES);

		wizardAction.finishToWait();

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectName));

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createJsfStandard() {
		String projectName = "test-jsf-standard-maven";

		wizardAction.openNewLiferayJsfProjectWizard();

		wizardAction.prepareJsfProjectMaven(projectName, JSF_STANDARD);

		wizardAction.finishToWait();

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectName));

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createLiferayFacesAlloy() {
		String projectName = "test-liferay-faces-alloy-maven";

		wizardAction.openNewLiferayJsfProjectWizard();

		wizardAction.prepareJsfProjectMaven(projectName, LIFERAY_FACES_ALLOY);

		wizardAction.finishToWait();

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectName));

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createPrimeFaces() {
		String projectName = "test-prime-faces-maven";

		wizardAction.openNewLiferayJsfProjectWizard();

		wizardAction.prepareJsfProjectMaven(projectName, PRIMEFACES);

		wizardAction.finishToWait();

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectName));

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createRichFaces() {
		String projectName = "test-rich-faces-maven";

		wizardAction.openNewLiferayJsfProjectWizard();

		wizardAction.prepareJsfProjectMaven(projectName, RICHFACES);

		wizardAction.finishToWait();

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectName));

		viewAction.deleteProject(projectName);
	}

}