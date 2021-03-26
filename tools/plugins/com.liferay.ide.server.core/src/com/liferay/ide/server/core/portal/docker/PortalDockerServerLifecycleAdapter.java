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

package com.liferay.ide.server.core.portal.docker;

import com.liferay.ide.core.util.JobUtil;
import com.liferay.ide.core.workspace.LiferayWorkspaceUtil;
import com.liferay.ide.server.core.LiferayServerCore;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.util.ServerLifecycleAdapter;

/**
 * @author Simon Jiang
 * @author Ethan Sun
 */
public class PortalDockerServerLifecycleAdapter extends ServerLifecycleAdapter {

	@Override
	public void serverRemoved(IServer server) {
		PortalDockerServer portalDockerServer = (PortalDockerServer)server.loadAdapter(PortalDockerServer.class, null);

		if (portalDockerServer == null) {
			return;
		}

		IProject project = LiferayWorkspaceUtil.getWorkspaceProject();

		Job removeDockerContainerJob = new Job(project.getName() + " - removeDockerContainer") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				IDockerSupporter dockerSupporter = LiferayServerCore.getDockerSupporter();

				if (dockerSupporter == null) {
					return LiferayServerCore.createErrorStatus("Failed to get docker supporter");
				}

				try {
					dockerSupporter.removeDockerContainer(monitor);

					JobUtil.signalForLiferayJob();
				}
				catch (Exception e) {
					LiferayServerCore.logError("Failed to remove server", e);
				}

				return Status.OK_STATUS;
			}

		};

		removeDockerContainerJob.schedule();
	}

}