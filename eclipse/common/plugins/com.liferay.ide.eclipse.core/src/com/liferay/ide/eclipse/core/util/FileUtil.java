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

import com.liferay.ide.eclipse.core.CorePlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;

/**
 * @author Greg Amerson
 */
public class FileUtil {

	public static void clearContents(File versionFile) {
		if (versionFile != null && versionFile.exists()) {
			try {
				RandomAccessFile file = new RandomAccessFile(versionFile, "rw");
				file.setLength(0);
				file.close();
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}

		}
	}

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

	public static String readContents(File file) {
		if (file == null) {
			return null;
		}

		if (!file.exists()) {
			return null;
		}

		StringBuffer contents = new StringBuffer();

		try {
			FileReader fileReader = new FileReader(file);

			BufferedReader bufferedReader = new BufferedReader(fileReader);

			String line;

			while ((line = bufferedReader.readLine()) != null) {
				contents.append(line);
			}
		}
		catch (Exception e) {
			CorePlugin.logError("Could not read file: " + file.getPath());
		}

		return contents.toString();
	}

	public static String[] readLinesFromFile(File file) {
		if (file == null) {
			return null;
		}

		if (!file.exists()) {
			return null;
		}

		List<String> lines = new ArrayList<String>();

		try {
			FileReader fileReader = new FileReader(file);

			BufferedReader bufferedReader = new BufferedReader(fileReader);

			String line;

			while ((line = bufferedReader.readLine()) != null) {
				lines.add(line);
			}
		}
		catch (Exception e) {
			CorePlugin.logError("Could not read file: " + file.getPath());
		}

		return lines.toArray(new String[lines.size()]);
	}

	public static String validateNewFolder(IFolder docroot, String folderValue) {
		if (docroot == null || folderValue == null) {
			return null;
		}

		if (CoreUtil.isNullOrEmpty(folderValue)) {
			return "Folder value cannot be empty.";
		}

		if (!Path.ROOT.isValidPath(folderValue)) {
			return "Folder value is invalid.";
		}

		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		IStatus result =
			workspace.validatePath(docroot.getFolder(folderValue).getFullPath().toString(), IResource.FOLDER);

		if (!result.isOK()) {
			return result.getMessage();
		}

		if (docroot.getFolder(new Path(folderValue)).exists()) {
			return "Folder already exists.";
		}

		return null;
	}

}
