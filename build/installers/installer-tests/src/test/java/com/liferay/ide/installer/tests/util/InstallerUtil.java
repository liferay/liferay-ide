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

package com.liferay.ide.installer.tests.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Terry Jia
 */
public class InstallerUtil implements Constants {

	private static Properties _installerInfo = null;

	private static Properties _getInfo() {
		if (_installerInfo == null) {
			_installerInfo = new Properties();

			Class<?> clazz = InstallerUtil.class;

			ClassLoader classLoader = clazz.getClassLoader();

			try (InputStream in = classLoader.getResourceAsStream("installers.properties")) {
				_installerInfo.load(in);
			}
			catch (IOException e) {
			}
		}

		return _installerInfo;
	}

	public static String getProjectSdkFullNameWin() {
		return _getInfo().getProperty("project.sdk.name") + DASH + _getInfo().getProperty("version") + DASH + WINDOWS
				+ DASH + INSTALLER + WINDOWS_SUFFIX;
	}

	public static String getProjectSdkFullNameLinux() {
		return _getInfo().getProperty("project.sdk.name") + DASH + _getInfo().getProperty("version") + DASH + LINUX_X64
				+ DASH + INSTALLER + LINUX_SUFFIX;
	}

	public static String getProjectSdkFullNameMacos() {
		return _getInfo().getProperty("project.sdk.name") + DASH + _getInfo().getProperty("version") + DASH + OSX
				+ DASH + INSTALLER + OSX_SUFFIX;
	}

	public static String getDevStudioCEFullNameWin() {
		return _getInfo().getProperty("dev.studio.ce.name") + DASH + _getInfo().getProperty("version") + DASH + WINDOWS
				+ DASH + INSTALLER + WINDOWS_SUFFIX;
	}

	public static String getDevStudioCEFullNameLinux() {
		return _getInfo().getProperty("dev.studio.ce.name") + DASH + _getInfo().getProperty("version") + DASH + LINUX_X64
				+ DASH + INSTALLER + LINUX_SUFFIX;
	}

	public static String getDevStudioCEFullNameMacos() {
		return _getInfo().getProperty("dev.studio.ce.name") + DASH + _getInfo().getProperty("version") + DASH + OSX
				+ DASH + INSTALLER + OSX_SUFFIX;
	}

	public static String getDevStudioDXPFullNameWin() {
		return _getInfo().getProperty("dev.studio.dxp.name") + DASH + _getInfo().getProperty("version") + DASH + WINDOWS
				+ DASH + INSTALLER + WINDOWS_SUFFIX;
	}

	public static String getDevStudioDXPFullNameLinux() {
		return _getInfo().getProperty("dev.studio.dxp.name") + DASH + _getInfo().getProperty("version") + DASH + LINUX_X64
				+ DASH + INSTALLER + LINUX_SUFFIX;
	}

	public static String getDevStudioDXPFullNameMacos() {
		return _getInfo().getProperty("dev.studio.dxp.name") + DASH + _getInfo().getProperty("version") + DASH + OSX
				+ DASH + INSTALLER + OSX_SUFFIX;
	}

	public static String getJpmVersion() {
		return _getInfo().getProperty("jpm.version");
	}

	public static String getBladeVersion() {
		return _getInfo().getProperty("blade.version");
	}

	public static String getJpmHome() {
		return _getInfo().getProperty("jpm.home.dir");
	}

	public static String getLiferayHome() {
		return _getInfo().getProperty("liferay.home.dir");
	}

	public static File getUserHome() {
		String userHome = System.getProperty("user.home");

		return new File(userHome);
	}

	public static File getJpmHomeDir() {
		return new File(getUserHome(), getJpmHome());
	}

	public static File getLiferayHomeDir() {
		return new File(getUserHome(), getLiferayHome());
	}

	public static File getBundleHomeDir() {
		return new File(getLiferayHomeDir(), "bundles");
	}

	public static File getOutputDir() {
		Class<?> clazz = InstallerUtil.class;

		ClassLoader classLoader = clazz.getClassLoader();

		String dir = classLoader.getResource(".").getPath();

		File installerTests = new File(dir).getParentFile().getParentFile();

		return new File(installerTests.getParentFile(), "outputs");
	}

	public static File getProjectSdkWinFile() {
		return new File(getOutputDir(), getProjectSdkFullNameWin());
	}

	public static File getProjectSdkLinuxFile() {
		return new File(getOutputDir(), getProjectSdkFullNameLinux());
	}

	public static File getProjectSdkMacosFile() {
		return new File(getOutputDir(), getProjectSdkFullNameMacos());
	}

	public static File getDevStudioCEWinFile() {
		return new File(getOutputDir(), getDevStudioCEFullNameWin());
	}

	public static File getDevStudioCELinuxFile() {
		
		return new File(getOutputDir(), getDevStudioCEFullNameLinux());
	}

	public static File getDevStudioCEMacosFile() {
		return new File(getOutputDir(), getDevStudioCEFullNameMacos());
	}

	public static File getDevStudioDXPWinFile() {
		return new File(getOutputDir(), getDevStudioDXPFullNameWin());
	}

	public static File getDevStudioDXPLinuxFile() {
		return new File(getOutputDir(), getDevStudioDXPFullNameLinux());
	}

	public static File getDevStudioDXPMacosFile() {
		return new File(getOutputDir(), getDevStudioDXPFullNameMacos());
	}

}
