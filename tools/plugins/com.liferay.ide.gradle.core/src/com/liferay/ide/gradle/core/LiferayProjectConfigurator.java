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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.liferay.blade.gradle.tooling.ProjectInfo;
import com.liferay.ide.core.LiferayNature;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.io.File;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.buildship.core.InitializationContext;
import org.eclipse.buildship.core.ProjectConfigurator;
import org.eclipse.buildship.core.ProjectContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;

/**
 * @author Simon Jiang
 */
public class LiferayProjectConfigurator implements ProjectConfigurator {

	@Override
	public void configure(ProjectContext context, IProgressMonitor monitor) {
		IProject project = context.getProject();

		try {
			_configureIfLiferayProject(project);
		}
		catch (Exception e) {
			LiferayGradleCore.logError(e);
		}
	}

	@Override
	public void init(InitializationContext arg0, IProgressMonitor arg1) {
	}

	@Override
	public void unconfigure(ProjectContext arg0, IProgressMonitor arg1) {
	}

	private static IClasspathEntry _createContainerEntry(IPath path) {
		return JavaCore.newContainerEntry(path);
	}

	private void _configureIfLiferayProject(final IProject project) throws CoreException {
		IJavaProject javaProject = JavaCore.create(project);

		IProgressMonitor monitor = new NullProgressMonitor();

		if (project.hasNature(JavaCore.NATURE_ID)) {
			List<IClasspathEntry> classpath = Lists.newArrayList(javaProject.getRawClasspath());

			Map<IPath, IClasspathEntry> oldContainers = _removeOldContainers(classpath);

			Map<IPath, IClasspathEntry> containersToAdd = new HashMap<>();

			IClasspathEntry jreEntry = _createContainerEntry(_getJrePathFromSourceSettings());

			containersToAdd.put(jreEntry.getPath(), jreEntry);

			containersToAdd.putAll(oldContainers);

			classpath.addAll(_indexOfNewContainers(classpath), containersToAdd.values());

			javaProject.setRawClasspath(classpath.toArray(new IClasspathEntry[0]), monitor);
		}

		if (project.hasNature("org.eclipse.buildship.core.gradleprojectnature") && !LiferayNature.hasNature(project)) {
			final boolean[] needAddNature = new boolean[1];

			needAddNature[0] = false;

			IFile bndFile = project.getFile("bnd.bnd");

			// case 1: has bnd file

			if (FileUtil.exists(bndFile)) {
				needAddNature[0] = true;
			}
			else if (ProjectUtil.isWorkspaceWars(project)) {
				needAddNature[0] = true;
			}
			else {
				IFile gulpFile = project.getFile("gulpfile.js");

				if (FileUtil.exists(gulpFile)) {
					String gulpFileContent;

					File file = FileUtil.getFile(gulpFile);

					try {
						gulpFileContent = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);

						if (gulpFileContent.contains("require('liferay-theme-tasks')")) {
							needAddNature[0] = true;
						}
					}
					catch (IOException ioe) {
						LiferayGradleCore.logError("read gulpfile.js file fail", ioe);
					}
				}
			}

			try {
				if (needAddNature[0]) {
					LiferayNature.addLiferayNature(project, monitor);

					return;
				}

				final ProjectInfo projectInfo = LiferayGradleCore.getToolingModel(ProjectInfo.class, project);

				if (projectInfo == null) {
					throw new CoreException(
						LiferayGradleCore.createErrorStatus("Unable to get read gradle configuration"));
				}

				Set<String> pluginClassNames = projectInfo.getPluginClassNames();

				if (projectInfo.isLiferayProject() || pluginClassNames.contains("org.gradle.api.plugins.WarPlugin") ||
					pluginClassNames.contains("com.liferay.gradle.plugins.theme.builder.ThemeBuilderPlugin")) {

					LiferayNature.addLiferayNature(project, monitor);
				}
			}
			catch (Exception e) {
				LiferayGradleCore.logError("Unable to get tooling model", e);
			}
		}
	}

	private IPath _getJrePathFromSourceSettings() {
		return JavaRuntime.newJREContainerPath(JavaRuntime.getDefaultVMInstall());
	}

	private int _indexOfNewContainers(List<IClasspathEntry> classpath) {
		int index = 0;

		for (int i = 0; i < classpath.size(); i++) {
			IClasspathEntry classpathEntry = classpath.get(i);

			if (classpathEntry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
				index = i + 1;
			}
		}

		return index;
	}

	private Map<IPath, IClasspathEntry> _removeOldContainers(List<IClasspathEntry> classpath) {
		Map<IPath, IClasspathEntry> retainedEntries = Maps.newLinkedHashMap();
		ListIterator<IClasspathEntry> iterator = classpath.listIterator();

		while (iterator.hasNext()) {
			IClasspathEntry entry = iterator.next();

			if (entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
				if (_shouldRetainContainer(entry)) {
					retainedEntries.put(entry.getPath(), entry);
				}

				iterator.remove();
			}
		}

		return retainedEntries;
	}

	private boolean _shouldRetainContainer(IClasspathEntry entry) {
		IPath newDefaultJREContainerPath = JavaRuntime.newDefaultJREContainerPath();

		return !newDefaultJREContainerPath.isPrefixOf(entry.getPath());
	}

}