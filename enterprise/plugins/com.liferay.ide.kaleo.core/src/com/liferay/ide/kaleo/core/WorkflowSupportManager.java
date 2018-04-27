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

package com.liferay.ide.kaleo.core;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Gregory Amerson
 */
public class WorkflowSupportManager {

	public static final String SUPPORT_PROJECT_NAME = "Kaleo Designer Support";

	public WorkflowSupportManager() {
	}

	public IProject createSupportProject(IProgressMonitor monitor) throws CoreException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		IWorkspaceRoot root = workspace.getRoot();

		IProject project = root.getProject(SUPPORT_PROJECT_NAME);

		if (FileUtil.exists(project)) {
			if (!project.isOpen()) {
				project.open(monitor);
			}

			return project;
		}

		project.create(CoreUtil.newSubmonitor(monitor, 1));
		project.open(CoreUtil.newSubmonitor(monitor, 1));

		CoreUtil.makeFolders(project.getFolder("src"));

		String[] natureID = {JavaCore.NATURE_ID};

		CoreUtil.addNaturesToProject(project, natureID, CoreUtil.newSubmonitor(monitor, 1));

		IJavaProject jProject = JavaCore.create(project);

		IPath fullPath = project.getFullPath();

		jProject.setOutputLocation(fullPath.append("bin"), CoreUtil.newSubmonitor(monitor, 1));

		_computeClasspath(jProject, CoreUtil.newSubmonitor(monitor, 1));

		return project;
	}

	public IJavaProject getSupportProject() {
		try {
			_checkForSupportProject();

			IWorkspace workSpace = ResourcesPlugin.getWorkspace();

			IWorkspaceRoot root = workSpace.getRoot();

			IProject project = root.getProject(SUPPORT_PROJECT_NAME);

			if (FileUtil.exists(project) && project.isOpen() && project.hasNature(JavaCore.NATURE_ID)) {
				return JavaCore.create(project);
			}
		}
		catch (CoreException ce) {
		}

		return null;
	}

	public void setCurrentServer(IServer server) {
		_currentServer = server;
	}

	private void _checkForSupportProject() {
		try {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();

			IWorkspaceRoot root = workspace.getRoot();

			IProject project = root.getProject(SUPPORT_PROJECT_NAME);

			if (!FileUtil.exists(project) || !project.isOpen()) {
				createSupportProject(new NullProgressMonitor());
			}

			_computeClasspath(JavaCore.create(project), new NullProgressMonitor());
		}
		catch (CoreException ce) {
		}
	}

	private void _computeClasspath(IJavaProject project, IProgressMonitor monitor) {
		int numEntries = 2;
		IPath runtimeContainerPath = null;

		try {
			IRuntime runtime = _currentServer.getRuntime();

			String id = runtime.getId();

			String runtimeClasspathProvider =
				"org.eclipse.jst.server.core.container/com.liferay.ide.eclipse.server.tomcat.runtimeClasspathProvider/";

			runtimeContainerPath = new Path(runtimeClasspathProvider + id);

			numEntries++;
		}
		catch (Throwable t) {
		}

		IClasspathEntry[] classpath = new IClasspathEntry[numEntries];

		classpath[0] = JavaCore.newContainerEntry(JavaRuntime.newDefaultJREContainerPath());

		IProject javaProject = project.getProject();

		IFolder folder = javaProject.getFolder("src");

		classpath[1] = JavaCore.newSourceEntry(folder.getFullPath());

		if (FileUtil.exists(runtimeContainerPath)) {
			classpath[2] = JavaCore.newContainerEntry(runtimeContainerPath);
		}

		try {
			project.setRawClasspath(classpath, monitor);
		}
		catch (JavaModelException jme) {
		}
	}

	private IServer _currentServer;

}