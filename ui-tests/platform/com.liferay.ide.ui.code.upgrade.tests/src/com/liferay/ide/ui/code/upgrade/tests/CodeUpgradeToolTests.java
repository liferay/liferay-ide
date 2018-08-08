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

package com.liferay.ide.ui.code.upgrade.tests;

import com.liferay.ide.ui.liferay.SwtbotBase;
import com.liferay.ide.ui.liferay.page.view.CodeUpgradeView;
import com.liferay.ide.ui.swtbot.util.StringPool;

import org.eclipse.core.runtime.Platform;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Terry Jia
 * @author Ying Xu
 */
public class CodeUpgradeToolTests extends SwtbotBase {

	@AfterClass
	public static void restartUpgradeTool() {
		viewAction.codeUpgrade.restartUpgrade();

		dialogAction.confirm(YES);

		viewAction.codeUpgrade.switchGear(0);
	}

	@BeforeClass
	public static void runCodeUpgradeTool() {
		viewAction.showCodeUpgradeView();

		viewAction.codeUpgrade.switchGear(0);

		// the number of gears starts from 0, that means, the Import Page should be 1 or not 2

		viewAction.codeUpgrade.switchGear(1);

		viewAction.codeUpgrade.switchGear(0);

		viewAction.codeUpgrade.showAllPages();

		dialogAction.confirm(YES);
	}

	@Test
	public void testBuild() {
		viewAction.codeUpgrade.switchGear(8);

		validationAction.assertEnabledTrue(_codeUpgradeView.getBuildBtn());
	}

	@Ignore("unstable need more research")
	@Test
	public void testBuildServices() {
		viewAction.codeUpgrade.switchGear(5);

		validationAction.assertEnabledTrue(_codeUpgradeView.getBuildServicesBtn());
	}

	@Ignore("unstable need more research")
	@Test
	public void testCustomJsp() {
		viewAction.codeUpgrade.switchGear(7);

		String workspacePath = envAction.getEclipseWorkspacePathOSString();

		if ("win32".equals(Platform.getOS())) {
			workspacePath = workspacePath.replaceAll("\\\\", "/");
		}

		validationAction.assertTextEquals(workspacePath, _codeUpgradeView.getConvertedProjectLocation());

		validationAction.assertEnabledTrue(_codeUpgradeView.getBrowseBtn());

		validationAction.assertEnabledTrue(_codeUpgradeView.getSelectProjectsBtn());

		validationAction.assertEnabledTrue(_codeUpgradeView.getRefreshResultsBtn());

		validationAction.assertEnabledTrue(_codeUpgradeView.getClearResultsBtn());
	}

	@Test
	public void testFindBreakingChanges() {
		viewAction.codeUpgrade.switchGear(3);

		validationAction.assertEnabledTrue(_codeUpgradeView.getFindBreakingChangesBtn());

		validationAction.assertEnabledFalse(_codeUpgradeView.getAutomaticallyCorrectProblemsBtn());

		validationAction.assertEnabledTrue(_codeUpgradeView.getExpandAllBtn());

		validationAction.assertEnabledTrue(_codeUpgradeView.getCollapseAllBtn());

		validationAction.assertEnabledTrue(_codeUpgradeView.getOpenIgnoredListBtn());
	}

	@Test
	public void testGear() {
		viewAction.codeUpgrade.switchGear(0);

		viewAction.codeUpgrade.switchGear(1);

		viewAction.codeUpgrade.switchGear(2);

		viewAction.codeUpgrade.switchGear(3);

		viewAction.codeUpgrade.switchGear(4);

		viewAction.codeUpgrade.switchGear(5);

		viewAction.codeUpgrade.switchGear(6);

		viewAction.codeUpgrade.switchGear(7);

		viewAction.codeUpgrade.switchGear(8);

		viewAction.codeUpgrade.switchGear(9);
	}

	@Test
	public void testLayoutTemplate() {
		viewAction.codeUpgrade.switchGear(6);

		validationAction.assertEnabledTrue(_codeUpgradeView.getUpgradeBtn());
	}

	@Test
	public void testSelectProjectsToUpgrade() {
		String bundleUrl =
			"https://releases-cdn.liferay.com/portal/7.0.6-ga7/liferay-ce-portal-tomcat-7.0-ga7-20180507111753223.zip";

		viewAction.codeUpgrade.switchGear(1);

		validationAction.assertTextEquals(StringPool.BLANK, _codeUpgradeView.getPluginsSdkOrMavenProjectRootLocation());

		validationAction.assertEnabledTrue(_codeUpgradeView.getBrowseBtn());

		validationAction.assertCheckedTrue(_codeUpgradeView.getDownloadLiferayBundleRecommended());

		validationAction.assertTextEquals(LIFERAY_7_X, _codeUpgradeView.getServerName());

		validationAction.assertTextEquals(bundleUrl, _codeUpgradeView.getBundleUrl());

		validationAction.assertEnabledFalse(_codeUpgradeView.getImportProjectsBtn());

		viewAction.codeUpgrade.prepareMigrateLayout(UPGRADE_TO_LIFERAY_PLUGINS_SDK_7);

		validationAction.assertTextEquals(StringPool.BLANK, _codeUpgradeView.getLiferayServerName());

		validationAction.assertEnabledTrue(_codeUpgradeView.getAddServerBtn());
	}

	@Test
	public void testUpdateDescriptorFiles() {
		viewAction.codeUpgrade.switchGear(4);

		validationAction.assertEnabledTrue(_codeUpgradeView.getUpgradeBtn());
	}

	@Test
	public void testUpgradePomFiles() {
		viewAction.codeUpgrade.switchGear(2);

		validationAction.assertEnabledTrue(_codeUpgradeView.getSelectAllBtn());

		validationAction.assertEnabledTrue(_codeUpgradeView.getDeselectAllBtn());

		validationAction.assertEnabledTrue(_codeUpgradeView.getUpgradeSelectedBtn());
	}

	private static final CodeUpgradeView _codeUpgradeView = new CodeUpgradeView(bot);

}