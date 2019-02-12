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

import com.liferay.ide.project.core.modules.ModuleProjectNameValidationService;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;

import org.eclipse.sapphire.modeling.Status;

/**
 * @author Charles Wu
 */
public class ModuleExtProjectNameValidationService extends ModuleProjectNameValidationService {

	@Override
	protected Status compute() {
		if (LiferayWorkspaceUtil.getGradleWorkspaceProject() == null) {
			return Status.createErrorStatus("We recommend Liferay Gradle workspace to develop module ext project!");
		}

		return super.compute();
	}

}