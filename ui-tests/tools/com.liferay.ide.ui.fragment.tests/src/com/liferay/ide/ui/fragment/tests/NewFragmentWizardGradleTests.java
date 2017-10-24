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

package com.liferay.ide.ui.fragment.tests;

import com.liferay.ide.ui.liferay.SwtbotBase;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Vicky Wang
 * @author Sunny Shi
 */
public class NewFragmentWizardGradleTests extends SwtbotBase {

	@BeforeClass
	public static void init() throws IOException {
		envAction.unzipServer();
	}

	@Test
	public void createFragmentUseTableActions() {
	}

	@Test
	public void createFragmentWithJsp() {
		String projectName = "test-fragment";

		wizardAction.openNewFragmentWizard();

		wizardAction.openNewRuntimeWizardFragment();

		wizardAction.next();

		wizardAction.prepareLiferay7RuntimeInfo(envAction.getLiferayServerDir().toOSString());

		wizardAction.finish();

		wizardAction.prepareFragmentGradle(projectName);

		wizardAction.next();

		wizardAction.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.announcements.");

		dialogAction.confirm();

		wizardAction.openAddOverrideFilesDialog();

		dialogAction.selectItem("META-INF/resources/configuration.jsp");

		dialogAction.confirm();

		wizardAction.finishToWait();

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createFragmentWithPortletProperites() {
		String projectName = "test-fragment";

		wizardAction.openNewFragmentWizard();

		wizardAction.openNewRuntimeWizardFragment();

		wizardAction.next();

		wizardAction.prepareLiferay7RuntimeInfo(envAction.getLiferayServerDir().toOSString());

		wizardAction.finish();

		wizardAction.prepareFragmentGradle(projectName);

		wizardAction.next();

		wizardAction.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.blogs.web");

		dialogAction.confirm();

		String[] files = {
			"META-INF/resources/blogs_admin/configuration.jsp", "META-INF/resources/blogs_aggregator/init.jsp",
			"META-INF/resources/blogs/asset/abstract.jsp", "META-INF/resources/blogs/edit_entry.jsp",
			"portlet.properties"
		};

		wizardAction.openAddOverrideFilesDialog();

		dialogAction.selectItems(files);

		dialogAction.confirm();

		wizardAction.finishToWait();

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createFragmentWithResourceAction() {
	}

}