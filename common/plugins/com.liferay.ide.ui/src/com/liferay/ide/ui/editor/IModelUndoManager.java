/*******************************************************************************
 *  Copyright (c) 2000, 2008 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.liferay.ide.ui.editor;

import org.eclipse.jface.action.IAction;

import com.liferay.ide.core.model.IModelChangeProvider;

/**
 * Classes that implement this interface provide undo/redo
 * capability linked to changes reported by model change
 * providers. Model change events carry sufficient data
 * to be used in an undo/redo stack and reverted to or
 * reapplied after the change.
 * <p>Model undo manager adds itself as a change listener
 * after being connected to the provider. It is expected
 * to stop listening to change events after being disconnected.
 * Changes reported while being connected are kept in the
 * operation stack whose size can be controlled.
 * <p>The part that uses the undo manager is responsible
 * for supplying Undo and Redo action objects for
 * the purpose of controlling their availability. 
 * Undo manager should keep track of its current
 * operation stack pointer and adjust Undo/Redo action
 * availability by calling 'setEnabled' on the
 * provided action objects. Implementation of this
 * interface may also opt to modify Undo/Redo action
 * labels in order to better indicate the effect
 * of the operations if selected (for example,
 * 'Undo Delete' instead of 'Undo').
 */
public interface IModelUndoManager {
	/**
	 * Connects to the change provider. Until disconnecting,
	 * the manager will keep model changes in the operation
	 * stack and will be able to revert or reapply these
	 * changes in the source model.
	 * @param provider the model change provider to connect to
	 */
	public void connect(IModelChangeProvider provider);

	/**
	 * Disconnects from the change provider. Upon disconnecting,
	 * the manager will no longer be able to revert or reapply
	 * changes in the source model.
	 * @param provider the model change provider to disconnect from
	 */
	public void disconnect(IModelChangeProvider provider);

	/**
	 * Tests whether the current operation in the undo stack can
	 * be reverted.
	 * @return true if the current operation can be undone.
	 */
	public boolean isUndoable();

	/**
	 * Tests whether the current operation in the undo stack can
	 * be reapplied.
	 * @return true if the current operation can be redone.
	 */
	public boolean isRedoable();

	/**
	 * Reverts the current operation in the undo stack and decrements
	 * the stack pointer.
	 */
	public void undo();

	/**
	 * Reapplies the next operation in the undo stack and sets
	 * the stack pointer to that operation.
	 * 
	 */
	public void redo();

	/**
	 * Sets the depth of the undo stack.
	 * @param limit number of levels in the undo stack.
	 */
	public void setUndoLevelLimit(int limit);

	/**
	 * Temporarily suspends undo manager.
	 * @param ignore if true, model changes reported by the 
	 * model change provider will be ignore until this
	 * property is set to <samp>false</samp> again.
	 */
	public void setIgnoreChanges(boolean ignore);

	/**
	 * Connects the undo manager with the undo and redo actions 
	 * in the workbench part using the manager. The manager
	 * uses these objects to enable or disable the actions
	 * according to the state of the undo stack and the current
	 * location of the stack pointer. 
	 * @param undoAction the action in the workbench part that performs
	 * the undo operation.
	 * @param redoAction the action in the workbench part that performs
	 * the redo operation.
	 */
	public void setActions(IAction undoAction, IAction redoAction);
}
