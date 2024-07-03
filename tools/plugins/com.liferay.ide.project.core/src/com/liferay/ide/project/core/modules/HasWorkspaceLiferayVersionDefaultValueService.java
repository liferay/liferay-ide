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

package com.liferay.ide.project.core.modules;

import aQute.bnd.version.Version;

import com.liferay.ide.core.IWorkspaceProject;
import com.liferay.ide.core.util.VersionUtil;
import com.liferay.ide.core.workspace.LiferayWorkspaceUtil;
import com.liferay.release.util.ReleaseEntry;

import java.util.Objects;

import org.eclipse.sapphire.DefaultValueService;

/**
 * @author Simon.Jiang
 */
public class HasWorkspaceLiferayVersionDefaultValueService extends DefaultValueService {

	@Override
	protected String compute() {
		IWorkspaceProject liferayWorkspaceProject = LiferayWorkspaceUtil.getLiferayWorkspaceProject();

		if (liferayWorkspaceProject == null) {
			return Boolean.toString(false);
		}

		try {
			String simplifiedVersion = VersionUtil.simplifyTargetPlatformVersion(
				liferayWorkspaceProject.getTargetPlatformVersion());

			if (Version.isVersion(simplifiedVersion)) {
				return Boolean.toString(true);
			}

			ReleaseEntry workspaceReleaseEntry = liferayWorkspaceProject.getWorkspaceReleaseEntry();

			if (Objects.nonNull(workspaceReleaseEntry)) {
				String workspaceProductTargetPlatformVersion = workspaceReleaseEntry.getTargetPlatformVersion();

				if (Version.isVersion(workspaceProductTargetPlatformVersion) ||
					workspaceProductTargetPlatformVersion.contains("q")) {

					return Boolean.toString(true);
				}
			}
		}
		catch (Exception exception) {
		}

		return Boolean.toString(false);
	}

}