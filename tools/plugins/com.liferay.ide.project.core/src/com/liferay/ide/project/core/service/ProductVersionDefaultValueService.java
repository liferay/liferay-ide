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

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.WorkspaceProductInfoUtil;

import java.util.List;

import org.eclipse.sapphire.DefaultValueService;

/**
 * @author Ethan Sun
 */
public class ProductVersionDefaultValueService extends DefaultValueService {

	@Override
	protected String compute() {
		String defaultValue = "";

		String defaultProductKey = "portal";

		List<String> productVersionsList = WorkspaceProductInfoUtil.getProductVersionList(defaultProductKey);

		if (ListUtil.isNotEmpty(productVersionsList)) {
			defaultValue = productVersionsList.get(0);
		}

		return defaultValue;
	}

}