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

package com.liferay.ide.project.ui.action.sdk;

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.ui.action.AbstractObjectAction;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * @author Gregory Amerson
 */
public abstract class SDKCommandAction extends AbstractObjectAction {

	public SDKCommandAction() {
	}

	@Override
	public void run(IAction action) {
		if (fSelection instanceof IStructuredSelection) {
			Object[] elems = ((IStructuredSelection)fSelection).toArray();

			IFile buildXmlFile = null;
			IProject project = null;

			Object elem = elems[0];

			if (elem instanceof IFile) {
				buildXmlFile = (IFile)elem;

				project = buildXmlFile.getProject();
			}
			else if (elem instanceof IProject) {
				project = (IProject)elem;

				buildXmlFile = project.getFile("build.xml");
			}

			if (buildXmlFile.exists()) {
				final IProject p = project;
				final IFile buildFile = buildXmlFile;

				new Job(p.getName() + " : " + getSDKCommand()) {

					@Override
					protected IStatus run(IProgressMonitor monitor) {
						try {
							SDK sdk = SDKUtil.getSDK(p);
							ILiferayProject liferayProject = LiferayCore.create(ILiferayProject.class, p);

							if (liferayProject != null) {
								sdk.runCommand(p, buildFile, getSDKCommand(), null, monitor);
								p.refreshLocal(IResource.DEPTH_INFINITE, monitor);
							}
						}
						catch (Exception e) {
							return ProjectUI.createErrorStatus("Error running SDK command " + getSDKCommand(), e);
						}

						return Status.OK_STATUS;
					}

				}.schedule();
			}
		}
	}

	protected abstract String getSDKCommand();

}