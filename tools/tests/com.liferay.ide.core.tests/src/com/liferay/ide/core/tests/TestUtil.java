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

package com.liferay.ide.core.tests;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.JobUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import java.nio.file.Files;

import junit.framework.TestCase;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.wst.validation.internal.operations.ValidatorManager;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class TestUtil {

	public static void copyDir(File src, File dst) throws IOException {
		_copyDir(src, dst, true);
	}

	public static void failTest(Exception e) {
		StringWriter s = new StringWriter();

		e.printStackTrace(new PrintWriter(s));

		TestCase.fail(s.toString());
	}

	public static void waitForBuildAndValidation() throws Exception {
		IWorkspaceRoot root = null;

		IJobManager manager = Job.getJobManager();

		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		try {
			workspace.checkpoint(true);

			manager.join(ResourcesPlugin.FAMILY_AUTO_BUILD, new NullProgressMonitor());
			manager.join(ResourcesPlugin.FAMILY_MANUAL_BUILD, new NullProgressMonitor());
			manager.join(ValidatorManager.VALIDATOR_JOB_FAMILY, new NullProgressMonitor());
			manager.join(ResourcesPlugin.FAMILY_AUTO_BUILD, new NullProgressMonitor());
			JobUtil.waitForLiferayProjectJob();
			Thread.sleep(200);
			manager.beginRule(root = workspace.getRoot(), null);
		}
		catch (InterruptedException ie) {
			failTest(ie);
		}
		catch (IllegalArgumentException iae) {
			failTest(iae);
		}
		catch (OperationCanceledException oce) {
			failTest(oce);
		}
		finally {
			if (root != null) {
				manager.endRule(root);
			}
		}
	}

	private static void _copyDir(File src, File dst, boolean deleteDst) throws IOException {
		if (!src.isDirectory()) {
			throw new IllegalArgumentException("Not a directory:" + src.getAbsolutePath());
		}

		if (deleteDst) {
			FileUtil.deleteDir(dst, true);
		}

		dst.mkdirs();

		File[] files = src.listFiles();

		if (files != null) {
			for (File file : files) {
				if (file.canRead()) {
					File dstChild = new File(dst, file.getName());

					if (file.isDirectory()) {
						_copyDir(file, dstChild, false);
					}
					else {
						_copyFile(file, dstChild);
					}
				}
			}
		}
	}

	private static void _copyFile(File src, File dst) throws IOException {
		try (InputStream inputStream = Files.newInputStream(src.toPath());
			BufferedInputStream in = new BufferedInputStream(inputStream);
			OutputStream outputStream = Files.newOutputStream(dst.toPath());
			BufferedOutputStream out = new BufferedOutputStream(outputStream)) {

			byte[] buf = new byte[10240];

			int len;

			while ((len = in.read(buf)) != -1) {
				out.write(buf, 0, len);
			}
		}
	}

}