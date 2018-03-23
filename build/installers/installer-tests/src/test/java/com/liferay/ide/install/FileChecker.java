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
package com.liferay.ide.install;

import java.io.File;

/**
 * @author Ashley Yuan
 * @author Terry Jia
 */
public class FileChecker {

	String usrHome = System.getProperty("user.home");
	// usrHome=System.g.getproperty("user.dir")

	public static boolean isFileExists(String dir, String filename) {
		File file = new File(dir + "/" + filename);

		return file.exists();

	}

	public boolean isFileExistsWin(String filename) {
		String dir = usrHome + "/.jpm";

		return isFileExists(dir, filename);
	}

	public boolean isTokenExistsWin() {
		String dir = usrHome + "/.liferay";
		String filename = "token";

		return isFileExists(dir, filename);
	}

	public boolean isBundleExistsWin(String portal) {
		String dir = usrHome + "/.liferay/bundles";

		return isFileExists(dir, portal);
	}
}
