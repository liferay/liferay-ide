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

package com.liferay.ide.project.core.modules.ext;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.modules.ModuleProjectNameValidationService;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.modeling.Status;

import org.osgi.framework.Version;

/**
 * @author Charles Wu
 * @author Terry Jia
 */
public class ModuleExtProjectNameValidationService extends ModuleProjectNameValidationService {

	@Override
	protected Status compute() {
		if (LiferayWorkspaceUtil.getGradleWorkspaceProject() == null) {
			return Status.createErrorStatus("We recommend Liferay Gradle workspace to develop module ext project!");
		}

		IProject workspaceProject = LiferayWorkspaceUtil.getWorkspaceProject();

		Version liferayWorkspaceVersion = new Version(LiferayWorkspaceUtil.guessLiferayVersion(workspaceProject));

		Version version70 = new Version("7.0");

		if (CoreUtil.compareVersions(liferayWorkspaceVersion, version70) <= 0) {
			return Status.createErrorStatus(
				"Module Ext projects only work on liferay workspace which version is greater than 7.0.");
		}

		return super.compute();
	}

}