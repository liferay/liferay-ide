/*******************************************************************************
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
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
 *
 *******************************************************************************/

package com.liferay.ide.eclipse.core.util;

import com.liferay.ide.eclipse.core.CorePlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Version;

/**
 * Core Utility methods
 * 
 * @author Greg Amerson
 */
public class CoreUtil {

	public static IStatus createErrorStatus(String msg) {

		return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, msg);
	}

	public static IProject[] getAllProjects() {

		return ResourcesPlugin.getWorkspace().getRoot().getProjects();
	}

	public static Object getNewObject(Object[] oldObjects, Object[] newObjects) {

		if (oldObjects != null && newObjects != null && oldObjects.length < newObjects.length) {

			for (int i = 0; i < newObjects.length; i++) {
				boolean found = false;
				Object object = newObjects[i];

				for (int j = 0; j < oldObjects.length; j++) {
					if (oldObjects[j] == object) {
						found = true;
						break;
					}
				}

				if (!found) {
					return object;
				}
			}
		}

		if (oldObjects == null && newObjects != null && newObjects.length == 1) {
			return newObjects[0];
		}

		return null;
	}

	public static IProject getProject(String projectName) {

		return getWorkspaceRoot().getProject(projectName);
	}

	public static IWorkspaceRoot getWorkspaceRoot() {

		return ResourcesPlugin.getWorkspace().getRoot();
	}

	public static Object invoke(String methodName, Object object, Class<?>[] argTypes, Object[] args)
		throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException,
		InvocationTargetException {

		Method method = object.getClass().getMethod(methodName, argTypes);
		return method.invoke(object, args);
	}

	public static boolean isEqual(Object object1, Object object2) {
		return object1 != null && object2 != null && object1.equals(object2);
	}

	public static boolean isNullOrEmpty(String val) {

		return val == null || val.equals("") || val.trim().equals("");
	}

	public static void prepareFolder(IFolder folder)
		throws CoreException {

		IContainer parent = folder.getParent();

		if (parent instanceof IFolder) {
			prepareFolder((IFolder) parent);
		}

		if (!folder.exists()) {
			folder.create(IResource.FORCE, true, null);
		}
	}

	public static String readPropertyFileValue(File propertiesFile, String key)
		throws FileNotFoundException, IOException {

		Properties props = new Properties();
		props.load(new FileInputStream(propertiesFile));
		return props.getProperty(key);
	}

	public static String readStreamToString(InputStream contents)
		throws IOException {

		if (contents == null) {
			return null;
		}

		final char[] buffer = new char[0x10000];

		StringBuilder out = new StringBuilder();

		Reader in = new InputStreamReader(contents, "UTF-8");

		int read;
		do {
			read = in.read(buffer, 0, buffer.length);
			if (read > 0) {
				out.append(buffer, 0, read);
			}
		}
		while (read >= 0);

		return out.toString();
	}

	public static Version readVersionFile(File versionInfoFile) {
		String versionContents = FileUtil.readContents(versionInfoFile);

		Version version = null;;

		try {
			version = Version.parseVersion(versionContents.trim());
		}
		catch (NumberFormatException e) {
			version = Version.emptyVersion;
		}

		return version;
	}

	public static void deleteResource(IResource resource)
		throws CoreException {
		if (resource == null || !resource.exists()) {
			return;
		}

		resource.delete(true, null);
	}

}
