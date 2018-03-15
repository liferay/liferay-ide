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

package com.liferay.ide.installer.tests;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Terry Jia
 */
public class InstallerUtil extends Constants {

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

	private static String _getProjectSdkFullNameWin() {
		return _getInfo().getProperty("project.sdk.name") + DASH + _getInfo().getProperty("version") + DASH + WINDOWS
				+ DASH + INSTALLER + WINDOWS_SUFFIX;
	}

	private static String _getProjectSdkFullNameLinux() {
		return _getInfo().getProperty("project.sdk.name") + DASH + _getInfo().getProperty("version") + DASH + LINUX_X64
				+ DASH + INSTALLER + LINUX_SUFFIX;
	}

	private static String _getProjectSdkFullNameMacos() {
		return _getInfo().getProperty("project.sdk.name") + DASH + _getInfo().getProperty("version") + DASH + OSX
				+ DASH + INSTALLER + OSX_SUFFIX;
	}

	private static String _getDevStudioCEFullNameWin() {
		return _getInfo().getProperty("dev.studio.ce.name") + DASH + _getInfo().getProperty("version") + DASH + WINDOWS
				+ DASH + INSTALLER + WINDOWS_SUFFIX;
	}

	private static String _getDevStudioCEFullNameLinux() {
		return _getInfo().getProperty("dev.studio.ce.name") + DASH + _getInfo().getProperty("version") + DASH + LINUX_X64
				+ DASH + INSTALLER + LINUX_SUFFIX;
	}

	private static String _getDevStudioCEFullNameMacos() {
		return _getInfo().getProperty("dev.studio.ce.name") + DASH + _getInfo().getProperty("version") + DASH + OSX
				+ DASH + INSTALLER + OSX_SUFFIX;
	}

	private static String _getDevStudioDXPFullNameWin() {
		return _getInfo().getProperty("dev.studio.dxp.name") + DASH + _getInfo().getProperty("version") + DASH + WINDOWS
				+ DASH + INSTALLER + WINDOWS_SUFFIX;
	}

	private static String _getDevStudioDXPFullNameLinux() {
		return _getInfo().getProperty("dev.studio.dxp.name") + DASH + _getInfo().getProperty("version") + DASH + LINUX_X64
				+ DASH + INSTALLER + LINUX_SUFFIX;
	}

	private static String _getDevStudioDXPFullNameMacos() {
		return _getInfo().getProperty("dev.studio.dxp.name") + DASH + _getInfo().getProperty("version") + DASH + OSX
				+ DASH + INSTALLER + OSX_SUFFIX;
	}
	private static File _getOutputDir() {
		Class<?> clazz = InstallerUtil.class;

		ClassLoader classLoader = clazz.getClassLoader();

		String dir = classLoader.getResource(".").getPath();

		File installerTests = new File(dir).getParentFile().getParentFile();

		return new File(installerTests.getParentFile(), "outputs");
	}

	public static File getProjectSdkWinFile() {
		return new File(_getOutputDir(), _getProjectSdkFullNameWin());
	}

	public static File getProjectSdkLinuxFile() {
		return new File(_getOutputDir(), _getProjectSdkFullNameLinux());
	}

	public static File getProjectSdkMacosFile() {
		return new File(_getOutputDir(), _getProjectSdkFullNameMacos());
	}

	public static File getDevStudioCEWinFile() {
		return new File(_getOutputDir(), _getDevStudioCEFullNameWin());
	}

	public static File getDevStudioCELinuxFile() {
		return new File(_getOutputDir(), _getDevStudioCEFullNameLinux());
	}

	public static File getDevStudioCEMacosFile() {
		return new File(_getOutputDir(), _getDevStudioCEFullNameMacos());
	}

	public static File getDevStudioDXPWinFile() {
		return new File(_getOutputDir(), _getDevStudioDXPFullNameWin());
	}

	public static File getDevStudioDXPLinuxFile() {
		return new File(_getOutputDir(), _getDevStudioDXPFullNameLinux());
	}

	public static File getDevStudioDXPMacosFile() {
		return new File(_getOutputDir(), _getDevStudioDXPFullNameMacos());
	}
}
