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

package com.liferay.ide.upgrade.steps.core;

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.IWorkspaceProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.adapter.NoopLiferayProject;
import com.liferay.ide.project.core.LiferayWorkspaceProject;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;
import com.liferay.ide.project.core.util.SearchFilesVisitor;

import java.nio.file.Path;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public interface ResourceSelection {

	public Path selectPath(String message);

	public List<IProject> selectProjects(String message, boolean initialSelectAll, Predicate<IProject> filter);

	public Predicate<IProject> JAVA_PROJECTS = new Predicate<IProject>() {

		@Override
		public boolean test(IProject project) {
			if (project == null) {
				return false;
			}

			if (!project.isOpen()) {
				return false;
			}

			try {
				if (project.hasNature("org.eclipse.jdt.core.javanature") &&
					!LiferayWorkspaceUtil.isValidWorkspace(project)) {

					return true;
				}

				return false;
			}
			catch (CoreException ce) {
				return false;
			}
		}

	};

	public Predicate<IProject> LIFERAY_PROJECTS = new Predicate<IProject>() {

		@Override
		public boolean test(IProject project) {
			if (!JAVA_PROJECTS.test(project)) {
				return false;
			}

			boolean result = Optional.ofNullable(
				project
			).filter(
				p -> LiferayCore.create(IWorkspaceProject.class, project) == null
			).map(
				p -> LiferayCore.create(ILiferayProject.class, p)
			).filter(
				Objects::nonNull
			).filter(
				p -> !(p instanceof NoopLiferayProject)
			).isPresent();

			return result;
		}

	};

	public Predicate<IProject> SERVICE_BUILDER_PROJECTS = new Predicate<IProject>() {

		@Override
		public boolean test(IProject project) {
			if (!LIFERAY_PROJECTS.test(project)) {
				return false;
			}

			SearchFilesVisitor searchFilesVisitor = new SearchFilesVisitor();

			List<IFile> serviceXmls = searchFilesVisitor.searchFiles(project, "service.xml");

			return !serviceXmls.isEmpty();
		}

	};

	public Predicate<IProject> WORKSPACE_PROJECTS = new Predicate<IProject>() {

		@Override
		public boolean test(IProject project) {
			return Optional.ofNullable(
				project
			).map(
				p -> LiferayCore.create(LiferayWorkspaceProject.class, p)
			).filter(
				Objects::nonNull
			).isPresent();
		}

	};

}