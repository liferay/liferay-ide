/*******************************************************************************
 *  Copyright (c) 2003, 2008 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.liferay.ide.ui.editor;

import com.liferay.ide.core.model.IBaseModel;
import com.liferay.ide.core.model.IModelChangeProvider;
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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;

public abstract class InputContextManager implements IResourceChangeListener {
	protected IDEFormEditor editor;
	private Hashtable inputContexts;
	private ArrayList monitoredFiles;
	private ArrayList listeners;
	private IModelUndoManager undoManager;

	/**
	 *  
	 */
	public InputContextManager(IDEFormEditor editor) {
		this.editor = editor;
		inputContexts = new Hashtable();
		listeners = new ArrayList();
		LiferayUIPlugin.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);
	}

	public void addInputContextListener(IInputContextListener listener) {
		if (!listeners.contains(listener))
			listeners.add(listener);
	}

	public void removeInputContextListener(IInputContextListener listener) {
		listeners.remove(listener);
	}

	/**
	 * 
	 *
	 */
	public void dispose() {
		LiferayUIPlugin.getWorkspace().removeResourceChangeListener(this);
		// dispose input contexts
		for (Enumeration contexts = inputContexts.elements(); contexts.hasMoreElements();) {
			InputContext context = (InputContext) contexts.nextElement();
			unhookUndo(context);
			context.dispose();
		}
		inputContexts.clear();
		undoManager = null;
	}

	/**
	 * Saves dirty contexts.
	 * @param monitor
	 */
	public void save(IProgressMonitor monitor) {
		for (Enumeration contexts = inputContexts.elements(); contexts.hasMoreElements();) {
			InputContext context = (InputContext) contexts.nextElement();
			if (context.mustSave())
				context.doSave(monitor);
		}
	}

	public IProject getCommonProject() {
		for (Enumeration contexts = inputContexts.elements(); contexts.hasMoreElements();) {
			InputContext context = (InputContext) contexts.nextElement();
			IEditorInput input = context.getInput();
			if (input instanceof IFileEditorInput)
				return ((IFileEditorInput) input).getFile().getProject();
		}
		return null;
	}

	public boolean hasContext(String id) {
		return findContext(id) != null;
	}

	public InputContext findContext(String id) {
		for (Enumeration contexts = inputContexts.elements(); contexts.hasMoreElements();) {
			InputContext context = (InputContext) contexts.nextElement();
			if (context.getId().equals(id))
				return context;
		}
		return null;
	}

	public InputContext findContext(IResource resource) {
		for (Enumeration contexts = inputContexts.elements(); contexts.hasMoreElements();) {
			InputContext context = (InputContext) contexts.nextElement();
			if (context.matches(resource))
				return context;
		}
		return null;
	}

	public abstract IBaseModel getModel();

	public InputContext getContext(IEditorInput input) {
		return (InputContext) inputContexts.get(input);
	}

	public void putContext(IEditorInput input, InputContext context) {
		inputContexts.put(input, context);
		fireContextChange(context, true);
	}

	/**
	 * Update the key (the editor input in this case) associated with the
	 * input context without firing a context change event.
	 * Used for save as operations.
	 * @param newInput
	 * @param oldInput
	 * @throws Exception
	 */
	private void updateInputContext(IEditorInput newInput, IEditorInput oldInput) throws Exception {
		Object value = null;
		// Retrieve the input context referenced by the old editor input and
		// remove it from the context manager
		if (inputContexts.containsKey(oldInput)) {
			value = inputContexts.remove(oldInput);
		} else {
			throw new Exception("Input context not found."); //$NON-NLS-1$
		}
		// Re-insert the input context back into the context manager using the
		// new editor input as its key
		inputContexts.put(newInput, value);
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
			updateInputContext(newInput, oldInput);
		} else {
			throw new Exception("Input context not found."); //$NON-NLS-1$
		}
	}

	public InputContext getPrimaryContext() {
		for (Enumeration contexts = inputContexts.elements(); contexts.hasMoreElements();) {
			InputContext context = (InputContext) contexts.nextElement();
			if (context.isPrimary())
				return context;
		}
		return null;
	}

	public InputContext[] getInvalidContexts() {
		ArrayList result = new ArrayList();
		for (Enumeration contexts = inputContexts.elements(); contexts.hasMoreElements();) {
			InputContext context = (InputContext) contexts.nextElement();
			if (context.isModelCorrect() == false)
				result.add(context);
		}
		return (InputContext[]) result.toArray(new InputContext[result.size()]);
	}

	public boolean isDirty() {
		for (Enumeration contexts = inputContexts.elements(); contexts.hasMoreElements();) {
			InputContext context = (InputContext) contexts.nextElement();
			if (context.mustSave())
				return true;
		}
		return false;
	}

	public void monitorFile(IFile file) {
		if (monitoredFiles == null)
			monitoredFiles = new ArrayList();
		monitoredFiles.add(file);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
	 */
	public void resourceChanged(IResourceChangeEvent event) {
		IResourceDelta delta = event.getDelta();

		try {
			delta.accept(new IResourceDeltaVisitor() {
				public boolean visit(IResourceDelta delta) {
					int kind = delta.getKind();
					IResource resource = delta.getResource();
					if (resource instanceof IFile) {
						if (kind == IResourceDelta.ADDED)
							asyncStructureChanged((IFile) resource, true);
						else if (kind == IResourceDelta.REMOVED)
							asyncStructureChanged((IFile) resource, false);
						return false;
					}
					return true;
				}
			});
		} catch (CoreException e) {
			LiferayUIPlugin.logError(e);
		}
	}

	private void asyncStructureChanged(final IFile file, final boolean added) {
		if (editor == null || editor.getEditorSite() == null)
			return;
		Shell shell = editor.getEditorSite().getShell();
		Display display = shell != null ? shell.getDisplay() : Display.getDefault();

		display.asyncExec(new Runnable() {
			public void run() {
				structureChanged(file, added);
			}
		});
	}

	private void structureChanged(IFile file, boolean added) {
		if (monitoredFiles == null)
			return;
		for (int i = 0; i < monitoredFiles.size(); i++) {
			IFile ifile = (IFile) monitoredFiles.get(i);
			if (ifile.equals(file)) {
				if (added) {
					fireStructureChange(file, true);
				} else {
					fireStructureChange(file, false);
					removeContext(file);
				}
			}
		}
	}

	private void removeContext(IFile file) {
		for (Enumeration contexts = inputContexts.elements(); contexts.hasMoreElements();) {
			InputContext context = (InputContext) contexts.nextElement();
			IEditorInput input = context.getInput();
			if (input instanceof IFileEditorInput) {
				IFileEditorInput fileInput = (IFileEditorInput) input;
				if (file.equals(fileInput.getFile())) {
					inputContexts.remove(input);
					fireContextChange(context, false);
					return;
				}
			}
		}
	}

	protected void fireStructureChange(IFile file, boolean added) {
		for (int i = 0; i < listeners.size(); i++) {
			IInputContextListener listener = (IInputContextListener) listeners.get(i);
			if (added)
				listener.monitoredFileAdded(file);
			else
				listener.monitoredFileRemoved(file);
		}
	}

	protected void fireContextChange(InputContext context, boolean added) {
		for (int i = 0; i < listeners.size(); i++) {
			IInputContextListener listener = (IInputContextListener) listeners.get(i);
			if (added)
				listener.contextAdded(context);
			else
				listener.contextRemoved(context);
		}
		if (added)
			hookUndo(context);
		else
			unhookUndo(context);
	}

	public void undo() {
		if (undoManager != null && undoManager.isUndoable())
			undoManager.undo();
	}

	public void redo() {
		if (undoManager != null && undoManager.isRedoable())
			undoManager.redo();
	}

	private void hookUndo(InputContext context) {
		if (undoManager == null)
			return;
		IBaseModel model = context.getModel();
		if (model instanceof IModelChangeProvider)
			undoManager.connect((IModelChangeProvider) model);
	}

	private void unhookUndo(InputContext context) {
		if (undoManager == null)
			return;
		IBaseModel model = context.getModel();
		if (model instanceof IModelChangeProvider)
			undoManager.disconnect((IModelChangeProvider) model);
	}

	/**
	 * @return Returns the undoManager.
	 */
	public IModelUndoManager getUndoManager() {
		return undoManager;
	}

	/**
	 * @param undoManager The undoManager to set.
	 */
	public void setUndoManager(IModelUndoManager undoManager) {
		this.undoManager = undoManager;
	}
}
