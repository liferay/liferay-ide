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

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;

/**
 * @author Kamesh Sampath
 */
public class NewPortletActionProvider extends CommonActionProvider {

	/**
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.ui.actions.ActionGroup#fillActionBars(IActionBars)
	 */
	@Override
	public void fillActionBars(IActionBars actionBars) {
		if (this._newPortletAction.isEnabled()) {
			actionBars.setGlobalActionHandler(IWorkbenchActionConstants.NEW_GROUP, _newPortletAction);
		}
	}

	/**
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.ui.actions.ActionGroup#fillContextMenu(IMenuManager)
	 */
	@Override
	public void fillContextMenu(IMenuManager menuManager) {
		ActionContext context = getContext();

		if ((context == null) || context.getSelection().isEmpty()) {
			return;
		}

		if (this._newPortletAction.isEnabled()) {
			menuManager.insertAfter(ICommonMenuConstants.GROUP_NEW, _newPortletAction);
		}
	}

	/**
	 * (non-Javadoc)
	 *
	 * @see
	 * CommonActionProvider#init(ICommonActionExtensionSite)
	 */
	@Override
	public void init(ICommonActionExtensionSite aSite) {
		_newPortletAction = new NewPortletAction();
	}

	/**
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ui.actions.ActionGroup#setContext(ActionContext)
	 */
	@Override
	public void setContext(ActionContext context) {
		if ((context != null) && (context.getSelection() instanceof IStructuredSelection)) {
			IStructuredSelection selection = (IStructuredSelection)context.getSelection();

			this._newPortletAction.selectionChanged(selection);
		}

		super.setContext(context);
	}

	private NewPortletAction _newPortletAction;

}