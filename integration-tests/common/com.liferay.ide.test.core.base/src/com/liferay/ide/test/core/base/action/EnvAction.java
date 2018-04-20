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

package com.liferay.ide.test.core.base.action;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.test.core.base.util.BundleInfo;
import com.liferay.ide.test.core.base.util.CSVReader;
import com.liferay.ide.test.core.base.util.ValidationMessage;

import java.io.File;
import java.io.IOException;

import java.net.InetAddress;
import java.net.URL;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import org.junit.Assert;

/**
 * @author Terry Jia
 */
public class EnvAction {

	public File getBundleFile(String fileName) {
		IPath path = getBundlesPath().append(fileName);

		return path.toFile();
	}

	public BundleInfo[] getBundleInfos() {
		return _bundleInfos;
	}

	public IPath getBundlesPath() {
		if (_bundlesPath == null) {
			if ((_bundlesDir == null) || _bundlesDir.equals("") || _bundlesDir.equals("null")) {
				URL rootUrl = Platform.getBundle("com.liferay.ide.test.core.base").getEntry("/");

				try {
					String filePath = FileLocator.toFileURL(rootUrl).getFile();

					if (filePath.contains("target/work/configuration")) {
						int index = filePath.indexOf("/integration-tests/");

						_bundlesPath = new Path(filePath.substring(0, index) + "/tests-resources");
					}
					else {
						_bundlesPath = new Path(filePath).removeLastSegments(3).append("tests-resources");
					}
				}
				catch (IOException ioe) {
				}
			}
			else {
				_bundlesPath = new Path(_bundlesDir);
			}
		}

		Assert.assertTrue(_bundlesPath.toFile().exists());

		return _bundlesPath;
	}

	public IPath getEclipseWorkspacePath() {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

		return root.getLocation();
	}

	public File getProjectsDir() {
		IPath path = getBundlesPath().append("projects");

		return path.toFile();
	}

	public File getTempDir() {
		IPath path = getBundlesPath().append("bundles");

		return path.toFile();
	}

	public String getUsername() {
		String retval = StringPool.BLANK;

		retval = System.getenv("USERNAME");

		if (CoreUtil.empty(retval)) {
			retval = System.getenv("USER");
		}

		Assert.assertTrue((retval != null) && !retval.equals(""));

		return retval;
	}

	public File getValidationDir() {
		IPath path = getBundlesPath().append("validation");

		return path.toFile();
	}

	public ValidationMessage[] getValidationMsgs(File csv) {
		Assert.assertTrue(csv.exists());

		String[][] msgs = CSVReader.readCSV(csv);

		ValidationMessage[] validationMsgs = new ValidationMessage[msgs.length];

		for (int i = 0; i < msgs.length; i++) {
			validationMsgs[i] = new ValidationMessage();

			String[] columns = msgs[i];

			for (int t = 0; t < columns.length; t++) {
				if (t == 0) {
					validationMsgs[i].setInput(columns[t]);
				}
				else if (t == 1) {
					validationMsgs[i].setExpect(columns[t]);
				}
				else if (t == 2) {
					validationMsgs[i].setOs(columns[t]);
				}
			}
		}

		return validationMsgs;
	}

	public boolean internal() {
		boolean retval = false;

		if ((_internal == null) || _internal.equals("") || _internal.equals("null")) {
			retval = true;
		}
		else {
			retval = Boolean.parseBoolean(_internal);
		}

		boolean reachable = false;

		if (retval) {
			try {
				InetAddress address = InetAddress.getByAddress(_internalServerIp);

				reachable = address.isReachable(2000);
			}
			catch (Exception e) {
			}
			finally {
				Assert.assertTrue("The argument \"internal\" is true but can't reach the internal server", reachable);
			}
		}

		return retval;
	}

	public boolean localConnected() {
		boolean connected = true;

		try {
			URL url = new URL("http://127.0.0.1:8080");

			url.openStream();
		}
		catch (Exception e) {
			connected = false;
		}

		return connected;
	}

	public boolean notInternal() {
		return !internal();
	}

	public final String test_in_the_internal_net =
		"Only do this test in the internal net, add -Dinternal=\"false\" if you are out of the China office.";

	public EnvAction() {
		_bundleInfos = _parseBundleInfos();
	}

	private BundleInfo[] _parseBundleInfos() {
		IPath bundlesCsvPath = getBundlesPath().append("bundles.csv");

		File bundleCsv = bundlesCsvPath.toFile();

		Assert.assertTrue(bundleCsv.exists());

		String[][] infos = CSVReader.readCSV(bundleCsv);

		BundleInfo[] bundleInfos = new BundleInfo[infos.length];

		for (int i = 0; i < infos.length; i++) {
			bundleInfos[i] = new BundleInfo();

			String[] columns = infos[i];

			for (int t = 0; t < columns.length; t++) {
				String value = columns[t];

				if (t == 0) {
					bundleInfos[i].setBundleZip(value);
				}
				else if (t == 1) {
					bundleInfos[i].setBundleDir(value);
				}
				else if (t == 2) {
					bundleInfos[i].setServerDir(value);
				}
				else if (t == 3) {
					bundleInfos[i].setType(value);
				}
				else if (t == 4) {
					bundleInfos[i].setVersion(value);
				}
			}
		}

		return bundleInfos;
	}

	private final BundleInfo[] _bundleInfos;
	private String _bundlesDir = System.getProperty("liferay.bundles.dir");
	private IPath _bundlesPath;
	private String _internal = System.getProperty("internal");
	private byte[] _internalServerIp = {(byte)192, (byte)168, (byte)130, 84};

}