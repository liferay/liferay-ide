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

package com.liferay.ide.kaleo.core.op.internal;

import com.liferay.ide.core.util.CoreUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.services.ReferenceService;

/**
 * @author Gregory Amerson
 */
public class ProjectReferenceService extends ReferenceService<IProject> {

	@Override
	protected IProject compute() {
		IProject retval = null;

		String reference = context(Value.class).text();

		if (!CoreUtil.isNullOrEmpty(reference)) {
			IWorkspaceRoot wsRoot = CoreUtil.getWorkspaceRoot();

			retval = wsRoot.getProject(reference);
		}

		return retval;
	}

}