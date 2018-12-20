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
import java.util.Arrays;
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
public class InitJSPParseTest {

	@Test
	public void initParseErrorCheck() throws Exception {
		ServiceReference<Migration> sr = _context .getServiceReference(Migration.class);

		Migration m = _context.getService(sr);

		List<String> versions = Arrays.asList(new String[] {"7.0", "7.1"});

		List<Problem> problems = m.findProblems(new File("jsptests/jukebox-portlet/"), versions, new NullProgressMonitor());

		Assert.assertEquals("", 332, problems.size());

		boolean found = false;

		for (Problem problem : problems) {
			if (problem.file.getName().endsWith("view_search.jsp")) {
				if (problem.lineNumber == 109) {
					found = true;
				}
			}
		}

		if (!found) {
			Assert.fail();
		}
	}

	private final BundleContext _context = FrameworkUtil.getBundle(getClass()).getBundleContext();

}