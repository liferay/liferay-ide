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

package com.liferay.ide.project.ui.action;

import com.liferay.ide.project.ui.ProjectUI;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.server.ui.ServerUIUtil;

/**
 * @author Gregory Amerson
 */
public class NewServerAction extends Action {

	public NewServerAction(Shell shell) {
		super(
			Msgs.newLiferayServer,
			ImageDescriptor.createFromURL(ProjectUI.getPluginBundle().getEntry("/icons/n16/server_new.png")));

		_shell = shell;
	}

	@Override
	public void run() {
		ServerUIUtil.showNewServerWizard(_shell, null, null, "com.liferay.");
	}

	private Shell _shell;

	private static class Msgs extends NLS {

		public static String newLiferayServer;

		static {
			initializeMessages(NewServerAction.class.getName(), Msgs.class);
		}

	}

}