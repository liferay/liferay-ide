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

package com.liferay.ide.maven.core;

import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.util.ServerUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.m2e.core.project.IProjectCreationListener;

/**
 * @author Haoyi Sun
 */
public class LiferayMavenWorkspaceCreateListener implements IProjectCreationListener {

	public LiferayMavenWorkspaceCreateListener(String workspacepLocation, String bundleUrl) {
		_bundleUrl = bundleUrl;
		_workspacepLocation = workspacepLocation;
	}

	@Override
	public void projectCreated(IProject project) {
		String projectLocation = project.getLocation().toOSString();

		if (!projectLocation.equals(_workspacepLocation) &&
			!LiferayWorkspaceUtil.isValidMavenWorkspaceLocation(projectLocation)) {

			return;
		}

		Job initBundleJob = new Job("Init Liferay Bundle") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				IProject workspaceProject = ProjectUtil.getProject(project.getName());

				MavenProjectBuilder mavenProjectBuilder = new MavenProjectBuilder(workspaceProject);

				try {
					mavenProjectBuilder.initBundle(workspaceProject, _bundleUrl, monitor);
					workspaceProject.refreshLocal(IResource.DEPTH_INFINITE, monitor);

					String workspaceProjectName = workspaceProject.getName();

					IPath workspaceProjectPath = workspaceProject.getLocation();

					IPath bundlesPath = workspaceProjectPath.append("bundles");

					if (LiferayServerCore.isPortalBundlePath(bundlesPath)) {
						ServerUtil.addPortalRuntimeAndServer(workspaceProjectName, bundlesPath, monitor);
					}
				}
				catch (CoreException ce) {
					LiferayMavenCore.logError("Init LiferayBundle failed", ce);
				}

				return Status.OK_STATUS;
			}

		};

		initBundleJob.schedule();
	}

	private String _bundleUrl;
	private String _workspacepLocation;

}