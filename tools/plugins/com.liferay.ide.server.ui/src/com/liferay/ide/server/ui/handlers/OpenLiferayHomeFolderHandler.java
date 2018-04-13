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

package com.liferay.ide.server.ui.handlers;

import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.ui.LiferayServerUI;
import com.liferay.ide.server.ui.util.ServerUIUtil;
import com.liferay.ide.server.util.ServerUtil;

import java.io.IOException;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Terry Jia
 */
@SuppressWarnings("rawtypes")
public class OpenLiferayHomeFolderHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (selection instanceof IStructuredSelection) {
			List selectedObj = ((IStructuredSelection)selection).toList();

			if ((selectedObj.size() == 1) && (selectedObj.get(0) instanceof IServer)) {
				IServer server = (IServer)selectedObj.get(0);

				ILiferayRuntime liferayRuntime = ServerUtil.getLiferayRuntime(server.getRuntime());

				IPath path = liferayRuntime.getAppServerDir();

				try {
					ServerUIUtil.openFileInSystemExplorer(path);
				}
				catch (IOException ioe) {
					LiferayServerUI.logError("Error opening portal home folder.", ioe);
				}
			}
		}

		return null;
	}

}