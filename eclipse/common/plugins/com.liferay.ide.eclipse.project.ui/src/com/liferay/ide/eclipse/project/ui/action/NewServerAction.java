package com.liferay.ide.eclipse.project.ui.action;

import com.liferay.ide.eclipse.project.ui.ProjectUIPlugin;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.server.ui.ServerUIUtil;


public class NewServerAction extends Action {

	private Shell shell;

	public NewServerAction(Shell shell) {
		super("New Liferay Server", ImageDescriptor.createFromURL(ProjectUIPlugin.getDefault().getBundle().getEntry(
			"/icons/n16/server_new.png")));
		this.shell = shell;
	}

	@Override
	public void run() {
		ServerUIUtil.showNewServerWizard(shell, null, null, "com.liferay.");
	}

}
