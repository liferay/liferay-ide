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

import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.api.Problem;

import java.io.File;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Andy Wu
 */
public class MVCPortletClassInPortletXMLTest extends APITestBase {

	@Override
	public int getExpectedNumber() {
		return 2;
	}

	@Override
	public String getComponentName() {
		return "com.liferay.blade.upgrade.liferay70.apichanges.MVCPortletClassInPortletXML";
	}

	@Override
	public File getTestFile() {
		return new File("projects/test-portlet/docroot/WEB-INF/portlet.xml");
	}

	@Test
	public void testPortletXmlFileLineNumbers() throws Exception {
		Assert.assertNotNull(fileMigrators[0]);

		FileMigrator fmigrator = context.getService(fileMigrators[0]);

		List<Problem> problems = fmigrator.analyze(getTestFile());

		Assert.assertEquals(7, problems.get(0).lineNumber);
		Assert.assertEquals(37, problems.get(1).lineNumber);
	}

}