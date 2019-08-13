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

package com.liferay.ide.functional.liferay.support.project;

import com.liferay.ide.functional.liferay.util.FileUtil;

import java.io.File;
import java.io.IOException;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Terry Jia
 */
public class ImportProjectSupport extends ProjectSupport {

	public ImportProjectSupport(SWTWorkbenchBot bot, String name) {
		super(bot, name);
	}

	public void before() {
		super.before();

		try {
			File source = new File(envAction.getProjectsDir(), name);

			File dist = new File(envAction.getTempDir(), source.getName() + timestamp);

			FileUtil.copyDirectiory(source.getPath(), dist.getPath());

			_project = dist;
		}
		catch (IOException ioe) {
		}
	}

	public String getPath() {
		return _project.getPath();
	}

	private File _project;

}