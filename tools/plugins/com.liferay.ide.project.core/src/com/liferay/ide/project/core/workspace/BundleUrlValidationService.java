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

import org.apache.commons.validator.routines.UrlValidator;

import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Terry Jia
 * @author Simon Jiang
 */
public class BundleUrlValidationService extends ValidationService {

	@Override
	protected Status compute() {
		Status retval = Status.createOkStatus();

		BaseLiferayWorkspaceOp op = _op();

		String bundleUrl = op.getBundleUrl().content();

		UrlValidator urlValidator = new UrlValidator(
				UrlValidator.ALLOW_LOCAL_URLS | UrlValidator.ALLOW_ALL_SCHEMES);

		if ((bundleUrl!= null) && !urlValidator.isValid(bundleUrl)) {
			retval = Status.createWarningStatus("The bundle URL may not be a vaild URL.");
		}

		return retval;
	}

	private BaseLiferayWorkspaceOp _op() {
		return context(BaseLiferayWorkspaceOp.class);
	}

}