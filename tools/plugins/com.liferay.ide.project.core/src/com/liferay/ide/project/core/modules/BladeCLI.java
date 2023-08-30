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

import com.liferay.ide.core.IWorkspaceProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.StringBufferOutputStream;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.core.workspace.LiferayWorkspaceUtil;
import com.liferay.ide.project.core.ProjectCore;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Environment;

import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import org.osgi.framework.Bundle;

/**
 * @author Gregory Amerson
 * @author Andy Wu
 * @author Simon Jiang
 * @author Seiphon Wang
 */
public class BladeCLI {

	public static final String BLADE_392 = "blade-3.9.2.jar";

	public static final String BLADE_LATEST = "blade-latest.jar";

	public static String[] execute(String args) throws BladeCLIException {
		return _execute(getBladeCLIPath(), args);
	}

	public static String[] executeWithLatestBlade(String args) throws BladeCLIException, IOException {
		return _execute(_getBladeJarFromBundle(BLADE_LATEST), args);
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
		IWorkspaceProject liferayWorkspaceProject = LiferayWorkspaceUtil.getLiferayWorkspaceProject();

		if (Objects.nonNull(liferayWorkspaceProject)) {
			if (liferayWorkspaceProject.isFlexibleLiferayWorkspace()) {
				_bladeJarName = BLADE_LATEST;
			}
			else {
				_bladeJarName = BLADE_392;
			}
		}
		else {
			_bladeJarName = BLADE_LATEST;
		}

		try {
			return _getBladeJarFromBundle(_bladeJarName);
		}
		catch (IOException ioe) {
			throw new BladeCLIException("Could not find blade cli jar", ioe);
		}
	}

	public static synchronized IPath getLatestBladeCLIPath() throws BladeCLIException {
		try {
			return _getBladeJarFromBundle(BLADE_LATEST);
		}
		catch (IOException ioe) {
			throw new BladeCLIException("Could not find blade cli jar", ioe);
		}
	}

	public static synchronized String[] getProjectTemplatesNames() throws BladeCLIException {
		List<String> templateNames = new ArrayList<>();

		String[] outputLines = execute("create -q -l");

		for (String line : outputLines) {
			line = line.trim();

			if (line.startsWith("Picked up")) {

				// skip any lines that may be printed out by JVM

				continue;
			}

			if (line.indexOf(" ") != -1) {

				// for latest blade which print template descriptor

				templateNames.add(line.substring(0, line.indexOf(" ")));
			}
			else {

				// for legacy blade

				templateNames.add(line);
			}
		}

		return templateNames.toArray(new String[0]);
	}

	public static synchronized String[] getWorkspaceProducts(boolean showAll) throws BladeCLIException, IOException {
		List<String> workspaceProducts = new ArrayList<>();

		String[] executeResult;

		if (showAll) {
			executeResult = _execute(_getBladeJarFromBundle(BLADE_LATEST), "init --list --all");
		}
		else {
			executeResult = _execute(_getBladeJarFromBundle(BLADE_LATEST), "init --list");
		}

		for (String result : executeResult) {
			String category = result.trim();

			if (category.indexOf(" ") == -1) {
				workspaceProducts.add(category);
			}
		}

		return workspaceProducts.toArray(new String[0]);
	}

	private static String[] _execute(IPath bladeCLIPath, String args) throws BladeCLIException {
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

		IProxyService proxyService = LiferayCore.getProxyService();

		IProxyData[] proxyDatas = proxyService.getProxyData();

		for (IProxyData proxyData : proxyDatas) {
			if (Objects.isNull(proxyData)) {
				continue;
			}

			if (Objects.isNull(proxyData.getHost())) {
				continue;
			}

			String proxyType = StringUtil.toLowerCase(proxyData.getType());

			Environment.Variable proxyHostVariable = new Environment.Variable();

			proxyHostVariable.setKey(proxyType + ".proxyHost");
			proxyHostVariable.setValue(proxyData.getHost());

			javaTask.addSysproperty(proxyHostVariable);

			Environment.Variable proxyPortVariable = new Environment.Variable();

			proxyPortVariable.setKey(proxyType + ".proxyPort");
			proxyPortVariable.setValue(String.valueOf(proxyData.getPort()));

			javaTask.addSysproperty(proxyPortVariable);

			if (!proxyData.isRequiresAuthentication()) {
				continue;
			}

			if (Objects.isNull(proxyData.getUserId()) || Objects.isNull(proxyData.getPassword())) {
				continue;
			}

			Environment.Variable proxyUserVariable = new Environment.Variable();

			proxyUserVariable.setKey(proxyType + ".proxyUser");
			proxyUserVariable.setValue(proxyData.getUserId());

			javaTask.addSysproperty(proxyUserVariable);

			Environment.Variable proxyPasswordVariable = new Environment.Variable();

			proxyPasswordVariable.setKey(proxyType + ".proxyPassword");
			proxyPasswordVariable.setValue(proxyData.getPassword());

			javaTask.addSysproperty(proxyPasswordVariable);
		}

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

	private static IPath _getBladeJarFromBundle(String jarName) throws IOException {
		ProjectCore projectCore = ProjectCore.getDefault();

		Bundle bundle = projectCore.getBundle();

		File bladeJarBundleFile = FileUtil.getFile(FileLocator.toFileURL(bundle.getEntry("lib/" + jarName)));

		return new Path(bladeJarBundleFile.getCanonicalPath());
	}

	private static String _bladeJarName = null;

}