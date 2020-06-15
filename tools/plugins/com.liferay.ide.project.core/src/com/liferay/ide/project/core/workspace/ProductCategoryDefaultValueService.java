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

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.project.core.modules.BladeCLI;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.sapphire.DefaultValueService;

/**
 * @author Ethan Sun
 */
public class ProductCategoryDefaultValueService extends DefaultValueService {

	@Override
	protected String compute() {
		if (ListUtil.isEmpty(_workspaceProducts)) {
			return null;
		}

		return "portal";
	}

	@Override
	protected void initDefaultValueService() {
		Job refreshWorkspaceProductJob = new Job("") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					_workspaceProducts = BladeCLI.getInitPromotedWorkspaceProduct(true);

					refresh();
				}
				catch (Exception e) {
					e.printStackTrace();
				}

				return Status.OK_STATUS;
			}

		};

		refreshWorkspaceProductJob.setSystem(true);

		refreshWorkspaceProductJob.schedule();
	}

	private String[] _workspaceProducts;

}