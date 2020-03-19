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

package com.liferay.ide.project.ui.modules.fragment.action;

import com.liferay.ide.core.ILiferayProjectProvider;
import com.liferay.ide.core.IWorkspaceProjectBuilder;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.core.workspace.LiferayWorkspaceUtil;
import com.liferay.ide.project.core.modules.fragment.NewModuleFragmentOp;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.server.util.ServerUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.forms.swt.SwtPresentation;
import org.eclipse.wst.server.ui.ServerUIUtil;

/**
 * @author Terry Jia
 * @author Simon Jiang
 */
public class ModuleNewLiferayRuntimeAction extends SapphireActionHandler {

	@Override
	protected Object run(Presentation context) {
		SapphirePart part = context.part();

		Element element = part.getModelElement();

		NewModuleFragmentOp op = element.nearest(NewModuleFragmentOp.class);

		IProject liferayWorkspaceProject = LiferayWorkspaceUtil.getWorkspaceProject();

		if (liferayWorkspaceProject != null) {
			try {
				String jobName = liferayWorkspaceProject.getName() + " - create workspace bundle";

				Job job = new Job(jobName) {

					@Override
					protected IStatus run(IProgressMonitor monitor) {
						try {
							monitor.beginTask(jobName, 100);

							monitor.worked(20);

							IWorkspaceProjectBuilder workspaceProjectBuilder =
								LiferayWorkspaceUtil.getWorkspaceProjectBuilder(liferayWorkspaceProject);

							workspaceProjectBuilder.initBundle(
								liferayWorkspaceProject, null, new NullProgressMonitor());

							monitor.worked(80);
						}
						catch (Exception e) {
							return ProjectUI.createErrorStatus("Error execute " + jobName, e);
						}

						return StatusBridge.create(Status.createOkStatus());
					}

				};

				job.addJobChangeListener(
					new JobChangeAdapter() {

						@Override
						public void done(IJobChangeEvent event) {
							try {
								liferayWorkspaceProject.refreshLocal(
									IResource.DEPTH_INFINITE, new NullProgressMonitor());

								ServerUtil.addPortalRuntime();

								SapphireUtil.refresh(op.property(NewModuleFragmentOp.PROP_LIFERAY_RUNTIME_NAME));
							}
							catch (CoreException ce) {
								ProjectUI.logError(ce);
							}
						}

					});

				job.setProperty(ILiferayProjectProvider.LIFERAY_PROJECT_JOB, new Object());

				job.schedule();
			}
			catch (Exception e) {
				ProjectUI.logError(e);
			}
		}
		else {
			boolean oK = ServerUIUtil.showNewRuntimeWizard(
				((SwtPresentation)context).shell(), "liferay.bundle", null, "com.liferay.");

			if (oK) {
				SapphireUtil.refresh(op.property(NewModuleFragmentOp.PROP_LIFERAY_RUNTIME_NAME));
			}
		}

		return Status.createOkStatus();
	}

}