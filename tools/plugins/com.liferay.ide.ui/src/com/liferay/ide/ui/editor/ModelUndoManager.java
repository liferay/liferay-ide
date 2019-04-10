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

import com.liferay.ide.core.model.IModelChangeProvider;
import com.liferay.ide.core.model.IModelChangedEvent;
import com.liferay.ide.core.model.IModelChangedListener;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.ui.form.IDEFormEditor;

import java.util.List;
import java.util.Vector;

import org.eclipse.jface.action.IAction;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.forms.editor.IFormPage;

/**
 * @author Gregory Amerson
 */
public abstract class ModelUndoManager implements IModelChangedListener, IModelUndoManager {

	public ModelUndoManager(IDEFormEditor editor) {
		_editor = editor;

		_operations = new Vector();
	}

	/**
	 * @see IModelUndoManager#connect(IModelChangeProvider)
	 */
	public void connect(IModelChangeProvider provider) {
		provider.addModelChangedListener(this);

		if (_operations == null) {
			_initialize();
		}
	}

	/**
	 * @see IModelUndoManager#disconnect(IModelChangeProvider)
	 */
	public void disconnect(IModelChangeProvider provider) {
		provider.removeModelChangedListener(this);
	}

	/**
	 * @see IModelUndoManager#isRedoable()
	 */
	public boolean isRedoable() {
		if (_operations == null) {
			_initialize();
		}

		if ((_cursor + 1) < _operations.size()) {
			return true;
		}

		return false;
	}

	/**
	 * @see IModelUndoManager#isUndoable()
	 */
	public boolean isUndoable() {
		if (_cursor >= 0) {
			return true;
		}

		return false;
	}

	/**
	 * @see IModelChangedListener#modelChanged(IModelChangedEvent)
	 */
	public void modelChanged(IModelChangedEvent event) {
		if (_ignoreChanges) {
			return;
		}

		if (event.getChangeType() == IModelChangedEvent.WORLD_CHANGED) {
			_initialize();

			return;
		}

		_addOperation(event);
	}

	/**
	 * @see IModelUndoManager#redo()
	 */
	public void redo() {
		_cursor++;
		IModelChangedEvent op = _getCurrentOperation();

		if (op == null) {
			return;
		}

		_ignoreChanges = true;
		_openRelatedPage(op);
		execute(op, false);
		_ignoreChanges = false;
		_updateActions();
	}

	public void setActions(IAction undoAction, IAction redoAction) {
		_undoAction = undoAction;
		_redoAction = redoAction;
		_updateActions();
	}

	public void setIgnoreChanges(boolean ignore) {
		_ignoreChanges = ignore;
	}

	public void setUndoLevelLimit(int limit) {
		_undoLevelLimit = limit;
	}

	/**
	 * @see IModelUndoManager#undo()
	 */
	public void undo() {
		IModelChangedEvent op = _getCurrentOperation();

		if (op == null) {
			return;
		}

		_ignoreChanges = true;
		_openRelatedPage(op);
		execute(op, true);
		_cursor--;
		_updateActions();
		_ignoreChanges = false;
	}

	protected abstract void execute(IModelChangedEvent op, boolean undo);

	protected abstract String getPageId(Object object);

	private void _addOperation(IModelChangedEvent operation) {
		_operations.add(operation);

		int size = _operations.size();

		if (size > _undoLevelLimit) {
			int extra = size - _undoLevelLimit;

			// trim

			for (int i = 0; i < extra; i++) {
				_operations.remove(i);
			}
		}

		_cursor = _operations.size() - 1;
		_updateActions();
	}

	private IModelChangedEvent _getCurrentOperation() {
		if ((_cursor == -1) || (_cursor == _operations.size())) {
			return null;
		}

		return (IModelChangedEvent)_operations.get(_cursor);
	}

	private IModelChangedEvent _getNextOperation() {
		int peekCursor = _cursor + 1;

		if (peekCursor >= _operations.size()) {
			return null;
		}

		return (IModelChangedEvent)_operations.get(peekCursor);
	}

	private String _getOperationText(IModelChangedEvent op) {
		String opText = StringPool.EMPTY;

		switch (op.getChangeType()) {
			case IModelChangedEvent.INSERT:
				opText = Msgs.insert;

				break;
			case IModelChangedEvent.REMOVE:
				opText = Msgs.remove;

				break;
			case IModelChangedEvent.CHANGE:
				opText = Msgs.propertyChange;

				break;
		}

		return opText;
	}

	private String _getRedoText() {
		IModelChangedEvent op = _getNextOperation();

		if (op == null) {
			return Msgs.redo;
		}

		return NLS.bind(Msgs.redoText, _getOperationText(op));
	}

	private String _getUndoText() {
		IModelChangedEvent op = _getCurrentOperation();

		if (op == null) {
			return Msgs.undo;
		}

		return NLS.bind(Msgs.undoText, _getOperationText(op));
	}

	private void _initialize() {
		_operations = new Vector();
		_cursor = -1;
		_updateActions();
	}

	private void _openRelatedPage(IModelChangedEvent op) {
		Object obj = op.getChangedObjects()[0];

		String pageId = getPageId(obj);

		if (pageId != null) {
			IFormPage cpage = _editor.getActivePageInstance();
			IFormPage newPage = _editor.findPage(pageId);

			if (cpage != newPage) {
				_editor.setActivePage(newPage.getId());
			}
		}
	}

	private void _updateActions() {
		if ((_undoAction != null) && (_redoAction != null)) {
			_undoAction.setEnabled(isUndoable());
			_undoAction.setText(_getUndoText());
			_redoAction.setEnabled(isRedoable());
			_redoAction.setText(_getRedoText());
		}
	}

	private int _cursor = -1;
	private IDEFormEditor _editor;
	private boolean _ignoreChanges;
	private List _operations;
	private IAction _redoAction;
	private IAction _undoAction;
	private int _undoLevelLimit = 10;

	private static class Msgs extends NLS {

		public static String insert;
		public static String propertyChange;
		public static String redo;
		public static String redoText;
		public static String remove;
		public static String undo;
		public static String undoText;

		static {
			initializeMessages(ModelUndoManager.class.getName(), Msgs.class);
		}

	}

}