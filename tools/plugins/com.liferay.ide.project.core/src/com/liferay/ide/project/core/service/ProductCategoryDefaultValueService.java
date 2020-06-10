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

import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.project.core.util.WorkspaceProductInfoUtil;
import com.liferay.ide.project.core.workspace.NewLiferayWorkspaceOp;

import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.Value;

/**
 * @author Ethan Sun
 */
public class ProductCategoryDefaultValueService extends DefaultValueService {

	@Override
	protected String compute() {
		Set<String> productCategoryValues;

		Value<String> productCategory = _op.getProductCategory();

		PossibleValuesService service = productCategory.service(ProductCategoryPossibleValuesService.class);

		productCategoryValues = service.values();

		if (!productCategoryValues.isEmpty()) {
			return productCategoryValues.toArray(new String[0])[0];
		}

		return null;
	}

	@Override
	protected void initDefaultValueService() {
		_listener = new FilteredListener<PropertyContentEvent>() {

			@Override
			protected void handleTypedEvent(PropertyContentEvent event) {
				refresh();
			}

		};

		_op = context(NewLiferayWorkspaceOp.class);

		SapphireUtil.attachListener(_op.property(NewLiferayWorkspaceOp.PROP_PRODUCT_CATEGORY), _listener);

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

			WorkspaceProductInfoUtil.downloadProductInfo();
		}
	}

	private Job _job = WorkspaceProductInfoUtil.job;
	private FilteredListener<PropertyContentEvent> _listener;
	private NewLiferayWorkspaceOp _op;

}