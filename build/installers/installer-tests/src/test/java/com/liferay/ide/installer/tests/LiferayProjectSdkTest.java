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

/**
 * @author Terry Jia
 */
public class LiferayProjectSdkTest {

	@EnabledOnOs(OS.WINDOWS)
	@Test
	public void quickInstallOnWindows() {
		Assertions.assertTrue(true);
	}

	@EnabledOnOs(OS.LINUX)
	@Test
	public void quickInstallOnLinux() {
		Assertions.assertTrue(true);
	}

}
