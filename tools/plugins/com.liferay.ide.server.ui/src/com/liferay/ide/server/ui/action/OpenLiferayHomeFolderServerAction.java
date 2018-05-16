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
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.ui.LiferayServerUI;
import com.liferay.ide.server.ui.util.ServerUIUtil;
import com.liferay.ide.server.util.ServerUtil;

import java.io.IOException;

import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Terry Jia
 */
public class OpenLiferayHomeFolderServerAction extends AbstractServerRunningAction {

	public OpenLiferayHomeFolderServerAction() {
	}

	public void runAction(IAction action) {
		if (selectedServer != null) {
			ILiferayRuntime liferayRuntime = ServerUtil.getLiferayRuntime(selectedServer.getRuntime());

			IPath path = liferayRuntime.getAppServerDir();

			try {
				ServerUIUtil.openFileInSystemExplorer(path);
			}
			catch (IOException ioe) {
				LiferayServerUI.logError("Error opening portal home folder.", ioe);
			}
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		super.selectionChanged(action, selection);

		if ((selectedServer != null) && (selection instanceof IStructuredSelection)) {
			boolean enabled = false;

			IRuntime runtime = selectedServer.getRuntime();

			if (runtime != null) {
				IPath path = runtime.getLocation();

				if (path != null) {
					try {
						IStructuredSelection sel = (IStructuredSelection)selection;

						String launchCmd = ServerUIUtil.getSystemExplorerCommand(path.toFile());

						List<?> selList = sel.toList();

						if (!CoreUtil.isNullOrEmpty(launchCmd) && (selList.size() == 1)) {
							enabled = true;
						}
					}
					catch (IOException ioe) {
					}
				}
			}

			action.setEnabled(enabled);
		}
	}

	@Override
	protected int getRequiredServerState() {
		return IServer.STATE_STARTED | IServer.STATE_STARTING | IServer.STATE_STOPPING | IServer.STATE_STOPPED;
	}

}