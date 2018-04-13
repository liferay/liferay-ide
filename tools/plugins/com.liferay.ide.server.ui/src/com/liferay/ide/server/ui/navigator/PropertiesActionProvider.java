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

package com.liferay.ide.server.ui.navigator;

import com.liferay.ide.server.ui.LiferayServerUI;
import com.liferay.ide.server.ui.action.EditPropertiesFileAction;
import com.liferay.ide.server.ui.action.OpenLiferayHomeFolderAction;

import java.util.Iterator;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonViewerSite;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

/**
 * @author Gregory Amerson
 */
public class PropertiesActionProvider extends CommonActionProvider {

	public static final String NEW_MENU_ID = "org.eclipse.wst.server.ui.internal.cnf.newMenuId";

	public static final String TOP_SECTION_END_SEPARATOR = "org.eclipse.wst.server.ui.internal.cnf.topSectionEnd";

	public static final String TOP_SECTION_START_SEPARATOR = "org.eclipse.wst.server.ui.internal.cnf.topSectionStart";

	public PropertiesActionProvider() {
	}

	public void fillContextMenu(IMenuManager menu) {

		// This is a temp workaround to clean up the default group that are provided by CNF

		menu.removeAll();

		ICommonViewerSite site = _actionSite.getViewSite();
		IStructuredSelection selection = null;

		if (site instanceof ICommonViewerWorkbenchSite) {
			ICommonViewerWorkbenchSite wsSite = (ICommonViewerWorkbenchSite)site;

			ISelectionProvider selectionProvider = wsSite.getSelectionProvider();

			selection = (IStructuredSelection)selectionProvider.getSelection();
		}

		PropertiesFile file = null;

		if ((selection != null) && !selection.isEmpty()) {
			Iterator<?> iterator = selection.iterator();

			Object obj = iterator.next();

			if (obj instanceof PropertiesFile) {
				file = (PropertiesFile)obj;
			}

			if (iterator.hasNext()) {
				file = null;
			}
		}

		menu.add(_invisibleSeparator(TOP_SECTION_START_SEPARATOR));

		addTopSection(menu, file);

		menu.add(_invisibleSeparator(TOP_SECTION_END_SEPARATOR));
		menu.add(new Separator());
		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS + "-end"));
	}

	public void init(ICommonActionExtensionSite site) {
		super.init(site);

		_actionSite = site;

		ICommonViewerSite viewerSite = site.getViewSite();

		if (viewerSite instanceof ICommonViewerWorkbenchSite) {
			StructuredViewer v = site.getStructuredViewer();

			if (v instanceof CommonViewer) {
				CommonViewer cv = (CommonViewer)v;
				ICommonViewerWorkbenchSite wsSite = (ICommonViewerWorkbenchSite)viewerSite;

				_addListeners(cv);

				_makeActions(wsSite.getSelectionProvider());
			}
		}
	}

	protected void addTopSection(IMenuManager menu, PropertiesFile file) {
		if (file != null) {
			menu.add(_editAction);
			menu.add(_openFolderAction);
		}
	}

	private void _addListeners(CommonViewer tableViewer) {
		tableViewer.addOpenListener(
			new IOpenListener() {

				public void open(OpenEvent event) {
					try {
						IStructuredSelection sel = (IStructuredSelection)event.getSelection();

						Object data = sel.getFirstElement();

						if (!(data instanceof PropertiesFile)) {
							return;
						}

						PropertiesActionProvider.this._editAction.run();
					}
					catch (Exception e) {
						LiferayServerUI.logError("Error opening kaleo workflow.", e);
					}
				}

			});
	}

	private Separator _invisibleSeparator(String s) {
		Separator sep = new Separator(s);

		sep.setVisible(false);

		return sep;
	}

	private void _makeActions(ISelectionProvider provider) {
		_editAction = new EditPropertiesFileAction(provider);
		_openFolderAction = new OpenLiferayHomeFolderAction(provider);
	}

	private ICommonActionExtensionSite _actionSite;
	private EditPropertiesFileAction _editAction;
	private OpenLiferayHomeFolderAction _openFolderAction;

}