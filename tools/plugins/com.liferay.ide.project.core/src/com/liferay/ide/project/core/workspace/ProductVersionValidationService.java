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
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.project.core.ProjectCore;

import java.util.Objects;

import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Simon Jiang
 */
public class ProductVersionValidationService extends ValidationService implements SapphireContentAccessor {

	@Override
	public void dispose() {
		if (_op != null) {
			SapphireUtil.detachListener(_op.property(NewLiferayWorkspaceOp.PROP_PRODUCT_CATEGORY), _listener);
			SapphireUtil.detachListener(_op.property(NewLiferayWorkspaceOp.PROP_SHOW_ALL_VERSION_PRODUCT), _listener);
		}

		super.dispose();
	}

	@Override
	protected Status compute() {
		Status retval = Status.createOkStatus();

		Value<String> productVersion = _op.getProductVersion();

		ProductVersionPossibleValuesService possibleValuesService = productVersion.service(
			ProductVersionPossibleValuesService.class);

		if (Objects.isNull(possibleValuesService) || ListUtil.isEmpty(possibleValuesService.values())) {
			return StatusBridge.create(ProjectCore.createErrorStatus("Product Version can not be empty."));
		}

		return retval;
	}

	@Override
	protected void initValidationService() {
		_op = context(NewLiferayWorkspaceOp.class);

		_listener = new FilteredListener<PropertyContentEvent>() {

			@Override
			protected void handleTypedEvent(PropertyContentEvent event) {
				refresh();
			}

		};

		SapphireUtil.attachListener(_op.property(NewLiferayWorkspaceOp.PROP_PRODUCT_CATEGORY), _listener);
		SapphireUtil.attachListener(_op.property(NewLiferayWorkspaceOp.PROP_SHOW_ALL_VERSION_PRODUCT), _listener);
	}

	private FilteredListener<PropertyContentEvent> _listener;
	private NewLiferayWorkspaceOp _op;

}