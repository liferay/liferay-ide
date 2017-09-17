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

package com.liferay.ide.swtbot.project.ui.tests;

import com.liferay.ide.swtbot.liferay.ui.SwtbotBase;

import org.junit.Test;

/**
 * @author Ying Xu
 */
public class NewLiferayMavenJSFProjectWizardTests extends SwtbotBase {

	@Test
	public void createMavenICEFacesProject()
	{

		String projectName = "testMavenICEFacesProject";

		wizardAction.openNewLiferayJsfProjectWizard();
		wizardAction.prepareJsfProjectMaven(projectName, ICEFACES);
		wizardAction.finishToWait();

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createMavenJSFStandardProject()
	{

		String projectName = "testMavenJSFStandardProject";

		wizardAction.openNewLiferayJsfProjectWizard();
		wizardAction.prepareJsfProjectMaven(projectName, JSF_STANDARD);
		wizardAction.finishToWait();

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createMavenLiferayFacesAlloyProject()
	{

		String projectName = "testMavenLiferayFacesAlloyProject";

		wizardAction.openNewLiferayJsfProjectWizard();
		wizardAction.prepareJsfProjectMaven(projectName, LIFERAY_FACES_ALLOY);
		wizardAction.finishToWait();

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createMavenPrimeFacesProject()
	{

		String projectName = "testMavenPrimeFacesProject";

		wizardAction.openNewLiferayJsfProjectWizard();
		wizardAction.prepareJsfProjectMaven(projectName, PRIMEFACES);
		wizardAction.finishToWait();

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createMavenRichFacesProject()
	{

		String projectName = "testMavenRichFacesProject";

		wizardAction.openNewLiferayJsfProjectWizard();
		wizardAction.prepareJsfProjectMaven(projectName, RICHFACES);
		wizardAction.finishToWait();

		viewAction.deleteProject(projectName);
	}

}