/*******************************************************************************
 * Copyright (c) 2003, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Joern Dinkla <devnull@dinkla.com> - bug 197821
 *     Greg Amerson <gregory.amerson@liferay.com>
 *******************************************************************************/
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
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.ide.IDEActionFactory;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.eclipse.ui.texteditor.IUpdate;

/**
 * @author Greg Amerson
 */
public class IDEFormEditorContributor extends MultiPageEditorActionBarContributor {

	protected IDEFormEditor fEditor;

	protected IFormPage fPage;

	private SaveAction fSaveAction;

	protected RevertAction fRevertAction;

	private ClipboardAction fCutAction;

	private ClipboardAction fCopyAction;

	private ClipboardAction fPasteAction;

	private Hashtable fGlobalActions = new Hashtable();

	private ISharedImages fSharedImages;

	class GlobalAction extends Action implements IUpdate {
		private String id;

		public GlobalAction(String id) {
			this.id = id;
		}

		public void run() {
			fEditor.performGlobalAction(id);
			updateSelectableActions(fEditor.getSelection());
		}

		public void update() {
			getActionBars().updateActionBars();
		}
	}

	class ClipboardAction extends GlobalAction {
		public ClipboardAction(String id) {
			super(id);
			setEnabled(false);
		}

		public void selectionChanged(ISelection selection) {
		}

		public boolean isEditable() {
			if (fEditor == null)
				return false;
			IBaseModel model = fEditor.getModel();
			return model instanceof IEditable ? ((IEditable) model).isEditable() : false;
		}
	}

	class CutAction extends ClipboardAction {
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

	class CopyAction extends ClipboardAction {
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

	class PasteAction extends ClipboardAction {
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

	class SaveAction extends Action implements IUpdate {
		public SaveAction() {
		}

		public void run() {
			if (fEditor != null)
				LiferayUIPlugin.getActivePage().saveEditor(fEditor, false);
		}

		public void update() {
			setEnabled(fEditor != null ? fEditor.isDirty() : false);
		}
	}

	class RevertAction extends Action implements IUpdate {
		public RevertAction() {
		}

		public void run() {
			if (fEditor != null)
				fEditor.doRevert();
		}

		public void update() {
			setEnabled(fEditor != null ? fEditor.isDirty() : false);
		}
	}

	public IDEFormEditorContributor(String menuName) {
	}

	private void addGlobalAction(String id) {
		GlobalAction action = new GlobalAction(id);
		addGlobalAction(id, action);
	}

	private void addGlobalAction(String id, Action action) {
		fGlobalActions.put(id, action);
		getActionBars().setGlobalActionHandler(id, action);
	}

	public void addClipboardActions(IMenuManager mng) {
		mng.add(fCutAction);
		mng.add(fCopyAction);
		mng.add(fPasteAction);
		mng.add(new Separator());
		mng.add(fRevertAction);
	}

	public void contextMenuAboutToShow(IMenuManager mng) {
		contextMenuAboutToShow(mng, true);
	}

	public void contextMenuAboutToShow(IMenuManager mng, boolean addClipboard) {
		if (fEditor != null)
			updateSelectableActions(fEditor.getSelection());
		if (addClipboard)
			addClipboardActions(mng);
		mng.add(fSaveAction);
	}

	public void contributeToMenu(IMenuManager mm) {
	}

	public void contributeToStatusLine(IStatusLineManager slm) {
	}

	public void contributeToToolBar(IToolBarManager tbm) {
	}

	public void contributeToCoolBar(ICoolBarManager cbm) {
	}

	public IDEFormEditor getEditor() {
		return fEditor;
	}

	public IAction getGlobalAction(String id) {
		return (IAction) fGlobalActions.get(id);
	}

	public IAction getSaveAction() {
		return fSaveAction;
	}

	public IAction getRevertAction() {
		return fRevertAction;
	}

	public IStatusLineManager getStatusLineManager() {
		return getActionBars().getStatusLineManager();
	}

	protected void makeActions() {
		// clipboard actions
		fCutAction = new CutAction();
		fCopyAction = new CopyAction();
		fPasteAction = new PasteAction();
		addGlobalAction(ActionFactory.CUT.getId(), fCutAction);
		addGlobalAction(ActionFactory.COPY.getId(), fCopyAction);
		addGlobalAction(ActionFactory.PASTE.getId(), fPasteAction);
		addGlobalAction(ActionFactory.DELETE.getId());
		// undo/redo
		addGlobalAction(ActionFactory.UNDO.getId());
		addGlobalAction(ActionFactory.REDO.getId());
		// select/find
		addGlobalAction(ActionFactory.SELECT_ALL.getId());
		addGlobalAction(ActionFactory.FIND.getId());
		// bookmark
		addGlobalAction(IDEActionFactory.BOOKMARK.getId());
		// save/revert
		fSaveAction = new SaveAction();
		fSaveAction.setText(Msgs.save);
		fRevertAction = new RevertAction();
		fRevertAction.setText(Msgs.revert);
		addGlobalAction(ActionFactory.REVERT.getId(), fRevertAction);
	}

	public void setActiveEditor(IEditorPart targetEditor) {
		
		if (!(targetEditor instanceof IDEFormEditor))
			return;

		fEditor = (IDEFormEditor) targetEditor;
		fEditor.updateUndo(getGlobalAction(ActionFactory.UNDO.getId()), getGlobalAction(ActionFactory.REDO.getId()));
		setActivePage(fEditor.getActiveEditor());
		updateSelectableActions(fEditor.getSelection());
	}

	public void setActivePage(IEditorPart newEditor) {
		if (fEditor == null)
			return;
		IFormPage oldPage = fPage;
		fPage = fEditor.getActivePageInstance();
		if (fPage != null) {
			updateActions();
			if (oldPage != null && !oldPage.isEditor() && !fPage.isEditor()) {
				getActionBars().updateActionBars();
			}
		}
	}

	public void updateActions() {
		fSaveAction.update();
		fRevertAction.update();
	}

	public void updateSelectableActions(ISelection selection) {
		if (fEditor != null) {
			fCutAction.selectionChanged(selection);
			fCopyAction.selectionChanged(selection);
			fPasteAction.selectionChanged(selection);
		}
	}

	public IEditorActionBarContributor getSourceContributor() {
		return null;
	}

	public void init(IActionBars bars) {
		super.init(bars);
		makeActions();
	}

	protected ISharedImages getSharedImages() {
		if (fSharedImages == null)
			fSharedImages = getPage().getWorkbenchWindow().getWorkbench().getSharedImages();
		return fSharedImages;
	}

	private static class Msgs extends NLS
	{
	    public static String copy;
	    public static String cut;
	    public static String paste;
	    public static String revert;
	    public static String save;

	    static
	    {
	        initializeMessages( IDEFormEditorContributor.class.getName(), Msgs.class );
	    }
	}
}
