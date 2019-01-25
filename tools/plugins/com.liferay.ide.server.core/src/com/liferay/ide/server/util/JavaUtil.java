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

package com.liferay.ide.server.util;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Files;

import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstallType;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 * @author Charles Wu
 */
public class JavaUtil {

	public static final String EXT_JAR = ".jar";

	public static String createUniqueId(IVMInstallType vmType) {
		String id = null;

		do {
			id = String.valueOf(System.currentTimeMillis());
		}
		while (vmType.findVMInstall(id) != null);

		return id;
	}

	public static String getJarProperty(final File systemJarFile, final String propertyName) {
		if (systemJarFile.canRead()) {
			try (ZipFile jar = new ZipFile(systemJarFile)) {
				ZipEntry manifest = jar.getEntry("META-INF/MANIFEST.MF");

				Manifest mf = new Manifest(jar.getInputStream(manifest));

				Attributes a = mf.getMainAttributes();

				return a.getValue(propertyName);
			}
			catch (IOException ioe) {
				return null;
			}
		}

		return null;
	}

	public static String getJDKVersion(IVMInstall vmInstall) {
		File vmLocation = vmInstall.getInstallLocation();

		if (FileUtil.exists(vmLocation)) {
			String absolutePath = vmLocation.getAbsolutePath();

			File signatureJar = FileUtil.findFirstExist(
				absolutePath + "/jre/lib/rt.jar", absolutePath + "/lib/rt.jar", absolutePath + "/lib/jrt-fs.jar",
				absolutePath + "/jre/lib/vm.jar");

			if (signatureJar != null) {
				return getJarProperty(signatureJar, Attributes.Name.SPECIFICATION_VERSION.toString());
			}
		}

		return null;
	}

	public static String getManifestProperty(File manifestFile, String propertyName) {
		try {
			String contents = CoreUtil.readStreamToString(Files.newInputStream(manifestFile.toPath()));

			if (contents != null) {
				try (InputStream input = new ByteArrayInputStream(contents.getBytes())) {
					Manifest mf = new Manifest(input);

					Attributes a = mf.getMainAttributes();

					String val = a.getValue(propertyName);

					return val;
				}
			}
		}
		catch (IOException ioe) {
		}

		return null;
	}

	public static String getManifestPropFromFolderJars(File location, String mainFolder, String property) {
		File f = new File(location, mainFolder);

		if (f.exists()) {
			File[] children = f.listFiles();

			for (File child : children) {
				String childrenName = child.getName();

				if (childrenName.endsWith(EXT_JAR)) {
					return getJarProperty(child, property);
				}
			}
		}

		return null;
	}

	public static boolean isVMRequireVersion(String javaVersion, int requireVersion) {
		Integer version = null;
		int index = javaVersion.indexOf('.');

		if (index > 0) {
			try {
				int major = Integer.parseInt(javaVersion.substring(0, index)) * 100;
				index++;
				int index2 = javaVersion.indexOf('.', index);

				if (index2 > 0) {
					int minor = Integer.parseInt(javaVersion.substring(index, index2));

					version = Integer.valueOf(major + minor);
				}
			}
			catch (NumberFormatException nfe) {
			}
		}

		// If we have a version, and it isn't equal to the required version, fail the check

		if ((version != null) && (version.intValue() != requireVersion)) {
			return false;
		}

		return true;
	}

	public static boolean scanFolderJarsForManifestProp(
		File location, String mainFolder, String property, String propPrefix) {

		String value = getManifestPropFromFolderJars(location, mainFolder, property);

		if (value != null) {
			String trimedValue = value.trim();

			if (trimedValue.startsWith(propPrefix)) {
				return true;
			}
		}

		return false;
	}

}