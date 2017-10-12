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
import com.liferay.ide.ui.liferay.util.CoreUtil;
import com.liferay.ide.ui.liferay.util.FileUtil;
import com.liferay.ide.ui.liferay.util.ValidationMsg;
import com.liferay.ide.ui.liferay.util.ZipUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.utils.FileUtils;

import org.junit.Assert;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public class EnvAction extends UIAction {

	public EnvAction(SWTWorkbenchBot bot) {
		super(bot);

		_bundleInfos = getBundleInfos();
	}

	public BundleInfo[] getBundleInfos() {
		IPath bundlesCsvPath = getLiferayBundlesPath().append("bundles.csv");

		File bundleCSV = bundlesCsvPath.toFile();

		Assert.assertTrue(bundleCSV.exists());

		String[][] infos = CSVReader.readCSV(bundleCSV);

		BundleInfo[] bundleInfos = new BundleInfo[infos.length];

		for (int i = 0; i < infos.length; i++) {
			bundleInfos[i] = new BundleInfo();

			String[] columns = infos[i];

			for (int t = 0; t < columns.length; t++) {
				if (t == 0) {
					bundleInfos[i].setBundleZip(columns[t]);
				}
				else if (t == 1) {
					bundleInfos[i].setBundleDir(columns[t]);
				}
				else if (t == 2) {
					bundleInfos[i].setTomcatDir(columns[t]);
				}
				else if (t == 3) {
					bundleInfos[i].setType(columns[t]);
				}
				else if (t == 4) {
					bundleInfos[i].setVersion(columns[t]);
				}
			}
		}

		return bundleInfos;
	}

	public IPath getEclipseWorkspacePath() {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

		return root.getLocation();
	}

	public IPath getLiferayBundlesPath() {
		if (_liferayBundlesPath == null) {
			if ((_liferayBundlesDir == null) || _liferayBundlesDir.equals("")) {
				URL rootUrl = Platform.getBundle("com.liferay.ide.ui.liferay").getEntry("/");

				try {
					String filePath = FileLocator.toFileURL(rootUrl).getFile();

					_liferayBundlesPath = new Path(filePath).removeLastSegments(3).append("tests-resources");
				}
				catch (IOException ioe) {
				}
			}
			else {
				_liferayBundlesPath = new Path(_liferayBundlesDir);
			}
		}

		Assert.assertTrue(_liferayBundlesPath.toFile().exists());

		return _liferayBundlesPath;
	}

	public String getLiferayPluginServerName() {
		return _bundleInfos[0].getTomcatDir();
	}

	public IPath getLiferayPluginsSdkDir() {
		IPath bundlesPath = getLiferayBundlesPath().append("bundles");

		return bundlesPath.append(_PLUGINS_SDK_DIR);
	}

	public String getLiferayPluginsSdkName() {
		return _PLUGINS_SDK_DIR;
	}

	public IPath getLiferayPluginsSDKZip() {
		return getLiferayBundlesPath().append(_PLUGINS_SDK_ZIP);
	}

	public IPath getLiferayServerDir() {
		IPath bundlesPath = getLiferayBundlesPath().append("bundles");

		return bundlesPath.append(_bundleInfos[0].getBundleDir());
	}

	public IPath getLiferayServerFullDir() {
		return getLiferayServerDir().append(getLiferayPluginServerName());
	}

	public IPath getLiferayServerZip() {
		return getLiferayBundlesPath().append(_bundleInfos[0].getBundleZip());
	}

	public String getLiferayServerZipFolder() {
		return _bundleInfos[0].getBundleDir();
	}

	public IPath getProjectsFolder() {
		return getLiferayBundlesPath().append("projects");
	}

	public File getProjectZip(String bundleId, String projectName) throws IOException {
		URL projectZipUrl = Platform.getBundle(bundleId).getEntry("projects/" + projectName + ".zip");

		File projectZipFile = new File(FileLocator.toFileURL(projectZipUrl).getFile());

		return projectZipFile;
	}

	public File getTempDir() {
		IPath temp = getLiferayBundlesPath().append("temp");

		return temp.toFile();
	}

	public File getValidationFolder() {
		IPath validationPath = getLiferayBundlesPath().append("validation");

		return validationPath.toFile();
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
			}
		}

		return validationMsgs;
	}

	public void killGradleProcess() throws IOException {
		String jpsCmd = "jps";

		Process proc = Runtime.getRuntime().exec(jpsCmd);

		BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));

		String temp = in.readLine();

		List<String> result = new ArrayList<>();

		while (temp != null) {
			temp = in.readLine();

			if ((temp != null) && temp.contains("GradleDaemon")) {
				result.add(temp);
			}
		}

		try {
			for (String pid : result) {
				String[] allGradleProcess = pid.split(" ");

				Runtime.getRuntime().exec("taskkill /F /PID " + allGradleProcess[0]);
			}
		}
		catch (Exception e) {
		}
	}

	public void prepareGeoFile() {
		String filename = "com.liferay.ip.geocoder.internal.IPGeocoderConfiguration.cfg";

		IPath sourceGeoPath = getLiferayBundlesPath().append(filename);

		File source = sourceGeoPath.toFile();

		IPath osgiPath = getLiferayServerDir().append("osgi");

		IPath configsPath = osgiPath.append("configs");

		IPath destGeoPath = configsPath.append(filename);

		File dest = destGeoPath.toFile();

		try {
			FileUtil.copyFile(source, dest);

			String content = "filePath=" + getLiferayBundlesPath().toPortableString() + "/GeoLiteCity.dat";

			FileUtils.write(content, dest);
		}
		catch (Exception e) {
		}
	}

	public void preparePortalExtFile() {
		String filename = "portal-ext.properties";

		IPath sourcePortalExtPath = getLiferayBundlesPath().append(filename);

		File source = sourcePortalExtPath.toFile();

		IPath destPortalExtPath = getLiferayServerDir().append(filename);

		File dest = destPortalExtPath.toFile();

		try {
			FileUtil.copyFile(source, dest);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void preparePortalSetupWizardFile() {
		String filename = "portal-setup-wizard.properties";

		IPath sourcePortalSetupWizardPath = getLiferayBundlesPath().append(filename);

		File source = sourcePortalSetupWizardPath.toFile();

		IPath destPortalSetupWizardPath = getLiferayServerDir().append(filename);

		File dest = destPortalSetupWizardPath.toFile();

		try {
			FileUtil.copyFile(source, dest);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public File prepareTempProject(File source) throws IOException {
		File dist = new File(getTempDir(), source.getName());

		FileUtil.copyDirectiory(source.getPath(), dist.getPath());

		return dist;
	}

	public void unzipPluginsSDK() throws IOException {
		FileUtil.deleteDir(getLiferayPluginsSdkDir().toFile(), true);

		File sdkDir = getLiferayPluginsSdkDir().toFile();

		Assert.assertEquals(
			"Expected file to be not exist:" + getLiferayPluginsSdkDir().toPortableString(), false, sdkDir.exists());

		File liferayPluginsSdkZipFile = getLiferayPluginsSDKZip().toFile();

		Assert.assertEquals(
			"Expected file to exist: " + liferayPluginsSdkZipFile.getAbsolutePath(), true,
			liferayPluginsSdkZipFile.exists());

		File liferayPluginsSdkDirFile = getLiferayPluginsSdkDir().toFile();

		liferayPluginsSdkDirFile.mkdirs();

		if (CoreUtil.isNullOrEmpty(_PLUGINS_SDK_DIR)) {
			ZipUtil.unzip(liferayPluginsSdkZipFile, liferayPluginsSdkDirFile);
		}
		else {
			ZipUtil.unzip(
				liferayPluginsSdkZipFile, _PLUGINS_SDK_DIR, liferayPluginsSdkDirFile, new NullProgressMonitor());
		}

		Assert.assertEquals(true, liferayPluginsSdkDirFile.exists());

		Properties evnMap = System.getProperties();

		String username = evnMap.getProperty("USERNAME");

		File userBuildFile = new File(liferayPluginsSdkDirFile, "build." + username + ".properties");

		if (!userBuildFile.exists()) {
			userBuildFile.createNewFile();

			File serverDir = getLiferayServerDir().toFile();

			String appServerParentDir = "app.server.parent.dir=" + serverDir.getPath().replace("\\", "/");

			try {
				FileWriter writer = new FileWriter(userBuildFile.getPath(), true);

				writer.write(appServerParentDir);
				writer.close();
			}
			catch (IOException ioe) {
			}
		}
	}

	public void unzipServer() throws IOException {
		FileUtil.deleteDir(getLiferayServerDir().toFile(), true);

		File serverDir = getLiferayServerDir().toFile();

		Assert.assertEquals(
			"Expected file to be not exist:" + getLiferayServerDir().toPortableString(), false, serverDir.exists());

		File liferayServerZipFile = getLiferayServerZip().toFile();

		Assert.assertEquals(
			"Expected file to exist: " + liferayServerZipFile.getAbsolutePath(), true, liferayServerZipFile.exists());

		File liferayServerDirFile = getLiferayServerDir().toFile();

		liferayServerDirFile.mkdirs();

		String liferayServerZipFolder = getLiferayServerZipFolder();

		if (CoreUtil.isNullOrEmpty(liferayServerZipFolder)) {
			ZipUtil.unzip(liferayServerZipFile, liferayServerDirFile);
		}
		else {
			ZipUtil.unzip(
				liferayServerZipFile, liferayServerZipFolder, liferayServerDirFile, new NullProgressMonitor());
		}
	}

	private static final String _PLUGINS_SDK_DIR = "com.liferay.portal.plugins.sdk-1.0.11-withdependencies";

	private static final String _PLUGINS_SDK_ZIP =
		"com.liferay.portal.plugins.sdk-1.0.11-withdependencies-20170613175008905.zip";

	private BundleInfo[] _bundleInfos;
	private String _liferayBundlesDir = System.getProperty("liferay.bundles.dir");
	private IPath _liferayBundlesPath;

}