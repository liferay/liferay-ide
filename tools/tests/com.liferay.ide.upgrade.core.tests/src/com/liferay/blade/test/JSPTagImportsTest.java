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

import com.liferay.blade.api.AutoMigrator;
import com.liferay.blade.api.Migration;
import com.liferay.blade.api.Problem;
import com.liferay.blade.util.NullProgressMonitor;

import java.io.File;

import java.nio.file.Files;

import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class JSPTagImportsTest {

	@Test
	public void allProblems() throws Exception {
		ServiceReference<Migration> sr = _context.getServiceReference(Migration.class);

		Migration migration = _context.getService(sr);

		File tempFolder = Files.createTempDirectory("autocorrect").toFile();

		File testFile = new File(tempFolder, "jsptaglist.jsp");

		tempFolder.deleteOnExit();

		File originalTestfile = new File("jsptests/imports/view.jsp");

		Files.copy(originalTestfile.toPath(), testFile.toPath());

		List<Problem> problems = migration.findProblems(testFile, new NullProgressMonitor());

		Assert.assertEquals(6, problems.size());

		Collection<ServiceReference<AutoMigrator>> refs = _context.getServiceReferences(
			AutoMigrator.class, "(auto.correct=import)");

		for (ServiceReference<AutoMigrator> ref : refs) {
			AutoMigrator autoMigrator = _context.getService(ref);

			autoMigrator.correctProblems(testFile, problems);
		}

		File dest = new File(tempFolder, "Updated.jsp");

		Assert.assertTrue(testFile.renameTo(dest));

		List<Problem> problems2 = migration.findProblems(dest, new NullProgressMonitor());

		Assert.assertEquals(0, problems2.size());
	}

	private final BundleContext _context = FrameworkUtil.getBundle(getClass()).getBundleContext();

}