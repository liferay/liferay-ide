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
import com.liferay.ide.gradle.core.GradleCore;
import com.liferay.ide.server.core.gogo.GogoBundleDeployer;

import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.actions.SelectionProviderAction;

/**
 * @author Terry Jia
 */
public class RemoveWorkspaceModulesAction extends SelectionProviderAction {

	public RemoveWorkspaceModulesAction(ISelectionProvider provider) {
		super(provider, "Remove module(s) from portal");
	}

	@Override
	public void run() {
		Iterator<?> iterator = getStructuredSelection().iterator();

		GogoBundleDeployer deployer = new GogoBundleDeployer();

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
						GradleCore.logError(e);
					}
				}
			}
		}
	}

}