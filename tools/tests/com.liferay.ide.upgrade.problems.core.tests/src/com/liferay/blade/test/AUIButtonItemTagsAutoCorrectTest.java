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

package com.liferay.blade.test;

import com.liferay.blade.test.apichanges.AutoCorrectJSPTagTestBase;

import java.io.File;

import java.util.Collections;
import java.util.List;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class AUIButtonItemTagsAutoCorrectTest extends AutoCorrectJSPTagTestBase {

	@Override
	public List<String> getCheckPoints() {
		return Collections.singletonList("3,<aui:button></aui:button>");
	}

	@Override
	public String getImplClassName() {
		return "AUIButtonItemTags";
	}

	@Override
	public File getOriginalTestFile() {
		return new File("jsptests/aui-button/AUIButtonItemTagTest.jsp");
	}

}