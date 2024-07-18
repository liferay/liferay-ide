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
package com.liferay.ide.installer.tests.util;

import java.io.IOException;

/**
 * @author Ashley Yuan
 * @author Terry Jia
 */
public class AppChecker {

	public static boolean installed(String cmd, String exceptResult) throws IOException {
		boolean execOutput = CommandHelper.execWithResult(cmd).contains(exceptResult);
		boolean execResult = CommandHelper.execWithResult(cmd, true).contains(exceptResult);

		return (execOutput || execResult);
	}

	public static boolean jpmInstalled() throws IOException {
		return installed("jpm version", InstallerUtil.getJpmVersion());
	}

	public static boolean bladeInstalled() throws IOException {
		return installed("blade version", InstallerUtil.getBladeVersion());
	}

}
