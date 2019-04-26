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

package com.liferay.blade.test.apichanges71;

import com.liferay.blade.test.apichanges.APITestBase;

import java.io.File;

/**
 * @author Haoyi Sun
 */
public class ChangedLastPublishDateOptionTest extends APITestBase {

	@Override
	public String getComponentName() {
		return "com.liferay.ide.upgrade.problems.core.internal.liferay71.ChangedLastPublishDateOption";
	}

	@Override
	public File getTestFile() {
		return new File("javatests/BlogsPortletDataHandler.java");
	}

}