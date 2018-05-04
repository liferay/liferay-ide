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

package com.liferay.blade.test.apichanges;

import java.io.File;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class BreadcrumbPropertiesTest extends APITestBase {

	@Override
	public int getExpectedNumber() {
		return 2;
	}

	@Override
	public String getImplClassName() {
		return "BreadcrumbProperties";
	}

	@Override
	public File getTestFile() {
		return new File("projects/knowledge-base-portlet-6.2.x/docroot/WEB-INF/src/portal.properties");
	}

}