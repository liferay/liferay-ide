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
 * @author Seiphon Wang
 */
public class DescriptorsTest extends APIVersionSupportTestBase {

	@Override
	public String getComponentName() {
		return "com.liferay.blade.upgrade.liferay70.descriptors.LiferayDescriptorVersion70";
	}

	@Override
	public File getTestFile() {
		return new File("projects/legacy-apis-ant-portlet/docroot/WEB-INF/liferay-portlet.xml");
	}

	@Override
	public String getVersion() {
		return "7.0";
	}
}
