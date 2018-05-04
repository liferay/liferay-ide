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

import com.liferay.blade.api.Migration;
import com.liferay.blade.api.Problem;
import com.liferay.blade.util.NullProgressMonitor;

import java.io.File;

import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class AllProblemsTest {

	@Test
	@Ignore
	public void allProblems() throws Exception {
		ServiceReference<Migration> sr = _context .getServiceReference(Migration.class);

		Migration m = _context.getService(sr);

		List<Problem> problems = m
			.findProblems(new File("../com.liferay.blade.upgrade.liferay70/projects/"), new NullProgressMonitor());

		final int expectedSize = 304;
		final int size = problems.size();

		if (size != expectedSize) {
			System.err.println("All problems size is " + size + ", expected size is " + expectedSize);
		}

		Assert.assertEquals(expectedSize, size);

		for (Problem problem : problems) {
			if (problem.html != null && problem.html.equals("#legacy"))continue;

			Assert.assertNotNull("problem.title=" + problem.title + ", problem.file=" + problem.file, problem.html);
			Assert.assertTrue(
				"problem.title=" + problem.title + ", problem.file=" + problem.file, problem.html.length() > 0);
		}
	}

	private final BundleContext _context = FrameworkUtil.getBundle(getClass()).getBundleContext();

}