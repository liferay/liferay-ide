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
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.WorkspaceProductInfo;

import java.util.Objects;

import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Simon Jiang
 */
public class ProductCategoryValidationService extends ValidationService implements SapphireContentAccessor {

	@Override
	protected Status compute() {
		Status retval = Status.createOkStatus();

		Value<String> productCategory = _op().getProductCategory();

		ProductCategoryPossibleValuesService possibleValuesService = productCategory.service(
			ProductCategoryPossibleValuesService.class);

		if (Objects.isNull(possibleValuesService) || ListUtil.isEmpty(possibleValuesService.values())) {
			return StatusBridge.create(ProjectCore.createErrorStatus("Product Category can not be empty."));
		}

		return retval;
	}

	@Override
	protected void initValidationService() {
		_productInfo.startWorkspaceProductDownload(
			new Runnable() {

				@Override
				public void run() {
					System.out.println("BBBBBBBBBBBBBBBBBBBBBBBBBB");
					refresh();
				}

			});
	}

	private NewLiferayWorkspaceOp _op() {
		return context(NewLiferayWorkspaceOp.class);
	}

	private WorkspaceProductInfo _productInfo = WorkspaceProductInfo.getInstance();

}