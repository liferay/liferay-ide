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
import com.liferay.ide.core.model.IModelChangeProvider;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.ui.LiferayUIPlugin;
import com.liferay.ide.ui.form.IDEFormEditor;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;

/**
 * @author Gregory Amerson
 */
public abstract class InputContextManager implements IResourceChangeListener {

	public InputContextManager(IDEFormEditor editor) {
		this.editor = editor;
		_inputContexts = new Hashtable<>();
		_listeners = new ArrayList<>();

		IWorkspace workspace = LiferayUIPlugin.getWorkspace();

		workspace.addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);
	}

	public void addInputContextListener(IInputContextListener listener) {
		if (!_listeners.contains(listener)) {
			_listeners.add(listener);
		}
	}

	public void dispose() {
		IWorkspace workspace = LiferayUIPlugin.getWorkspace();

		workspace.removeResourceChangeListener(this);

		// dispose input contexts

		for (Enumeration<InputContext> contexts = _inputContexts.elements(); contexts.hasMoreElements();) {
			InputContext context = (InputContext)contexts.nextElement();

			_unhookUndo(context);
			context.dispose();
		}

		_inputContexts.clear();
		_undoManager = null;
	}

	public InputContext findContext(IResource resource) {
		for (Enumeration<InputContext> contexts = _inputContexts.elements(); contexts.hasMoreElements();) {
			InputContext context = (InputContext)contexts.nextElement();

			if (context.matches(resource)) {
				return context;
			}
		}

		return null;
	}

	public InputContext findContext(String id) {
		for (Enumeration<InputContext> contexts = _inputContexts.elements(); contexts.hasMoreElements();) {
			InputContext context = (InputContext)contexts.nextElement();

			if (StringUtil.equals(context.getId(), id)) {
				return context;
			}
		}

		return null;
	}

	public IProject getCommonProject() {
		for (Enumeration<InputContext> contexts = _inputContexts.elements(); contexts.hasMoreElements();) {
			InputContext context = (InputContext)contexts.nextElement();

			IEditorInput input = context.getInput();

			if (input instanceof IFileEditorInput) {
				IFileEditorInput fileInput = (IFileEditorInput)input;

				IFile file = fileInput.getFile();

				return file.getProject();
			}
		}

		return null;
	}

	public InputContext getContext(IEditorInput input) {
		return (InputContext)_inputContexts.get(input);
	}

	public InputContext[] getInvalidContexts() {
		ArrayList<InputContext> result = new ArrayList<>();

		for (Enumeration<InputContext> contexts = _inputContexts.elements(); contexts.hasMoreElements();) {
			InputContext context = (InputContext)contexts.nextElement();

			if (context.isModelCorrect() == false) {
				result.add(context);
			}
		}

		return (InputContext[])result.toArray(new InputContext[0]);
	}

	public abstract IBaseModel getModel();

	public InputContext getPrimaryContext() {
		for (Enumeration<InputContext> contexts = _inputContexts.elements(); contexts.hasMoreElements();) {
			InputContext context = (InputContext)contexts.nextElement();

			if (context.isPrimary()) {
				return context;
			}
		}

		return null;
	}

	/**
	 * @return Returns the undoManager.
	 */
	public IModelUndoManager getUndoManager() {
		return _undoManager;
	}

	public boolean hasContext(String id) {
		if (findContext(id) != null) {
			return true;
		}

		return false;
	}

	public boolean isDirty() {
		for (Enumeration<InputContext> contexts = _inputContexts.elements(); contexts.hasMoreElements();) {
			InputContext context = (InputContext)contexts.nextElement();

			if (context.mustSave()) {
				return true;
			}
		}

		return false;
	}

	public void monitorFile(IFile file) {
		if (_monitoredFiles == null) {
			_monitoredFiles = new ArrayList<>();
		}

		_monitoredFiles.add(file);
	}

	public void putContext(IEditorInput input, InputContext context) {
		_inputContexts.put(input, context);

		fireContextChange(context, true);
	}

	public void redo() {
		if ((_undoManager != null) && _undoManager.isRedoable()) {
			_undoManager.redo();
		}
	}

	public void removeInputContextListener(IInputContextListener listener) {
		_listeners.remove(listener);
	}

	/**
	 * (non-Javadoc)
	 * @see IResourceChangeListener#resourceChanged(IResourceChangeEvent)
	 */
	public void resourceChanged(IResourceChangeEvent event) {
		IResourceDelta delta = event.getDelta();

		try {
			delta.accept(
				new IResourceDeltaVisitor() {

					public boolean visit(IResourceDelta delta) {
						int kind = delta.getKind();
						IResource resource = delta.getResource();

						if (resource instanceof IFile) {
							if (kind == IResourceDelta.ADDED) {
								_asyncStructureChanged((IFile)resource, true);
							}
							else if (kind == IResourceDelta.REMOVED) {
								_asyncStructureChanged((IFile)resource, false);
							}

							return false;
						}

						return true;
					}

				});
		}
		catch (CoreException ce) {
			LiferayUIPlugin.logError(ce);
		}
	}

	/**
	 * Saves dirty contexts.
	 *
	 * @param monitor
	 */
	public void save(IProgressMonitor monitor) {
		for (Enumeration<InputContext> contexts = _inputContexts.elements(); contexts.hasMoreElements();) {
			InputContext context = (InputContext)contexts.nextElement();

			if (context.mustSave()) {
				context.doSave(monitor);
			}
		}
	}

	/**
	 * @param monitor
	 * @param contextID
	 * @throws Exception
	 */
	public void saveAs(IProgressMonitor monitor, String contextID) throws Exception {

		// Find the existing context

		InputContext inputContext = findContext(contextID);

		if (inputContext != null) {

			// Keep the old editor input

			IEditorInput oldInput = editor.getEditorInput();

			// Perform the save as operation

			inputContext.doSaveAs(monitor);

			// Get the new editor input

			IEditorInput newInput = inputContext.getInput();

			// Update the context manager accordingly

			_updateInputContext(newInput, oldInput);
		}
		else {
			throw new Exception("Input context not found");
		}
	}

	/**
	 * @param undoManager
	 *            The undoManager to set.
	 */
	public void setUndoManager(IModelUndoManager undoManager) {
		_undoManager = undoManager;
	}

	public void undo() {
		if ((_undoManager != null) && _undoManager.isUndoable()) {
			_undoManager.undo();
		}
	}

	protected void fireContextChange(InputContext context, boolean added) {
		for (IInputContextListener listener : _listeners) {
			if (added) {
				listener.contextAdded(context);
			}
			else {
				listener.contextRemoved(context);
			}
		}

		if (added) {
			_hookUndo(context);
		}
		else {
			_unhookUndo(context);
		}
	}

	protected void fireStructureChange(IFile file, boolean added) {
		for (IInputContextListener listener : _listeners) {
			if (added) {
				listener.monitoredFileAdded(file);
			}
			else {
				listener.monitoredFileRemoved(file);
			}
		}
	}

	protected IDEFormEditor editor;

	private void _asyncStructureChanged(IFile file, boolean added) {
		if ((editor == null) || (editor.getEditorSite() == null)) {
			return;
		}

		IEditorSite editorSite = editor.getEditorSite();

		Shell shell = editorSite.getShell();

		Display display = (shell != null) ? shell.getDisplay() : Display.getDefault();

		display.asyncExec(
			new Runnable() {

				public void run() {
					_structureChanged(file, added);
				}

			});
	}

	private void _hookUndo(InputContext context) {
		if (_undoManager == null) {
			return;
		}

		IBaseModel model = context.getModel();

		if (model instanceof IModelChangeProvider) {
			_undoManager.connect((IModelChangeProvider)model);
		}
	}

	private void _removeContext(IFile file) {
		for (Enumeration<InputContext> contexts = _inputContexts.elements(); contexts.hasMoreElements();) {
			InputContext context = (InputContext)contexts.nextElement();

			IEditorInput input = context.getInput();

			if (input instanceof IFileEditorInput) {
				IFileEditorInput fileInput = (IFileEditorInput)input;

				if (file.equals(fileInput.getFile())) {
					_inputContexts.remove(input);
					fireContextChange(context, false);

					return;
				}
			}
		}
	}

	private void _structureChanged(IFile file, boolean added) {
		if (_monitoredFiles == null) {
			return;
		}

		for (Object monitoredFile : _monitoredFiles) {
			IFile iFile = (IFile)monitoredFile;

			if (iFile.equals(file)) {
				if (added) {
					fireStructureChange(file, true);
				}
				else {
					fireStructureChange(file, false);
					_removeContext(file);
				}
			}
		}
	}

	private void _unhookUndo(InputContext context) {
		if (_undoManager == null) {
			return;
		}

		IBaseModel model = context.getModel();

		if (model instanceof IModelChangeProvider) {
			_undoManager.disconnect((IModelChangeProvider)model);
		}
	}

	/**
	 * Update the key (the editor input in this case) associated with the input
	 * context without firing a context change event. Used for save as
	 * operations.
	 *
	 * @param newInput
	 * @param oldInput
	 * @throws Exception
	 */
	private void _updateInputContext(IEditorInput newInput, IEditorInput oldInput) throws Exception {
		InputContext value = null;

		// Retrieve the input context referenced by the old editor input and
		// remove it from the context manager

		if (_inputContexts.containsKey(oldInput)) {
			value = _inputContexts.remove(oldInput);
		}
		else {
			throw new Exception("Input context not found");
		}

		// Re-insert the input context back into the context manager using the
		// new editor input as its key

		_inputContexts.put(newInput, value);
	}

	private Hashtable<IEditorInput, InputContext> _inputContexts;
	private ArrayList<IInputContextListener> _listeners;
	private ArrayList<IFile> _monitoredFiles;
	private IModelUndoManager _undoManager;

}