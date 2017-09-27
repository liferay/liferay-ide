/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.liferay.blade.eclipse.provider;

import com.liferay.blade.api.MigrationConstants;
import com.liferay.blade.api.SourceFile;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

/**
 * @author Gregory Amerson
 */
public class WorkspaceFile implements SourceFile {

	File _file;
	WorkspaceHelper _helper = new WorkspaceHelper();

	@Override
	public IFile getIFile(File file) {
		IFile retval = null;

		// first try to find this file in the current workspace
		final IFile[] files = ResourcesPlugin.getWorkspace().getRoot()
				.findFilesForLocationURI(file.toURI());

		// if there are multiple files in this workspace use the shortest path
		if (files != null && files.length == 1) {
			retval = files[0];
		} else if (files != null && files.length > 0) {
			for (IFile ifile : files) {
				if (retval == null) {
					retval = ifile;
				} else {
					// prefer the path that is shortest (to avoid a nested
					// version)
					if (ifile.getFullPath().segmentCount() <
							retval.getFullPath().segmentCount()) {
						retval = ifile;
					}
				}
			}
		}

		if (retval == null) {
			try {
				retval = _helper.createIFile( MigrationConstants.HELPER_PROJECT_NAME, file );
			} catch (CoreException | IOException e) {
				e.printStackTrace();
			}
		}

		return retval;
	}

	@Override
	public void setFile(File file) {
		_file = file;
	}

}
