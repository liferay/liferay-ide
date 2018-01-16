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
import org.junit.Assert;
import org.junit.BeforeClass;
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

		Assert.assertTrue(_codeUpgradeView.getBuildBtn().isEnabled());
	}

	@Test
	public void testBuildServices() {
		viewAction.codeUpgrade.switchGear(5);

		Assert.assertTrue(_codeUpgradeView.getBuildServicesBtn().isEnabled());
	}

	@Test
	public void testCustomJsp() {
		viewAction.codeUpgrade.switchGear(7);

		String workspacePath = envAction.getEclipseWorkspacePath().toOSString();

		if (Platform.getOS().equals("win32")) {
			workspacePath = workspacePath.replaceAll("\\\\", "/");
		}

		Assert.assertEquals(workspacePath, _codeUpgradeView.getConvertedProjectLocation().getText());

		Assert.assertTrue(_codeUpgradeView.getBrowseBtn().isEnabled());

		Assert.assertTrue(_codeUpgradeView.getSelectProjectsBtn().isEnabled());

		Assert.assertTrue(_codeUpgradeView.getRefreshResultsBtn().isEnabled());

		Assert.assertTrue(_codeUpgradeView.getClearResultsBtn().isEnabled());
	}

	@Test
	public void testFindBreakingChanges() {
		viewAction.codeUpgrade.switchGear(3);

		Assert.assertTrue(_codeUpgradeView.getFindBreakingChangesBtn().isEnabled());

		Assert.assertFalse(_codeUpgradeView.getAutomaticallyCorrectProblemsBtn().isEnabled());

		Assert.assertTrue(_codeUpgradeView.getExpandAllBtn().isEnabled());

		Assert.assertTrue(_codeUpgradeView.getCollapseAllBtn().isEnabled());

		Assert.assertTrue(_codeUpgradeView.getOpenIgnoredListBtn().isEnabled());
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

		Assert.assertTrue(_codeUpgradeView.getUpgradeBtn().isEnabled());
	}

	@Test
	public void testSelectProjectsToUpgrade() {
		String bundleUrl =
			"https://cdn.lfrs.sl/releases.liferay.com/portal/7.0.4-ga5" +
				"/liferay-ce-portal-tomcat-7.0-ga5-20171018150113838.zip";

		viewAction.codeUpgrade.switchGear(1);

		Assert.assertEquals(StringPool.BLANK, _codeUpgradeView.getPluginsSdkOrMavenProjectRootLocation().getText());

		Assert.assertTrue(_codeUpgradeView.getBrowseBtn().isEnabled());

		Assert.assertTrue(_codeUpgradeView.getDownloadLiferayBundleRecommended().isChecked());

		Assert.assertEquals(LIFERAY_7_X, _codeUpgradeView.getServerName().getText());

		Assert.assertEquals(bundleUrl, _codeUpgradeView.getBundleUrl().getText());

		Assert.assertFalse(_codeUpgradeView.getImportProjectsBtn().isEnabled());

		viewAction.codeUpgrade.prepareMigrateLayout(UPGRADE_TO_LIFERAY_PLUGINS_SDK_7);

		Assert.assertEquals(StringPool.BLANK, _codeUpgradeView.getLiferayServerName().getText());

		Assert.assertTrue(_codeUpgradeView.getAddServerBtn().isEnabled());
	}

	@Test
	public void testUpdateDescriptorFiles() {
		viewAction.codeUpgrade.switchGear(4);

		Assert.assertTrue(_codeUpgradeView.getUpgradeBtn().isEnabled());
	}

	@Test
	public void testUpgradePomFiles() {
		viewAction.codeUpgrade.switchGear(2);

		Assert.assertTrue(_codeUpgradeView.getSelectAllBtn().isEnabled());

		Assert.assertTrue(_codeUpgradeView.getDeselectAllBtn().isEnabled());

		Assert.assertTrue(_codeUpgradeView.getUpgradeSelectedBtn().isEnabled());
	}

	private static final CodeUpgradeView _codeUpgradeView = new CodeUpgradeView(bot);

}