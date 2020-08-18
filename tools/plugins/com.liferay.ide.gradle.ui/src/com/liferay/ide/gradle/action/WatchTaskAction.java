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
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.gradle.core.GradleUtil;
import com.liferay.ide.gradle.core.LiferayGradleCore;
import com.liferay.ide.gradle.core.WatchJob;
import com.liferay.ide.gradle.ui.LiferayGradleUI;
import com.liferay.ide.server.core.ILiferayServer;
import com.liferay.ide.server.core.gogo.GogoShellClient;
import com.liferay.ide.server.core.portal.PortalServerBehavior;
import com.liferay.ide.ui.action.AbstractObjectAction;
import com.liferay.ide.ui.util.UIUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IDecoratorManager;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.server.core.ServerCore;

import org.gradle.tooling.model.DomainObjectSet;
import org.gradle.tooling.model.GradleProject;

/**
 * @author Terry Jia
 * @author Simon Jiang
 */
public class WatchTaskAction extends AbstractObjectAction {

	@Override
	public void run(IAction action) {
		if (fSelection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection)fSelection;

			Object[] elements = structuredSelection.toArray();

			if (ListUtil.isEmpty(elements)) {
				return;
			}

			Object element = elements[0];

			if (!(element instanceof IProject)) {
				return;
			}

			IProject project = (IProject)element;

			IJobManager jobManager = Job.getJobManager();

			Job[] jobs = jobManager.find(
				project.getName() + ":" + LiferayGradleCore.LIFERAY_WATCH + ":" +
					LiferayGradleUI.LIFERAY_STANDALONE_WATCH_JOB_FAMILY);

			if (ListUtil.isNotEmpty(jobs)) {
				return;
			}

			Job job = new WatchJob(
				project, Arrays.asList("watch"), LiferayGradleUI.LIFERAY_STANDALONE_WATCH_JOB_FAMILY);

			job.addJobChangeListener(
				new JobChangeAdapter() {

					@Override
					public void done(IJobChangeEvent event) {
						List<Path> bndPaths = _getBndPaths(project);

						if (ListUtil.isEmpty(bndPaths)) {
							return;
						}

						try (GogoShellClient client = new GogoShellClient("localhost", 11311)) {
							for (Path bndPath : bndPaths) {
								Properties properties = new Properties();

								try (InputStream in = Files.newInputStream(bndPath)) {
									properties.load(in);

									String bsn = properties.getProperty("Bundle-SymbolicName");

									String cmd = "uninstall " + bsn;

									client.send(cmd);

									GradleProject gradleProject = GradleUtil.getGradleProject(project);

									if (gradleProject != null) {
										_deleteInstallBundleIdFromBuildDirectory(gradleProject);
									}
								}
								catch (IOException ioe) {
								}
							}
						}
						catch (IOException ioe) {
							LiferayGradleUI.logError("Could not uninstall bundles installed by watch task", ioe);
						}

						_refreshDecorator();
					}

					@Override
					public void running(IJobChangeEvent event) {
						_refreshDecorator();
					}

					@Override
					public void scheduled(IJobChangeEvent event) {
						Stream.of(
							ServerCore.getServers()
						).map(
							server -> (PortalServerBehavior)server.loadAdapter(
								PortalServerBehavior.class, new NullProgressMonitor())
						).filter(
							serverBehavior -> serverBehavior != null
						).forEach(
							serverBehavior -> serverBehavior.addWatchProject(project)
						);
					}

				});

			job.setProperty(ILiferayServer.LIFERAY_SERVER_JOB, this);
			job.setSystem(false);
			job.schedule();
		}
	}

	private void _deleteInstallBundleIdFromBuildDirectory(GradleProject gradleProject) {
		if (gradleProject == null) {
			return;
		}

		File buildDirectory = gradleProject.getBuildDirectory();

		if (FileUtil.exists(buildDirectory)) {
			File installedBundleIdFile = new File(buildDirectory, "installedBundleId");

			FileUtil.delete(installedBundleIdFile);
		}

		DomainObjectSet<? extends GradleProject> childrenGradleProjects = gradleProject.getChildren();

		if (ListUtil.isEmpty(childrenGradleProjects)) {
			return;
		}

		for (GradleProject childGradleProject : childrenGradleProjects) {
			_deleteInstallBundleIdFromBuildDirectory(childGradleProject);
		}
	}

	private List<Path> _getBndPaths(IProject project) {
		List<Path> bndPaths = new ArrayList<>();

		IFile projectBndFile = project.getFile("bnd.bnd");

		if (FileUtil.notExists(projectBndFile)) {
			IPath location = project.getLocation();

			try {
				Files.walkFileTree(
					Paths.get(location.toOSString()),
					new SimpleFileVisitor<Path>() {

						@Override
						public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
							if (FileUtil.exists(new File(dir.toFile(), "bnd.bnd"))) {
								return FileVisitResult.SKIP_SUBTREE;
							}

							return FileVisitResult.CONTINUE;
						}

						@Override
						public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
							if (path.endsWith("bnd.bnd")) {
								bndPaths.add(path);

								return FileVisitResult.SKIP_SIBLINGS;
							}

							return FileVisitResult.CONTINUE;
						}

					});
			}
			catch (IOException ioe) {
			}
		}
		else {
			File bndFile = FileUtil.getFile(projectBndFile);

			bndPaths.add(bndFile.toPath());
		}

		return bndPaths;
	}

	private void _refreshDecorator() {
		IWorkbench workbench = PlatformUI.getWorkbench();

		IDecoratorManager decoratorManager = workbench.getDecoratorManager();

		UIUtil.async(() -> decoratorManager.update(LiferayGradleUI.LIFERAY_WATCH_DECORATOR_ID));
	}

}