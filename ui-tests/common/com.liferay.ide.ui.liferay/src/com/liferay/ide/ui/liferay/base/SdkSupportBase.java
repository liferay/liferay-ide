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

package com.liferay.ide.ui.liferay.base;

import com.liferay.ide.ui.liferay.util.FileUtil;
import com.liferay.ide.ui.liferay.util.SdkInfo;
import com.liferay.ide.ui.liferay.util.ZipUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

import org.junit.Assert;

/**
 * @author Terry Jia
 */
public class SdkSupportBase extends SupportBase {

	public SdkSupportBase(SWTWorkbenchBot bot, String version, ServerSupport server) {
		super(bot);

		_server = server;

		SdkInfo[] infos = envAction.getSdkInfos();

		for (SdkInfo info : infos) {
			if (info.getVersion().equals(version)) {
				_sdk = info;

				break;
			}
		}

		Assert.assertNotNull("Unable to get sdk info from sdks.csv by using " + version, _sdk);
	}

	@Override
	public void before() {
		super.before();

		File zipFile = envAction.getBundleSubfile(_sdk.getSdkZip());

		File sdkDir = new File(envAction.getTempDir(), getSdkDirName());

		sdkDir.mkdirs();

		try {
			ZipUtil.unzip(zipFile, _sdk.getSdkDir(), sdkDir, new NullProgressMonitor());

			String username = envAction.getUsername();

			File userBuildFile = new File(sdkDir, "build." + username + ".properties");

			try {
				userBuildFile.createNewFile();
			}
			catch (IOException ioe) {
			}

			Assert.assertTrue("Expect build." + username + ".properties exists but not", userBuildFile.exists());

			String appServerParentDir = "app.server.parent.dir=" + _server.getFullServerDir().replace("\\", "/");

			FileWriter writer = new FileWriter(userBuildFile.getPath(), true);

			writer.write(appServerParentDir);

			writer.close();
		}
		catch (IOException ioe) {
		}

		if (envAction.internal()) {
			IPath bundlesPath = envAction.getBundlesPath();

			IPath source = bundlesPath.append("internal").append("ivy-settings.xml");

			File dest = new File(sdkDir, "ivy-settings.xml");

			FileUtil.copyFile(source.toFile(), dest);
		}
	}

	public String getFullSdkDir() {
		return new File(envAction.getTempDir(), getSdkDirName()).getAbsolutePath();
	}

	public String getSdkDirName() {
		return _sdk.getSdkDir() + timestamp;
	}

	private SdkInfo _sdk;
	private ServerSupport _server;

}