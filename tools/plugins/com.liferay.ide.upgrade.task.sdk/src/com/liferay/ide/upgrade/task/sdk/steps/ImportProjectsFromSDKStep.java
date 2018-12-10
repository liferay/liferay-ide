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

package com.liferay.ide.upgrade.task.sdk.steps;

import com.liferay.ide.project.ui.wizard.ImportSDKProjectsWizard;
import com.liferay.ide.ui.util.UIUtil;
import com.liferay.ide.upgrade.plan.api.UpgradeTaskStep;
import com.liferay.ide.upgrade.plan.base.WorkspaceUpgradeTaskStep;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.PlatformUI;

import org.osgi.service.component.annotations.Component;

/**
 * @author Terry Jia
 */
@Component(properties = "OSGI-INF/ImportProjectsFromSDKStep.properties", service = UpgradeTaskStep.class)
public class ImportProjectsFromSDKStep extends WorkspaceUpgradeTaskStep {

	@Override
	protected IStatus execute(IProject project, IProgressMonitor progressMonitor) {
		IPath projectLocation = project.getLocation();

		IPath sdkLocation = projectLocation.append("plugins-sdk");

		INewWizard wizard = new ImportSDKProjectsWizard(sdkLocation);

		wizard.init(PlatformUI.getWorkbench(), null);

		WizardDialog dialog = new WizardDialog(UIUtil.getActiveShell(), wizard);

		PixelConverter converter = new PixelConverter(JFaceResources.getDialogFont());

		dialog.setMinimumPageSize(
			converter.convertWidthInCharsToPixels(70), converter.convertHeightInCharsToPixels(20));

		dialog.create();

		if (dialog.open() == Window.OK) {
			return Status.OK_STATUS;
		}

		return Status.CANCEL_STATUS;
	}

}