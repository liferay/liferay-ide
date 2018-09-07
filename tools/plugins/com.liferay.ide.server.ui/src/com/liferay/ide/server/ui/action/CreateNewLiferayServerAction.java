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

import com.liferay.ide.server.ui.LiferayServerUI;
import com.liferay.ide.server.util.ServerUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerCore;

/**
 * @author Gregory Amerson
 */
public class CreateNewLiferayServerAction implements IObjectActionDelegate {

	@Override
	public void run(IAction action) {
		IWorkbench workbench = PlatformUI.getWorkbench();

		IWorkbenchWindow getActiveWorkbenchWindow = workbench.getActiveWorkbenchWindow();

		ISelectionService selectionService = getActiveWorkbenchWindow.getSelectionService();

		ISelection selection = selectionService.getSelection();

		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sSelection = (IStructuredSelection)selection;

			Object element = sSelection.getFirstElement();

			if (element instanceof IFolder) {
				IFolder folder = (IFolder)element;

				String[] serverName = {folder.getName()};

				IPath location = folder.getRawLocation();

				List<IRuntime> runtimes = Arrays.asList(ServerCore.getRuntimes());

				Stream<IRuntime> runtimeStream = runtimes.stream();

				while (runtimeStream.anyMatch(r -> serverName[0].equals(r.getName()))) {
					serverName[0] = serverName[0] + "Extra";
				}

				List<IServer> servers = Arrays.asList(ServerCore.getServers());

				Stream<IServer> serverStream = servers.stream();

				while (serverStream.anyMatch(s -> serverName[0].equals(s.getName()))) {
					serverName[0] = serverName[0] + "Extra";
				}

				try {
					ServerUtil.addPortalRuntimeAndServer(serverName[0], location, new NullProgressMonitor());
				}
				catch (CoreException ce) {
					LiferayServerUI.logError("Unable to add server at specified location", ce);
				}
			}
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

}