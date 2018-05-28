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

package com.liferay.ide.test.core.base.support;

import com.liferay.ide.test.core.base.util.FileUtil;

import java.io.File;

/**
 * @author Terry Jia
 */
public class FileSupport extends SupportBase {

	public FileSupport(String fileName, boolean needTimestamp) {
		super();

		_fileName = fileName;
		_needTimestamp = needTimestamp;
	}

	public void after() {
		_file.delete();

		super.after();
	}

	public void before() {
		super.before();

		File source = new File(envAction.getProjectsDir(), _fileName);

		String sourceName = source.getName();

		if (_needTimestamp) {
			sourceName = sourceName + timestamp;
		}

		File dist = new File(envAction.getTempDir(), sourceName);

		FileUtil.copyFile(source, dist);

		_file = dist;
	}

	public String getPath() {
		return _file.getPath();
	}

	public File getProjectFile() {
		return _file;
	}

	private File _file;

	private final String _fileName;
	private final boolean _needTimestamp;

}