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

import com.liferay.ide.ui.LiferayUIPlugin;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Gregory Amerson
 */
public abstract class OpenPortalURLAction extends AbstractServerRunningAction {

	public void runAction(IAction action) {
		if (selectedServer != null) {
			IWorkbench workbench = PlatformUI.getWorkbench();

			ICommandService actionService = (ICommandService)workbench.getService(ICommandService.class);

			Command actionCmd = actionService.getCommand(getCommandId());

			try {
				actionCmd.executeWithChecks(new ExecutionEvent());
			}
			catch (Exception e) {
				LiferayUIPlugin.logError("Error running command " + getCommandId(), e);
			}
		}
	}

	protected abstract String getCommandId();

	protected int getRequiredServerState() {
		return IServer.STATE_STARTED;
	}

}