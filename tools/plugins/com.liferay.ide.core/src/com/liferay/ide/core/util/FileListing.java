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

package com.liferay.ide.core.util;

import java.io.File;
import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * @author Gregory Amerson
 */
public class FileListing {

	public static List<File> getFileListing(File dir) throws FileNotFoundException {
		File[] files = dir.listFiles();

		if (ListUtil.isEmpty(files)) {
			return Collections.emptyList();
		}

		List<File> result = new ArrayList<>();

		for (File file : files) {
			result.add(file);

			if (!file.isFile()) {
				List<File> deeperList = getFileListing(file);

				result.addAll(deeperList);
			}
		}

		return result;
	}

	/**
	 * Recursively walk a directory tree and return a List of all Files found; the
	 * List is sorted using File.compareTo().
	 *
	 * @param aStartingDir
	 *            is a valid directory, which can be read.
	 */
	public static List<File> getFileListing(File aStartingDir, boolean sort) throws FileNotFoundException {
		_validateDirectory(aStartingDir);

		List<File> result = getFileListing(aStartingDir);

		if (sort) {
			Collections.sort(result);
		}

		return result;
	}

	public static List<IPath> getFileListing(File dir, String fileType) {
		Collection<File> files = FileUtils.listFiles(dir, new String[] {fileType}, true);

		Stream<File> stream = files.stream();

		return stream.filter(
			File::exists
		).map(
			file -> new Path(file.getPath())
		).collect(
			Collectors.toList()
		);
	}

	/**
	 * Directory is valid if it exists, does not represent a file, and can be read.
	 */
	private static void _validateDirectory(File aDirectory) throws FileNotFoundException {
		if (aDirectory == null) {
			throw new IllegalArgumentException("Directory should not be null");
		}

		if (!aDirectory.exists()) {
			throw new FileNotFoundException("Directory does not exist: " + aDirectory);
		}

		if (!aDirectory.isDirectory()) {
			throw new IllegalArgumentException("Is not a directory: " + aDirectory);
		}

		if (!aDirectory.canRead()) {
			throw new IllegalArgumentException("Directory cannot be read: " + aDirectory);
		}
	}

}