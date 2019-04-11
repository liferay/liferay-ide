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

package com.liferay.ide.core.describer;

import java.io.File;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;

/**
 * @author Gregory Amerson
 */
public class LiferayPortalPropertiesFileDescriber extends LiferayPropertiesFileDescriber {

	public LiferayPortalPropertiesFileDescriber() {
		_fileNames.add("portal-ext.properties");
		_fileNames.add("portal-setup-wizard.properties");
		_fileNames.add("portal.properties");
		_fileNames.add("system-ext.properties");
	}

	@Override
	protected boolean isPropertiesFile(Object file) {
		String fileName = null;

		if (file instanceof File) {
			fileName = ((File)file).getName();
		}
		else if (file instanceof IFile) {
			fileName = ((IFile)file).getName();
		}
		else if (file instanceof String) {
			fileName = new File(
				(String)file
			).getName();
		}

		return _fileNames.contains(fileName);
	}

	private final Set<String> _fileNames = new HashSet<>();

}