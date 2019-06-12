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
 */
public class TargetPlatformIndexSourcesValidationService extends ValidationService implements SapphireContentAccessor {

	@Override
	protected Status compute() {
		boolean indexSources = false;

		NewLiferayWorkspaceOp op = _op();

		if (op != null) {
			indexSources = get(op.getIndexSources());
		}

		if (indexSources) {
			return Status.createWarningStatus(
				"This will cause all of the BOM artifact jars and their Java sources to be indexed by Eclipse. Note: " +
					"this process will slow down your IDE's project synchronization.");
		}

		return Status.createOkStatus();
	}

	private NewLiferayWorkspaceOp _op() {
		return context(NewLiferayWorkspaceOp.class);
	}

}