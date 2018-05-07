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
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class PortletConfigGetFormatMethodsTest extends APITestBase {

	@Override
	public int getExpectedNumber() {
		return 3;
	}

	@Override
	public String getImplClassName() {
		return "PortletConfigGetFormatMethods";
	}

	@Override
	public File getTestFile() {
		return new File("projects/filetests/LiferayPortlet.java");
	}

	@Test
	public void languageUtilJspFile()
	{

		FileMigrator fmigrator = context.getService(fileMigrators[0]);

		List<Problem> problems = fmigrator.analyze(languageUtilJspFile);

		context.ungetService(fileMigrators[0]);

		Assert.assertNotNull(problems);
		Assert.assertEquals("", 5, problems.size());
	}

	@Test
	public void unicodeLanguageImplFile() throws Exception {
		FileMigrator fmigrator = context.getService(fileMigrators[0]);

		List<Problem> problems = fmigrator.analyze(unicodeLanguageImplFile);

		context.ungetService(fileMigrators[0]);

		Assert.assertNotNull(problems);
		Assert.assertEquals("", 23, problems.size());
	}

	public File languageUtilJspFile = new File("jsptests/language-util/edit_task.jsp");
	public File unicodeLanguageImplFile = new File("projects/filetests/UnicodeLanguageImpl.java");

}