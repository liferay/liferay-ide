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

import com.liferay.ide.core.IWorkspaceProject;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.DeleteArguments;
import org.eclipse.ltk.core.refactoring.participants.DeleteParticipant;

/**
 * @author Simon Jiang
 */
public class LiferayGradleModuleProjectDeleteParticipant extends DeleteParticipant {

	@Override
	public RefactoringStatus checkConditions(IProgressMonitor monitor, CheckConditionsContext context)
		throws OperationCanceledException {

		return new RefactoringStatus();
	}

	@Override
	public Change createChange(IProgressMonitor monitor) throws CoreException, OperationCanceledException {
		return new RemoveModulePostChange(_deleteProject);
	}

	@Override
	public Change createPreChange(IProgressMonitor monitor) throws CoreException, OperationCanceledException {
		return new RemoveModulePreChange(_deleteProject);
	}

	@Override
	public String getName() {
		return "Remove Liferay Module Project DeleteParticipant";
	}

	public abstract class RemoveModuleChange extends Change {

		public RemoveModuleChange(IProject project) {
			deleteProject = project;
		}

		@Override
		public Object getModifiedElement() {
			return deleteProject;
		}

		@Override
		public void initializeValidationData(IProgressMonitor monitor) {
		}

		@Override
		public RefactoringStatus isValid(IProgressMonitor monitor) throws CoreException, OperationCanceledException {
			return new RefactoringStatus();
		}

		@Override
		public abstract Change perform(IProgressMonitor monitor) throws CoreException;

		protected IProject deleteProject;

	}

	public class RemoveModulePostChange extends RemoveModuleChange {

		public RemoveModulePostChange(IProject project) {
			super(project);
		}

		@Override
		public String getName() {
			return "Postproccess for removing module project from watch list '" + deleteProject.getName() + "'";
		}

		@Override
		public Change perform(IProgressMonitor monitor) throws CoreException {
			IWorkspaceProject liferayWorkspaceProject = LiferayWorkspaceUtil.getLiferayWorkspaceProject();

			if (liferayWorkspaceProject == null) {
				return null;
			}

			if (ListUtil.isNotEmpty(_projectsToWatch)) {
				liferayWorkspaceProject.watch(_projectsToWatch);
			}

			return null;
		}

	}

	public class RemoveModulePreChange extends RemoveModuleChange {

		public RemoveModulePreChange(IProject project) {
			super(project);
		}

		@Override
		public String getName() {
			return "Preparing to remove module project from watch task '" + deleteProject.getName() + "'";
		}

		@Override
		public Change perform(IProgressMonitor monitor) throws CoreException {
			IWorkspaceProject liferayWorkspaceProject = LiferayWorkspaceUtil.getLiferayWorkspaceProject();

			if (liferayWorkspaceProject == null) {
				return null;
			}

			IProject workspaceProject = liferayWorkspaceProject.getProject();

			if (!_projectsToWatch.contains(workspaceProject)) {
				Stream<IProject> stream = _projectsToWatch.stream();

				_projectsToWatch = stream.filter(
					project -> !project.equals(deleteProject)
				).collect(
					Collectors.toSet()
				);
			}
			else if (deleteProject.equals(workspaceProject)) {
				_projectsToWatch.remove(deleteProject);
			}

			String jobName = workspaceProject.getName() + ":" + LiferayGradleCore.LIFERAY_WATCH;

			IJobManager jobManager = Job.getJobManager();

			Job[] jobs = jobManager.find(jobName);

			if (ListUtil.isNotEmpty(jobs)) {
				Job job = jobs[0];

				job.cancel();

				try {
					job.join();
				}
				catch (InterruptedException ie) {
				}
			}

			return null;
		}

	}

	@Override
	protected boolean initialize(Object element) {
		if (!(element instanceof IProject)) {
			return false;
		}

		DeleteArguments deleteArguments = getArguments();

		if (!deleteArguments.getDeleteProjectContents()) {
			return false;
		}

		_deleteProject = (IProject)element;

		try {
			boolean gradleProject = GradleUtil.isGradleProject(_deleteProject);

			if (!gradleProject) {
				return false;
			}

			IProject workspaceProject = LiferayWorkspaceUtil.getWorkspaceProject();

			IWorkspaceProject liferayWorkspaceProject = LiferayWorkspaceUtil.getLiferayWorkspaceProject();

			if (liferayWorkspaceProject != null) {
				_projectsToWatch = new HashSet<>(liferayWorkspaceProject.watching());

				Set<IProject> childProjects = new HashSet<>(liferayWorkspaceProject.getChildProjects());

				if (ListUtil.contains(_projectsToWatch, workspaceProject)) {
					if (_deleteProject.equals(workspaceProject)) {
						return true;
					}

					return childProjects.contains(_deleteProject);
				}

				return _projectsToWatch.contains(_deleteProject);
			}
		}
		catch (Exception e) {
			LiferayGradleCore.logError("Failed to initialize module project deleteParticipant", e);
		}

		return false;
	}

	private IProject _deleteProject;
	private Set<IProject> _projectsToWatch;

}