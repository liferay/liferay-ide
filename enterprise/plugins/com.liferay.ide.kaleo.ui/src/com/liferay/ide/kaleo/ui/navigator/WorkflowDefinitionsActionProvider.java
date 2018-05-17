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

package com.liferay.ide.kaleo.ui.navigator;

import com.liferay.ide.kaleo.ui.KaleoUI;
import com.liferay.ide.kaleo.ui.action.EditWorkflowDefinitionAction;
import com.liferay.ide.kaleo.ui.action.PublishWorkflowDefinitionAction;
import com.liferay.ide.kaleo.ui.action.RefreshWorkflowDefinitionsAction;
import com.liferay.ide.kaleo.ui.action.UploadNewWorkflowDefinitionAction;

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
public class WorkflowDefinitionsActionProvider extends CommonActionProvider {

	public static final String NEW_MENU_ID = "org.eclipse.wst.server.ui.internal.cnf.newMenuId";

	public static final String TOP_SECTION_END_SEPARATOR = "org.eclipse.wst.server.ui.internal.cnf.topSectionEnd";

	public static final String TOP_SECTION_START_SEPARATOR = "org.eclipse.wst.server.ui.internal.cnf.topSectionStart";

	public WorkflowDefinitionsActionProvider() {
	}

	public void fillContextMenu(IMenuManager menu) {
		/*
		 * This is a temp workaround to clean up the default group that are
		 * provided by CNF
		 */
		menu.removeAll();

		ICommonViewerSite site = _actionSite.getViewSite();
		IStructuredSelection selection = null;

		if (site instanceof ICommonViewerWorkbenchSite) {
			ICommonViewerWorkbenchSite wsSite = (ICommonViewerWorkbenchSite)site;

			selection = (IStructuredSelection)wsSite.getSelectionProvider().getSelection();
		}

		WorkflowDefinitionEntry definition = null;

		WorkflowDefinitionsFolder definitionsFolder = null;

		if ((selection != null) && !selection.isEmpty()) {
			Iterator<?> iterator = selection.iterator();

			Object obj = iterator.next();

			if (obj instanceof WorkflowDefinitionEntry) {
				definition = (WorkflowDefinitionEntry)obj;
			}

			if (obj instanceof WorkflowDefinitionsFolder) {
				definitionsFolder = (WorkflowDefinitionsFolder)obj;
			}

			if (iterator.hasNext()) {
				definition = null;
				definitionsFolder = null;
			}
		}

		menu.add(_invisibleSeparator(TOP_SECTION_START_SEPARATOR));

		addTopSection(menu, definition, definitionsFolder);

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

	protected void addTopSection(
		IMenuManager menu, WorkflowDefinitionEntry definition, WorkflowDefinitionsFolder definitionsFolder) {

		// open action

		if (definition != null) {
			menu.add(_editAction);
			menu.add(_publishAction);
		}

		if (definitionsFolder != null) {
			menu.add(_refreshAction);
			menu.add(_uploadAction);
		}
	}

	private void _addListeners(CommonViewer tableViewer) {
		tableViewer.addOpenListener(
			new IOpenListener() {

				public void open(OpenEvent event) {
					try {
						IStructuredSelection sel = (IStructuredSelection)event.getSelection();

						Object data = sel.getFirstElement();

						if (!(data instanceof WorkflowDefinitionEntry)) {
							return;
						}

						WorkflowDefinitionsActionProvider.this._editAction.run();
					}
					catch (Exception e) {
						KaleoUI.logError("Error opening kaleo workflow.", e);
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

		// Shell shell = tableViewer.getTree().getShell();

		// create the open action

		_editAction = new EditWorkflowDefinitionAction(provider);
		_refreshAction = new RefreshWorkflowDefinitionsAction(provider);
		_publishAction = new PublishWorkflowDefinitionAction(provider);
		_uploadAction = new UploadNewWorkflowDefinitionAction(provider);

		/*
		 * create copy, paste, and delete actions pasteAction = new
		 * PasteAction(shell, provider, clipboard); copyAction = new
		 * CopyAction(provider, clipboard, pasteAction); globalDeleteAction =
		 * new GlobalDeleteAction(shell, provider); renameAction = new
		 * RenameAction(shell, tableViewer, provider);
		 */
	}

	private ICommonActionExtensionSite _actionSite;
	private EditWorkflowDefinitionAction _editAction;
	private PublishWorkflowDefinitionAction _publishAction;
	private RefreshWorkflowDefinitionsAction _refreshAction;
	private UploadNewWorkflowDefinitionAction _uploadAction;

}