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

package com.liferay.ide.project.core.service;

import com.liferay.ide.project.core.util.WorkspaceProductInfoUtil;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.sapphire.PossibleValuesService;

/**
 * @author Ethan Sun
 */
public class ProductCategoryPossibleValuesService extends PossibleValuesService {

	@Override
	public void dispose() {
		if (_job.getState() == Job.RUNNING) {
			_job.cancel();
		}
	}

	@Override
	protected void compute(Set<String> values) {
		Set<String> productCategory = new HashSet<>();

		Set<String> productCategorySet = WorkspaceProductInfoUtil.getProductCategory();

		if (productCategorySet != null) {
			productCategorySet.forEach(
				entry -> {
					String category = entry.split("-")[0];

					if (!"commerce".equals(category)) {
						productCategory.add(category);
					}
				});

			values.addAll(productCategory);
		}
	}

	@Override
	protected void initPossibleValuesService() {
		if (!WorkspaceProductInfoUtil.workspaceCacheFile.exists()) {
			_job.addJobChangeListener(
				new JobChangeAdapter() {

					@Override
					public void done(final IJobChangeEvent event) {
						IStatus status = event.getResult();

						if (status.isOK()) {
							refresh();
						}
					}

				});
		}
	}

	private Job _job = WorkspaceProductInfoUtil.job;

}