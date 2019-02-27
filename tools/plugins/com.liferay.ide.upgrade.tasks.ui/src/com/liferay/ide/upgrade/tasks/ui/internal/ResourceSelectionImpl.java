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

package com.liferay.ide.upgrade.tasks.ui.internal;

import com.liferay.ide.ui.util.UIUtil;
import com.liferay.ide.upgrade.tasks.core.ResourceSelection;
import com.liferay.ide.upgrade.tasks.ui.internal.dialog.ProjectsSelectionDialog;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.DirectoryDialog;

import org.osgi.service.component.annotations.Component;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 */
@Component(service = ResourceSelection.class)
public class ResourceSelectionImpl implements ResourceSelection {

	@Override
	public Path selectPath(String message) {
		final String[] pathValue = new String[1];

		UIUtil.sync(
			() -> {
				DirectoryDialog directoryDialog = new DirectoryDialog(UIUtil.getActiveShell());

				pathValue[0] = directoryDialog.open();
			});

		if (pathValue[0] == null) {
			return null;
		}

		return Paths.get(pathValue[0]);
	}

	@Override
	public List<IProject> selectProjects(String message, boolean initialSelectAll, Predicate<IProject> filter) {
		final AtomicInteger returnCode = new AtomicInteger();

		List<IProject> selectedProjects = new ArrayList<>();

		ViewerFilter viewerFilter = new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				return Optional.ofNullable(
					filter
				).filter(
					Objects::nonNull
				).map(
					predicate -> predicate.test((IProject)element)
				).orElse(
					true
				);
			}

		};

		UIUtil.sync(
			() -> {
				ProjectsSelectionDialog projectsSelectionDialog = new ProjectsSelectionDialog(
					UIUtil.getActiveShell(), viewerFilter, initialSelectAll, message);

				returnCode.set(projectsSelectionDialog.open());

				selectedProjects.addAll(projectsSelectionDialog.getSelectedProjects());
			});

		if (returnCode.get() == Window.OK) {
			return selectedProjects;
		}

		return Collections.emptyList();
	}

}