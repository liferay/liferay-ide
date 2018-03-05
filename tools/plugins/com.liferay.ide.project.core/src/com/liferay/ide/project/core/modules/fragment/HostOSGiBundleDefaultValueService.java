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

package com.liferay.ide.project.core.modules.fragment;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.DerivedValueService;

/**
 * @author Terry Jia
 * @author Andy Wu
 */
public class HostOSGiBundleDefaultValueService extends DerivedValueService {

	@Override
	protected String compute() {
		NewModuleFragmentFilesOp op = _op();

		String projectName = op.getProjectName().content();

		if (CoreUtil.empty(projectName)) {
			return null;
		}

		IProject project = CoreUtil.getProject(projectName);

		IFile bndFile = project.getFile("bnd.bnd");

		if (FileUtil.notExists(bndFile)) {
			return null;
		}

		String fragmentFlag = "Fragment-Host:";

		String[] lines = FileUtil.readLinesFromFile(bndFile.getLocation().toFile());

		for (String line : lines) {
			if (line.startsWith(fragmentFlag)) {
				String[] s = line.split(":");

				if (!s[1].contains(";")) {
					return s[1];
				}
				else {
					String[] f = s[1].split(";");

					String hostName = f[0];

					String bundleVersion = f[1];

					String[] b = bundleVersion.split("=");

					String version = b[1].substring(1, b[1].length() - 1);

					return hostName + "-" + version;
				}
			}
		}

		return null;
	}

	private NewModuleFragmentFilesOp _op() {
		return context(NewModuleFragmentFilesOp.class);
	}

}