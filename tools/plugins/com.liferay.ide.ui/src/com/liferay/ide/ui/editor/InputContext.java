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
import com.liferay.ide.core.model.IEditingModel;
import com.liferay.ide.core.model.IModelChangeProvider;
import com.liferay.ide.core.model.IModelChangedEvent;
import com.liferay.ide.core.model.IModelChangedListener;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.ui.LiferayUIPlugin;
import com.liferay.ide.ui.form.IDEFormEditor;
import com.liferay.ide.ui.util.UIUtil;

import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.MoveSourceEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.editors.text.ForwardingDocumentProvider;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.progress.IProgressService;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.IElementStateListener;

/**
 * This class maintains objects associated with a single editor input.
 * @author Gregory Amerson
 */
public abstract class InputContext {

	public static int getInsertOffset(IDocument doc) {
		int offset = doc.getLength();

		for (int i = doc.getNumberOfLines() - 1; i >= 0; i--) {
			try {
				String string = doc.get(doc.getLineOffset(i), doc.getLineLength(i));

				string = string.trim();

				if (string.length() > 0) {
					break;
				}

				offset = doc.getLineOffset(i);
			}
			catch (BadLocationException ble) {
			}
		}

		return offset;
	}

	public static boolean isNewlineNeeded(IDocument doc) throws BadLocationException {
		int line = doc.getLineOfOffset(getInsertOffset(doc));

		String string = doc.get(doc.getLineOffset(line), doc.getLineLength(line));

		string = string.trim();

		if (string.length() > 0) {
			return true;
		}

		return false;
	}

	public InputContext(IDEFormEditor editor, IEditorInput input, boolean primary) {
		_fEditor = editor;
		_fEditorInput = input;

		setPrimary(primary);
	}

	public void dispose() {
		IAnnotationModel amodel = _fDocumentProvider.getAnnotationModel(_fEditorInput);

		if (amodel != null) {
			amodel.disconnect(_fDocumentProvider.getDocument(_fEditorInput));
		}

		_fDocumentProvider.removeElementStateListener(_fElementListener);
		_fDocumentProvider.disconnect(_fEditorInput);

		if ((_fModelListener != null) && (_fModel instanceof IModelChangeProvider)) {
			IModelChangeProvider modelProvider = (IModelChangeProvider)_fModel;

			modelProvider.removeModelChangedListener(_fModelListener);
		}

		if (_fModel != null) {
			_fModel.dispose();
		}
	}

	public void doRevert() {
		_fMustSynchronize = true;
		_synchronizeModelIfNeeded();
	}

	public void doSave(IProgressMonitor monitor) {
		try {
			IDocument doc = _fDocumentProvider.getDocument(_fEditorInput);
			_fDocumentProvider.aboutToChange(_fEditorInput);
			flushModel(doc);
			_fDocumentProvider.saveDocument(monitor, _fEditorInput, doc, true);
			_fDocumentProvider.changed(_fEditorInput);
			_fValidated = false;
		}
		catch (CoreException ce) {
			LiferayUIPlugin.logError(ce);
		}
	}

	/**
	 * @param monitor
	 */
	public void doSaveAs(IProgressMonitor monitor) throws Exception {

		// Get the editor shell

		IWorkbenchPartSite workbenchPartSite = getEditor().getSite();

		Shell shell = workbenchPartSite.getShell();

		// Create the save as dialog

		SaveAsDialog dialog = new SaveAsDialog(shell);

		// Set the initial file name to the original file name

		IFile file = null;

		if (_fEditorInput instanceof IFileEditorInput) {
			IFileEditorInput fileInput = (IFileEditorInput)_fEditorInput;

			file = fileInput.getFile();

			dialog.setOriginalFile(file);
		}

		// Create the dialog

		dialog.create();

		// Warn the user if the underlying file does not exist

		if (_fDocumentProvider.isDeleted(_fEditorInput) && (file != null)) {
			String message = NLS.bind("File does not exist: {0}", file.getName());
			dialog.setErrorMessage(null);
			dialog.setMessage(message, IMessageProvider.WARNING);
		}

		// Open the dialog

		if (dialog.open() == Window.OK) {

			// Get the path to where the new file will be stored

			IPath path = dialog.getResult();

			_handleSaveAs(monitor, path);
		}
	}

	public void flushEditorInput() {
		if (fEditOperations.size() > 0) {
			IDocument doc = _fDocumentProvider.getDocument(_fEditorInput);
			_fDocumentProvider.aboutToChange(_fEditorInput);
			flushModel(doc);
			_fDocumentProvider.changed(_fEditorInput);
			_fValidated = false;
		}
		else if (_fModel instanceof IEditable) {

			// When text edit operations are made that cancel each other out,
			// the editor is not undirtied
			// e.g. Extensions page: Move an element up and then move it down
			// back in the same position: Bug # 197831

			IEditable editableModel = (IEditable)_fModel;

			if (editableModel.isDirty()) {
				editableModel.setDirty(false);
			}
		}
	}

	public IDocumentProvider getDocumentProvider() {
		return _fDocumentProvider;
	}

	public IDEFormEditor getEditor() {
		return _fEditor;
	}

	public abstract String getId();

	public IEditorInput getInput() {
		return _fEditorInput;
	}

	public String getLineDelimiter() {
		if (_fDocumentProvider != null) {
			IDocument document = _fDocumentProvider.getDocument(_fEditorInput);

			if (document != null) {
				return TextUtilities.getDefaultLineDelimiter(document);
			}
		}

		return System.getProperty("line.separator");
	}

	public IBaseModel getModel() {
		return _fModel;
	}

	public boolean isInSourceMode() {
		return _fIsSourceMode;
	}

	public boolean isModelCorrect() {
		_synchronizeModelIfNeeded();

		if (_fModel != null) {
			return _fModel.isValid();
		}

		return false;
	}

	/**
	 * @return Returns the primary.
	 */
	public boolean isPrimary() {
		return _fPrimary;
	}

	/**
	 * @return Returns the validated.
	 */
	public boolean isValidated() {
		return _fValidated;
	}

	public boolean matches(IResource resource) {
		if (_fEditorInput instanceof IFileEditorInput) {
			IFileEditorInput finput = (IFileEditorInput)_fEditorInput;

			IFile file = finput.getFile();

			if (file.equals(resource)) {
				return true;
			}
		}

		return false;
	}

	public boolean mustSave() {
		if (!_fIsSourceMode && (_fModel instanceof IEditable)) {
			IEditable editableModel = (IEditable)_fModel;

			if (editableModel.isDirty()) {
				return true;
			}
		}

		if ((fEditOperations.size() > 0) || _fDocumentProvider.canSaveDocument(_fEditorInput)) {
			return true;
		}

		return false;
	}

	/**
	 * @param primary
	 *            The primary to set.
	 */
	public void setPrimary(boolean primary) {
		_fPrimary = primary;
	}

	public boolean setSourceEditingMode(boolean sourceMode) {
		_fIsSourceMode = sourceMode;

		if (sourceMode) {

			// entered source editing mode; in this mode,
			// this context's document will be edited directly
			// in the source editor. All changes in the model
			// are caused by reconciliation and should not be
			// fired to the world.

			flushModel(_fDocumentProvider.getDocument(_fEditorInput));
			_fMustSynchronize = true;

			return true;
		}

		// leaving source editing mode; if the document
		// has been modified while in this mode,
		// fire the 'world changed' event from the model
		// to cause all the model listeners to become stale.

		return _synchronizeModelIfNeeded();
	}

	/**
	 * @param validated
	 *            The validated to set.
	 */
	public void setValidated(boolean validated) {
		_fValidated = validated;
	}

	public synchronized boolean validateEdit() {
		if (!_fValidated && (_fEditorInput instanceof IFileEditorInput)) {
			IFileEditorInput fileInpit = (IFileEditorInput)_fEditorInput;

			IFile file = fileInpit.getFile();

			if (file.isReadOnly()) {
				IEditorSite editorSite = _fEditor.getEditorSite();

				Shell shell = editorSite.getShell();

				IWorkspace workspace = LiferayUIPlugin.getWorkspace();

				IStatus validateStatus = workspace.validateEdit(new IFile[] {file}, shell);

				// to prevent loops

				_fValidated = true;

				if (validateStatus.getSeverity() != IStatus.OK) {
					ErrorDialog.openError(shell, _fEditor.getTitle(), null, validateStatus);
				}

				if (validateStatus.getSeverity() == IStatus.OK) {
					return true;
				}

				return false;
			}
		}

		return true;
	}

	public class ElementListener implements IElementStateListener {

		public void elementContentAboutToBeReplaced(Object element) {
		}

		public void elementContentReplaced(Object element) {
			if ((element != null) && element.equals(_fEditorInput)) {
				doRevert();
			}
		}

		public void elementDeleted(Object element) {
			if ((element != null) && element.equals(_fEditorInput)) {
				dispose();
			}
		}

		public void elementDirtyStateChanged(Object element, boolean dirty) {
			if ((element != null) && element.equals(_fEditorInput)) {
				_fMustSynchronize = true;
			}
		}

		public void elementMoved(Object originalElement, Object movedElement) {
			if ((originalElement != null) && originalElement.equals(_fEditorInput)) {
				dispose();
				_fEditor.close(true);
			}
		}

	}

	protected static boolean covers(TextEdit thisEdit, TextEdit otherEdit) {
		if (thisEdit.getLength() == 0) {
			return false;
		}

		int thisOffset = thisEdit.getOffset();
		int thisEnd = thisEdit.getExclusiveEnd();

		if (otherEdit.getLength() == 0) {
			int otherOffset = otherEdit.getOffset();

			if ((thisOffset < otherOffset) && (otherOffset < thisEnd)) {
				return true;
			}

			return false;
		}

		int otherOffset = otherEdit.getOffset();
		int otherEnd = otherEdit.getExclusiveEnd();

		if ((thisOffset <= otherOffset) && (otherEnd <= thisEnd)) {
			return true;
		}

		return false;
	}

	protected static void insert(TextEdit parent, TextEdit edit) {
		if (!parent.hasChildren()) {
			parent.addChild(edit);

			if (edit instanceof MoveSourceEdit) {
				MoveSourceEdit sourceEdit = (MoveSourceEdit)edit;

				parent.addChild(sourceEdit.getTargetEdit());
			}

			return;
		}

		TextEdit[] children = parent.getChildren();

		// First dive down to find the right parent.

		for (TextEdit child : children) {
			if (covers(child, edit)) {
				insert(child, edit);

				return;
			}
		}

		// We have the right parent. Now check if some of the children have to
		// be moved under the new edit since it is covering it.

		for (int i = children.length - 1; i >= 0; i--) {
			TextEdit child = children[i];

			if (covers(edit, child)) {
				parent.removeChild(i);
				edit.addChild(child);
			}
		}

		parent.addChild(edit);

		if (edit instanceof MoveSourceEdit) {
			MoveSourceEdit sourceEdit = (MoveSourceEdit)edit;

			parent.addChild(sourceEdit.getTargetEdit());
		}
	}

	protected abstract void addTextEditOperation(ArrayList ops, IModelChangedEvent event);

	protected void create() {
		_fDocumentProvider = createDocumentProvider(_fEditorInput);

		try {
			_fDocumentProvider.connect(_fEditorInput);
			_fModel = createModel(_fEditorInput);

			if (_fModel instanceof IModelChangeProvider) {
				_fModelListener = new IModelChangedListener() {

					public void modelChanged(IModelChangedEvent e) {
						if (e.getChangeType() != IModelChangedEvent.WORLD_CHANGED) {
							if (!_fEditor.getLastDirtyState()) {
								_fEditor.fireSaveNeeded(_fEditorInput, true);
							}

							IModelChangeProvider provider = e.getChangeProvider();

							if (provider instanceof IEditingModel) {

								// this is to guard against false notifications
								// when a revert operation is performed, focus
								// is taken away from a FormEntry
								// and a text edit operation is falsely
								// requested

								IEditingModel modelProvider = (IEditingModel)provider;

								if (modelProvider.isDirty()) {
									addTextEditOperation(fEditOperations, e);
								}
							}
						}
					}

				};

				IModelChangeProvider providerModel = (IModelChangeProvider)_fModel;

				providerModel.addModelChangedListener(_fModelListener);
			}

			IAnnotationModel amodel = _fDocumentProvider.getAnnotationModel(_fEditorInput);

			if (amodel != null) {
				amodel.connect(_fDocumentProvider.getDocument(_fEditorInput));
			}

			_fElementListener = new ElementListener();

			_fDocumentProvider.addElementStateListener(_fElementListener);
		}
		catch (CoreException ce) {
			LiferayUIPlugin.logError(ce);
		}
	}

	protected IDocumentProvider createDocumentProvider(IEditorInput input) {
		if (input instanceof IFileEditorInput) {
			LiferayUIPlugin plugin = LiferayUIPlugin.getDefault();

			return new ForwardingDocumentProvider(
				getPartitionName(), getDocumentSetupParticipant(), plugin.getTextFileDocumentProvider());
		}

		return null;
	}

	protected abstract IBaseModel createModel(IEditorInput input) throws CoreException;

	protected void flushModel(IDocument doc) {
		boolean flushed = true;

		if (fEditOperations.size() > 0) {
			try {
				MultiTextEdit edit = new MultiTextEdit();

				if (isNewlineNeeded(doc)) {
					insert(edit, new InsertEdit(doc.getLength(), TextUtilities.getDefaultLineDelimiter(doc)));
				}

				for (TextEdit textEdit : fEditOperations) {
					insert(edit, textEdit);
				}

				if (_fModel instanceof IEditingModel) {
					IEditingModel editingModel = (IEditingModel)_fModel;

					editingModel.setStale(true);
				}

				edit.apply(doc);
				fEditOperations.clear();
			}
			catch (MalformedTreeException mte) {
				LiferayUIPlugin.logError(mte);
				flushed = false;
			}
			catch (BadLocationException ble) {
				LiferayUIPlugin.logError(ble);
				flushed = false;
			}
		}

		// If no errors were encountered flushing the model, then undirty the
		// model. This needs to be done regardless of whether there are any
		// edit operations or not; since, the contributed actions need to be
		// updated and the editor needs to be undirtied

		if (flushed && (_fModel instanceof IEditable)) {
			IEditable editableModel = (IEditable)_fModel;

			editableModel.setDirty(false);
		}
	}

	protected abstract String getDefaultCharset();

	protected IDocumentSetupParticipant getDocumentSetupParticipant() {
		return new IDocumentSetupParticipant() {

			public void setup(IDocument document) {
			}

		};
	}

	protected abstract String getPartitionName();

	protected boolean synchronizeModel(IDocument doc) {
		return true;
	}

	protected ArrayList<TextEdit> fEditOperations = new ArrayList<>();

	/**
	 * @param newInput
	 * @return
	 */
	private WorkspaceModifyOperation _createWorkspaceModifyOperation(IEditorInput newInput) {
		return new WorkspaceModifyOperation() {

			public void execute(IProgressMonitor monitor) throws CoreException {

				// Save the old editor input content to the new editor input
				// location

				_fDocumentProvider.saveDocument(
					monitor,

					// New editor input location

					newInput,

					// Old editor input content

					_fDocumentProvider.getDocument(_fEditorInput), true);
			}

		};
	}

	private void _deinitializeDocumentProvider() {
		IAnnotationModel amodel = _fDocumentProvider.getAnnotationModel(_fEditorInput);

		if (amodel != null) {
			amodel.disconnect(_fDocumentProvider.getDocument(_fEditorInput));
		}

		_fDocumentProvider.removeElementStateListener(_fElementListener);
		_fDocumentProvider.disconnect(_fEditorInput);
	}

	/**
	 * @param monitor
	 * @param path
	 * @throws Exception
	 * @throws CoreException
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 */
	private void _handleSaveAs(IProgressMonitor monitor, IPath path)
		throws CoreException, Exception, InterruptedException, InvocationTargetException {

		// Ensure a new location was selected

		if (path == null) {
			monitor.setCanceled(true);

			throw new Exception("Location not set");
		}

		// Resolve the new file location

		IFile newFile = CoreUtil.getIFileFromWorkspaceRoot(path);

		// Create the new editor input

		IEditorInput newInput = new FileEditorInput(newFile);

		// Send notice of editor input changes

		_fDocumentProvider.aboutToChange(newInput);

		// Flush any unsaved changes

		flushModel(_fDocumentProvider.getDocument(_fEditorInput));

		try {

			// Execute the workspace modification in a separate thread

			IProgressService progressService = UIUtil.getProgressService();

			progressService.busyCursorWhile(_createWorkspaceModifyOperation(newInput));

			monitor.setCanceled(false);

			// Store the new editor input in this context

			_updateInput(newInput);
		}
		catch (InterruptedException ie) {
			monitor.setCanceled(true);

			throw ie;
		}
		catch (InvocationTargetException ite) {
			monitor.setCanceled(true);

			throw ite;
		}
		finally {
			_fDocumentProvider.changed(newInput);
		}
	}

	/**
	 * @throws CoreException
	 */
	private void _initializeDocumentProvider() throws CoreException {
		_fDocumentProvider.connect(_fEditorInput);

		IAnnotationModel amodel = _fDocumentProvider.getAnnotationModel(_fEditorInput);

		if (amodel != null) {
			amodel.connect(_fDocumentProvider.getDocument(_fEditorInput));
		}

		_fDocumentProvider.addElementStateListener(_fElementListener);
	}

	private boolean _synchronizeModelIfNeeded() {
		if (_fMustSynchronize) {
			boolean result = synchronizeModel(_fDocumentProvider.getDocument(_fEditorInput));
			_fMustSynchronize = false;

			return result;
		}

		return true;
	}

	/**
	 * @param input
	 * @throws CoreException
	 */
	private void _updateInput(IEditorInput newInput) throws CoreException {
		_deinitializeDocumentProvider();
		_fEditorInput = newInput;
		_initializeDocumentProvider();
	}

	private IDocumentProvider _fDocumentProvider;
	private IDEFormEditor _fEditor;
	private IEditorInput _fEditorInput;
	private IElementStateListener _fElementListener;
	private boolean _fIsSourceMode;
	private IBaseModel _fModel;
	private IModelChangedListener _fModelListener;
	private boolean _fMustSynchronize;
	private boolean _fPrimary;
	private boolean _fValidated;

}