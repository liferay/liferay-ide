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
 *
 * Contributors:
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 */

package com.liferay.ide.idea.util;

import java.io.File;
import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Greg Amerson
 * @author Terry Jia
 */
public class FileListing {

	public static List<File> getFileListing(File aStartingDir) throws FileNotFoundException {
		List<File> result = new ArrayList<>();

		File[] filesAndDirs = aStartingDir.listFiles();

		List<File> filesDirs = Arrays.asList(filesAndDirs);

		for (File file : filesDirs) {
			result.add(file);

			if (!file.isFile()) {
				List<File> deeperList = getFileListing(file);

				result.addAll(deeperList);
			}
		}

		return result;
	}

}