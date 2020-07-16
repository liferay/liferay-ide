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
import com.liferay.ide.core.StringBufferOutputStream;
import com.liferay.ide.core.util.FileUtil;
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

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import org.osgi.framework.Bundle;

/**
 * @author Gregory Amerson
 * @author Andy Wu
 * @author Simon Jiang
 */
public class BladeCLI {

	public static final String BLADE_392 = "blade-3.9.2.jar";

	public static final String BLADE_LATEST = "blade-latest.jar";

	public static String[] execute(String args) throws BladeCLIException {
		IPath bladeCLIPath = getBladeCLIPath();

		return _execute(bladeCLIPath, args);
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

	public static synchronized String[] getProjectTemplates() throws BladeCLIException {
		List<String> templateNames = new ArrayList<>();

		String[] executeResult = execute("create -q -l");

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