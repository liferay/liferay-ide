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

package com.liferay.ide.gradle.ui.navigator.workspace;

import com.liferay.ide.gradle.ui.action.RefreshWorkspaceModulesAction;
import com.liferay.ide.gradle.ui.action.RemoveWorkspaceModulesAction;
import com.liferay.ide.gradle.ui.action.StopWorkspaceModulesAction;
import com.liferay.ide.gradle.ui.action.WatchWorkspaceModulesAction;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonViewerSite;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

/**
 * @author Terry Jia
 */
public class LiferayWorkspaceServerActionProvider extends CommonActionProvider {

	public static final String NEW_MENU_ID = "org.eclipse.wst.server.ui.internal.cnf.newMenuId";

	public static final String TOP_SECTION_END_SEPARATOR = "org.eclipse.wst.server.ui.internal.cnf.topSectionEnd";

	public static final String TOP_SECTION_START_SEPARATOR = "org.eclipse.wst.server.ui.internal.cnf.topSectionStart";

	public LiferayWorkspaceServerActionProvider() {
	}

	@Override
	public void fillContextMenu(IMenuManager menu) {
		menu.removeAll();

		menu.add(_invisibleSeparator(TOP_SECTION_START_SEPARATOR));
		menu.add(_watchAction);
		menu.add(_stopAction);
		menu.add(_refreshAction);
		menu.add(_removeAction);
		menu.add(_invisibleSeparator(TOP_SECTION_END_SEPARATOR));
		menu.add(new Separator());
		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS + "-end"));
	}

	@Override
	public void init(ICommonActionExtensionSite site) {
		super.init(site);

		ICommonViewerSite viewerSite = site.getViewSite();

		if (viewerSite instanceof ICommonViewerWorkbenchSite) {
			StructuredViewer structuredViewer = site.getStructuredViewer();

			if (structuredViewer instanceof CommonViewer) {
				ICommonViewerWorkbenchSite wsSite = (ICommonViewerWorkbenchSite)viewerSite;

				_makeActions(wsSite.getSelectionProvider());
			}
		}
	}

	private Separator _invisibleSeparator(String s) {
		Separator separator = new Separator(s);

		separator.setVisible(false);

		return separator;
	}

	private void _makeActions(ISelectionProvider provider) {
		_watchAction = new WatchWorkspaceModulesAction(provider);
		_stopAction = new StopWorkspaceModulesAction(provider);
		_refreshAction = new RefreshWorkspaceModulesAction(provider);
		_removeAction = new RemoveWorkspaceModulesAction(provider);
	}

	private RefreshWorkspaceModulesAction _refreshAction;
	private RemoveWorkspaceModulesAction _removeAction;
	private StopWorkspaceModulesAction _stopAction;
	private WatchWorkspaceModulesAction _watchAction;

}