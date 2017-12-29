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

package com.liferay.ide.ui.liferay.base;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * @author Terry Jia
 */
public class LiferayWorkspaceTomcatMavenBase extends TomcatBase {

	@AfterClass
	public static void cleanLiferayWorkspace() {
		String[] modulesFolderNames = {getLiferayWorkspaceName(), getModulesDirName()};

		String[] themesFolderNames = {getLiferayWorkspaceName(), getThemesDirName()};

		String[] warsFolderNames = {getLiferayWorkspaceName(), getWarsDirName()};

		viewAction.project.closeAndDelete(modulesFolderNames);

		viewAction.project.closeAndDelete(themesFolderNames);

		viewAction.project.closeAndDelete(warsFolderNames);

		viewAction.project.closeAndDelete(getLiferayWorkspaceName());
	}

	public static String getLiferayWorkspaceName() {
		return "liferay-workspace-maven-support";
	}

	public static String getModulesDirName() {
		return getLiferayWorkspaceName() + "-modules (in modules)";
	}

	public static String getThemesDirName() {
		return getLiferayWorkspaceName() + "-themes (in themes)";
	}

	public static String getWarsDirName() {
		return getLiferayWorkspaceName() + "-wars (in wars)";
	}

	@BeforeClass
	public static void prepareLiferayWorkspace() throws IOException {
		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareMaven(getLiferayWorkspaceName());

		wizardAction.finish();

		Assert.assertTrue(viewAction.project.visibleFileTry(getLiferayWorkspaceName()));
	}

}