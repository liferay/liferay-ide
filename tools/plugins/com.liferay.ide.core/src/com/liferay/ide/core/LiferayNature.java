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

package com.liferay.ide.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;

/**
 * @author Terry Jia
 * @author Andy Wu
 */
public class LiferayNature implements IProjectNature {

	public static final String NATURE_ID = LiferayCore.PLUGIN_ID + ".liferayNature";

	public static void addLiferayNature(IProject project, IProgressMonitor monitor) throws CoreException {
		if ((monitor != null) && monitor.isCanceled()) {
			throw new OperationCanceledException();
		}

		if (!hasNature(project)) {
			IProjectDescription description = project.getDescription();

			String[] prevNatures = description.getNatureIds();

			String[] newNatures = new String[prevNatures.length + 1];

			System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);

			newNatures[prevNatures.length] = NATURE_ID;

			description.setNatureIds(newNatures);

			project.setDescription(description, monitor);

			project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		}
		else if (monitor != null) {
			monitor.worked(1);
		}
	}

	public static boolean hasNature(IProject project) {
		try {
			if (project.hasNature(NATURE_ID)) {
				return true;
			}
		}
		catch (CoreException ce) {
		}

		return false;
	}

	public static void removeLiferayNature(IProject project, IProgressMonitor monitor) throws CoreException {
		if ((monitor != null) && monitor.isCanceled()) {
			throw new OperationCanceledException();
		}

		if (hasNature(project)) {
			IProjectDescription description = project.getDescription();

			String[] prevNatures = description.getNatureIds();

			String[] newNatures = new String[prevNatures.length - 1];

			int k = 0;

			for (String nature : prevNatures) {
				if (!nature.equals(NATURE_ID)) {
					newNatures[k++] = nature;
				}
			}

			description.setNatureIds(newNatures);

			project.setDescription(description, monitor);
		}
		else if (monitor != null) {
			monitor.worked(1);
		}
	}

	public LiferayNature() {
		_monitor = new NullProgressMonitor();
	}

	public LiferayNature(IProject project, IProgressMonitor monitor) {
		_currentProject = project;

		if (monitor != null) {
			_monitor = monitor;
		}
		else {
			monitor = new NullProgressMonitor();
		}
	}

	@Override
	public void configure() throws CoreException {
		addLiferayNature(_currentProject, _monitor);

		_currentProject.refreshLocal(IResource.DEPTH_INFINITE, _monitor);
	}

	@Override
	public void deconfigure() throws CoreException {
		removeLiferayNature(_currentProject, _monitor);

		_currentProject.refreshLocal(IResource.DEPTH_INFINITE, _monitor);
	}

	public IProject getProject() {
		return _currentProject;
	}

	@Override
	public void setProject(IProject project) {
		_currentProject = project;
	}

	private IProject _currentProject;
	private IProgressMonitor _monitor;

}