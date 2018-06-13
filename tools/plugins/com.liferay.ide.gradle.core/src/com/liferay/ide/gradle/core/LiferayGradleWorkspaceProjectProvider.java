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

import com.liferay.ide.core.AbstractLiferayProjectProvider;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.IWorkspaceProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.BladeCLI;
import com.liferay.ide.project.core.modules.BladeCLIException;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;
import com.liferay.ide.project.core.workspace.BaseLiferayWorkspaceOp;
import com.liferay.ide.project.core.workspace.NewLiferayWorkspaceOp;
import com.liferay.ide.project.core.workspace.NewLiferayWorkspaceProjectProvider;
import com.liferay.ide.server.util.ServerUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Andy Wu
 * @author Terry Jia
 * @author Charles Wu
 */
public class LiferayGradleWorkspaceProjectProvider
	extends AbstractLiferayProjectProvider implements NewLiferayWorkspaceProjectProvider<NewLiferayWorkspaceOp> {

	public LiferayGradleWorkspaceProjectProvider() {
		super(new Class<?>[] {IProject.class, IServer.class});
	}

	@Override
	public IStatus createNewProject(NewLiferayWorkspaceOp op, IProgressMonitor monitor) throws CoreException {
		Value<Path> locationPath = op.getLocation();

		IPath location = PathBridge.create(locationPath.content());

		Value<String> workspaceNameValue = op.getWorkspaceName();

		String workspaceName = workspaceNameValue.toString();

		IPath workspaceLocation = location.append(workspaceName);

		Value<String> version = op.getLiferayVersion();

		StringBuilder sb = new StringBuilder();

		sb.append("--base ");
		sb.append("\"");
		sb.append(workspaceLocation.toOSString());
		sb.append("\" ");
		sb.append("init ");
		sb.append("-v ");
		sb.append(version.content());

		try {
			BladeCLI.execute(sb.toString());
		}
		catch (BladeCLIException bclie) {
			return ProjectCore.createErrorStatus(bclie);
		}

		IPath wsLocation = location.append(workspaceName);

		IStatus importProjectStatus = importProject(wsLocation, monitor);

		if (importProjectStatus != Status.OK_STATUS) {
			return importProjectStatus;
		}

		Value<Boolean> provisionLiferayBundleValue = op.getProvisionLiferayBundle();

		boolean initBundle = provisionLiferayBundleValue.content();

		if (initBundle) {
			Value<String> bundleUrlValue = op.getBundleUrl();

			String bundleUrl = bundleUrlValue.content(false);

			Value<String> serverNameValue = op.getServerName();

			String serverName = serverNameValue.content(true);

			initBundle(bundleUrl, serverName, workspaceName);
		}

		return Status.OK_STATUS;
	}

	@Override
	public String getInitBundleUrl(String workspaceLocation) {
		return LiferayWorkspaceUtil.getGradleProperty(
			workspaceLocation, LiferayWorkspaceUtil.LIFERAY_WORKSPACE_BUNDLE_URL,
			BaseLiferayWorkspaceOp.LIFERAY_70_BUNDLE_URL);
	}

	@Override
	public IStatus importProject(IPath wsLocation, IProgressMonitor monitor) {
		try {
			CoreUtil.openProject(wsLocation.lastSegment(), wsLocation, monitor);
		}
		catch (CoreException ce) {
			return ProjectCore.createErrorStatus(ce);
		}

		return GradleUtil.sychronizeProject(wsLocation, monitor);
	}

	@Override
	public void initBundle(String bundleUrl, String serverName, String wsName) {
		IProject project = CoreUtil.getProject(wsName);

		String jobName = "Initializing Liferay bundle";

		Job job = new Job(jobName) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					if (bundleUrl != null) {
						IFile gradlePropertiesFile = project.getFile("gradle.properties");

						try (InputStream gradleStream = gradlePropertiesFile.getContents()) {
							String content = FileUtil.readContents(gradleStream);

							String bundleUrlProp = LiferayWorkspaceUtil.LIFERAY_WORKSPACE_BUNDLE_URL + "=" + bundleUrl;

							String separator = System.getProperty("line.separator", "\n");

							String newContent = content + separator + bundleUrlProp;

							try (InputStream inputStream = new ByteArrayInputStream(newContent.getBytes())) {
								gradlePropertiesFile.setContents(inputStream, IResource.FORCE, monitor);
							}
						}
					}

					GradleUtil.runGradleTask(project, jobName, monitor);

					monitor.worked(95);

					project.refreshLocal(IResource.DEPTH_INFINITE, monitor);

					monitor.worked(5);
				}
				catch (Exception e) {
					return GradleCore.createErrorStatus("Init Liferay Bundle failed", e);
				}

				return Status.OK_STATUS;
			}

		};

		job.addJobChangeListener(
			new JobChangeAdapter() {

				@Override
				public void done(IJobChangeEvent event) {
					LiferayWorkspaceUtil.addPortalRuntime(serverName);
				}

			});

		job.schedule();
	}

	@Override
	public synchronized ILiferayProject provide(Object adaptable) {
		if (adaptable instanceof IProject) {
			final IProject project = (IProject)adaptable;

			try {
				if (GradleUtil.isGradleProject(project) && LiferayWorkspaceUtil.isValidWorkspace(project)) {
					return new LiferayGradleWorkspaceProject(project);
				}
			}
			catch (CoreException ce) {
				return null;
			}
		}

		Optional<Object> nullableAdaptable = Optional.ofNullable(adaptable);

		return nullableAdaptable.filter(
			i -> i instanceof IServer
		).map(
			IServer.class::cast
		).map(
			ServerUtil::getLiferayRuntime
		).map(
			liferayRuntime -> liferayRuntime.getLiferayHome()
		).map(
			LiferayGradleWorkspaceProjectProvider::_getWorkspaceProjectFromLiferayHome
		).orElse(
			null
		);
	}

	@Override
	public IStatus validateProjectLocation(String projectName, IPath path) {
		IStatus retval = Status.OK_STATUS;

		// TODO validation gradle project location

		return retval;
	}

	private static IWorkspaceProject _getWorkspaceProjectFromLiferayHome(final IPath liferayHome) {
		Optional<IProject> workspace = Optional.ofNullable(LiferayWorkspaceUtil.getWorkspaceProject());

		return workspace.filter(
			workspaceProject -> {
				IPath workspaceProjectLocation = workspaceProject.getRawLocation();

				if (workspaceProjectLocation == null) {
					return false;
				}

				return workspaceProjectLocation.isPrefixOf(liferayHome);
			}
		).map(
			workspaceProject -> LiferayCore.create(IWorkspaceProject.class, workspaceProject)
		).orElse(
			null
		);
	}

}