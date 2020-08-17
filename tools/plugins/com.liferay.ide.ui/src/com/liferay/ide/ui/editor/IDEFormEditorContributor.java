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

package com.liferay.ide.ui.editor;

import com.liferay.ide.core.model.IBaseModel;
import com.liferay.ide.core.model.IEditable;
import com.liferay.ide.ui.LiferayUIPlugin;
import com.liferay.ide.ui.form.IDEFormEditor;

import java.util.Hashtable;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.ide.IDEActionFactory;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.eclipse.ui.texteditor.IUpdate;

/**
 * @author Gregory Amerson
 */
public class IDEFormEditorContributor extends MultiPageEditorActionBarContributor {

	public IDEFormEditorContributor(String menuName) {
	}

	public void addClipboardActions(IMenuManager mng) {
		mng.add(_fCutAction);
		mng.add(_fCopyAction);
		mng.add(_fPasteAction);
		mng.add(new Separator());
		mng.add(fRevertAction);
	}

	public void contextMenuAboutToShow(IMenuManager mng) {
		contextMenuAboutToShow(mng, true);
	}

	public void contextMenuAboutToShow(IMenuManager mng, boolean addClipboard) {
		if (fEditor != null) {
			updateSelectableActions(fEditor.getSelection());
		}

		if (addClipboard) {
			addClipboardActions(mng);
		}

		mng.add(_fSaveAction);
	}

	public void contributeToCoolBar(ICoolBarManager cbm) {
	}

	public void contributeToMenu(IMenuManager mm) {
	}

	public void contributeToStatusLine(IStatusLineManager slm) {
	}

	public void contributeToToolBar(IToolBarManager tbm) {
	}

	public IDEFormEditor getEditor() {
		return fEditor;
	}

	public IAction getGlobalAction(String id) {
		return (IAction)_fGlobalActions.get(id);
	}

	public IAction getRevertAction() {
		return fRevertAction;
	}

	public IAction getSaveAction() {
		return _fSaveAction;
	}

	public IEditorActionBarContributor getSourceContributor() {
		return null;
	}

	public IStatusLineManager getStatusLineManager() {
		return getActionBars().getStatusLineManager();
	}

	public void init(IActionBars bars) {
		super.init(bars);

		makeActions();
	}

	public void setActiveEditor(IEditorPart targetEditor) {
		if (!(targetEditor instanceof IDEFormEditor)) {
			return;
		}

		fEditor = (IDEFormEditor)targetEditor;

		fEditor.updateUndo(getGlobalAction(ActionFactory.UNDO.getId()), getGlobalAction(ActionFactory.REDO.getId()));

		setActivePage(fEditor.getActiveEditor());
		updateSelectableActions(fEditor.getSelection());
	}

	public void setActivePage(IEditorPart newEditor) {
		if (fEditor == null) {
			return;
		}

		IFormPage oldPage = fPage;
		fPage = fEditor.getActivePageInstance();

		if (fPage != null) {
			updateActions();

			if ((oldPage != null) && !oldPage.isEditor() && !fPage.isEditor()) {
				getActionBars().updateActionBars();
			}
		}
	}

	public void updateActions() {
		_fSaveAction.update();
		fRevertAction.update();
	}

	public void updateSelectableActions(ISelection selection) {
		if (fEditor != null) {
			_fCutAction.selectionChanged(selection);
			_fCopyAction.selectionChanged(selection);
			_fPasteAction.selectionChanged(selection);
		}
	}

	protected ISharedImages getSharedImages() {
		if (_fSharedImages == null) {
			IWorkbenchPage page = getPage();

			IWorkbenchWindow workbenchWindow = page.getWorkbenchWindow();

			IWorkbench workbench = workbenchWindow.getWorkbench();

			_fSharedImages = workbench.getSharedImages();
		}

		return _fSharedImages;
	}

	protected void makeActions() {

		// clipboard actions

		_fCutAction = new CutAction();
		_fCopyAction = new CopyAction();
		_fPasteAction = new PasteAction();
		_addGlobalAction(ActionFactory.CUT.getId(), _fCutAction);
		_addGlobalAction(ActionFactory.COPY.getId(), _fCopyAction);
		_addGlobalAction(ActionFactory.PASTE.getId(), _fPasteAction);
		_addGlobalAction(ActionFactory.DELETE.getId());

		// undo/redo

		_addGlobalAction(ActionFactory.UNDO.getId());
		_addGlobalAction(ActionFactory.REDO.getId());

		// select/find

		_addGlobalAction(ActionFactory.SELECT_ALL.getId());
		_addGlobalAction(ActionFactory.FIND.getId());

		// bookmark

		_addGlobalAction(IDEActionFactory.BOOKMARK.getId());

		// save/revert

		_fSaveAction = new SaveAction();

		_fSaveAction.setText(Msgs.save);

		fRevertAction = new RevertAction();

		fRevertAction.setText(Msgs.revert);

		_addGlobalAction(ActionFactory.REVERT.getId(), fRevertAction);
	}

	protected IDEFormEditor fEditor;
	protected IFormPage fPage;
	protected RevertAction fRevertAction;

	private void _addGlobalAction(String id) {
		GlobalAction action = new GlobalAction(id);

		_addGlobalAction(id, action);
	}

	private void _addGlobalAction(String id, Action action) {
		_fGlobalActions.put(id, action);
		getActionBars().setGlobalActionHandler(id, action);
	}

	private ClipboardAction _fCopyAction;
	private ClipboardAction _fCutAction;
	private Hashtable _fGlobalActions = new Hashtable();
	private ClipboardAction _fPasteAction;
	private SaveAction _fSaveAction;
	private ISharedImages _fSharedImages;

	private static class Msgs extends NLS {

		public static String copy;
		public static String cut;
		public static String paste;
		public static String revert;
		public static String save;

		static {
			initializeMessages(IDEFormEditorContributor.class.getName(), Msgs.class);
		}

	}

	private class ClipboardAction extends GlobalAction {

		public ClipboardAction(String id) {
			super(id);

			setEnabled(false);
		}

		public boolean isEditable() {
			if (fEditor == null) {
				return false;
			}

			IBaseModel model = fEditor.getModel();

			if (model instanceof IEditable) {
				IEditable editableModel = (IEditable)model;

				return editableModel.isEditable();
			}

			return false;
		}

		public void selectionChanged(ISelection selection) {
		}

	}

	private class CopyAction extends ClipboardAction {

		public CopyAction() {
			super(ActionFactory.COPY.getId());

			setText(Msgs.copy);
			setImageDescriptor(getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
			setDisabledImageDescriptor(getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_COPY_DISABLED));
			setActionDefinitionId(ActionFactory.COPY.getCommandId());
		}

		public void selectionChanged(ISelection selection) {
			setEnabled(fEditor.canCopy(selection));
		}

	}

	private class CutAction extends ClipboardAction {

		public CutAction() {
			super(ActionFactory.CUT.getId());

			setText(Msgs.cut);
			setImageDescriptor(getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_CUT));
			setDisabledImageDescriptor(getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_CUT_DISABLED));
			setActionDefinitionId(ActionFactory.CUT.getCommandId());
		}

		public void selectionChanged(ISelection selection) {
			setEnabled(isEditable() && fEditor.canCut(selection));
		}

	}

	private class GlobalAction extends Action implements IUpdate {

		public GlobalAction(String id) {
			_id = id;
		}

		public void run() {
			fEditor.performGlobalAction(_id);

			updateSelectableActions(fEditor.getSelection());
		}

		public void update() {
			getActionBars().updateActionBars();
		}

		private String _id;

	}

	private class PasteAction extends ClipboardAction {

		public PasteAction() {
			super(ActionFactory.PASTE.getId());

			setText(Msgs.paste);
			setImageDescriptor(getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
			setDisabledImageDescriptor(getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_PASTE_DISABLED));
			setActionDefinitionId(ActionFactory.PASTE.getCommandId());
		}

		public void selectionChanged(ISelection selection) {
			setEnabled(isEditable() && fEditor.canPasteFromClipboard());
		}

	}

	private class RevertAction extends Action implements IUpdate {

		public RevertAction() {
		}

		public void run() {
			if (fEditor != null) {
				fEditor.doRevert();
			}
		}

		public void update() {
			setEnabled((fEditor != null) ? fEditor.isDirty() : false);
		}

	}

	private class SaveAction extends Action implements IUpdate {

		public SaveAction() {
		}

		public void run() {
			if (fEditor != null) {
				IWorkbenchPage workbenchPage = LiferayUIPlugin.getActivePage();

				workbenchPage.saveEditor(fEditor, false);
			}
		}

		public void update() {
			setEnabled((fEditor != null) ? fEditor.isDirty() : false);
		}

	}

}