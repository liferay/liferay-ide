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

import com.liferay.ide.project.core.WorkspaceProductInfo;

import java.util.Set;

import org.eclipse.sapphire.PossibleValuesService;

/**
 * @author Ethan Sun
 */
public class ProductCategoryPossibleValuesService extends PossibleValuesService {

	@Override
	protected void compute(Set<String> values) {
		values.addAll(_productInfo.getProductCategory());
	}

	@Override
	protected void initPossibleValuesService() {
		_productInfo.startWorkspaceProductDownload(
			new Runnable() {

				@Override
				public void run() {
					System.out.println("VVVVVVVVVVVVVVVVVVVVVVVV");
					refresh();
				}

			});
	}

	private WorkspaceProductInfo _productInfo = WorkspaceProductInfo.getInstance();

}