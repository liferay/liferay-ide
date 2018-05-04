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
import com.liferay.blade.api.ProgressMonitor;

import java.io.File;

import java.util.ArrayList;
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
public class ProgressMonitorCancelTest {

	@Test
	public void cancelableProgressMonitor() throws Exception {
		ServiceReference<Migration> sr = _context .getServiceReference(Migration.class);

		Migration m = _context.getService(sr);

		List<Problem> result = new ArrayList<>();

		CancelableProgressMonitor cancelable = new CancelableProgressMonitor();

		Thread t = new Thread() {

			@Override
			public void run() {
				List<Problem> problems = m .findProblems(new File("projects"), cancelable);

				result.addAll(problems);
			}

		};

		t.start();

		Thread.sleep(5000);

		cancelable.canceled = true;

		t.join();

		final int expectedSize = 1324;
		final int size = result.size();

		Assert.assertTrue(size < expectedSize);
	}

	public static class CancelableProgressMonitor implements ProgressMonitor {

		@Override
		public void beginTask(String taskName, int totalWork) {
		}

		@Override
		public void done() {
		}

		@Override
		public boolean isCanceled() {
			return canceled;
		}

		@Override
		public void setTaskName(String taskName) {
		}

		@Override
		public void worked(int work) {
		}

		public boolean canceled = false;

	}

	private final BundleContext _context = FrameworkUtil.getBundle(getClass()).getBundleContext();

}