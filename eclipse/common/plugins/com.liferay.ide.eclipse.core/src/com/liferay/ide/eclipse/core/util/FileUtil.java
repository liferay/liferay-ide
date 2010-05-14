/*******************************************************************************
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
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
 *******************************************************************************/

package com.liferay.ide.eclipse.core.util;

import java.io.File;

/**
 * @author Greg Amerson
 */
public class FileUtil {

	public static void deleteDir(File directory, boolean removeAll) {

		if (directory == null || !directory.isDirectory()) {
			return;
		}

		for (File file : directory.listFiles()) {
			if (file.isDirectory() && removeAll) {
				deleteDir(file, removeAll);
			}
			else {
				file.delete();
			}
		}

		directory.delete();
	}

}
