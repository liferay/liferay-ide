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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.InvalidRegistryObjectException;

/**
 * @author Gregory Amerson
 */
public abstract class AbstractConfigurationElement implements IConfigurationElement {

	public Object createExecutableExtension(String propertyName) throws CoreException {
		return null;
	}

	public String getAttribute(String name) throws InvalidRegistryObjectException {
		return null;
	}

	public String getAttribute(String attrName, String locale) throws InvalidRegistryObjectException {
		return null;
	}

	public String getAttributeAsIs(String name) throws InvalidRegistryObjectException {
		return null;
	}

	public String[] getAttributeNames() throws InvalidRegistryObjectException {
		return null;
	}

	public IConfigurationElement[] getChildren() throws InvalidRegistryObjectException {
		return null;
	}

	public IConfigurationElement[] getChildren(String name) throws InvalidRegistryObjectException {
		return null;
	}

	public IContributor getContributor() throws InvalidRegistryObjectException {
		return null;
	}

	public IExtension getDeclaringExtension() throws InvalidRegistryObjectException {
		return null;
	}

	public int getHandleId() {
		return 0;
	}

	public String getName() throws InvalidRegistryObjectException {
		return null;
	}

	public String getNamespace() throws InvalidRegistryObjectException {
		return null;
	}

	public String getNamespaceIdentifier() throws InvalidRegistryObjectException {
		return null;
	}

	public Object getParent() throws InvalidRegistryObjectException {
		return null;
	}

	public String getValue() throws InvalidRegistryObjectException {
		return null;
	}

	public String getValue(String locale) throws InvalidRegistryObjectException {
		return null;
	}

	public String getValueAsIs() throws InvalidRegistryObjectException {
		return null;
	}

	public boolean isValid() {
		return false;
	}

}