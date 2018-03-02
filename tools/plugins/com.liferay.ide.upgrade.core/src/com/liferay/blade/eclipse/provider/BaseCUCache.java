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

package com.liferay.blade.eclipse.provider;

import com.liferay.blade.api.MigrationConstants;
import com.liferay.ide.core.util.ListUtil;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

/**
 * @author Gregory Amerson
 */
public class BaseCUCache {

	public IFile getIFile(File file) {
		IFile retval = null;

		// first try to find this file in the current workspace

		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		IFile[] files = workspace.getRoot().findFilesForLocationURI(file.toURI());

		// if there are multiple files in this workspace use the shortest path

		if ((files != null) && (files.length == 1)) {
			retval = files[0];
		}
		else if (ListUtil.isNotEmpty(files)) {
			for (IFile ifile : files) {
				if (retval == null) {
					retval = ifile;
				}
				else {

					// prefer the path that is shortest (to avoid a nested
					// version)

					if (ifile.getFullPath().segmentCount() < retval.getFullPath().segmentCount()) {
						retval = ifile;
					}
				}
			}
		}

		if (retval == null) {
			try {
				retval = new WorkspaceHelper().createIFile(MigrationConstants.HELPER_PROJECT_NAME, file);
			}
			catch (CoreException | IOException e) {
				e.printStackTrace();
			}
		}

		return retval;
	}

}