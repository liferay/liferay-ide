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

package com.liferay.ide.gradle.action;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.gradle.core.GradleUtil;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.ui.action.AbstractObjectAction;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * @author Lovett Li
 * @author Terry Jia
 * @author Charles Wu
 */
public abstract class GradleTaskAction extends AbstractObjectAction {

	public GradleTaskAction() {
	}

	public void run(IAction action) {
		if (fSelection instanceof IStructuredSelection) {
			Object[] elems = ((IStructuredSelection)fSelection).toArray();

			IFile gradleBuildFile = null;

			Object elem = elems[0];

			if (elem instanceof IFile) {
				gradleBuildFile = (IFile)elem;

				project = gradleBuildFile.getProject();
			}
			else if (elem instanceof IProject) {
				project = (IProject)elem;

				gradleBuildFile = project.getFile("build.gradle");
			}

			if (FileUtil.notExists(gradleBuildFile)) {
				return;
			}

			Job job = new Job(project.getName() + " - " + getGradleTask()) {

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						monitor.beginTask(getGradleTask(), 100);

						GradleUtil.runGradleTask(project, getGradleTask(), monitor);

						monitor.worked(80);

						project.refreshLocal(IResource.DEPTH_INFINITE, monitor);

						monitor.worked(20);
					}
					catch (Exception e) {
						return ProjectUI.createErrorStatus("Error running Gradle goal " + getGradleTask(), e);
					}

					return Status.OK_STATUS;
				}

			};

			job.addJobChangeListener(new JobChangeAdapter() {

				@Override
				public void done(IJobChangeEvent event) {
					afterTask();
				}
			});

			job.schedule();
		}
	}

	protected void afterTask() {
	}

	protected abstract String getGradleTask();

	protected IProject project = null;

}