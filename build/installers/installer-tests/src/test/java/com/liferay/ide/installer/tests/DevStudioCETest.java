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

package com.liferay.ide.installer.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import com.liferay.ide.install.InstallerTestsBase;
import com.liferay.ide.installer.tests.util.InstallerUtil;

/**
 * @author Terry Jia
 */
public class DevStudioCETest extends InstallerTestsBase{

	@EnabledOnOs(OS.WINDOWS)
	@Test
	public void quickInstallOnWindows() throws Exception{

		String cmd = InstallerUtil.getDevStudioCEWinFile().getPath() + " --mode unattended --proxyhttps jj";

		String processName = InstallerUtil.getDevStudioCEFullNameWin();

		commandHelper.exec(cmd);

		Assertions.assertTrue(processHelper.checkProcessWin(processName));
		
		Assertions.assertTrue(processHelper.waitProcessWin(processName));
		
		Thread.sleep(1000);

		Assertions.assertTrue(fileChecker.isTokenExistsWin());
		
		Assertions.assertTrue(appChecker.isAppInstalled("jpm version", JPM_VERSION));	
		Assertions.assertTrue(appChecker.isAppInstalled("blade version", BLADE_VERSION));
		Assertions.assertTrue(appChecker.isAppInstalled("bnd version", BND_VERSION));
		Assertions.assertTrue(appChecker.isAppInstalled("gw",GW_OUTPUT));
	}

	@EnabledOnOs(OS.LINUX)
	@Test
	public void quickInstallOnLinux() {
		Assertions.assertTrue(true);
	}

}
