/*******************************************************************************
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
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
 *
 *******************************************************************************/

package com.liferay.ide.eclipse.portlet.ui.action;

import com.liferay.ide.eclipse.portlet.core.PortletCore;
import com.liferay.ide.eclipse.portlet.core.job.BuildServiceJob;
import com.liferay.ide.eclipse.portlet.core.util.PortletUtil;
import com.liferay.ide.eclipse.server.core.IPortalConstants;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Greg Amerson
 */
public class BuildServicesAction implements IObjectActionDelegate {

	private ISelection fSelection;

	public BuildServicesAction() {
	}

	public Display getDisplay() {
		Display display = Display.getCurrent();

		if (display == null)
			display = Display.getDefault();

		return display;
	}

	public void run(IAction action) {
		if (fSelection instanceof IStructuredSelection) {
			Object[] elems = ((IStructuredSelection) fSelection).toArray();

			IFile servicesFile = null;

			Object elem = elems[0];

			if (elem instanceof IFile) {
				servicesFile = (IFile) elem;

			}
			else if (elem instanceof IProject) {
				IProject project = (IProject) elem;

				IFolder docroot = PortletUtil.getDocroot(project);

				if (docroot != null && docroot.exists()) {
					servicesFile = docroot.getFile("WEB-INF/" + IPortalConstants.LIFERAY_SERVICE_BUILDER_XML_FILE);
				}
			}

			if (servicesFile != null && servicesFile.exists()) {
				BuildServiceJob job = PortletCore.createBuildServiceJob(servicesFile);

				job.schedule();
			}
		}

	}

	public void selectionChanged(IAction action, ISelection selection) {
		fSelection = selection;
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}
}
