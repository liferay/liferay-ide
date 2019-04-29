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

import java.io.File;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.problems.core.AutoFileMigrator;
import com.liferay.ide.upgrade.problems.core.FileMigrator;

/**
 * @author Gregory Amerson
 */
public class MVCPortletClassInPortletXMLAutoCorrectTest {

	@Before
	public void beforeTest() throws Exception {
		Filter filter =
			_context.createFilter(
				"(component.name=com.liferay.ide.upgrade.problems.core.internal.liferay70.MVCPortletClassInPortletXML)");

		ServiceTracker<AutoFileMigrator, AutoFileMigrator> tracker = new ServiceTracker<>(_context, filter, null);

		tracker.open();

		ServiceReference<AutoFileMigrator>[] refs = tracker.getServiceReferences();

		Assert.assertNotNull(refs);

		Assert.assertEquals("", 1, refs.length);

		_autoMigrator = _context.getService(refs[0]);
	}

	@Test
	public void testAutoCorrectPortletXmlBoth() throws Exception {
		Assert.assertNotNull(_autoMigrator);

		FileMigrator fileMigrator = (FileMigrator)_autoMigrator;

		File testfile = new File("target/test/MVCPortletClassInPortletXMLAutoCorrectTest/portlet.xml");

		if (testfile.exists()) {
			Assert.assertTrue(testfile.delete());
		}

		testfile.getParentFile().mkdirs();

		Files.copy(new File("projects/test-portlet/docroot/WEB-INF/portlet.xml").toPath(), testfile.toPath());

		List<UpgradeProblem> problems = fileMigrator.analyze(testfile);

		Assert.assertEquals("", 2, problems.size());

		int corrected = _autoMigrator.correctProblems(testfile, problems);

		Assert.assertEquals("", 2, corrected);

		problems = fileMigrator.analyze(testfile);

		Assert.assertEquals("", 0, problems.size());
	}

	@Test
	public void testAutoCorrectPortletXmlSingle() throws Exception {
		Assert.assertNotNull(_autoMigrator);

		FileMigrator fileMigrator = (FileMigrator)_autoMigrator;

		File testfile = new File("generated/test/MVCPortletClassInPortletXMLAutoCorrectTest/portlet.xml");

		if (testfile.exists()) {
			Assert.assertTrue(testfile.delete());
		}

		testfile.getParentFile().mkdirs();

		Files.copy(new File("projects/test-portlet/docroot/WEB-INF/portlet.xml").toPath(), testfile.toPath());

		List<UpgradeProblem> problems = fileMigrator.analyze(testfile);

		Assert.assertEquals("", 2, problems.size());

		int corrected = _autoMigrator.correctProblems(testfile, Collections.singletonList(problems.get(0)));

		Assert.assertEquals("", 1, corrected);

		problems = fileMigrator.analyze(testfile);

		Assert.assertEquals("", 1, problems.size());

		corrected = _autoMigrator.correctProblems(testfile, Collections.singletonList(problems.get(0)));

		Assert.assertEquals("", 1, corrected);

		problems = fileMigrator.analyze(testfile);

		Assert.assertEquals("", 0, problems.size());
	}

	private AutoFileMigrator _autoMigrator = null;
	private BundleContext _context = FrameworkUtil.getBundle(getClass()).getBundleContext();

}