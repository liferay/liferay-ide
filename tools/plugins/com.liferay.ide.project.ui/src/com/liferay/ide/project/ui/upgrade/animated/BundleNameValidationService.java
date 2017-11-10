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

package com.liferay.ide.project.ui.upgrade.animated;

import com.liferay.ide.core.util.CoreUtil;

import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;
import org.eclipse.wst.server.core.internal.ServerPlugin;

/**
 * @author Andy Wu
 */
@SuppressWarnings("restriction")
public class BundleNameValidationService extends ValidationService {

	@Override
	protected Status compute() {
		Status retval = Status.createOkStatus();

		Value<String> bundleName = _op().getBundleName();

		String serverName = bundleName.content();

		if (CoreUtil.empty(serverName)) {
			retval = Status.createErrorStatus("The Server Name is empty. Please input one.");

			return retval;
		}

		if (ServerPlugin.isNameInUse(null, serverName)) {
			retval = Status.createErrorStatus(
				"The Server Name \"" + serverName + "\"+ is already in use. Specify a different name.");

			return retval;
		}

		return retval;
	}

	private LiferayUpgradeDataModel _op() {
		return context(LiferayUpgradeDataModel.class);
	}

}