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

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.ui.IServerModule;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.wizard.TaskWizard;
import org.eclipse.wst.server.ui.internal.wizard.WizardTaskUtil;
import org.eclipse.wst.server.ui.wizard.WizardFragment;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public abstract class AbstractServerRunningAction implements IObjectActionDelegate {

	public AbstractServerRunningAction() {
	}

	public void run(IAction action) {
		if (selectedServer != null) {
			IRuntime runtime = selectedServer.getRuntime();

			IStatus validate = runtime.validate(new NullProgressMonitor());

			if (!validate.isOK()) {
				MessageDialog dialog = new MessageDialog(
					getActiveShell(), "Server runtime configuration invalid", null, validate.getMessage(),
					MessageDialog.ERROR, new String[] {"Edit runtime configuration", "Cancel"}, 0);

				if (dialog.open() == 0) {
					IRuntimeWorkingCopy runtimeWorkingCopy = runtime.createWorkingCopy();

					_showWizard(runtimeWorkingCopy);
				}
			}
			else {
				runAction(action);
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		selectedServer = null;

		if (!selection.isEmpty()) {
			if (selection instanceof IStructuredSelection) {
				Object obj = ((IStructuredSelection)selection).getFirstElement();

				if (obj instanceof IServer) {
					selectedServer = (IServer)obj;

					action.setEnabled((selectedServer.getServerState() & getRequiredServerState()) > 0);
				}
				else if (obj instanceof IServerModule) {
					selectedModule = (IServerModule)obj;

					IServer server = selectedModule.getServer();

					action.setEnabled((server.getServerState() & getRequiredServerState()) > 0);
				}
			}
		}
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		activePart = targetPart;
	}

	protected IWorkbenchPart getActivePart() {
		return activePart;
	}

	protected Shell getActiveShell() {
		if (getActivePart() != null) {
			IWorkbenchPartSite site = getActivePart().getSite();

			return site.getShell();
		}
		else {
			IWorkbench workbench = PlatformUI.getWorkbench();

			IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();

			return activeWorkbenchWindow.getShell();
		}
	}

	protected abstract int getRequiredServerState();

	protected abstract void runAction(IAction action);

	protected IWorkbenchPart activePart;
	protected IServerModule selectedModule;
	protected IServer selectedServer;

	private int _showWizard(IRuntimeWorkingCopy runtimeWorkingCopy) {
		IRuntimeType runtimeType = runtimeWorkingCopy.getRuntimeType();

		WizardFragment childFragment = ServerUIPlugin.getWizardFragment(runtimeType.getId());

		if (childFragment == null) {
			return Window.CANCEL;
		}

		TaskModel taskModel = new TaskModel();

		taskModel.putObject(TaskModel.TASK_RUNTIME, runtimeWorkingCopy);

		WizardFragment fragment = new WizardFragment() {

			protected void createChildFragments(List<WizardFragment> list) {
				list.add(childFragment);
				list.add(WizardTaskUtil.SaveRuntimeFragment);
			}

		};

		TaskWizard wizard = new TaskWizard(_wizardTitle, fragment, taskModel);

		wizard.setForcePreviousAndNextButtons(true);
		WizardDialog dialog = new WizardDialog(getActiveShell(), wizard);

		return dialog.open();
	}

	private static String _wizardTitle = "Edit Server Runtime Environment";

}