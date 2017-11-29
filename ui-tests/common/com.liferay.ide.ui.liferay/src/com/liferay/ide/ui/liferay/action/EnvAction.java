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
import com.liferay.ide.ui.liferay.util.FileUtil;
import com.liferay.ide.ui.liferay.util.SdkInfo;
import com.liferay.ide.ui.liferay.util.ValidationMsg;
import com.liferay.ide.ui.liferay.util.ZipUtil;
import com.liferay.ide.ui.swtbot.util.CoreUtil;
import com.liferay.ide.ui.swtbot.util.StringPool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.URL;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

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

		_bundleInfos = _getBundleInfos();
		_sdkInfos = _getSdkInfos();

		log = Logger.getLogger(getClass());
	}

	public IPath getEclipseWorkspacePath() {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

		return root.getLocation();
	}

	public IPath getLiferayBundlesPath() {
		if (_liferayBundlesPath == null) {
			if ((_liferayBundlesDir == null) || _liferayBundlesDir.equals("") || _liferayBundlesDir.equals("null")) {
				URL rootUrl = Platform.getBundle("com.liferay.ide.ui.liferay").getEntry("/");

				try {
					String filePath = FileLocator.toFileURL(rootUrl).getFile();

					if (filePath.contains("target/work/configuration")) {
						int index = filePath.indexOf("/ui-tests/");

						_liferayBundlesPath = new Path(filePath.substring(0, index) + "/tests-resources");
					}
					else {
						_liferayBundlesPath = new Path(filePath).removeLastSegments(3).append("tests-resources");
					}
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
		return _bundleInfos[0].getServerDir();
	}

	public String getLiferayPluginServerName62() {
		return _bundleInfos[1].getServerDir();
	}

	public IPath getLiferayPluginsSdk62Dir() {
		IPath bundlesPath = getLiferayBundlesPath().append("bundles");

		return bundlesPath.append(_sdkInfos[1].getSdkDir());
	}

	public String getLiferayPluginsSdk62Name() {
		return _sdkInfos[1].getSdkDir();
	}

	public IPath getLiferayPluginsSDK62Zip() {
		return getLiferayBundlesPath().append(_sdkInfos[1].getSdkZip());
	}

	public IPath getLiferayPluginsSdkDir() {
		IPath bundlesPath = getLiferayBundlesPath().append("bundles");

		String sdkDir = _sdkInfos[0].getSdkDir() + "-" + getTimestamp();

		return bundlesPath.append(sdkDir);
	}

	public String getLiferayPluginsSdkName() {
		return _sdkInfos[0].getSdkDir();
	}

	public IPath getLiferayPluginsSDKZip() {
		return getLiferayBundlesPath().append(_sdkInfos[0].getSdkZip());
	}

	public IPath getLiferayServerDir() {
		IPath bundlesPath = getLiferayBundlesPath().append("bundles");

		return bundlesPath.append(_bundleInfos[0].getBundleDir());
	}

	public IPath getLiferayServerDir62() {
		IPath bundlesPath = getLiferayBundlesPath().append("bundles");

		return bundlesPath.append(_bundleInfos[1].getBundleDir());
	}

	public IPath getLiferayServerFullDir() {
		return getLiferayServerDir().append(getLiferayPluginServerName());
	}

	public IPath getLiferayServerFullDir62() {
		return getLiferayServerDir62().append(getLiferayPluginServerName62());
	}

	public IPath getLiferayServerZip() {
		return getLiferayBundlesPath().append(_bundleInfos[0].getBundleZip());
	}

	public IPath getLiferayServerZip62() {
		return getLiferayBundlesPath().append(_bundleInfos[1].getBundleZip());
	}

	public String getLiferayServerZipFolder() {
		return _bundleInfos[0].getBundleDir();
	}

	public String getLiferayServerZipFolder62() {
		return _bundleInfos[1].getBundleDir();
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

	public long getTimestamp() {
		if (_timestamp == 0) {
			_timestamp = System.currentTimeMillis();
		}

		return _timestamp;
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
				else if (t == 2) {
					validationMsgs[i].setOs(columns[t]);
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
			if ((temp != null) && temp.contains("GradleDaemon")) {
				result.add(temp);
			}

			temp = in.readLine();
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

	public void logWarn(String msg) {
		BasicConfigurator.configure();

		Logger logger = Logger.getRootLogger();

		logger.warn(msg);

		BasicConfigurator.resetConfiguration();
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

	public void preparePortalExtFile62() {
		String filename = "portal-ext.properties";

		IPath sourcePortalExtPath = getLiferayBundlesPath().append(filename);

		File source = sourcePortalExtPath.toFile();

		IPath destPortalExtPath = getLiferayServerDir62().append(filename);

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

	public void resetTimestamp() {
		_timestamp = 0;
	}

	public void unzipPluginsSDK() throws IOException {
		File sdkDir = getLiferayPluginsSdkDir().toFile();

		Assert.assertEquals(
			"Expected file to be not exist:" + getLiferayPluginsSdkDir().toPortableString(), false, sdkDir.exists());

		File liferayPluginsSdkZipFile = getLiferayPluginsSDKZip().toFile();

		Assert.assertEquals(
			"Expected file to exist: " + liferayPluginsSdkZipFile.getAbsolutePath(), true,
			liferayPluginsSdkZipFile.exists());

		sdkDir.mkdirs();

		ZipUtil.unzip(liferayPluginsSdkZipFile, getLiferayPluginsSdkName(), sdkDir, new NullProgressMonitor());

		Assert.assertEquals(true, sdkDir.exists());

		String username = _getUsername();

		File userBuildFile = new File(sdkDir, "build." + username + ".properties");

		userBuildFile.createNewFile();

		unzipServer();

		File serverDir = getLiferayServerDir().toFile();

		String appServerParentDir = "app.server.parent.dir=" + serverDir.getPath().replace("\\", "/");

		FileWriter writer = new FileWriter(userBuildFile.getPath(), true);

		writer.write(appServerParentDir);

		writer.close();
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

		ZipUtil.unzip(liferayServerZipFile, liferayServerZipFolder, liferayServerDirFile, new NullProgressMonitor());
	}

	public void unzipServer62() throws IOException {
		FileUtil.deleteDir(getLiferayServerDir62().toFile(), true);

		File serverDir62 = getLiferayServerDir62().toFile();

		Assert.assertEquals(
			"Expected file to be not exist:" + getLiferayServerDir62().toPortableString(), false, serverDir62.exists());

		File liferayServerZipFile62 = getLiferayServerZip62().toFile();

		Assert.assertEquals(
			"Expected file to exist: " + liferayServerZipFile62.getAbsolutePath(), true,
			liferayServerZipFile62.exists());

		File liferayServerDirFile62 = getLiferayServerDir62().toFile();

		liferayServerDirFile62.mkdirs();

		String liferayServerZipFolder62 = getLiferayServerZipFolder62();

		ZipUtil.unzip(
			liferayServerZipFile62, liferayServerZipFolder62, liferayServerDirFile62, new NullProgressMonitor());
	}

	protected Logger log;

	private BundleInfo[] _getBundleInfos() {
		IPath bundlesCsvPath = getLiferayBundlesPath().append("bundles.csv");

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

	private SdkInfo[] _getSdkInfos() {
		IPath sdksCsvPath = getLiferayBundlesPath().append("sdks.csv");

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

	private String _getUsername() {
		String retval = StringPool.BLANK;

		retval = System.getenv("USERNAME");

		if (CoreUtil.empty(retval)) {
			retval = System.getenv("USER");
		}

		Assert.assertTrue((retval != null) && !retval.equals(""));

		return retval;
	}

	private final BundleInfo[] _bundleInfos;
	private String _liferayBundlesDir = System.getProperty("liferay.bundles.dir");
	private IPath _liferayBundlesPath;
	private final SdkInfo[] _sdkInfos;
	private long _timestamp = 0;

}