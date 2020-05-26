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

package com.liferay.ide.project.core;

import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.workspace.LiferayWorkspaceUtil;

import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Seiphon Wang
 */
public class LiferayWorkspaceValidationService extends ValidationService implements SapphireContentAccessor {

	@Override
	protected Status compute() {
		Status retval = Status.createOkStatus();

		if (LiferayWorkspaceUtil.getGradleWorkspaceProject() == null) {
			retval = Status.createErrorStatus("We recommend Liferay Gradle workspace to develop current project!");
		}

		return retval;
	}

}