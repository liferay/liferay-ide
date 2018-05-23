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

package com.liferay.ide.studio.ui;

import com.liferay.ide.project.core.workspace.ImportLiferayWorkspaceOp;
import com.liferay.ide.project.ui.workspace.ImportLiferayWorkspaceWizard;
import com.liferay.ide.ui.LiferayWorkspacePerspectiveFactory;
import com.liferay.ide.ui.util.ProjectExplorerLayoutUtil;

import java.io.File;

import java.util.Properties;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.intro.impl.IntroPlugin;
import org.eclipse.ui.intro.IIntroSite;
import org.eclipse.ui.intro.config.IIntroAction;

/**
 * @author Andy Wu
 */
@SuppressWarnings("restriction")
public class ImportLiferayWorkspaceFromInstallerAction implements IIntroAction {

	@Override
	public void run(IIntroSite site, Properties params) {
		Location platformLocation = Platform.getInstallLocation();

		File location = new File(platformLocation.getURL().getFile());

		if (Platform.getOS().equals(Platform.OS_MACOSX)) {
			location = location.getParentFile().getParentFile();
		}

		IPath path = new Path(location.getAbsolutePath());

		File workspaceDir = path.append("../liferay-workspace").toFile();

		if (!workspaceDir.exists()) {
			MessageDialog.openInformation(
				site.getShell(), "Liferay",
				"Can't import liferay workspace.\nDirectory \"" + workspaceDir.getAbsolutePath() + "\" doesn't exist.");

			return;
		}

		Job job = new WorkspaceJob("Importing Liferay Workspace...") {

			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
				ImportLiferayWorkspaceWizard wizard = new ImportLiferayWorkspaceWizard();

				ImportLiferayWorkspaceOp op = wizard.element().nearest(ImportLiferayWorkspaceOp.class);

				op.setWorkspaceLocation(workspaceDir.getAbsolutePath());

				op.setProvisionLiferayBundle(true);

				if (op.validation().ok()) {
					op.execute(ProgressMonitorBridge.create(monitor));

					return Status.OK_STATUS;
				}
				else {
					return StatusBridge.create(op.validation());
				}
			}

		};

		job.schedule();

		_openLiferayPerspective();

		ProjectExplorerLayoutUtil.setNested(true);

		IntroPlugin.closeIntro();
	}

	private void _openLiferayPerspective() {
		IWorkbench workbench = PlatformUI.getWorkbench();

		IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();

		IPerspectiveDescriptor perspective = activeWorkbenchWindow.getActivePage().getPerspective();

		if (!perspective.getId().equals(LiferayWorkspacePerspectiveFactory.ID)) {
			IPerspectiveRegistry reg = PlatformUI.getWorkbench().getPerspectiveRegistry();

			IPerspectiveDescriptor finalPersp = reg.findPerspectiveWithId(LiferayWorkspacePerspectiveFactory.ID);

			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

			if (window == null) {
				return;
			}

			IWorkbenchPage page = window.getActivePage();

			if (page == null) {
				return;
			}

			page.setPerspective(finalPersp);
		}
	}

}