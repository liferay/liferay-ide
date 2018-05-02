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
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.templates.BndProperties;
import com.liferay.ide.project.core.modules.templates.BndPropertiesValue;

import java.io.IOException;

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

		try {
			BndProperties bnd = new BndProperties();

			bnd.load(bndFile.getLocation().toFile());

			BndPropertiesValue fragmentHost = (BndPropertiesValue)bnd.get("Fragment-Host");

			if (fragmentHost != null) {
				String fragmentHostValue = fragmentHost.getOriginalValue();

				String[] b = fragmentHostValue.split(";");

				if (ListUtil.isNotEmpty(b) && (b.length > 1)) {
					String[] f = b[1].split("=");

					String version = f[1].substring(1, f[1].length() - 1);

					return b[0] + "-" + version;
				}
			}

			return null;
		}
		catch (IOException ioe) {
			ProjectCore.logError("Failed to parsed bnd.bnd for project " + project.getName(), ioe);
		}

		return null;
	}

	private NewModuleFragmentFilesOp _op() {
		return context(NewModuleFragmentFilesOp.class);
	}

}