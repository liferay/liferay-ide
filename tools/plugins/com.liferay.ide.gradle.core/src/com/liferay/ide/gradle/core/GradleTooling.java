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

import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.eclipse.core.runtime.FileLocator;

import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ModelBuilder;
import org.gradle.tooling.ProjectConnection;

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Andy Wu
 */
public class GradleTooling {

	public static <T> T getModel(Class<T> modelClass, File cacheDir, File projectDir) throws Exception {
		T retval = null;

		GradleConnector connector = GradleConnector.newConnector();

		connector = connector.forProjectDirectory(projectDir);

		ProjectConnection connection = null;

		try {
			connection = connector.connect();

			ModelBuilder<T> modelBuilder = (ModelBuilder<T>)connection.model(modelClass);

			File depsDir = new File(cacheDir, "deps");

			depsDir.mkdirs();

			String path = depsDir.getAbsolutePath();

			path = path.replaceAll("\\\\", "/");

			_extractJar(depsDir, "com.liferay.blade.gradle.model");
			_extractJar(depsDir, "com.liferay.blade.gradle.plugin");

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

			ModelBuilder<T> builder = modelBuilder.withArguments("--init-script", scriptFile.getAbsolutePath());

			retval = builder.get();
		}
		catch (Exception e) {
			GradleCore.logError("get gradle custom model error", e);
		}
		finally {
			if (connection != null) {
				connection.close();
			}
		}

		return retval;
	}

	private static void _extractJar(File depsDir, String jarName) throws IOException {
		String fullFileName = jarName + ".jar";

		File[] files = depsDir.listFiles();

		// clear legacy dependency files

		for (File file : files) {
			if (file.isFile() && StringUtil.startsWith(file.getName(), jarName) &&
				!StringUtil.equals(file.getName(), fullFileName)) {

				if (!file.delete()) {
					GradleCore.logError("Error: delete file " + file.getAbsolutePath() + " fail");
				}
			}
		}

		String embeddedJarVersion = null;

		GradleCore gradleCore = GradleCore.getDefault();

		Bundle bundle = gradleCore.getBundle();

		File embeddedJarFile = FileUtil.getFile(FileLocator.toFileURL(bundle.getEntry("lib/" + fullFileName)));

		try (JarFile embededJarFile = new JarFile(embeddedJarFile)) {
			Manifest manifest = embededJarFile.getManifest();

			Attributes attributes = manifest.getMainAttributes();

			embeddedJarVersion = attributes.getValue("Bundle-Version");
		}
		catch (IOException ioe) {
		}

		File jarFile = new File(depsDir, fullFileName);

		if (FileUtil.exists(jarFile)) {
			boolean shouldDelete = false;

			try (JarFile jar = new JarFile(jarFile)) {
				Manifest manifest = jar.getManifest();

				Attributes attributes = manifest.getMainAttributes();

				String bundleVersion = attributes.getValue("Bundle-Version");

				if (!CoreUtil.empty(bundleVersion)) {
					Version rightVersion = new Version(embeddedJarVersion);
					Version version = new Version(bundleVersion);

					if (version.compareTo(rightVersion) != 0) {
						shouldDelete = true;
					}
				}
				else {
					shouldDelete = true;
				}
			}
			catch (Exception e) {
			}

			if (shouldDelete) {
				if (!jarFile.delete()) {
					GradleCore.logError("Error: delete file " + jarFile.getAbsolutePath() + " fail");
				}
			}
		}

		if (FileUtil.notExists(jarFile)) {
			FileUtil.copyFile(embeddedJarFile, jarFile);
		}
	}

}