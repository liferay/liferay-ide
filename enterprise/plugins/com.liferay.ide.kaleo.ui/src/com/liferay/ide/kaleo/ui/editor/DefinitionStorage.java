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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.PlatformObject;

/**
 * @author Gregory Amerson
 */
public class DefinitionStorage extends PlatformObject implements IStorage {

	public DefinitionStorage(WorkflowDefinitionEntry defNode) {
		_node = defNode;
	}

	public InputStream getContents() throws CoreException {
		return new ByteArrayInputStream((_node.getContent() != null) ? _node.getContent().getBytes() : "".getBytes());
	}

	public IPath getFullPath() {
		return new Path(_node.getLocation() + "/KaleoWorkflowDefinitions/" + _node.getName());
	}

	public String getName() {
		return _node.getName();
	}

	public boolean isReadOnly() {
		return false;
	}

	private WorkflowDefinitionEntry _node;

}