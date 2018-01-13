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

package com.liferay.ide.kaleo.ui.editor;

import com.liferay.ide.kaleo.ui.navigator.WorkflowDefinitionEntry;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;

/**
 * @author Gregory Amerson
 */
public class WorkflowDefinitionEditorInput extends PlatformObject implements IStorageEditorInput {

	public WorkflowDefinitionEditorInput(WorkflowDefinitionEntry defNode) {
		_defNode = defNode;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof WorkflowDefinitionEditorInput) {
			WorkflowDefinitionEditorInput input = (WorkflowDefinitionEditorInput)obj;

			return _defNode.equals(input.getWorkflowDefinitionEntry());
		}

		return false;
	}

	public boolean exists() {
		return true;

		// return (modelElement != null) && (valueProperty != null);

	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public String getName() {
		StringBuilder builder = new StringBuilder();

		builder.append(_defNode.getName() + " [Version: " + _defNode.getVersion());

		if (_defNode.getDraftVersion() != -1) {
			builder.append(", Draft Version: " + _defNode.getDraftVersion() + "]");
		}
		else {
			builder.append("]");
		}

		return builder.toString();
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public IStorage getStorage() {
		return new DefinitionStorage(_defNode);
	}

	public String getToolTipText() {
		return getName();
	}

	public WorkflowDefinitionEntry getWorkflowDefinitionEntry() {
		return _defNode;
	}

	public void setWorkflowDefinitionEntry(WorkflowDefinitionEntry newNode) {
		_defNode = newNode;
	}

	private WorkflowDefinitionEntry _defNode;

}