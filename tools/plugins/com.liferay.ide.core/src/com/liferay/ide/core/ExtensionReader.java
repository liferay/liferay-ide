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

package com.liferay.ide.core;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

/**
 * @author Gregory Amerson
 */
public abstract class ExtensionReader<T> extends RegistryReader {

	public ExtensionReader(String pluginID, String extension, String element) {
		super(pluginID, extension);

		_element = element;
	}

	public List<T> getExtensions() {
		if (!_hasInitialized) {
			readRegistry();

			_hasInitialized = true;
		}

		List<T> adapters = new LinkedList<>();

		for (T adapter : _extensions.values()) {
			adapters.add(adapter);
		}

		return adapters;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean readElement(IConfigurationElement element) {
		if (!_element.equals(element.getName())) {
			return true;
		}

		String id = element.getAttribute(_ATTRIBUTE_ID);

		try {
			T execExt = (T)element.createExecutableExtension(_ATTRIBUTE_CLASS);

			execExt = initElement(element, execExt);

			if (execExt != null) {
				_extensions.put(id, execExt);
			}
		}
		catch (CoreException ce) {
			LiferayCore.logError(ce);
		}

		return true;
	}

	protected abstract T initElement(IConfigurationElement configElement, T execExt);

	private static final String _ATTRIBUTE_CLASS = "class";

	private static final String _ATTRIBUTE_ID = "id";

	private String _element;
	private Map<String, T> _extensions = new HashMap<>();
	private boolean _hasInitialized = false;

}