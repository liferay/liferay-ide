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
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.DirectoryDialog;

import org.osgi.service.component.annotations.Component;

/**
 * @author Terry Jia
 */
@Component(service = ResourceSelection.class)
public class ResourceSelectionImpl implements ResourceSelection {

	@Override
	public Path selectFolder(String message, Function<Path, IStatus> pathValidator) {
		StringBuffer sb = new StringBuffer(1);

		UIUtil.sync(
			() -> {
				DirectoryDialog directoryDialog = new DirectoryDialog(UIUtil.getActiveShell());

				String pathValue = directoryDialog.open();

				if (pathValue != null) {
					sb.append(pathValue);
				}
			});

		String path = sb.toString();

		if ("".equals(path)) {
			return null;
		}

		return Paths.get(path);
	}

	@Override
	public IProject[] selectProjects(String message, boolean selectAllDefault, Function<Path, IStatus> pathValidator) {
		final AtomicInteger returnCode = new AtomicInteger();

		List<IProject> projects = new ArrayList<>();

		UIUtil.sync(
			() -> {
				ProjectsSelectionDialog dialog = new ProjectsSelectionDialog(
					UIUtil.getActiveShell(), null, selectAllDefault, message);

				returnCode.set(dialog.open());

				for (IProject project : dialog.getProjects()) {
					projects.add(project);
				}
			});

		if (returnCode.get() == Window.OK) {
			return projects.toArray(new IProject[0]);
		}

		return new IProject[0];
	}

}