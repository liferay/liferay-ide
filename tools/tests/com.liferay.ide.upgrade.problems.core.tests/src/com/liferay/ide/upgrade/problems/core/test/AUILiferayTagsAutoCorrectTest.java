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

package com.liferay.ide.upgrade.problems.core.test;

import com.liferay.ide.upgrade.problems.test.apichanges.AutoCorrectJSPTagTestBase;

import java.io.File;

import java.util.Collections;
import java.util.List;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class AUILiferayTagsAutoCorrectTest extends AutoCorrectJSPTagTestBase {

	@Override
	public List<String> getCheckPoints() {
		return Collections.singletonList("1,<%@ taglib uri=\"http://liferay.com/tld/aui\" prefix=\"aui\" %>");
	}

	@Override
	public String getImplClassName() {
		return "AUILiferayTags";
	}

	@Override
	public File getOriginalTestFile() {
		return new File("jsptests/aui-liferay/AUILiferayTagTest.jsp");
	}

}