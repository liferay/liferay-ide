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

package com.liferay.ide.kaleo.ui.editor;

import com.liferay.ide.core.util.FileUtil;

import java.io.File;

import org.eclipse.gef.requests.CreationFactory;

/**
 * @author Gregory Amerson
 */
public class ScriptCreationFactory implements CreationFactory {

	public ScriptCreationFactory(File file) {
		_scriptFile = file;
	}

	public Object getNewObject() {
		String contents = FileUtil.readContents(_scriptFile, true);

		if (contents.endsWith("\n")) {
			contents = contents.substring(0, contents.length() - 1);
		}

		return contents;
	}

	public Object getObjectType() {
		return File.class;
	}

	private File _scriptFile;

}