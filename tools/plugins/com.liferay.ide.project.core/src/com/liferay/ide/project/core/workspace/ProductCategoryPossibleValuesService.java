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

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.sapphire.PossibleValuesService;

/**
 * @author Ethan Sun
 */
public class ProductCategoryPossibleValuesService extends PossibleValuesService {

	@Override
	protected void compute(Set<String> values) {
		if (ListUtil.isEmpty(_workspaceProducts)) {
			return;
		}

		Set<String> categorySet = Stream.of(
			_workspaceProducts
		).map(
			productTemplete -> productTemplete.split("-")[0]
		).collect(
			Collectors.toSet()
		);

		if (ListUtil.isNotEmpty(categorySet)) {
			values.addAll(categorySet);

			String[] productCategoryValuesArr = categorySet.toArray(new String[0]);

			Arrays.sort(productCategoryValuesArr, String::compareTo);

			_op.setProductCategory(productCategoryValuesArr[productCategoryValuesArr.length - 1]);
		}
	}

	@Override
	protected void initPossibleValuesService() {
		_op = context(NewLiferayWorkspaceOp.class);

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

	private NewLiferayWorkspaceOp _op;
	private String[] _workspaceProducts;

}