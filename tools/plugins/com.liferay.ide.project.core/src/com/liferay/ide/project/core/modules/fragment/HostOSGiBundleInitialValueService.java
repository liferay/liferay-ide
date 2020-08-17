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
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.util.Map;

import org.eclipse.sapphire.InitialValueService;

/**
 * @author Terry Jia
 * @author Andy Wu
 * @author Seiphon Wang
 * @author Ethan Sun
 */
public class HostOSGiBundleInitialValueService extends InitialValueService implements SapphireContentAccessor {

	@Override
	protected String compute() {
		NewModuleFragmentFilesOp op = _op();

		String projectName = get(op.getProjectName());

		if (CoreUtil.empty(projectName)) {
			return null;
		}

		Map<String, String> fragmentProjectInfo = ProjectUtil.getFragmentProjectInfo(CoreUtil.getProject(projectName));

		return fragmentProjectInfo.get("HostOSGiBundleName");
	}

	private NewModuleFragmentFilesOp _op() {
		return context(NewModuleFragmentFilesOp.class);
	}

}