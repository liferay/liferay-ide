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
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.core.LiferayServerCore;
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
 * @author Gregory Amerson
 * @author Terry Jia
 */
@SuppressWarnings("restriction")
public class PluginClasspathContainerInitializer extends ClasspathContainerInitializer {

	public static final String ID = "com.liferay.ide.eclipse.server.plugin.container";

	@Override
	public boolean canUpdateClasspathContainer(IPath containerPath, IJavaProject project) {
		return true;
	}

	@Override
	public void initialize(IPath containerPath, IJavaProject project) throws CoreException {
		IClasspathContainer classpathContainer = null;

		int count = containerPath.segmentCount();

		if (count != 2) {
			String msg = "Invalid plugin classpath container should expecting 2 segments.";

			throw new CoreException(ProjectCore.createErrorStatus(msg));
		}

		String root = containerPath.segment(0);

		if (!ID.equals(root)) {
			String msg = "Invalid plugin classpath container, expecting container root ";

			throw new CoreException(ProjectCore.createErrorStatus(msg + ID));
		}

		String finalSegment = containerPath.segment(1);

		IPath portalDir = ServerUtil.getPortalDir(project);

		if (portalDir == null) {
			return;
		}

		String javadocURL = null;
		IPath sourceLocation = null;

		try {
			ILiferayRuntime liferayRuntime = ServerUtil.getLiferayRuntime(project.getProject());

			if (liferayRuntime != null) {
				javadocURL = liferayRuntime.getJavadocURL();

				sourceLocation = liferayRuntime.getSourceLocation();
			}
		}
		catch (Exception e) {
			ProjectCore.logError(e);
		}

		classpathContainer = getCorrectContainer(
			containerPath, finalSegment, project, portalDir, javadocURL, sourceLocation);

		IJavaProject[] projects = {project};

		IClasspathContainer[] containers = {classpathContainer};

		JavaCore.setClasspathContainer(containerPath, projects, containers, null);
	}

	@Override
	public void requestClasspathContainerUpdate(
			IPath containerPath, IJavaProject project, IClasspathContainer containerSuggestion)
		throws CoreException {

		String key = PluginClasspathContainer.getDecorationManagerKey(project.getProject(), containerPath.toString());

		IClasspathEntry[] entries = containerSuggestion.getClasspathEntries();

		cpDecorations.clearAllDecorations(key);

		for (IClasspathEntry entry : entries) {
			IPath srcpath = entry.getSourceAttachmentPath();
			IPath srcrootpath = entry.getSourceAttachmentRootPath();
			IClasspathAttribute[] attrs = entry.getExtraAttributes();

			if ((srcpath != null) || ListUtil.isNotEmpty(attrs)) {
				IPath path = entry.getPath();

				String eid = path.toString();

				ClasspathDecorations dec = new ClasspathDecorations();

				dec.setSourceAttachmentPath(srcpath);
				dec.setSourceAttachmentRootPath(srcrootpath);
				dec.setExtraAttributes(attrs);

				cpDecorations.setDecorations(key, eid, dec);
			}
		}

		cpDecorations.save();

		IPath portalDir = null;
		String javadocURL = null;
		IPath sourceLocation = null;

		if (containerSuggestion instanceof PluginClasspathContainer) {
			portalDir = ((PluginClasspathContainer)containerSuggestion).getPortalDir();

			javadocURL = ((PluginClasspathContainer)containerSuggestion).getJavadocURL();

			sourceLocation = ((PluginClasspathContainer)containerSuggestion).getSourceLocation();
		}
		else {
			portalDir = ServerUtil.getPortalDir(project);

			try {
				ILiferayRuntime liferayRuntime = ServerUtil.getLiferayRuntime(project.getProject());

				if (liferayRuntime != null) {
					javadocURL = liferayRuntime.getJavadocURL();

					sourceLocation = liferayRuntime.getSourceLocation();
				}
			}
			catch (Exception e) {
				ProjectCore.logError(e);
			}
		}

		if (portalDir != null) {
			IClasspathContainer newContainer = getCorrectContainer(
				containerPath, containerPath.segment(1), project, portalDir, javadocURL, sourceLocation);

			IJavaProject[] projects = {project};

			IClasspathContainer[] containers = {newContainer};

			JavaCore.setClasspathContainer(containerPath, projects, containers, null);
		}
	}

	protected IClasspathContainer getCorrectContainer(
			IPath containerPath, String finalSegment, IJavaProject project, IPath portalDir, String javadocURL,
			IPath sourceURL)
		throws CoreException {

		if (PortletClasspathContainer.SEGMENT_PATH.equals(finalSegment)) {
			return new PortletClasspathContainer(containerPath, project, portalDir, javadocURL, sourceURL);
		}
		else if (HookClasspathContainer.SEGMENT_PATH.equals(finalSegment)) {
			return new HookClasspathContainer(containerPath, project, portalDir, javadocURL, sourceURL);
		}
		else if (ExtClasspathContainer.SEGMENT_PATH.equals(finalSegment)) {
			return new ExtClasspathContainer(containerPath, project, portalDir, javadocURL, sourceURL);
		}
		else if (ThemeClasspathContainer.SEGMENT_PATH.equals(finalSegment)) {
			return new ThemeClasspathContainer(containerPath, project, portalDir, javadocURL, sourceURL);
		}
		else if (WebClasspathContainer.SEGMENT_PATH.equals(finalSegment)) {
			return new WebClasspathContainer(containerPath, project, portalDir, javadocURL, sourceURL);
		}
		else {
			throw new CoreException(LiferayServerCore.error("Invalid final segment of type: " + finalSegment));
		}
	}

	protected static final ClasspathDecorationsManager cpDecorations = PluginClasspathContainer.getDecorationsManager();

}