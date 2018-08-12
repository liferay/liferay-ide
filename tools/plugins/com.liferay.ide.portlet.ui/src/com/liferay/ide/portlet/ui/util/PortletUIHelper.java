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

package com.liferay.ide.portlet.ui.util;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.SapphireEditor;
import org.eclipse.sapphire.ui.forms.PropertyEditorPart;
import org.eclipse.swt.widgets.Text;

/**
 * @author Kamesh Sampath
 */
public class PortletUIHelper {

	/**
	 * @param text
	 * @param project
	 * @param searchScope
	 */
	public static void addTypeFieldAssistToText(
		PropertyEditorPart propertyEditor, Text text, IProject project, int searchScope) {
	}

	/**
	 * @param project
	 * @return
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	public static IPackageFragmentRoot[] getNonJRERoots(IJavaProject project) {
		ArrayList result = new ArrayList();

		try {
			IPackageFragmentRoot[] roots = project.getAllPackageFragmentRoots();

			for (IPackageFragmentRoot root : roots) {
				if (!isJRELibrary(root)) {
					result.add(root);
				}
			}
		}
		catch (JavaModelException jme) {
		}

		return (IPackageFragmentRoot[])result.toArray(new IPackageFragmentRoot[result.size()]);
	}

	/**
	 * @return
	 */
	public static IProject getProject(ISapphirePart sapphirePart) {
		IProject project = null;
		SapphireEditor sapphireEditor = sapphirePart.nearest(SapphireEditor.class);

		IFile editorFile = sapphireEditor.getFile();

		project = editorFile.getProject();

		return project;
	}

	/**
	 * @param project
	 * @return
	 */
	public static IJavaSearchScope getSearchScope(IJavaProject project) {
		return SearchEngine.createJavaSearchScope(getNonJRERoots(project));
	}

	/**
	 * @param project
	 * @return
	 */
	public static IJavaSearchScope getSearchScope(IProject project) {
		return getSearchScope(JavaCore.create(project));
	}

	/**
	 * @param root
	 * @return
	 */
	public static boolean isJRELibrary(IPackageFragmentRoot root) {
		try {
			IClasspathEntry classpathEntry = root.getRawClasspathEntry();

			IPath path = classpathEntry.getPath();

			if (path.equals(new Path(JavaRuntime.JRE_CONTAINER)) ||
				path.equals(new Path(JavaRuntime.JRELIB_VARIABLE))) {

				return true;
			}
		}
		catch (JavaModelException jme) {
		}

		return false;
	}

}