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

package com.liferay.ide.xml.search.ui;

import com.liferay.ide.core.util.CoreUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.sse.ui.internal.reconcile.TemporaryAnnotation;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class TempMarker implements IMarker {

	public TempMarker(TemporaryAnnotation temp) {
		_annotation = temp;

		_attributes = new HashMap<>();

		_creationTime = System.currentTimeMillis();

		Map attribute = _annotation.getAttributes();

		for (Object key : attribute.keySet()) {
			_attributes.put(key.toString(), attribute.get(key));
		}

		IWorkspaceRoot workspaceRoot = CoreUtil.getWorkspaceRoot();

		_file = workspaceRoot.getFile(Path.fromPortableString((String)_attributes.get(XMLSearchConstants.FULL_PATH)));

		_type = (String)_attributes.get(XMLSearchConstants.MARKER_TYPE);
	}

	@Override
	public void delete() throws CoreException {
	}

	@Override
	public boolean exists() {
		return false;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter) {
		return null;
	}

	@Override
	public Object getAttribute(String attributeName) throws CoreException {
		Map attributes = _annotation.getAttributes();

		return attributes.get(attributeName);
	}

	@Override
	public boolean getAttribute(String attributeName, boolean defaultValue) {
		Map attributes = _annotation.getAttributes();

		Object value = attributes.get(attributeName);

		if (value instanceof Boolean) {
			return Boolean.parseBoolean(value.toString());
		}

		return defaultValue;
	}

	@Override
	public int getAttribute(String attributeName, int defaultValue) {
		Map attributes = _annotation.getAttributes();

		Object value = attributes.get(attributeName);

		if (value instanceof Integer) {
			try {
				return Integer.parseInt(value.toString());
			}
			catch (NumberFormatException nfe) {
				return -1;
			}
		}

		return defaultValue;
	}

	@Override
	public String getAttribute(String attributeName, String defaultValue) {
		Map attributes = _annotation.getAttributes();

		Object value = attributes.get(attributeName);

		if (value != null) {
			return value.toString();
		}

		return defaultValue;
	}

	@Override
	public Map<String, Object> getAttributes() throws CoreException {
		return _attributes;
	}

	@Override
	public Object[] getAttributes(String[] attributeNames) throws CoreException {
		List<Object> retval = new ArrayList<>();

		for (String attributeName : attributeNames) {
			if (_attributes.get(attributeName) != null) {
				retval.add(_attributes.get(attributeName));
			}
		}

		return retval.toArray(new Object[0]);
	}

	@Override
	public long getCreationTime() throws CoreException {
		return _creationTime;
	}

	@Override
	public long getId() {
		return -1;
	}

	@Override
	public IResource getResource() {
		return _file;
	}

	@Override
	public String getType() throws CoreException {
		return _type;
	}

	@Override
	public boolean isSubtypeOf(String superType) throws CoreException {
		return false;
	}

	@Override
	public void setAttribute(String attributeName, boolean value) throws CoreException {
	}

	@Override
	public void setAttribute(String attributeName, int value) throws CoreException {
	}

	@Override
	public void setAttribute(String attributeName, Object value) throws CoreException {
	}

	@Override
	public void setAttributes(Map<String, ? extends Object> attributes) throws CoreException {
	}

	@Override
	public void setAttributes(String[] attributeNames, Object[] values) throws CoreException {
	}

	private final TemporaryAnnotation _annotation;
	private final Map<String, Object> _attributes;
	private final long _creationTime;
	private final IFile _file;
	private final String _type;

}