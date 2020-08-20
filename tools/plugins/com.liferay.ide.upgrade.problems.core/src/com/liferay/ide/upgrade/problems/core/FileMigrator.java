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

package com.liferay.ide.upgrade.problems.core;

import com.liferay.ide.upgrade.plan.core.UpgradeProblem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import java.util.Collection;
import java.util.Dictionary;
import java.util.List;

/**
 * @author Gregory Amerson
 */
public interface FileMigrator {

	public List<UpgradeProblem> analyze(File file);

	public default String readFully(InputStream inputStream) throws IOException {
		if (inputStream == null) {
			return null;
		}

		char[] buffer = new char[0x10000];

		StringBuilder out = new StringBuilder();

		try (Reader in = new InputStreamReader(inputStream, "UTF-8")) {
			int read;

			do {
				read = in.read(buffer, 0, buffer.length);

				if (read > 0) {
					out.append(buffer, 0, read);
				}
			}
			while (read >= 0);
		}

		inputStream.close();

		return out.toString();
	}

	public int reportProblems(File file, Collection<UpgradeProblem> upgradeProblems);

	public default String safeGet(Dictionary<String, Object> properties, String key) {
		Object value = properties.get(key);

		if (value == null) {
			return "";
		}

		return value.toString();
	}

}