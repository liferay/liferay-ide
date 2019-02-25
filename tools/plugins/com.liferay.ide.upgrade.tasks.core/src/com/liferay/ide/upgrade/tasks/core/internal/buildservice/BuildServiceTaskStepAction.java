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

package com.liferay.ide.upgrade.tasks.core.internal.buildservice;

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.adapter.NoopLiferayProject;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.IProjectBuilder;
import com.liferay.ide.project.core.util.SearchFilesVisitor;
import com.liferay.ide.upgrade.plan.core.BaseUpgradeTaskStepAction;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStepAction;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStepActionDoneEvent;
import com.liferay.ide.upgrade.tasks.core.ResourceSelection;
import com.liferay.ide.upgrade.tasks.core.internal.UpgradeTasksCorePlugin;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Simon Jiang
 */
@Component(
	property = {"id=build_services", "order=1", "stepId=build_services", "title=Build Services"},
	scope = ServiceScope.PROTOTYPE, service = UpgradeTaskStepAction.class
)
public class BuildServiceTaskStepAction extends BaseUpgradeTaskStepAction {

	@Override
	public IStatus perform() {
		final List<IProject> projects = _resourceSelection.selectJavaProjects(
			"Select Lifreay Service Builder Project",
			new Predicate<IProject>() {

				@Override
				public boolean test(IProject inputProject) {
					List<IFile> serviceXmls = (new SearchFilesVisitor()).searchFiles(inputProject, "service.xml");

					Stream<IFile> serviceXmlsStream = serviceXmls.stream();

					return serviceXmlsStream.map(
						serviceXml -> serviceXml.getProject()
					).filter(
						project -> FileUtil.exists(project)
					).filter(
						project -> project.isOpen()
					).filter(
						project -> {
							try {
								return project.hasNature("org.eclipse.jdt.core.javanature");
							}
							catch (Exception e) {
								return false;
							}
						}
					).filter(
						project -> LiferayCore.create(ILiferayProject.class, project) != null
					).filter(
						project -> !(LiferayCore.create(ILiferayProject.class, project) instanceof NoopLiferayProject)
					).findAny(
					).isPresent();
				}

			});

		if (projects.isEmpty()) {
			return Status.CANCEL_STATUS;
		}

		try {
			Job buildServiceJob = new Job("BuildServiceJob") {

				public IStatus run(IProgressMonitor monitor) {
					try {
						for (IProject project : projects) {
							_deleteLegacyFiles(project, monitor);

							final ILiferayProject liferayProject = LiferayCore.create(ILiferayProject.class, project);

							if (liferayProject != null) {
								IProjectBuilder builder = liferayProject.adapt(IProjectBuilder.class);

								builder.buildService(monitor);
							}
						}
					}
					catch (CoreException ce) {
					}

					return Status.OK_STATUS;
				}

			};

			buildServiceJob.addJobChangeListener(
				new JobChangeAdapter() {

					@Override
					public void done(IJobChangeEvent event) {
						_upgradePlanner.dispatch(new UpgradeTaskStepActionDoneEvent(BuildServiceTaskStepAction.this));
					}

				});

			buildServiceJob.schedule();
		}
		catch (Exception e1) {
		}

		return Status.OK_STATUS;
	}

	private void _deleteLegacyFiles(IProject project, IProgressMonitor monitor) {
		try {
			String relativePath = "/docroot/WEB-INF/src/META-INF";

			IFile portletSpringXML = project.getFile(relativePath + "/portlet-spring.xml");
			IFile shardDataSourceSpringXML = project.getFile(relativePath + "/shard-data-source-spring.xml");

			if (portletSpringXML.exists()) {
				portletSpringXML.delete(true, monitor);
			}

			if (shardDataSourceSpringXML.exists()) {
				shardDataSourceSpringXML.delete(true, monitor);
			}

			// for 6.2 maven project

			IFolder metaInfFolder = project.getFolder("/src/main/resources/META-INF/");

			if (metaInfFolder.exists()) {
				metaInfFolder.delete(true, monitor);
			}
		}
		catch (CoreException ce) {
			UpgradeTasksCorePlugin.logError(ce.getMessage());
		}
	}

	@Reference
	private ResourceSelection _resourceSelection;

	@Reference
	private UpgradePlanner _upgradePlanner;

}