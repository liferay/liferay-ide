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

package com.liferay.ide.project.core.workspace;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;

import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Terry Jia
 */
public class TargetPlatformVersionValidationService extends ValidationService implements SapphireContentAccessor {

	@Override
	protected Status compute() {
		NewLiferayWorkspaceOp op = _op();

		NewLiferayWorkspaceProjectProvider<NewLiferayWorkspaceOp> workspaceProjectProvider = get(
			op.getProjectProvider());

		String buildType = workspaceProjectProvider.getDisplayName();

		if (buildType.equals("Gradle")) {
			return Status.createOkStatus();
		}

		if (buildType.equals("Maven")) {
			String targetPlatform = get(op.getTargetPlatform());

			if (CoreUtil.isNullOrEmpty(targetPlatform)) {
				return Status.createErrorStatus(
					"The target platform version can not be null for maven workspace project.");
			}
		}

		return Status.createOkStatus();
	}

	private NewLiferayWorkspaceOp _op() {
		return context(NewLiferayWorkspaceOp.class);
	}

}