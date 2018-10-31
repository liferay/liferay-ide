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

package com.liferay.ide.project.core.modules;

import aQute.bnd.osgi.Domain;

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.StringBufferOutputStream;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.ProjectCore;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.net.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Java;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;
import org.osgi.service.prefs.Preferences;

/**
 * @author Gregory Amerson
 * @author Andy Wu
 */
public class BladeCLI {

	public static final String BLADE_CLI_REPO_URL = "BLADE_CLI_REPO_URL";

	public static final String BLADE_JAR_FILE_NAME = "blade.jar";

	public static synchronized void addToLocalInstance(File latestBladeJar) {
		FileUtil.copyFile(latestBladeJar, _bladeJarInstancePath.toFile());
	}

	public static String[] execute(String args) throws BladeCLIException {
		IPath bladeCLIPath = getBladeCLIPath();

		if (FileUtil.notExists(bladeCLIPath)) {
			throw new BladeCLIException("Could not get blade cli jar");
		}

		Project project = new Project();
		Java javaTask = new Java();

		javaTask.setProject(project);
		javaTask.setFork(true);
		javaTask.setFailonerror(true);
		javaTask.setJar(bladeCLIPath.toFile());
		javaTask.setArgs(args);

		DefaultLogger logger = new DefaultLogger();

		project.addBuildListener(logger);

		List<String> lines = new ArrayList<>();

		int returnCode = 0;

		try (StringBufferOutputStream out = new StringBufferOutputStream();
			PrintStream printStream = new PrintStream(out)) {

			logger.setOutputPrintStream(printStream);

			logger.setMessageOutputLevel(Project.MSG_INFO);

			returnCode = javaTask.executeJava();

			try (Scanner scanner = new Scanner(out.toString())) {
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();

					lines.add(line.replaceAll(".*\\[null\\] ", ""));
				}
			}

			boolean hasErrors = false;

			StringBuilder errors = new StringBuilder();

			for (String line : lines) {
				if (line.startsWith("Error")) {
					hasErrors = true;
				}
				else if (hasErrors) {
					errors.append(line);
				}
			}

			if ((returnCode != 0) || hasErrors) {
				throw new BladeCLIException(errors.toString());
			}

		}
		catch (IOException ioe) {
			throw new BladeCLIException(ioe.getMessage(), ioe);
		}

		return lines.toArray(new String[0]);
	}

	public static synchronized File fetchBladeJarFromRepo(boolean useCache) throws Exception {
		if (!useCache) {
			_bladeJarCacheFile = null;
		}

		if (_bladeJarCacheFile == null) {
			File settingsDir = LiferayCore.GLOBAL_SETTINGS_PATH.toFile();

			settingsDir.mkdirs();

			File repoCache = new File(settingsDir, "repoCache");

			repoCache.mkdirs();

			File bladeFile = new File(repoCache.getAbsolutePath(), BLADE_JAR_FILE_NAME);

			String urlStr = _getRepoURL() + "/" + BLADE_JAR_FILE_NAME;

			URL url = new URL(urlStr);

			FileUtils.copyURLToFile(url, bladeFile);

			_bladeJarCacheFile = bladeFile;

			return bladeFile;
		}
		else {
			return _bladeJarCacheFile;
		}
	}

	/**
	 * We need to get the correct path to the blade jar, here is the logic
	 *
	 * First see if we have a instance (workbench) copy of blade cli jar, that means
	 * that the developer has intentially updated their blade jar to a newer version
	 * than is shipped with the project.core bundle. The local instance copy will be
	 * in the plugin state area.
	 *
	 * If there is no instance copy of blade cli jar, then fallback to use the one
	 * that is in the bundle itself.
	 *
	 * @return path to local blade jar
	 * @throws BladeCLIException
	 */
	public static synchronized IPath getBladeCLIPath() throws BladeCLIException {
		File bladeJarInstanceFile = _bladeJarInstancePath.toFile();

		if (FileUtil.exists(bladeJarInstanceFile)) {
			try {
				Domain jar = Domain.domain(bladeJarInstanceFile);

				if (_supportedVersion(jar.getBundleVersion())) {
					return _bladeJarInstancePath;
				}
			}
			catch (IOException ioe) {
			}
		}

		try {
			return _getBladeJarFromBundle();
		}
		catch (IOException ioe) {
			throw new BladeCLIException("Could not find blade cli jar", ioe);
		}
	}

	public static synchronized String[] getProjectTemplates() throws BladeCLIException {
		List<String> templateNames = new ArrayList<>();

		String[] executeResult = execute("create -l");

		for (String name : executeResult) {
			name = name.trim();

			if (name.indexOf(" ") != -1) {

				// for latest blade which print template descriptor

				templateNames.add(name.substring(0, name.indexOf(" ")));
			}
			else {

				// for legacy blade

				templateNames.add(name);
			}
		}

		return templateNames.toArray(new String[0]);
	}

	public static synchronized void restoreOriginal() {
		File file = _bladeJarInstancePath.toFile();

		file.delete();
	}

	private static IPath _getBladeJarFromBundle() throws IOException {
		ProjectCore projectCore = ProjectCore.getDefault();

		Bundle bundle = projectCore.getBundle();

		File bladeJarBundleFile = FileUtil.getFile(
			FileLocator.toFileURL(bundle.getEntry("lib/" + BLADE_JAR_FILE_NAME)));

		return new Path(bladeJarBundleFile.getCanonicalPath());
	}

	private static String _getRepoURL() {
		IPreferencesService preferencesService = Platform.getPreferencesService();

		Preferences[] preferences = {
			InstanceScope.INSTANCE.getNode(ProjectCore.PLUGIN_ID), DefaultScope.INSTANCE.getNode(ProjectCore.PLUGIN_ID)
		};

		String repoURL = preferencesService.get(BLADE_CLI_REPO_URL, null, preferences);

		if (!repoURL.endsWith("/")) {
			repoURL = repoURL + "/";
		}

		return repoURL;
	}

	private static boolean _supportedVersion(String verisonValue) {
		Version version = new Version(verisonValue);
		Version lowVersion = new Version("2");
		Version highVersion = new Version("3");

		if ((version.compareTo(lowVersion) >= 0) && (version.compareTo(highVersion) < 0)) {
			return true;
		}

		return false;
	}

	private static File _bladeJarCacheFile = null;
	private static final IPath _bladeJarInstanceArea = ProjectCore.getDefault().getStateLocation().append("blade-jar");
	private static final IPath _bladeJarInstancePath = _bladeJarInstanceArea.append(BLADE_JAR_FILE_NAME);

	static {
		FileUtil.getFile(_bladeJarInstanceArea).mkdirs();
	}

}