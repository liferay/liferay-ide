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

package com.liferay.ide.server.ui.action;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.server.core.ILiferayServerBehavior;
import com.liferay.ide.server.ui.LiferayServerUI;
import com.liferay.ide.server.ui.util.ServerUIUtil;

import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Gregory Amerson
 */
public class OpenDeployedFolderAction extends AbstractServerRunningAction {

	public OpenDeployedFolderAction() {
	}

	public void runAction(IAction action) {
		if (selectedModule == null) {

			// can't do anything if server has not been selected

			return;
		}

		IPath folder = _getDeployFolderPath();

		try {
			String launchCmd = ServerUIUtil.getSystemExplorerCommand(folder.toFile());

			ServerUIUtil.openInSystemExplorer(launchCmd, folder.toFile());
		}
		catch (IOException ioe) {
			LiferayServerUI.logError("Unable to execute command", ioe);
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		super.selectionChanged(action, selection);

		if (action.isEnabled()) {
			IPath deployedPath = _getDeployFolderPath();

			try {
				if (FileUtil.notExists(deployedPath) ||
					CoreUtil.isNullOrEmpty(ServerUIUtil.getSystemExplorerCommand(deployedPath.toFile()))) {

					action.setEnabled(false);
				}
			}
			catch (Exception e) {
				action.setEnabled(false);
			}
		}
	}

	@Override
	protected int getRequiredServerState() {
		return IServer.STATE_STARTED | IServer.STATE_STOPPED | IServer.STATE_STARTING | IServer.STATE_STOPPING |
			   IServer.STATE_UNKNOWN;
	}

	private IPath _getDeployFolderPath() {
		IPath retval = null;

		if (selectedModule != null) {
			IModule module = selectedModule.getModule()[0];

			module.getProject();

			IServer server = selectedModule.getServer();

			ILiferayServerBehavior liferayServerBehavior = (ILiferayServerBehavior)server.loadAdapter(
				ILiferayServerBehavior.class, null);

			if (liferayServerBehavior != null) {
				retval = liferayServerBehavior.getDeployedPath(selectedModule.getModule());
			}
		}

		return retval;
	}

}