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

package com.liferay.ide.gradle.ui.action;

import com.liferay.ide.ui.util.UIUtil;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.actions.SelectionProviderAction;

/**
 * @author Terry Jia
 */
public class RefreshWorkspaceModulesAction extends SelectionProviderAction {

	public RefreshWorkspaceModulesAction(ISelectionProvider provider) {
		super(provider, "Refresh");
	}

	@Override
	public void run() {
		UIUtil.refreshCommonView("org.eclipse.wst.server.ui.ServersView");
	}

}