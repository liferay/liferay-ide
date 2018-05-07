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
import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.api.Problem;

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
public class RenamePortalKernelImportAutoCorrectTest {

	@Test
	public void autoCorrectProblems() throws Exception {
		File tempFolder = Files.createTempDirectory("autocorrect").toFile();

		File testFile = new File(tempFolder, "TasksEntryLocalServiceImpl.java");

		tempFolder.deleteOnExit();

		File originalTestfile = new File("javatests/TasksEntryLocalServiceImpl.java");

		Files.copy(originalTestfile.toPath(), testFile.toPath());

		List<Problem> problems = null;
		FileMigrator migrator = null;

		Collection<ServiceReference<FileMigrator>> mrefs = _context.getServiceReferences(FileMigrator.class, null);

		for (ServiceReference<FileMigrator> mref : mrefs) {
			migrator = _context.getService(mref);

			Class<?> clazz = migrator.getClass();

			if (clazz.getName().contains("RenamePortalKernelImport")) {
				problems = migrator.analyze(testFile);

				break;
			}
		}

		Assert.assertEquals("", 10, problems.size());

		int problemsFixed = ((AutoMigrator)migrator).correctProblems(testFile, problems);

		Assert.assertEquals("", 10, problemsFixed);

		File dest = new File(tempFolder, "Updated.java");

		Assert.assertTrue(testFile.renameTo(dest));

		problems = migrator.analyze(dest);

		Assert.assertEquals("", 0, problems.size());
	}

	private final BundleContext _context = FrameworkUtil.getBundle(getClass()).getBundleContext();

}