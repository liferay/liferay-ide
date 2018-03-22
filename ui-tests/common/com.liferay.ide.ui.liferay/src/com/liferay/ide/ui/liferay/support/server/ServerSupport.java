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
			if (info.getType().equals(type) && info.getVersion().equals(version)) {
				_bundle = info;

				break;
			}
		}

		Assert.assertNotNull(
			"Unable to get bundle info from bundles.csv by using " + type + " and " + version, _bundle);

		Assert.assertTrue(
			"Bundle zip " + _bundle.getBundleZip() + " doesn't exist in " + envAction.getBundlesPath(),
			envAction.getBundleFile(_bundle.getBundleZip()).exists());
	}

	@Override
	public void before() {
		super.before();

		File zipFile = envAction.getBundleFile(_bundle.getBundleZip());

		File serverDir = new File(envAction.getTempDir(), getServerDirName());

		serverDir.mkdirs();

		try {
			ZipUtil.unzip(zipFile, _bundle.getBundleDir(), serverDir, new NullProgressMonitor());
		}
		catch (IOException ioe) {
		}

		_preparePortalExtFile(serverDir);

		_preparePortalSetupWizardFile(serverDir);
	}

	public String getFullServerDir() {
		return new File(envAction.getTempDir(), getServerDirName()).getAbsolutePath();
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

		IPath sourcePortalExtPath = envAction.getBundlesPath().append(filename);

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

		IPath sourcePortalSetupWizardPath = envAction.getBundlesPath().append(filename);

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