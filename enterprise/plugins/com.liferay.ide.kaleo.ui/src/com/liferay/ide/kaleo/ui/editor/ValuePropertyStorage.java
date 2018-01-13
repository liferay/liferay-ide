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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ValueProperty;

/**
 * @author Gregory Amerson
 */
public class ValuePropertyStorage extends PlatformObject implements IStorage {

	public ValuePropertyStorage(Element modelElement, ValueProperty valueProperty) {
		_modelElement = modelElement;

		_valueProperty = valueProperty;
	}

	public InputStream getContents() throws CoreException {
		Object content = _modelElement.property(_valueProperty).content();

		if (content == null) {
			content = _EMPTY_CONTENTS;
		}

		return new ByteArrayInputStream(content.toString().getBytes());
	}

	public IPath getFullPath() {
		IPath retval = null;

		String localPath = _modelElement.type().getSimpleName() + "." + _valueProperty.name();

		IFile file = _modelElement.adapt(IFile.class);

		if (file != null) {
			retval = file.getFullPath().append(localPath);
		}
		else {
			retval = new Path(localPath);
		}

		return retval;
	}

	public String getName() {
		return _valueProperty.name();
	}

	public boolean isReadOnly() {
		return false;
	}

	protected Element element() {
		return _modelElement;
	}

	private static final String _EMPTY_CONTENTS = "";

	private Element _modelElement;
	private ValueProperty _valueProperty;

}