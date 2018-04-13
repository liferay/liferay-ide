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

package com.liferay.ide.server.ui.navigator;

import java.io.File;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.IFileSystem;

/**
 * @author Gregory Amerson
 */
public class PropertiesFile {

	public PropertiesFile(File f) {
		_file = f;

		if (f == null) {
			throw new IllegalArgumentException("file can not be null");
		}
	}

	public IFileStore getFileStore() {
		IFileSystem esfLocalFilesSystem = EFS.getLocalFileSystem();

		return esfLocalFilesSystem.fromLocalFile(_file);
	}

	public String getName() {
		return _file.getName();
	}

	public String getPath() {
		return _file.getPath();
	}

	private File _file;

}