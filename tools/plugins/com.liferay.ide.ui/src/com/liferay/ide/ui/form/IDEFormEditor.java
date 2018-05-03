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

package com.liferay.ide.ui.form;

import com.liferay.ide.core.model.IBaseModel;
import com.liferay.ide.core.model.IWorkspaceModel;
import com.liferay.ide.ui.LiferayUIPlugin;
import com.liferay.ide.ui.editor.IDEFormEditorContributor;
import com.liferay.ide.ui.editor.IInputContextListener;
import com.liferay.ide.ui.editor.IModelUndoManager;
import com.liferay.ide.ui.editor.InputContext;
import com.liferay.ide.ui.editor.InputContextManager;

import java.util.ArrayList;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.part.MultiPageEditorSite;

/**
 * @author Gregory Amerson
 */
public abstract class IDEFormEditor extends FormEditor implements IInputContextListener {

	public IDEFormEditor() {
		fInputContextManager = createInputContextManager();
	}

	public boolean canCopy(ISelection selection) {
		if (selection == null) {
			return false;
		}

		if (selection instanceof IStructuredSelection) {
			return !selection.isEmpty();
		}

		if (selection instanceof ITextSelection) {
			ITextSelection textSelection = (ITextSelection)selection;

			if (textSelection.getLength() > 0) {
				return true;
			}

			return false;
		}

		return false;
	}

	public boolean canCut(ISelection selection) {
		return canCopy(selection);
	}

	public boolean canPasteFromClipboard() {
		return false;
	}

	public void contextAdded(InputContext context) {
		if (_fError) {
			removePage(0);
			addPages();
		}
		else {
			editorContextAdded(context);
		}
	}

	// private ISortableContentOutlinePage fFormOutline;
	// private PDEMultiPageContentOutline fContentOutline;

	public void contributeToToolbar(IToolBarManager manager) {
	}

	public void dispose() {
		_storeDefaultPage();

		if (_fEditorSelectionChangedListener != null) {
			_fEditorSelectionChangedListener.uninstall(getSite().getSelectionProvider());
			_fEditorSelectionChangedListener = null;
		}

		if (_clipboard != null) {
			_clipboard.dispose();
			_clipboard = null;
		}

		super.dispose();

		fInputContextManager.dispose();
		fInputContextManager = null;
	}

	public void doRevert() {
		IFormPage formPage = getActivePageInstance();

		// If the active page is a form page, commit all of its dirty field
		// values to the model

		if ((formPage != null) && (formPage instanceof IDEFormPage)) {
			formPage.getManagedForm().commit(true);
		}

		// If the editor has source pages, revert them
		// Reverting the source page fires events to the associated form pages
		// which will cause all their values to be updated

		boolean reverted = _revertSourcePages();

		// If the editor does not have any source pages, revert the form pages
		// by directly reloading the underlying model.
		// Reloading the model fires a world changed event to all form pages
		// causing them to update their values

		if (reverted == false) {
			reverted = _revertFormPage();
		}

		// If the revert operation was performed disable the revert action and
		// fire the dirty event

		if (reverted) {
			editorDirtyStateChanged();
		}
	}

	public void doRevert(IEditorInput input) {
		editorDirtyStateChanged();
	}

	/**
	 * (non-Javadoc)
	 * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor) {
		commitPages(true);
		fInputContextManager.save(monitor);
		editorDirtyStateChanged();
	}

	/**
	 * (non-Javadoc)
	 * @see org.eclipse.ui.ISaveablePart#doSaveAs()
	 */
	public void doSaveAs() {
		try {

			// Get the context for which the save as operation should be
			// performed

			String contextID = getContextIDForSaveAs();

			// Perform the same as operation

			InputContextManager contextManager = getContextManager();

			contextManager.saveAs(getProgressMonitor(), contextID);

			// Get the new editor input

			IEditorInput input = contextManager.findContext(contextID).getInput();

			// Store the new editor input

			setInputWithNotify(input);

			// Update the title of the editor using the name of the new editor
			// input

			setPartName(input.getName());

			// Fire a property change accordingly

			firePropertyChange(PROP_DIRTY);
		}
		catch (InterruptedException ie) {

			// Ignore

		}
		catch (Exception e) {
			String title = "Save As Problem";

			String message = "Save not completed.";

			if (e.getMessage() != null) {
				message = message + ' ' + e.getMessage();
			}

			LiferayUIPlugin.logError(e);
		}
	}

	public abstract void editorContextAdded(InputContext context);

	public void editorDirtyStateChanged() {
		super.editorDirtyStateChanged();

		IDEFormEditorContributor contributor = getContributor();

		if (contributor != null) {
			contributor.updateActions();
		}
	}

	public void fireSaveNeeded(IEditorInput input, boolean notify) {
		if (notify) {
			editorDirtyStateChanged();
		}

		if (isDirty()) {
			_validateEdit(input);
		}
	}

	public void fireSaveNeeded(String contextId, boolean notify) {
		if (contextId == null) {
			return;
		}

		InputContext context = fInputContextManager.findContext(contextId);

		if (context != null) {
			fireSaveNeeded(context.getInput(), notify);
		}
	}

	public Object getAdapter(Class key) {
		return super.getAdapter(key);
	}

	public Clipboard getClipboard() {
		return _clipboard;
	}

	public IProject getCommonProject() {
		return fInputContextManager.getCommonProject();
	}

	/**
	 * @return
	 */
	public String getContextIDForSaveAs() {

		// Sub-classes must override this method and the isSaveAsAllowed
		// method to perform save as operations

		return null;
	}

	public InputContextManager getContextManager() {
		return fInputContextManager;
	}

	public Menu getContextMenu() {
		return _fContextMenu;
	}

	public IDEFormEditorContributor getContributor() {
		return (IDEFormEditorContributor)getEditorSite().getActionBarContributor();
	}

	public boolean getLastDirtyState() {
		return _fLastDirtyState;
	}

	public IBaseModel getModel() {
		if (fInputContextManager != null) {
			return fInputContextManager.getModel();
		}

		return null;
	}

	public IFormPage[] getPages() {
		ArrayList formPages = new ArrayList();

		for (Object page : pages) {
			if (page instanceof IFormPage) {
				formPages.add(page);
			}
		}

		return (IFormPage[])formPages.toArray(new IFormPage[formPages.size()]);
	}

	public ISelection getSelection() {
		IWorkbenchPartSite site = getSite();

		return site.getSelectionProvider().getSelection();
	}

	public String getTitle() {
		if (fInputContextManager == null) {
			return super.getTitle();
		}

		InputContext context = fInputContextManager.getPrimaryContext();

		if (context == null) {
			return super.getTitle();
		}

		return context.getInput().getName();
	}

	public String getTitleProperty() {
		return "";
	}

	/**
	 * @return
	 */
	public void gotoMarker(IMarker marker) {
		IResource resource = marker.getResource();

		InputContext context = fInputContextManager.findContext(resource);

		if (context == null) {
			return;
		}

		IFormPage page = getActivePageInstance();

		if (!context.getId().equals(page.getId())) {
			page = setActivePage(context.getId());
		}

		IDE.gotoMarker(page, marker);
	}

	/**
	 * Tests whether this editor has a context with a provided id. The test can
	 * be used to check whether to add certain pages.
	 *
	 * @param contextId
	 * @return <code>true</code> if provided context is present,
	 *         <code>false</code> otherwise.
	 */
	public boolean hasInputContext(String contextId) {
		return fInputContextManager.hasContext(contextId);
	}

	public boolean isDirty() {
		_fLastDirtyState = _computeDirtyState();

		return _fLastDirtyState;
	}

	/**
	 * (non-Javadoc)
	 * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed() {
		return false;
	}

	public void openToSourcePage(Object object, int offset, int length) {
		InputContext context = null;

		if (object instanceof InputContext) {
			context = (InputContext)object;
		}
		else {
			context = getInputContext(object);
		}
	}

	public void performGlobalAction(String id) {

		// preserve selection

		ISelection selection = getSelection();
		boolean handled = ((IDEFormPage)getActivePageInstance()).performGlobalAction(id);

		if (!handled) {
			IFormPage page = getActivePageInstance();

			if (page instanceof IDEFormPage) {
				if (id.equals(ActionFactory.UNDO.getId())) {
					fInputContextManager.undo();
					return;
				}

				if (id.equals(ActionFactory.REDO.getId())) {
					fInputContextManager.redo();
					return;
				}

				if (id.equals(ActionFactory.CUT.getId()) || id.equals(ActionFactory.COPY.getId())) {
					_copyToClipboard();
					return;
				}
			}
		}
	}

	/**
	 * (non-Javadoc)
	 * @see MultiPageEditorPart#setFocus()
	 */
	public void setFocus() {
		super.setFocus();

		IFormPage page = getActivePageInstance();

		// Could be done on setActive in PDEFormPage;
		// but setActive only handles page switches and not focus events

		if ((page != null) && (page instanceof IDEFormPage)) {
			((IDEFormPage)page).updateFormSelection();
		}
	}

	public void setSelection(ISelection selection) {
		IWorkbenchPartSite site = getSite();

		site.getSelectionProvider().setSelection(selection);

		getContributor().updateSelectableActions(selection);
	}

	public void updateTitle() {
		firePropertyChange(IWorkbenchPart.PROP_TITLE);
	}

	public void updateUndo(IAction undoAction, IAction redoAction) {
		IModelUndoManager undoManager = fInputContextManager.getUndoManager();

		if (undoManager != null) {
			undoManager.setActions(undoAction, redoAction);
		}
	}

	public class IDEFormEditorChangeListener implements ISelectionChangedListener {

		/**
		 * Installs this selection changed listener with the given selection
		 * provider. If the selection provider is a post selection provider,
		 * post selection changed events are the preferred choice, otherwise
		 * normal selection changed events are requested.
		 *
		 * @param selectionProvider
		 */
		public void install(ISelectionProvider selectionProvider) {
			if (selectionProvider == null) {
				return;
			}

			if (selectionProvider instanceof IPostSelectionProvider) {
				IPostSelectionProvider provider = (IPostSelectionProvider)selectionProvider;

				provider.addPostSelectionChangedListener(this);
			}
			else {
				selectionProvider.addSelectionChangedListener(this);
			}
		}

		/*
		 * @see
		 * ISelectionChangedListener#selectionChanged(
		 * SelectionChangedEvent)
		 */
		public void selectionChanged(SelectionChangedEvent event) {
		}

		/**
		 * Removes this selection changed listener from the given selection
		 * provider.
		 *
		 * @param selectionProviderstyle
		 */
		public void uninstall(ISelectionProvider selectionProvider) {
			if (selectionProvider == null) {
				return;
			}

			if (selectionProvider instanceof IPostSelectionProvider) {
				IPostSelectionProvider provider = (IPostSelectionProvider)selectionProvider;

				provider.removePostSelectionChangedListener(this);
			}
			else {
				selectionProvider.removeSelectionChangedListener(this);
			}
		}

	}

	protected abstract void addPages();

	protected void contextMenuAboutToShow(IMenuManager manager) {
		IDEFormEditorContributor contributor = getContributor();
		IFormPage page = getActivePageInstance();

		if (page instanceof IDEFormPage) {
			((IDEFormPage)page).contextMenuAboutToShow(manager);
		}

		if (contributor != null) {
			contributor.contextMenuAboutToShow(manager);
		}
	}

	protected abstract InputContextManager createInputContextManager();

	protected void createInputContexts(InputContextManager contextManager) {
		IEditorInput input = getEditorInput();

		if (input instanceof IFileEditorInput) {

			// resource - find the project

			createResourceContexts(contextManager, (IFileEditorInput)input);
		}
	}

	/**
	 * When subclassed, don't forget to call 'super'
	 */
	protected void createPages() {
		_clipboard = new Clipboard(getContainer().getDisplay());
		MenuManager manager = new MenuManager();

		IMenuListener listener = new IMenuListener() {

			public void menuAboutToShow(IMenuManager manager) {
				contextMenuAboutToShow(manager);
			}

		};
		manager.setRemoveAllWhenShown(true);
		manager.addMenuListener(listener);
		_fContextMenu = manager.createContextMenu(getContainer());

		getContainer().setMenu(_fContextMenu);

		createInputContexts(fInputContextManager);
		super.createPages();

		fInputContextManager.addInputContextListener(this);
		updateTitle();

		if ((getPageCount() == 1) && getContainer() instanceof CTabFolder) {
			((CTabFolder)getContainer()).setTabHeight(0);
		}
	}

	protected abstract void createResourceContexts(InputContextManager contexts, IFileEditorInput input);

	/**
	 * We must override nested site creation so that we properly pass the source
	 * editor contributor when asked.
	 */
	protected IEditorSite createSite(IEditorPart editor) {
		return new IDEMultiPageEditorSite(this, editor);
	}

	protected abstract String getEditorID();

	protected abstract InputContext getInputContext(Object object);

	/**
	 * @return
	 */
	protected IProgressMonitor getProgressMonitor() {
		IProgressMonitor monitor = null;
		IStatusLineManager manager = getStatusLineManager();

		if (manager != null) {
			monitor = manager.getProgressMonitor();
		}

		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}

		return monitor;
	}

	/**
	 * @return
	 */
	protected IStatusLineManager getStatusLineManager() {
		IEditorSite editorSite = getEditorSite();

		return editorSite.getActionBars().getStatusLineManager();
	}

	/**
	 * (non-Javadoc)
	 * @see FormEditor#pageChange(int)
	 */
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);

		IFormPage page = getActivePageInstance();

		// updateContentOutline(page);

		if (page != null) {
			_fLastActivePageId = page.getId();
		}
	}

	protected InputContextManager fInputContextManager;

	private boolean _computeDirtyState() {
		IFormPage page = getActivePageInstance();

		if (((page != null) && page.isDirty()) || ((fInputContextManager != null) && fInputContextManager.isDirty())) {
			return true;
		}

		return super.isDirty();
	}

	/**
	 * @param selection
	 */
	private void _copyToClipboard() {
	}

	private String _getFirstInvalidContextId() {
		InputContext[] invalidContexts = fInputContextManager.getInvalidContexts();

		if (invalidContexts.length == 0) {
			return null;
		}

		// If primary context is among the invalid ones, return that.

		for (int i = 0; i < invalidContexts.length; i++) {
			if (invalidContexts[i].isPrimary()) {
				return invalidContexts[i].getId();
			}
		}

		// Return the first one

		return invalidContexts[0].getId();
	}

	/**
	 * @return
	 */
	private boolean _revertFormPage() {
		boolean reverted = false;
		IBaseModel model = getModel();

		if (model instanceof IWorkspaceModel) {
			IWorkspaceModel workspaceModel = (IWorkspaceModel)model;

			workspaceModel.reload();

			reverted = true;
		}

		return reverted;
	}

	/**
	 * @return
	 */
	private boolean _revertSourcePages() {
		boolean reverted = false;

		return reverted;
	}

	private void _storeDefaultPage() {
		IEditorInput input = getEditorInput();
		String pageId = _fLastActivePageId;

		if (pageId == null) {
			return;
		}

		if (input instanceof IFileEditorInput) {
		}
		else if (input instanceof IStorageEditorInput) {
		}
	}

	private void _validateEdit(IEditorInput input) {
		InputContext context = fInputContextManager.getContext(input);

		if (!context.validateEdit()) {
			IWorkbenchPartSite site = getSite();

			Shell shell = site.getShell();

			shell.getDisplay().asyncExec(
				new Runnable() {

					public void run() {
						doRevert(context.getInput());
						context.setValidated(false);
					}

				});
		}
	}

	private Clipboard _clipboard;
	private Menu _fContextMenu;
	private IDEFormEditorChangeListener _fEditorSelectionChangedListener;
	private boolean _fError;
	private String _fLastActivePageId;
	private boolean _fLastDirtyState;

	private static class IDEMultiPageEditorSite extends MultiPageEditorSite {

		public IDEMultiPageEditorSite(MultiPageEditorPart multiPageEditor, IEditorPart editor) {
			super(multiPageEditor, editor);
		}

		public IEditorActionBarContributor getActionBarContributor() {
			IDEFormEditor editor = (IDEFormEditor)getMultiPageEditor();

			IDEFormEditorContributor contributor = editor.getContributor();

			return contributor.getSourceContributor();
		}

		public IWorkbenchPart getPart() {
			return getMultiPageEditor();
		}

	}

}