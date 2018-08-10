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

package com.liferay.ide.ui.liferay.util;

import java.io.File;

/**
 * @author Terry Jia
 */
public class CSVReader {

	public static String[][] readCSV(File file) {
		String[] lines = FileUtil.readLinesFromFile(file);

		if ((lines == null) || (lines.length == 0)) {
			return null;
		}

		String[] s = lines[0].split(",");

		String[][] results = new String[lines.length][s.length];

		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];

			String[] columns = line.split(",");

			for (int t = 0; t < columns.length; t++) {
				results[i][t] = columns[t];
			}
		}

		return results;
	}

}