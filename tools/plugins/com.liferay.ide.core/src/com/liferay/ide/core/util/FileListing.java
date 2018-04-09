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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * @author Greg Amerson
 */
public class FileListing {

	public static IPath findFilePattern(File location, String pattern) {
		try {
			List<File> fileList = getFileListing(location, false);

			for (File file : fileList) {
				if (file.getPath().contains(pattern)) {

					// found jvm

					File jreRoot = file.getParentFile().getParentFile();

					return new Path(jreRoot.getAbsolutePath());
				}
			}
		}
		catch (FileNotFoundException fnfe) {
		}

		return null;
	}

	public static List<File> getFileListing(File aStartingDir) throws FileNotFoundException {
		List<File> result = new ArrayList<>();

		File[] filesAndDirs = aStartingDir.listFiles();

		if ( ListUtil.isEmpty(filesAndDirs)) {
			return Collections.emptyList();
		}

		List<File> filesDirs = Arrays.asList(filesAndDirs);

		for (File file : filesDirs) {

			// always add, even if directory

			result.add(file);

			if (!file.isFile()) {

				// must be a directory recursive call!

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

	public static List<IPath> getFileListing(File aStartingDir, String fileType) {
		Collection<File> listFilesCollection = FileUtils.listFiles(aStartingDir, new String[] {fileType}, true);

		Stream<File> libStream = listFilesCollection.stream();

		List<IPath> retVal = libStream.filter(
			file -> file.exists()
		).map(
			file -> new Path(file.getPath())
		).collect(
			Collectors.toList()
		);

		return retVal;
	}

	/**
	 * Directory is valid if it exists, does not represent a file, and can be read.
	 */
	private static void _validateDirectory(File aDirectory) throws FileNotFoundException {
		if (aDirectory == null) {
			throw new IllegalArgumentException("Directory should not be null.");
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