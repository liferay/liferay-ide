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

/**
 * @author Terry Jia
 */
public class InstallersTest {

	@Test
	public void checkDevStudioCEInstallerFiles() {
		Assertions.assertTrue(InstallerUtil.getDevStudioCEWinFile().exists());

		Assertions.assertTrue(InstallerUtil.getDevStudioCELinuxFile().exists());

		Assertions.assertTrue(InstallerUtil.getDevStudioCEMacosFile().exists());
	}

	@Test
	public void checkDevStudioDXPInstallerFiles() {
		Assertions.assertTrue(InstallerUtil.getDevStudioDXPWinFile().exists());

		Assertions.assertTrue(InstallerUtil.getDevStudioDXPLinuxFile().exists());

		Assertions.assertTrue(InstallerUtil.getDevStudioDXPMacosFile().exists());
	}

	@Test
	public void checkProjectSDKInstallerFiles() {
		Assertions.assertTrue(InstallerUtil.getProjectSdkWinFile().exists());

		Assertions.assertTrue(InstallerUtil.getProjectSdkLinuxFile().exists());

		Assertions.assertTrue(InstallerUtil.getProjectSdkMacosFile().exists());
	}

}