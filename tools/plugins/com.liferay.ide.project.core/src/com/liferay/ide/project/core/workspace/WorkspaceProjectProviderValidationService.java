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

import com.liferay.ide.core.util.SapphireContentAccessor;

import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Terry Jia
 * @author Seiphon Wang
 */
public class WorkspaceProjectProviderValidationService extends ValidationService implements SapphireContentAccessor {

	@Override
	protected Status compute() {
		NewLiferayWorkspaceProjectProvider<NewLiferayWorkspaceOp> projectProvider = get(_op().getProjectProvider());

		if (projectProvider == null) {
			return Status.createErrorStatus("Create workspace project failure, project provider can not be null.");
		}

		return Status.createOkStatus();
	}

	private BaseLiferayWorkspaceOp _op() {
		return context(BaseLiferayWorkspaceOp.class);
	}

}