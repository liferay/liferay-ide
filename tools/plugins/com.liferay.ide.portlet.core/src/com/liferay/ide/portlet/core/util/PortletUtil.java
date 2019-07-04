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

package com.liferay.ide.portlet.core.util;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;

import java.util.Locale;
import java.util.stream.Stream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;

/**
 * @author Greg Amerson
 * @author Kamesh Sampath
 */
public class PortletUtil {

	/**
	 * @param displayName
	 * @param locale
	 * @return
	 */
	public static String buildLocaleDisplayString(String displayName, Locale locale) {
		StringBuilder builder = new StringBuilder(displayName);

		builder.append(" - ");
		builder.append(locale.toString());

		return builder.toString();
	}

	/**
	 * this will convert the IO file name of the resource bundle to java package
	 * name
	 *
	 * @param project
	 * @param value
	 *            - the io file name
	 * @return - the java package name of the resource bundle
	 */
	public static String convertIOToJavaFileName(IProject project, String value) {
		String rbIOFile = value.substring(value.lastIndexOf("/") + 1);
		IFile resourceBundleFile = null;

		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		IWorkspaceRoot wroot = workspace.getRoot();

		IClasspathEntry[] cpEntries = CoreUtil.getClasspathEntries(project);

		for (IClasspathEntry iClasspathEntry : cpEntries) {
			if (IClasspathEntry.CPE_SOURCE == iClasspathEntry.getEntryKind()) {
				IFolder folder = wroot.getFolder(iClasspathEntry.getPath());

				IPath entryPath = folder.getLocation();

				entryPath = entryPath.append(rbIOFile);

				resourceBundleFile = wroot.getFileForLocation(entryPath);

				if (FileUtil.exists(resourceBundleFile)) {
					break;
				}
			}
		}

		String javaName = FileUtil.toPortableString(resourceBundleFile.getProjectRelativePath());

		if (javaName.indexOf('.') != -1) {

			// Strip the extension

			javaName = value.substring(0, value.lastIndexOf('.'));

			// Replace all "/" by "."

			javaName = javaName.replace('/', '.');
		}

		return javaName;
	}

	/**
	 * This method is used to convert the java package name file to a io file name
	 * e.g. com.liferay.Test will be returned as com/liferay/Test.properties
	 *
	 * @param value
	 *            - the file name without extension in java packaging format
	 * @param extension
	 *            - the extension that needs to be attached to the file
	 * @param locales
	 *            - the locale String
	 * @return - actual io file name like value.<extension>
	 */
	public static String convertJavaToIoFileName(String value, String extension, String... locales) {

		// Replace all "." by "/"

		String strFileName = value.replace('.', '/');

		// Attach extension

		if (ListUtil.isNotEmpty(locales)) {
			strFileName = strFileName + "_" + locales[0];
		}

		strFileName = strFileName + "." + extension;

		return strFileName;
	}

	/**
	 * @param text
	 * @return
	 */
	public static IClasspathEntry getResourceFolderClasspathEntry(IProject project) {
		IClasspathEntry[] classpathEntries = Stream.of(
			CoreUtil.getClasspathEntries(project)
		).filter(
			classpathEntry -> IClasspathEntry.CPE_SOURCE == classpathEntry.getEntryKind()
		).toArray(
			IClasspathEntry[]::new
		);

		if (classpathEntries.length == 0) {
			return null;
		}

		IClasspathEntry classpathEntry = null;

		if (classpathEntries.length == 1) {
			classpathEntry = classpathEntries[0];
		}
		else {
			IFolder resourcesFolder = project.getFolder("/src/main/resources");

			if (resourcesFolder.exists()) {
				for (IClasspathEntry entry : classpathEntries) {
					IPath path = entry.getPath();

					if (path.equals(resourcesFolder.getFullPath())) {
						return entry;
					}
				}
			}
		}

		return classpathEntry;
	}

	/**
	 * This method will return the first source folder of the Java project
	 *
	 * @param javaProject
	 *            - the java project where the source folder needs to be indentified
	 * @return
	 * @throws JavaModelException
	 */
	public static IPackageFragmentRoot getResourcesFolderPacageFragmentRoot(IJavaProject javaProject)
		throws JavaModelException {

		IPackageFragmentRoot[] packageFragmentRoots = Stream.of(
			javaProject.getPackageFragmentRoots()
		).filter(
			packageFragmentRoot -> {
				try {
					if (packageFragmentRoot.getKind() == IPackageFragmentRoot.K_SOURCE) {
						return true;
					}
					else {
						return false;
					}
				}
				catch (JavaModelException jme) {
					return false;
				}
			}
		).toArray(
			IPackageFragmentRoot[]::new
		);

		if (packageFragmentRoots.length == 0) {
			return null;
		}

		IPackageFragmentRoot retval = null;

		if (packageFragmentRoots.length == 1) {
			retval = packageFragmentRoots[0];
		}
		else {
			IProject project = javaProject.getProject();

			IFolder resourcesFolder = project.getFolder("/src/main/resources");

			if (resourcesFolder.exists()) {
				for (IPackageFragmentRoot packageFragmentRoot : packageFragmentRoots) {
					retval = packageFragmentRoot;

					IPath path = retval.getPath();

					if (path.equals(resourcesFolder.getFullPath())) {
						return packageFragmentRoot;
					}
				}
			}
		}

		return retval;
	}

	/**
	 * @param text
	 * @return
	 */
	public static String localeDisplayString(String text) {
		int dash = text.indexOf("-");

		String localeDisplatString = text.substring(0, dash);

		return localeDisplatString.trim();
	}

	/**
	 * @param text
	 * @return
	 */
	public static String localeString(String text) {
		int dash = text.indexOf("-");

		String localeString = text.substring(dash + 1, text.length());

		return localeString.trim();
	}

	/**
	 * @param value
	 * @return
	 */
	public static String stripPrefix(String value) {
		String strippedValue = value;
		int colonIndex = value.indexOf(PortletAppModelConstants.COLON);

		if (colonIndex != -1) {
			strippedValue = strippedValue.substring(colonIndex + 1, strippedValue.length());
		}

		return strippedValue;
	}

	/**
	 * @param value
	 * @return
	 */
	public static String stripSuffix(String value) {
		String strippedValue = value;
		int colonIndex = value.indexOf(PortletAppModelConstants.COLON);

		if (colonIndex != -1) {
			strippedValue = strippedValue.substring(0, colonIndex);
		}

		return strippedValue;
	}

}