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

package com.liferay.ide.idea.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.Scanner;

import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Java;

/**
 * @author Terry Jia
 */
public class BladeCLI {

	public static String[] execute(String args) {
		Project project = new Project();
		Java javaTask = new Java();

		javaTask.setProject(project);
		javaTask.setFork(true);
		javaTask.setFailonerror(true);

		File temp = new File(System.getProperties().getProperty("user.home"), ".liferay-ide");

		File bladeJar = new File(temp, "com.liferay.blade.cli.jar");

		if (!bladeJar.exists()) {
			try (InputStream in = BladeCLI.class.getClassLoader().getResourceAsStream(
					"/libs/com.liferay.blade.cli.jar")) {

				FileUtil.writeFile(bladeJar, in);
			}
			catch (IOException ioe) {
			}
		}

		javaTask.setJar(bladeJar);
		javaTask.setArgs(args);

		DefaultLogger logger = new DefaultLogger();

		project.addBuildListener(logger);

		StringBufferOutputStream out = new StringBufferOutputStream();

		logger.setOutputPrintStream(new PrintStream(out));

		logger.setMessageOutputLevel(Project.MSG_INFO);

		int returnCode = javaTask.executeJava();

		java.util.List<String> lines = new ArrayList<>();
		Scanner scanner = new Scanner(out.toString());

		while (scanner.hasNextLine()) {
			lines.add(scanner.nextLine().replaceAll(".*\\[null\\] ", ""));
		}

		scanner.close();

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

		return lines.toArray(new String[0]);
	}

	public static synchronized String[] getProjectTemplates() {
		java.util.List<String> templateNames = new ArrayList<>();

		String[] executeResult = execute("create -l");

		for (String name : executeResult) {
			if (name.trim().indexOf(" ") != -1) {
				templateNames.add(name.substring(0, name.indexOf(" ")));
			}
			else {
				templateNames.add(name);
			}
		}

		return templateNames.toArray(new String[0]);
	}

}