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

package com.liferay.ide.portlet.ui.editor;

import com.liferay.ide.core.util.FileListing;

import java.io.File;
import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * @author Gregory Amerson
 */
public class PortalTldViewerFilter extends ViewerFilter {

	public PortalTldViewerFilter(File base, String[] roots, String[] existingTlds) {
		this.base = base;

		this.roots = roots;

		this.existingTlds = existingTlds;

		validRoots = new IPath[roots.length];

		for (int i = 0; i < roots.length; i++) {
			File fileRoot = new File(base, roots[i]);

			if (fileRoot.exists()) {
				validRoots[i] = new Path(fileRoot.getPath());
			}
		}
	}

	@Override
	public boolean select(Viewer viewer, Object parent, Object element) {
		if (element instanceof File) {
			File file = (File)element;

			IPath filePath = new Path(file.getPath());

			boolean validRootFound = false;

			for (IPath validRoot : validRoots) {
				if (validRoot.isPrefixOf(filePath)) {
					validRootFound = true;

					break;
				}
			}

			if (!validRootFound) {
				return false;
			}

			if (cachedDirs.contains(file)) {
				return true;
			}
			else if (file.isDirectory()) {

				// we only want to show the directory if it had children that
				// have jsps

				if (directoryContainsFiles(file, "tld", viewer)) {
					cachedDirs.add(file);

					return true;
				}
			}
			else {
				for (String existingJar : existingTlds) {
					if (filePath.lastSegment().equals(existingJar.trim())) {
						return false;
					}
				}

				if (filePath.getFileExtension().contains("tld")) {
					return true;
				}
			}
		}

		return false;
	}

	protected boolean directoryContainsFiles(File dir, String ext, Viewer viewer) {
		try {
			List<File> files = FileListing.getFileListing(dir);

			for (File file : files) {
				IPath filePath = new Path(file.getPath());

				if ((filePath.getFileExtension() != null) && filePath.getFileExtension().contains(ext)) {
					return true;
				}
			}
		}
		catch (FileNotFoundException fnfe) {

			// do nothing

		}

		return false;
	}

	protected File base;
	protected List<File> cachedDirs = new ArrayList<>();
	protected String[] existingTlds = null;
	protected String[] roots = null;
	protected IPath[] validRoots;

}