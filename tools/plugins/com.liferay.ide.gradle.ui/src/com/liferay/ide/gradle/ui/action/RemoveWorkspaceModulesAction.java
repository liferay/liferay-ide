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

package com.liferay.ide.gradle.ui.action;

import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.gradle.core.LiferayGradleCore;
import com.liferay.ide.server.core.gogo.GogoBundleDeployer;

import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.actions.SelectionProviderAction;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Terry Jia
 */
public class RemoveWorkspaceModulesAction extends SelectionProviderAction {

	public RemoveWorkspaceModulesAction(ISelectionProvider provider) {
		super(provider, "Remove module(s) from portal");
	}

	@Override
	public void run() {
		IStructuredSelection selection = getStructuredSelection();

		if (selection instanceof TreeSelection) {
			TreePath treePath = ((TreeSelection)selection).getPaths()[0];

			IServer server = (IServer)treePath.getFirstSegment();

			if (IServer.STATE_STARTED != server.getServerState()) {
				return;
			}
		}

		GogoBundleDeployer deployer = new GogoBundleDeployer();

		Iterator<?> iterator = selection.iterator();

		while (iterator.hasNext()) {
			Object obj = iterator.next();

			if (obj instanceof IProject) {
				IProject project = (IProject)obj;

				IBundleProject bundleProject = LiferayCore.create(IBundleProject.class, project);

				if (bundleProject != null) {
					try {
						deployer.uninstall(bundleProject);
					}
					catch (Exception e) {
						LiferayGradleCore.logError(e);
					}
				}
			}
		}
	}

}