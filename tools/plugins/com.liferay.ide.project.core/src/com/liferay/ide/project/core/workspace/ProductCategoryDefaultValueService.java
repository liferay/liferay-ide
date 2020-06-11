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

import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.PossibleValuesService;
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
		_op = context(NewLiferayWorkspaceOp.class);

		_productInfo.startWorkspaceProductDownload(
			new Runnable() {

				@Override
				public void run() {
					System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAA");
					refresh();
				}

			});
	}

	private NewLiferayWorkspaceOp _op;
	private WorkspaceProductInfo _productInfo = WorkspaceProductInfo.getInstance();

}