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

package com.liferay.ide.server.ui;

import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.ILiferayPortal;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.server.core.gogo.GogoBundleDeployer;
import com.liferay.ide.server.core.portal.PortalBundle;
import com.liferay.ide.server.core.portal.PortalRuntime;
import com.liferay.ide.server.util.ServerUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerCore;

import org.osgi.framework.Bundle;

/**
 * @author Terry Jia
 */
public class LiferayModuleDecorator extends LabelProvider implements ILightweightLabelDecorator {

	@Override
	public void decorate(Object element, IDecoration decoration) {
		if (!(element instanceof IProject)) {
			return;
		}

		IProject project = (IProject)element;

		String suffix = "";

		IBundleProject bundleProject = LiferayCore.create(IBundleProject.class, project);

		if (bundleProject != null) {
			ILiferayPortal liferayPortal = bundleProject.adapt(ILiferayPortal.class);

			if (liferayPortal instanceof PortalBundle) {
				PortalBundle portalBundle = (PortalBundle)liferayPortal;

				IServer[] servers = ServerCore.getServers();

				IServer server = null;

				for (IServer s : servers) {
					IRuntime runtime = s.getRuntime();

					IPath runtimeLocation = runtime.getLocation();

					if (runtimeLocation.equals(portalBundle.getLiferayHome())) {
						server = s;

						break;
					}
				}

				if (server != null) {
					IRuntime runtime = server.getRuntime();

					PortalRuntime portalRuntime = (PortalRuntime)runtime.loadAdapter(
						PortalRuntime.class, new NullProgressMonitor());

					try {
						GogoBundleDeployer deployer = ServerUtil.createBundleDeployer(portalRuntime, server);

						if (bundleProject != null) {
							int bundleState = deployer.getBundleState(bundleProject.getSymbolicName());

							switch (bundleState) {
								case Bundle.ACTIVE:
									suffix = suffix + " [Active]";

									break;
								case Bundle.INSTALLED:
									suffix = suffix + " [Installed]";

									break;
								case Bundle.RESOLVED:
									suffix = suffix + " [Resolved]";

									break;
								case Bundle.STARTING:
									suffix = suffix + " [Starting]";

									break;
								case Bundle.UNINSTALLED:
									suffix = suffix + " [Uninstalled]";

									break;
								case Bundle.STOPPING:
									suffix = suffix + " [Stopping]";

									break;
							}
						}
					}
					catch (Exception e) {
					}
				}
			}
		}

		decoration.addSuffix(suffix);
	}

}