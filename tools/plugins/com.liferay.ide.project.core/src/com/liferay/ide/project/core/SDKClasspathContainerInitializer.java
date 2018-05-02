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

package com.liferay.ide.project.core;

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.server.core.portal.PortalBundle;
import com.liferay.ide.server.util.ServerUtil;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jst.common.jdt.internal.classpath.ClasspathDecorations;
import org.eclipse.jst.common.jdt.internal.classpath.ClasspathDecorationsManager;

/**
 * @author Simon.Jiang
 */
@SuppressWarnings("restriction")
public class SDKClasspathContainerInitializer extends ClasspathContainerInitializer {

	@Override
	public boolean canUpdateClasspathContainer(IPath containerPath, IJavaProject project) {
		return true;
	}

	@Override
	public void initialize(IPath containerPath, IJavaProject project) throws CoreException {
		IClasspathContainer classpathContainer = null;

		String root = containerPath.segment(0);

		if (!SDKClasspathContainer.ID.equals(root)) {
			String msg = "Invalid plugin classpath container, expecting container root ";

			throw new CoreException(ProjectCore.createErrorStatus(msg + SDKClasspathContainer.ID));
		}

		PortalBundle bundle = ServerUtil.getPortalBundle(project.getProject());

		if (bundle == null) {
			String msg = "Invalid sdk properties setting.";

			throw new CoreException(ProjectCore.createErrorStatus(msg));
		}

		IPath globalDir = bundle.getAppServerLibGlobalDir();

		IPath portalDir = bundle.getAppServerPortalDir();

		IPath bundleDir = bundle.getAppServerDir();

		IPath[] bundleDependencyJars = bundle.getBundleDependencyJars();

		IPath[] sdkDependencyJarPaths = _getSDKDependencies(project);

		if (portalDir == null) {
			return;
		}

		classpathContainer = new SDKClasspathContainer(
			containerPath, project, portalDir, null, null, globalDir, bundleDir, bundleDependencyJars,
			sdkDependencyJarPaths);

		IJavaProject[] projects = {project};

		IClasspathContainer[] containers = {classpathContainer};

		JavaCore.setClasspathContainer(containerPath, projects, containers, null);
	}

	@Override
	public void requestClasspathContainerUpdate(
			IPath containerPath, IJavaProject project, IClasspathContainer containerSuggestion)
		throws CoreException {

		String key = SDKClasspathContainer.getDecorationManagerKey(project.getProject(), containerPath.toString());

		IClasspathEntry[] entries = containerSuggestion.getClasspathEntries();

		cpDecorations.clearAllDecorations(key);

		for (IClasspathEntry entry : entries) {
			IPath srcpath = entry.getSourceAttachmentPath();

			IPath srcrootpath = entry.getSourceAttachmentRootPath();

			IClasspathAttribute[] attrs = entry.getExtraAttributes();

			if ((srcpath != null) || ListUtil.isNotEmpty(attrs)) {
				String eid = entry.getPath().toString();

				ClasspathDecorations dec = new ClasspathDecorations();

				dec.setSourceAttachmentPath(srcpath);
				dec.setSourceAttachmentRootPath(srcrootpath);
				dec.setExtraAttributes(attrs);

				cpDecorations.setDecorations(key, eid, dec);
			}
		}

		cpDecorations.save();

		IPath portalDir = null;
		IPath portalGlobalDir = null;
		String javadocURL = null;
		IPath sourceLocation = null;
		IPath bundleDir = null;
		IPath[] bundleDependencyJarPaths = null;

		PortalBundle bundle = ServerUtil.getPortalBundle(project.getProject());

		boolean containerChanged = true;

		if (containerSuggestion instanceof SDKClasspathContainer) {
			portalDir = ((SDKClasspathContainer)containerSuggestion).getPortalDir();
			bundleDir = ((SDKClasspathContainer)containerSuggestion).getBundleDir();
			portalGlobalDir = ((SDKClasspathContainer)containerSuggestion).getPortalGlobalDir();
			javadocURL = ((SDKClasspathContainer)containerSuggestion).getJavadocURL();
			sourceLocation = ((SDKClasspathContainer)containerSuggestion).getSourceLocation();
			bundleDependencyJarPaths = ((SDKClasspathContainer)containerSuggestion).getBundleLibDependencyPath();

			if ((bundle != null) && bundle.getAppServerPortalDir().equals(portalDir)) {
				containerChanged = false;
			}
		}

		if (containerChanged == true) {
			if (bundle == null) {
				return;
			}

			portalDir = bundle.getAppServerPortalDir();
			portalGlobalDir = bundle.getAppServerLibGlobalDir();
			bundleDependencyJarPaths = bundle.getBundleDependencyJars();
		}

		IPath[] sdkDependencyPaths = _getSDKDependencies(project);

		if ((portalDir != null) && (portalGlobalDir != null)) {
			IClasspathContainer newContainer = new SDKClasspathContainer(
				containerPath, project, portalDir, javadocURL, sourceLocation, portalGlobalDir, bundleDir,
				bundleDependencyJarPaths, sdkDependencyPaths);

			IJavaProject[] projects = {project};

			IClasspathContainer[] containers = {newContainer};

			JavaCore.setClasspathContainer(containerPath, projects, containers, null);
		}
	}

	protected static final ClasspathDecorationsManager cpDecorations = SDKClasspathContainer.getDecorationsManager();

	private IPath[] _getSDKDependencies(IJavaProject project) {
		IPath path = project.getProject().getLocation();

		SDK sdk = SDKUtil.getSDKFromProjectDir(path.toFile());

		if (sdk == null) {
			return null;
		}

		return sdk.getDependencyJarPaths();
	}

}