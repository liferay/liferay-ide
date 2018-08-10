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

package com.liferay.ide.ui.swtbot.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;

import org.osgi.framework.Version;

import org.w3c.dom.Node;

/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 * @author Simon Jiang
 * @author Terry Jia
 */
public class CoreUtil {

	public static void addNaturesToProject(IProject project, String[] natureIds, IProgressMonitor monitor)
		throws CoreException {

		IProjectDescription description = project.getDescription();

		String[] prevNatures = description.getNatureIds();

		String[] newNatures = new String[prevNatures.length + natureIds.length];

		System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);

		for (int i = prevNatures.length; i < newNatures.length; i++) {
			newNatures[i] = natureIds[i - prevNatures.length];
		}

		description.setNatureIds(newNatures);

		project.setDescription(description, monitor);
	}

	public static int compareVersions(Version v1, Version v2) {
		if (v2 == v1) {
			return 0;
		}

		int result = v1.getMajor() - v2.getMajor();

		if (result != 0) {
			return result;
		}

		result = v1.getMinor() - v2.getMinor();

		if (result != 0) {
			return result;
		}

		result = v1.getMicro() - v2.getMicro();

		if (result != 0) {
			return result;
		}

		String v1Qualifier = v1.getQualifier();

		return v1Qualifier.compareTo(v2.getQualifier());
	}

	public static void createEmptyFile(IFile newFile) throws CoreException {
		if (newFile.getParent() instanceof IFolder) {
			prepareFolder((IFolder)newFile.getParent());
		}

		newFile.create(new ByteArrayInputStream(new byte[0]), true, null);
	}

	public static void deleteResource(IResource resource) throws CoreException {
		if ((resource == null) || !resource.exists()) {
			return;
		}

		resource.delete(true, null);
	}

	public static boolean empty(Object[] array) {
		return isNullOrEmpty(array);
	}

	public static boolean empty(String val) {
		return isNullOrEmpty(val);
	}

	public static IProject[] getAllProjects() {
		IWorkspace resourcesPluginWorkspace = ResourcesPlugin.getWorkspace();

		IWorkspaceRoot root = resourcesPluginWorkspace.getRoot();

		return root.getProjects();
	}

	public static IProject getProject(String projectName) {
		return getWorkspaceRoot().getProject(projectName);
	}

	public static IPath getResourceLocation(IResource resource) {
		IPath retval = null;

		if (resource != null) {
			retval = resource.getLocation();

			if (retval == null) {
				retval = resource.getRawLocation();
			}
		}

		return retval;
	}

	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	public static IWorkspaceRoot getWorkspaceRoot() {
		IWorkspace resourcesPluginWorkspace = ResourcesPlugin.getWorkspace();

		return resourcesPluginWorkspace.getRoot();
	}

	public static boolean isEqual(Object object1, Object object2) {
		if ((object1 != null) && (object2 != null) && object1.equals(object2)) {
			return true;
		}

		return false;
	}

	public static boolean isLinux() {
		return Platform.OS_LINUX.equals(Platform.getOS());
	}

	public static boolean isMac() {
		return Platform.OS_MACOSX.equals(Platform.getOS());
	}

	public static boolean isNullOrEmpty(List<?> list) {
		if (list.isEmpty()) {
			return true;
		}

		return false;
	}

	public static boolean isNullOrEmpty(Object[] array) {
		if ((array == null) || (array.length == 0)) {
			return true;
		}

		return false;
	}

	public static boolean isNullOrEmpty(String val) {
		if ((val == null) || val.equals(StringPool.EMPTY) || StringPool.EMPTY.equals(val.trim())) {
			return true;
		}

		return false;
	}

	public static boolean isNumeric(String str) {
		try {
			Double.parseDouble(str);
		}
		catch (NumberFormatException nfe) {
			return false;
		}

		return true;
	}

	public static boolean isWindows() {
		return Platform.OS_WIN32.equals(Platform.getOS());
	}

	public static void makeFolders(IFolder folder) throws CoreException {
		if (folder == null) {
			return;
		}

		IContainer parent = folder.getParent();

		if (parent instanceof IFolder) {
			makeFolders((IFolder)parent);
		}

		if (!folder.exists()) {
			folder.create(true, true, null);
		}
	}

	public static void prepareFolder(IFolder folder) throws CoreException {
		IContainer parent = folder.getParent();

		if (parent instanceof IFolder) {
			prepareFolder((IFolder)parent);
		}

		if (!folder.exists()) {
			folder.create(IResource.FORCE, true, null);
		}
	}

	public static String readPropertyFileValue(File propertiesFile, String key) throws IOException {
		try (FileInputStream fis = new FileInputStream(propertiesFile)) {
			Properties props = new Properties();

			props.load(fis);

			return props.getProperty(key);
		}
	}

	public static String readStreamToString(InputStream contents) throws IOException {
		return readStreamToString(contents, true);
	}

	public static String readStreamToString(InputStream contents, boolean closeStream) throws IOException {
		if (contents == null) {
			return null;
		}

		char[] buffer = new char[0x10000];

		StringBuilder out = new StringBuilder();

		Reader in = new InputStreamReader(contents, "UTF-8"); //$NON-NLS-1$

		int read;
		do {
			read = in.read(buffer, 0, buffer.length);

			if (read > 0) {
				out.append(buffer, 0, read);
			}
		}
		while (read >= 0);

		if (closeStream) {
			contents.close();
		}

		return out.toString();
	}

	public static void removeChildren(Node node) {
		if (node != null) {
			while (node.hasChildNodes()) {
				node.removeChild(node.getFirstChild());
			}
		}
	}

	public static void writeStreamFromString(String contents, OutputStream outputStream) throws IOException {
		if (contents == null) {
			return;
		}

		char[] buffer = new char[0x10000];

		Reader in = new InputStreamReader(new ByteArrayInputStream(contents.getBytes("UTF-8"))); //$NON-NLS-1$

		Writer out = new OutputStreamWriter(outputStream, "UTF-8"); //$NON-NLS-1$

		int read;
		do {
			read = in.read(buffer, 0, buffer.length);

			if (read > 0) {
				out.write(buffer, 0, read);
			}
		}
		while (read >= 0);

		in.close();
		out.flush();
		out.close();
	}

}