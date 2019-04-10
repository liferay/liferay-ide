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

package com.liferay.ide.hook.ui;

import com.liferay.ide.core.util.MarkerUtil;
import com.liferay.ide.hook.core.util.HookUtil;
import com.liferay.ide.ui.util.UIUtil;

import java.net.URL;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution2;

import org.osgi.framework.Bundle;

/**
 * @author Simon Jiang
 */
public class HookCustomJspValidationResolution implements IMarkerResolution2 {

	public String getDescription() {
		return getLabel();
	}

	public Image getImage() {
		HookUI hookUI = HookUI.getDefault();

		Bundle bundle = hookUI.getBundle();

		URL url = bundle.getEntry("/icons/e16/disabled.png");

		ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(url);

		return imageDescriptor.createImage();
	}

	public String getLabel() {
		return Msgs.disableCustomJspValidation;
	}

	public void run(IMarker marker) {
		IProject project = MarkerUtil.getProject(marker);

		IPath customJspPath = HookUtil.getCustomJspPath(project);

		if (customJspPath != null) {
			IFolder folder = project.getFolder(customJspPath.makeRelativeTo(project.getFullPath()));

			boolean retval = HookUtil.configureJSPSyntaxValidationExclude(project, folder, true);

			if (retval) {
				UIUtil.async(
					new Runnable() {

						public void run() {
							boolean revalidate = MessageDialog.openQuestion(
								UIUtil.getActiveShell(), Msgs.revalidateTitle, Msgs.revalidateMsg);

							if (revalidate) {
								new WorkspaceJob(
									"revalidating " + project.getName()
								) {

									@Override
									public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
										project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());

										return Status.OK_STATUS;
									}

								}.schedule();
							}
						}

					});
			}
		}
	}

	private static class Msgs extends NLS {

		public static String disableCustomJspValidation;
		public static String revalidateMsg;
		public static String revalidateTitle;

		static {
			initializeMessages(HookCustomJspValidationResolution.class.getName(), Msgs.class);
		}

	}

}