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

package com.liferay.ide.upgrade.problems.core.internal;

import com.liferay.ide.core.util.CoreUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import java.nio.file.Files;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

/**
 * @author Gregory Amerson
 */
public class WorkspaceHelper {

	public IFile createIFile(String projectName, File file) throws CoreException, IOException {
		IJavaProject javaProject = _getJavaProject(projectName);

		IProgressMonitor npm = new NullProgressMonitor();

		IProject project = javaProject.getProject();

		IFile projectFile = project.getFile("/temp/" + file.getName());

		if (projectFile.exists()) {
			try {
				projectFile.delete(IFile.FORCE, npm);
			}
			catch (CoreException ce) {
				IPath projectFileLocation = projectFile.getLocation();

				File tmpFile = projectFileLocation.toFile();

				tmpFile.delete();
			}
		}

		IContainer parent = projectFile.getParent();

		if (!parent.exists() && parent instanceof IFolder) {
			IFolder parentFolder = (IFolder)parent;

			parentFolder.create(true, true, npm);
		}

		byte[] bytes = Files.readAllBytes(file.toPath());

		try (ByteArrayInputStream bos = new ByteArrayInputStream(bytes)) {
			projectFile.create(bos, IFile.FORCE, npm);
		}

		return projectFile;
	}

	private void _addNaturesToProject(IProject proj, String[] natureIds, IProgressMonitor monitor)
		throws CoreException {

		IProjectDescription description = proj.getDescription();

		String[] prevNatures = description.getNatureIds();

		String[] newNatures = new String[prevNatures.length + natureIds.length];

		System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);

		for (int i = prevNatures.length; i < newNatures.length; i++) {
			newNatures[i] = natureIds[i - prevNatures.length];
		}

		description.setNatureIds(newNatures);

		proj.setDescription(description, monitor);
	}

	private IJavaProject _getJavaProject(String projectName) throws CoreException {
		IProject javaProject = CoreUtil.getProject(projectName);

		IProgressMonitor monitor = new NullProgressMonitor();

		if (!javaProject.exists()) {
			IWorkspace workspace = CoreUtil.getWorkspace();

			IProjectDescription description = workspace .newProjectDescription(projectName);

			javaProject.create(monitor);
			javaProject.open(monitor);
			javaProject.setDescription(description, monitor);
		}

		javaProject.open(monitor);

		_addNaturesToProject(javaProject, new String[] {JavaCore.NATURE_ID}, monitor);

		return JavaCore.create(javaProject);
	}

}