package com.liferay.ide.eclipse.project.ui.action;

import com.liferay.ide.eclipse.project.ui.ProjectUIPlugin;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.server.ui.ServerUIUtil;


public class NewRuntimeAction extends Action {

	private Shell shell;

	public NewRuntimeAction(Shell shell) {
		super("New Liferay Runtime", ImageDescriptor.createFromURL(ProjectUIPlugin.getDefault().getBundle().getEntry(
			"/icons/n16/server_new.png")));
		this.shell = shell;
	}

	@Override
	public void run() {
		ServerUIUtil.showNewRuntimeWizard(shell, null, null, "com.liferay.");
	}

}
