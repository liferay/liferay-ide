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

import com.liferay.ide.kaleo.core.model.ScriptLanguageType;
import com.liferay.ide.kaleo.core.model.Scriptable;

import java.io.File;

import java.lang.reflect.Field;

import java.net.URI;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.EnumSerialization;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.editors.text.ILocationProvider;

/**
 * @author Gregory Amerson
 */
public class ScriptPropertyEditorInput extends PlatformObject implements IStorageEditorInput, ILocationProvider {

	public ScriptPropertyEditorInput(Element modelElement, ValueProperty property) {
		_modelElement = modelElement;
		_valueProperty = property;
	}

	public boolean exists() {
		if ((_modelElement != null) && (_valueProperty != null)) {
			return true;
		}

		return false;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter) {
		if (ILocationProvider.class.equals(adapter)) {
			return this;
		}

		return super.getAdapter(adapter);
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public String getName() {
		return _valueProperty.name();
	}

	public IPath getPath(Object element) {
		return getStorage().getFullPath();
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public ValueProperty getProperty() {
		return _valueProperty;
	}

	public String getScriptLanguage() {
		String retval = null;

		try {
			Scriptable scriptable = _modelElement.nearest(Scriptable.class);

			Value<ScriptLanguageType> languageType = scriptable.getScriptLanguage();

			ScriptLanguageType scriptType = languageType.content();

			Class<?> scriptTypeClass = scriptType.getClass();

			Field field = scriptTypeClass.getFields()[scriptType.ordinal()];

			EnumSerialization enumValue = field.getAnnotation(EnumSerialization.class);

			retval = enumValue.primary();
		}
		catch (Exception e) {
		}

		return retval;
	}

	public IStorage getStorage() {
		return new ScriptPropertyStorage(_modelElement, _valueProperty);
	}

	public String getToolTipText() {
		IPath path = getStorage().getFullPath();

		return path.toPortableString();
	}

	public URI getURI(Object element) {
		IPath path = getStorage().getFullPath();

		File file = path.toFile();

		return file.toURI();
	}

	private Element _modelElement;
	private ValueProperty _valueProperty;

}