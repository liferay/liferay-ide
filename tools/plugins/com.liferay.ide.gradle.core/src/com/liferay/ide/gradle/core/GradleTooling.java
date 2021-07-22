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

package com.liferay.ide.gradle.core;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.StringUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;

import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ModelBuilder;
import org.gradle.tooling.ProjectConnection;

import org.osgi.framework.Bundle;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Andy Wu
 * @author Simon Jiang
 */
public class GradleTooling {

	public static <T> T getModel(Class<T> modelClass, File cacheDir, IProject project) throws Exception {
		try {
			File depsDir = new File(cacheDir, "deps");

			depsDir.mkdirs();

			String path = depsDir.getAbsolutePath();

			path = path.replaceAll("\\\\", "/");

			_extractJar(depsDir, "gradle-tooling");

			String initScriptTemplate = CoreUtil.readStreamToString(
				GradleTooling.class.getResourceAsStream("init.gradle"));

			String initScriptContents = initScriptTemplate.replaceFirst("%deps%", path);

			File scriptFile = new File(cacheDir, "init.gradle");

			if (FileUtil.notExists(scriptFile)) {
				scriptFile.createNewFile();
			}

			try (InputStream inputStream = new ByteArrayInputStream(initScriptContents.getBytes())) {
				FileUtil.writeFileFromStream(scriptFile, inputStream);
			}

			GradleConnector newConnector = GradleConnector.newConnector();

			IPath projectLocation = project.getLocation();

			newConnector.forProjectDirectory(projectLocation.toFile());

			ProjectConnection connect = newConnector.connect();

			ModelBuilder<T> model = connect.model(modelClass);

			ModelBuilder<T> withArguments = model.withArguments(
				"--init-script", scriptFile.getAbsolutePath(), "--stacktrace");

			return withArguments.get();
		}
		catch (Exception e) {
			LiferayGradleCore.logError("get gradle custom model error", e);
		}

		return null;
	}

	private static void _extractJar(File depsDir, String jarName) throws IOException {
		String fullFileName = jarName + ".jar";

		File[] files = depsDir.listFiles();

		// clear legacy dependency files

		for (File file : files) {
			if (file.isFile() && StringUtil.startsWith(file.getName(), jarName) &&
				!StringUtil.equals(file.getName(), fullFileName) && !file.delete()) {

				LiferayGradleCore.logError("Error: delete file " + file.getAbsolutePath() + " fail");
			}
		}

		LiferayGradleCore gradleCore = LiferayGradleCore.getDefault();

		Bundle bundle = gradleCore.getBundle();

		File embeddedJarFile = FileUtil.getFile(FileLocator.toFileURL(bundle.getEntry("lib/" + fullFileName)));

		File jarFile = new File(depsDir, fullFileName);

		if (FileUtil.exists(jarFile) && !jarFile.delete()) {
			LiferayGradleCore.logError("Error: delete file " + jarFile.getAbsolutePath() + " fail");
		}

		if (FileUtil.notExists(jarFile)) {
			FileUtil.copyFile(embeddedJarFile, jarFile);
		}
	}

}