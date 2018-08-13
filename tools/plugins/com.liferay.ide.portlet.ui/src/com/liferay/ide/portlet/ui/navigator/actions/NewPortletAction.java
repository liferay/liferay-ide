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

package com.liferay.ide.portlet.ui.navigator.actions;

import com.liferay.ide.portlet.ui.navigator.PortletResourcesRootNode;
import com.liferay.ide.portlet.ui.navigator.PortletsNode;
import com.liferay.ide.portlet.ui.wizard.NewPortletWizard;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Kamesh Sampath
 */
public class NewPortletAction extends BaseSelectionListenerAction {

	/**
	 * @param text
	 */
	public NewPortletAction() {
		super(_ACTION_MESSAGE);
	}

	/**
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		if (isEnabled()) {
			if (selectedNode instanceof PortletsNode) {
				PortletsNode portletsNode = (PortletsNode)selectedNode;

				PortletResourcesRootNode parent = portletsNode.getParent();

				IProject currentProject = parent.getProject();

				NewPortletWizard newPortletWizard = new NewPortletWizard(currentProject);

				Display display = Display.getDefault();

				WizardDialog wizardDialog = new WizardDialog(display.getActiveShell(), newPortletWizard);

				wizardDialog.create();
				wizardDialog.open();
			}
		}
	}

	/**
	 * (non-Javadoc)
	 *
	 * @see BaseSelectionListenerAction#updateSelection(org.
	 * eclipse.jface.viewers.IStructuredSelection )
	 */
	@Override
	protected boolean updateSelection(IStructuredSelection selection) {
		if (selection.size() == 1) {
			selectedNode = selection.getFirstElement();

			return true;
		}

		return false;
	}

	protected Object selectedNode;

	private static final String _ACTION_MESSAGE = Msgs.newPortlet;

	private static class Msgs extends NLS {

		public static String newPortlet;

		static {
			initializeMessages(NewPortletAction.class.getName(), Msgs.class);
		}

	}

}