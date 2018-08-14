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

package com.liferay.ide.ui.liferay.support.server;

import com.liferay.ide.ui.liferay.support.SupportBase;
import com.liferay.ide.ui.liferay.util.BundleInfo;
import com.liferay.ide.ui.liferay.util.FileUtil;
import com.liferay.ide.ui.liferay.util.ZipUtil;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

import org.junit.Assert;

/**
 * @author Terry Jia
 */
public class ServerSupport extends SupportBase {

	public ServerSupport(SWTWorkbenchBot bot, String type, String version) {
		super(bot);

		BundleInfo[] infos = envAction.getBundleInfos();

		for (BundleInfo info : infos) {
			String infoVersion = info.getVersion();

			String infoType = info.getType();

			if (infoType.equals(type) && infoVersion.equals(version)) {
				_bundle = info;

				break;
			}
		}

		Assert.assertNotNull(
			"Unable to get bundle info from bundles.csv by using " + type + " and " + version, _bundle);

		File bundle = envAction.getBundleFile(_bundle.getBundleZip());

		Assert.assertTrue(
			"Bundle zip " + _bundle.getBundleZip() + " does not exist in " + envAction.getBundlesPath(),
			bundle.exists());
	}

	@Override
	public void before() {
		super.before();

		File zipFile = envAction.getBundleFile(_bundle.getBundleZip());

		File serverDir = new File(envAction.getTempDir(), getServerDirName());

		serverDir.mkdirs();

		try {

			// TODO Need to do research why for 7.1 a1 zip file couldn't find the root folder /liferay-ce-portal-7.1-a1
			// ZipUtil.unzip(zipFile, _bundle.getBundleDir(), serverDir, new NullProgressMonitor());

			ZipUtil.unzip(zipFile, serverDir, new NullProgressMonitor());
		}
		catch (IOException ioe) {
		}

		File finalDir = new File(serverDir, _bundle.getBundleDir());

		_preparePortalExtFile(finalDir);

		_preparePortalSetupWizardFile(finalDir);
	}

	public String getFullServerDir() {
		File dir = new File(envAction.getTempDir(), getServerDirName());

		File serverDir = new File(dir, _bundle.getBundleDir());

		// TODO Need to do research why for 7.1 a1 zip file couldn't find the root folder /liferay-ce-portal-7.1-a1
		// return new File(envAction.getTempDir(), getServerDirName()).getAbsolutePath();

		return serverDir.getAbsolutePath();
	}

	/**
	 * @return tomcat-9.0.6 etc...
	 */
	public String getServerDir() {
		return _bundle.getServerDir();
	}

	public String getServerDirName() {
		return _bundle.getBundleDir() + timestamp;
	}

	public String getServerName() {
		return _bundle.getType() + timestamp;
	}

	public String getStartedLabel() {
		return getServerName() + "  [Started]";
	}

	public String getStoppedLabel() {
		return getServerName() + "  [Stopped]";
	}

	private void _preparePortalExtFile(File serverDir) {
		String filename = "portal-ext.properties";

		IPath bundlePath = envAction.getBundlesPath();

		IPath sourcePortalExtPath = bundlePath.append(filename);

		File source = sourcePortalExtPath.toFile();

		File dest = new File(serverDir, filename);

		try {
			FileUtil.copyFile(source, dest);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void _preparePortalSetupWizardFile(File serverDir) {
		String filename = "portal-setup-wizard.properties";

		IPath bundlePath = envAction.getBundlesPath();

		IPath sourcePortalSetupWizardPath = bundlePath.append(filename);

		File source = sourcePortalSetupWizardPath.toFile();

		File dest = new File(serverDir, filename);

		try {
			FileUtil.copyFile(source, dest);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private BundleInfo _bundle;

}