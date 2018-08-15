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

package com.liferay.ide.ui.liferay.action;

import com.liferay.ide.ui.liferay.UIAction;
import com.liferay.ide.ui.liferay.util.BundleInfo;
import com.liferay.ide.ui.liferay.util.CSVReader;
import com.liferay.ide.ui.liferay.util.SdkInfo;
import com.liferay.ide.ui.liferay.util.ValidationMsg;
import com.liferay.ide.ui.swtbot.util.CoreUtil;
import com.liferay.ide.ui.swtbot.util.StringPool;

import java.io.File;
import java.io.IOException;

import java.net.InetAddress;
import java.net.URL;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

import org.junit.Assert;

import org.osgi.framework.Bundle;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 * @author Lily Li
 */
public class EnvAction extends UIAction {

	public static EnvAction getInstance(SWTWorkbenchBot bot) {
		if (_envAction == null) {
			_envAction = new EnvAction(bot);
		}

		return _envAction;
	}

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
				Bundle bundle = Platform.getBundle("com.liferay.ide.ui.liferay");

				URL rootUrl = bundle.getEntry("/");

				try {
					URL url = FileLocator.toFileURL(rootUrl);

					String filePath = url.getFile();

					if (filePath.contains("target/work/configuration")) {
						int index = filePath.indexOf("/ui-tests/");

						_bundlesPath = new Path(filePath.substring(0, index) + "/tests-resources");
					}
					else {
						IPath path = new Path(filePath).removeLastSegments(3);

						_bundlesPath = path.append("tests-resources");
					}
				}
				catch (IOException ioe) {
				}
			}
			else {
				_bundlesPath = new Path(_bundlesDir);
			}
		}

		File bundleFile = _bundlesPath.toFile();

		Assert.assertTrue(bundleFile.exists());

		return _bundlesPath;
	}

	public IPath getEclipseWorkspacePath() {
		IWorkspaceRoot root = CoreUtil.getWorkspaceRoot();

		return root.getLocation();
	}

	public String getEclipseWorkspacePathOSString() {
		return getEclipseWorkspacePath().toOSString();
	}

	public File getProjectsDir() {
		IPath path = getBundlesPath().append("projects");

		return path.toFile();
	}

	public SdkInfo[] getSdkInfos() {
		return _sdkInfos;
	}

	public File getTempDir() {
		IPath path = getBundlesPath().append("bundles");

		return path.toFile();
	}

	public String getTempDirPath() {
		return getTempDir().getPath();
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

	public ValidationMsg[] getValidationMsgs(File csv) {
		Assert.assertTrue(csv.exists());

		String[][] msgs = CSVReader.readCSV(csv);

		ValidationMsg[] validationMsgs = new ValidationMsg[msgs.length];

		for (int i = 0; i < msgs.length; i++) {
			validationMsgs[i] = new ValidationMsg();

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
				Assert.assertTrue("The argument \"internal\" is true but can not reach the internal server", reachable);
			}
		}

		return retval;
	}

	public boolean isEclipse() {
		if ((_eclipseDir == null) || _eclipseDir.equals("") || _eclipseDir.equals("null")) {
			return false;
		}

		_eclipsePath = new Path(_eclipseDir);

		File file = _eclipsePath.toFile();

		return file.exists();
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

	public void logWarn(String msg) {
		BasicConfigurator.configure();

		Logger logger = Logger.getRootLogger();

		logger.warn(msg);

		BasicConfigurator.resetConfiguration();
	}

	public boolean notInternal() {
		return !internal();
	}

	public final String test_in_the_internal_net =
		"Only do this test in the internal net, add -Dinternal=\"false\" if you are out of the China office.";

	protected Logger log;

	private EnvAction(SWTWorkbenchBot bot) {
		super(bot);

		_bundleInfos = _parseBundleInfos();
		_sdkInfos = _parseSdkInfos();

		log = Logger.getLogger(getClass());
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

	private SdkInfo[] _parseSdkInfos() {
		IPath sdksCsvPath = getBundlesPath().append("sdks.csv");

		File sdksCsv = sdksCsvPath.toFile();

		Assert.assertTrue(sdksCsv.exists());

		String[][] infos = CSVReader.readCSV(sdksCsv);

		SdkInfo[] sdkInfos = new SdkInfo[infos.length];

		for (int i = 0; i < infos.length; i++) {
			sdkInfos[i] = new SdkInfo();

			String[] columns = infos[i];

			for (int t = 0; t < columns.length; t++) {
				String value = columns[t];

				if (t == 0) {
					sdkInfos[i].setSdkZip(value);
				}
				else if (t == 1) {
					sdkInfos[i].setSdkDir(value);
				}
				else if (t == 2) {
					sdkInfos[i].setVersion(value);
				}
			}
		}

		return sdkInfos;
	}

	private static EnvAction _envAction;

	private final BundleInfo[] _bundleInfos;
	private String _bundlesDir = System.getProperty("liferay.bundles.dir");
	private IPath _bundlesPath;
	private String _eclipseDir = System.getProperty("eclipse.dir");
	private IPath _eclipsePath;
	private String _internal = System.getProperty("internal");
	private byte[] _internalServerIp = {(byte)192, (byte)168, (byte)130, 84};
	private final SdkInfo[] _sdkInfos;

}