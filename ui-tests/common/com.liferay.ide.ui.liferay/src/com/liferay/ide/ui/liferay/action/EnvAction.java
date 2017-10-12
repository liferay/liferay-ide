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

import java.net.InetAddress;
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

	public static EnvAction getInstance(SWTWorkbenchBot bot) {
		if (_envAction == null) {
			_envAction = new EnvAction(bot);
		}

		return _envAction;
	}

	public IPath getEclipseWorkspacePath() {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

		return root.getLocation();
	}

	public IPath getProjectsFolder() {
		return _getBundlesPath().append("projects");
	}

	public File getProjectZip(String bundleId, String projectName) throws IOException {
		URL projectZipUrl = Platform.getBundle(bundleId).getEntry("projects/" + projectName + ".zip");

		File projectZipFile = new File(FileLocator.toFileURL(projectZipUrl).getFile());

		return projectZipFile;
	}

	public IPath getSdkDir() {
		IPath bundlesPath = _getBundlesPath().append("bundles");

		String sdkDir = _sdkInfos[0].getSdkDir() + "-" + getTimestamp();

		return bundlesPath.append(sdkDir);
	}

	public IPath getSdkDir62() {
		IPath bundlesPath = _getBundlesPath().append("bundles");

		String sdkDir = _sdkInfos[1].getSdkDir() + "-" + getTimestamp();

		return bundlesPath.append(sdkDir);
	}

	public String getSdkName() {
		return _sdkInfos[0].getSdkDir();
	}

	public String getSdkName62() {
		return _sdkInfos[1].getSdkDir();
	}

	public IPath getSdkZip62() {
		return _getBundlesPath().append(_sdkInfos[1].getSdkZip());
	}

	public IPath getServerDir() {
		IPath bundlesPath = _getBundlesPath().append("bundles");

		return bundlesPath.append(_bundleInfos[0].getBundleDir());
	}

	public IPath getServerDir62() {
		IPath bundlesPath = _getBundlesPath().append("bundles");

		return bundlesPath.append(_bundleInfos[1].getBundleDir());
	}

	public IPath getServerFullDir() {
		return getServerDir().append(getServerName());
	}

	public IPath getServerFullDir62() {
		return getServerDir62().append(getServerName62());
	}

	public IPath getWildflyServerFullDir() {
		return getWildflyServerDir().append(getWildflyServerName());
	}

	public IPath getWildflyServerDir() {
		IPath bundlesPath = _getBundlesPath().append("bundles");

		return bundlesPath.append(_bundleInfos[2].getBundleDir());
	}

	public String getServerName() {
		return _bundleInfos[0].getServerDir();
	}

	public String getServerName62() {
		return _bundleInfos[1].getServerDir();
	}

	public String getWildflyServerName() {
		return _bundleInfos[2].getServerDir();
	}

	public File getTempDir() {
		IPath temp = _getBundlesPath().append("temp");

		return temp.toFile();
	}

	public long getTimestamp() {
		if (_timestamp == 0) {
			_timestamp = System.currentTimeMillis();
		}

		return _timestamp;
	}

	public File getValidationFolder() {
		IPath validationPath = _getBundlesPath().append("validation");

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

	public void prepareGeoFile() {
		String filename = "com.liferay.ip.geocoder.internal.IPGeocoderConfiguration.cfg";

		IPath sourceGeoPath = _getBundlesPath().append(filename);

		File source = sourceGeoPath.toFile();

		IPath osgiPath = getServerDir().append("osgi");

		IPath configsPath = osgiPath.append("configs");

		IPath destGeoPath = configsPath.append(filename);

		File dest = destGeoPath.toFile();

		try {
			FileUtil.copyFile(source, dest);

			String content = "filePath=" + _getBundlesPath().toPortableString() + "/GeoLiteCity.dat";

			FileUtils.write(content, dest);
		}
		catch (Exception e) {
		}
	}

	public void preparePortalExtFile() {
		String filename = "portal-ext.properties";

		IPath sourcePortalExtPath = _getBundlesPath().append(filename);

		File source = sourcePortalExtPath.toFile();

		IPath destPortalExtPath = getServerDir().append(filename);

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

		IPath sourcePortalExtPath = _getBundlesPath().append(filename);

		File source = sourcePortalExtPath.toFile();

		IPath destPortalExtPath = getServerDir62().append(filename);

		File dest = destPortalExtPath.toFile();

		try {
			FileUtil.copyFile(source, dest);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void preparePortalWildflyExtFile() {
		String filename = "portal-ext.properties";

		IPath sourcePortalExtPath = _getBundlesPath().append(filename);

		File source = sourcePortalExtPath.toFile();

		IPath destPortalExtPath = getWildflyServerDir().append(filename);

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

		IPath sourcePortalSetupWizardPath = _getBundlesPath().append(filename);

		File source = sourcePortalSetupWizardPath.toFile();

		IPath destPortalSetupWizardPath = getServerDir().append(filename);

		File dest = destPortalSetupWizardPath.toFile();

		try {
			FileUtil.copyFile(source, dest);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void prepareWildflyPortalSetupWizardFile() {
		String filename = "portal-setup-wizard.properties";

		IPath sourcePortalSetupWizardPath = _getBundlesPath().append(filename);

		File source = sourcePortalSetupWizardPath.toFile();

		IPath destPortalSetupWizardPath = getWildflyServerDir().append(filename);

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

	public void unzipPluginsSdk() {
		try {
			File sdkDir = getSdkDir().toFile();

			Assert.assertEquals(
				"Expected file to be not exist:" + getSdkDir().toPortableString(), false, sdkDir.exists());

			File sdkZipFile = _getSdkZip().toFile();

			Assert.assertEquals("Expected file to exist: " + sdkZipFile.getAbsolutePath(), true, sdkZipFile.exists());

			sdkDir.mkdirs();

			ZipUtil.unzip(sdkZipFile, getSdkName(), sdkDir, new NullProgressMonitor());

			Assert.assertEquals(true, sdkDir.exists());

			String username = _getUsername();

			File userBuildFile = new File(sdkDir, "build." + username + ".properties");

			userBuildFile.createNewFile();

			unzipServer();

			File serverDir = getServerDir().toFile();

			String appServerParentDir = "app.server.parent.dir=" + serverDir.getPath().replace("\\", "/");

			FileWriter writer = new FileWriter(userBuildFile.getPath(), true);

			writer.write(appServerParentDir);

			writer.close();

			if (internal()) {
				IPath bundlesPath = _getBundlesPath();

				IPath source = bundlesPath.append("internal").append("ivy-settings.xml");

				IPath dest = getSdkDir().append("ivy-settings.xml");

				FileUtil.copyFile(source.toFile(), dest.toFile());
			}
		}
		catch (Exception e) {
		}
	}

	public void unzipPluginsSdk62() {
		try {
			Assert.assertTrue(test_in_the_internal_net, internal());

			File sdkDir = getSdkDir62().toFile();

			Assert.assertEquals(
				"Expected file to be not exist:" + getSdkDir62().toPortableString(), false, sdkDir.exists());

			File sdkZipFile = _getSdkZip62().toFile();

			Assert.assertEquals("Expected file to exist: " + sdkZipFile.getAbsolutePath(), true, sdkZipFile.exists());

			sdkDir.mkdirs();

			ZipUtil.unzip(sdkZipFile, getSdkName62(), sdkDir, new NullProgressMonitor());

			Assert.assertEquals(true, sdkDir.exists());

			String username = _getUsername();

			File userBuildFile = new File(sdkDir, "build." + username + ".properties");

			userBuildFile.createNewFile();

			unzipServer62();

			File serverDir = getServerDir62().toFile();

			String appServerParentDir = "app.server.parent.dir=" + serverDir.getPath().replace("\\", "/");

			FileWriter writer = new FileWriter(userBuildFile.getPath(), true);

			writer.write(appServerParentDir);

			writer.close();

			IPath bundlesPath = _getBundlesPath();

			IPath source = bundlesPath.append("internal").append("ivy-settings.xml");

			IPath dest = getSdkDir62().append("ivy-settings.xml");

			FileUtil.copyFile(source.toFile(), dest.toFile());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void unzipServer() {
		FileUtil.deleteDir(getServerDir().toFile(), true);

		File serverDir = getServerDir().toFile();

		Assert.assertEquals(
			"Expected file to be not exist:" + getServerDir().toPortableString(), false, serverDir.exists());

		File liferayServerZipFile = _getServerZip().toFile();

		Assert.assertEquals(
			"Expected file to exist: " + liferayServerZipFile.getAbsolutePath(), true, liferayServerZipFile.exists());

		File liferayServerDirFile = getServerDir().toFile();

		liferayServerDirFile.mkdirs();

		String liferayServerZipFolder = _getServerZipFolder();

		try {
			ZipUtil.unzip(
				liferayServerZipFile, liferayServerZipFolder, liferayServerDirFile, new NullProgressMonitor());
		}
		catch (IOException ioe) {
		}
	}

	public void unzipServer62() throws IOException {
		FileUtil.deleteDir(getServerDir62().toFile(), true);

		File serverDir62 = getServerDir62().toFile();

		Assert.assertEquals(
			"Expected file to be not exist:" + getServerDir62().toPortableString(), false, serverDir62.exists());

		File liferayServerZipFile62 = _getServerZip62().toFile();

		Assert.assertEquals(
			"Expected file to exist: " + liferayServerZipFile62.getAbsolutePath(), true,
			liferayServerZipFile62.exists());

		File liferayServerDirFile62 = getServerDir62().toFile();

		liferayServerDirFile62.mkdirs();

		String liferayServerZipFolder62 = _getServerZipFolder62();

		ZipUtil.unzip(
			liferayServerZipFile62, liferayServerZipFolder62, liferayServerDirFile62, new NullProgressMonitor());
	}

	public void unzipWildflyServer() throws IOException {
		FileUtil.deleteDir(getWildflyServerDir().toFile(), true);

		File serverDir = getWildflyServerDir().toFile();

		Assert.assertEquals("Expected file to be not exist:" + getWildflyServerDir().toPortableString(), false,
				serverDir.exists());

		File liferayServerZipFile = getWildflyServerZip().toFile();

		Assert.assertEquals("Expected file to exist: " + liferayServerZipFile.getAbsolutePath(), true,
				liferayServerZipFile.exists());

		File liferayServerDirFile = getWildflyServerDir().toFile();

		liferayServerDirFile.mkdirs();

		String liferayServerZipFolder = getWildflyServerZipFolder();

		ZipUtil.unzip(liferayServerZipFile, liferayServerZipFolder, liferayServerDirFile, new NullProgressMonitor());
	}

	public final String test_in_the_internal_net =
		"Only do this test in the internal net, add -Dinternal=\"false\" if you are out of the China office.";

	protected Logger log;

	private EnvAction(SWTWorkbenchBot bot) {
		super(bot);

		_bundleInfos = _getBundleInfos();
		_sdkInfos = _getSdkInfos();

		log = Logger.getLogger(getClass());
	}

	private BundleInfo[] _getBundleInfos() {
		IPath bundlesCsvPath = _getBundlesPath().append("bundles.csv");

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

	private IPath _getBundlesPath() {
		if (_bundlesPath == null) {
			if ((_bundlesDir == null) || _bundlesDir.equals("") || _bundlesDir.equals("null")) {
				URL rootUrl = Platform.getBundle("com.liferay.ide.ui.liferay").getEntry("/");

				try {
					String filePath = FileLocator.toFileURL(rootUrl).getFile();

					if (filePath.contains("target/work/configuration")) {
						int index = filePath.indexOf("/ui-tests/");

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

	private SdkInfo[] _getSdkInfos() {
		IPath sdksCsvPath = _getBundlesPath().append("sdks.csv");

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

	private IPath _getSdkZip() {
		return _getBundlesPath().append(_sdkInfos[0].getSdkZip());
	}

	private IPath _getSdkZip62() {
		return _getBundlesPath().append(_sdkInfos[1].getSdkZip());
	}

	private IPath _getServerZip() {
		return _getBundlesPath().append(_bundleInfos[0].getBundleZip());
	}

	private IPath _getServerZip62() {
		return _getBundlesPath().append(_bundleInfos[1].getBundleZip());
	}

	public IPath getWildflyServerZip() {
		return _getBundlesPath().append(_bundleInfos[2].getBundleZip());
	}

	private String _getServerZipFolder() {
		return _bundleInfos[0].getBundleDir();
	}

	private String _getServerZipFolder62() {
		return _bundleInfos[1].getBundleDir();
	}

	public String getWildflyServerZipFolder() {
		return _bundleInfos[2].getBundleDir();
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

	private static EnvAction _envAction;

	private final BundleInfo[] _bundleInfos;
	private String _bundlesDir = System.getProperty("liferay.bundles.dir");
	private IPath _bundlesPath;
	private String _internal = System.getProperty("internal");
	private byte[] _internalServerIp = {(byte)192, (byte)168, (byte)130, 84};
	private final SdkInfo[] _sdkInfos;
	private long _timestamp = 0;

}