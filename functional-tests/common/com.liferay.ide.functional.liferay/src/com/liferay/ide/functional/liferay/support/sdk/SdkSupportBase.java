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

package com.liferay.ide.functional.liferay.support.sdk;

import com.liferay.ide.functional.liferay.support.SupportBase;
import com.liferay.ide.functional.liferay.support.server.ServerSupport;
import com.liferay.ide.functional.liferay.util.FileUtil;
import com.liferay.ide.functional.liferay.util.SdkInfo;
import com.liferay.ide.functional.liferay.util.ZipUtil;

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
			String infoVersion = info.getVersion();

			if (infoVersion.equals(version)) {
				_sdk = info;

				break;
			}
		}

		Assert.assertNotNull("Unable to get sdk info from sdks.csv by using " + version, _sdk);
	}

	@Override
	public void before() {
		super.before();

		File zipFile = envAction.getBundleFile(_sdk.getSdkZip());

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

			String fullServerDir = _server.getFullServerDir();

			String appServerParentDir = "app.server.parent.dir=" + fullServerDir.replace("\\", "/");

			String appServerTomcatDir =
				"app.server.tomcat.dir=" + fullServerDir.replace("\\", "/") + "/" + _server.getServerDir();

			FileWriter writer = new FileWriter(userBuildFile.getPath(), true);

			writer.write(appServerParentDir);

			writer.write(System.getProperty("line.separator"));

			writer.write(appServerTomcatDir);

			writer.close();
		}
		catch (IOException ioe) {
		}

		if (envAction.internal()) {
			IPath bundlesPath = envAction.getBundlesPath();

			IPath internalPath = bundlesPath.append("internal");

			IPath source = internalPath.append("ivy-settings.xml");

			File dest = new File(sdkDir, "ivy-settings.xml");

			FileUtil.copyFile(source.toFile(), dest);
		}
	}

	public String getFullSdkDir() {
		return new File(
			envAction.getTempDir(), getSdkDirName()
		).getAbsolutePath();
	}

	public String getSdkDirName() {
		return _sdk.getSdkDir() + timestamp;
	}

	private SdkInfo _sdk;
	private ServerSupport _server;

}