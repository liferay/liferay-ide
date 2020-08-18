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

package com.liferay.ide.test.core.base.util;

import java.io.File;
import java.io.IOException;

/**
 * @author Terry Jia
 */
public class FileUtil extends com.liferay.ide.core.util.FileUtil {

	public static void copyDirectiory(String sourceDir, String targetDir) throws IOException {
		new File(
			targetDir
		).mkdirs();

		File[] files = new File(
			sourceDir
		).listFiles();

		for (File file : files) {
			if (file.isFile()) {
				File sourceFile = file;
				File targetFile = new File(
					new File(
						targetDir
					).getAbsolutePath() + File.separator + file.getName());

				copyFile(sourceFile, targetFile);
			}

			if (file.isDirectory()) {
				String dir1 = sourceDir + "/" + file.getName();
				String dir2 = targetDir + "/" + file.getName();

				copyDirectiory(dir1, dir2);
			}
		}
	}

}