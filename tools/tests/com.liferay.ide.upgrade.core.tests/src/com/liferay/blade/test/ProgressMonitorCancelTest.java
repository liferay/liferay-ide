/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.liferay.blade.test;

import static org.junit.Assert.assertTrue;

import com.liferay.blade.api.Migration;
import com.liferay.blade.api.Problem;
import com.liferay.blade.api.ProgressMonitor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

public class ProgressMonitorCancelTest {

	@Test
	public void cancelableProgressMonitor() throws Exception {
		ServiceReference<Migration> sr = context
			.getServiceReference(Migration.class);
		final Migration m = context.getService(sr);

		final List<Problem> result = new ArrayList<>();

		final CancelableProgressMonitor cancelable = new CancelableProgressMonitor();

		final Thread t = new Thread() {
			@Override
			public void run() {
				List<Problem> problems = m
						.findProblems(new File(
								"projects"), cancelable);
				result.addAll(problems);
			}
		};

		t.start();

		Thread.sleep(5000);

		cancelable.canceled = true;

		t.join();

		final int expectedSize = 1324;
		final int size = result.size();

		assertTrue(size < expectedSize);
	}

	private final BundleContext context = FrameworkUtil.getBundle(
		this.getClass()).getBundleContext();

	static class CancelableProgressMonitor implements ProgressMonitor {

		boolean canceled = false;

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

	}

}
