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
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.server.core.gogo.GogoTelnetClient;
import com.liferay.ide.ui.action.AbstractObjectAction;

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
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * @author Terry Jia
 */
public class WatchTaskAction extends AbstractObjectAction {

	@Override
	public void run(IAction action) {
		if (fSelection instanceof IStructuredSelection) {
			Object[] elems = ((IStructuredSelection)fSelection).toArray();

			Object elem = elems[0];

			if (!(elem instanceof IProject)) {
				return;
			}

			IProject project = (IProject)elem;

			String task = "watch";

			String jobName = project.getName() + " - " + task;

			IJobManager jobManager = Job.getJobManager();

			Job[] jobs = jobManager.find(jobName);

			if (ListUtil.isNotEmpty(jobs)) {
				return;
			}

			Job job = new Job(jobName) {

				@Override
				public boolean belongsTo(Object family) {
					return jobName.equals(family);
				}

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						GradleUtil.runGradleTask((IProject)elem, new String[] {
							"watch"
						}, new String[] {
							"--continuous"
						}, monitor);
					}
					catch (Exception e) {
						return ProjectUI.createErrorStatus("Error running Gradle task " + task, e);
					}

					return Status.OK_STATUS;
				}

			};

			job.addJobChangeListener(
				new JobChangeAdapter() {

					@Override
					public void done(IJobChangeEvent event) {
						IFile bndFile = project.getFile("bnd.bnd");

						List<Path> bndFiles = new ArrayList<>();

						if (FileUtil.notExists(bndFile)) {
							IPath location = project.getLocation();

							try {
								Files.walkFileTree(
									Paths.get(location.toOSString()), new SimpleFileVisitor<Path>() {

										@Override
										public FileVisitResult postVisitDirectory(Path dir, IOException e)
											throws IOException {

											if (FileUtil.exists(new File(dir.toFile(), "bnd.bnd"))) {
												return FileVisitResult.SKIP_SUBTREE;
											}

											return FileVisitResult.CONTINUE;
										}

										@Override
										public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
											throws IOException {

											if (file.endsWith("bnd.bnd")) {
												bndFiles.add(file);

												return FileVisitResult.SKIP_SIBLINGS;
											}

											return FileVisitResult.CONTINUE;
										}
									});
							}
							catch (IOException e) {
							}
						}
						else {
							File bnd = FileUtil.getFile(bndFile);

							bndFiles.add(bnd.toPath());
						}

						if (ListUtil.isEmpty(bndFiles)) {
							return;
						}

						for (Path bndPath : bndFiles) {
							Properties properties = new Properties();

							try (InputStream in = Files.newInputStream(bndPath)) {
								properties.load(in);

								String bsn = properties.getProperty("Bundle-SymbolicName");

								GogoTelnetClient client = new GogoTelnetClient("localhost", 11311);

								String cmd = "uninstall " + bsn;

								client.send(cmd);

								client.close();
							}
							catch (IOException ioe) {
							}
						}
					}

				});

			job.setSystem(true);

			job.schedule();
		}
	}

}