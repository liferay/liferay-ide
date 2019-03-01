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

package com.liferay.ide.upgrade.plan.ui.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.InvalidRegistryObjectException;

/**
 * @author Terry Jia
 */
@SuppressWarnings("deprecation")
public class DelegateConfigurationElement implements IConfigurationElement {

	public DelegateConfigurationElement(IConfigurationElement configurationElement) {
		_configurationElement = configurationElement;
	}

	public Object createExecutableExtension(String propertyName) throws CoreException {
		if (_configurationElement == null) {
			return null;
		}

		return _configurationElement.createExecutableExtension(propertyName);
	}

	public boolean equals(Object obj) {
		if (_configurationElement == null) {
			return false;
		}

		return _configurationElement.equals(obj);
	}

	public String getAttribute(String name) throws InvalidRegistryObjectException {
		if (_configurationElement == null) {
			return null;
		}

		return _configurationElement.getAttribute(name);
	}

	public String getAttribute(String attrName, String locale) throws InvalidRegistryObjectException {
		if (_configurationElement == null) {
			return null;
		}

		return _configurationElement.getAttribute(attrName, locale);
	}

	public String getAttributeAsIs(String name) throws InvalidRegistryObjectException {
		if (_configurationElement == null) {
			return null;
		}

		return _configurationElement.getAttributeAsIs(name);
	}

	public String[] getAttributeNames() throws InvalidRegistryObjectException {
		if (_configurationElement == null) {
			return new String[0];
		}

		return _configurationElement.getAttributeNames();
	}

	public IConfigurationElement[] getChildren() throws InvalidRegistryObjectException {
		if (_configurationElement == null) {
			return new IConfigurationElement[0];
		}

		return _configurationElement.getChildren();
	}

	public IConfigurationElement[] getChildren(String name) throws InvalidRegistryObjectException {
		if (_configurationElement == null) {
			return new IConfigurationElement[0];
		}

		return _configurationElement.getChildren(name);
	}

	public IContributor getContributor() throws InvalidRegistryObjectException {
		if (_configurationElement == null) {
			throw new InvalidRegistryObjectException();
		}

		return _configurationElement.getContributor();
	}

	public IExtension getDeclaringExtension() throws InvalidRegistryObjectException {
		if (_configurationElement == null) {
			throw new InvalidRegistryObjectException();
		}

		return _configurationElement.getDeclaringExtension();
	}

	public int getHandleId() {
		if (_configurationElement == null) {
			return -1;
		}

		return _configurationElement.getHandleId();
	}

	public String getName() throws InvalidRegistryObjectException {
		if (_configurationElement == null) {
			return "delegateConfigurationElement";
		}

		return _configurationElement.getName();
	}

	public String getNamespace() throws InvalidRegistryObjectException {
		if (_configurationElement == null) {
			return "delegateConfigurationElementNamespace";
		}

		return _configurationElement.getNamespace();
	}

	public String getNamespaceIdentifier() throws InvalidRegistryObjectException {
		if (_configurationElement == null) {
			return "delegateConfigurationElementNamespace";
		}

		return _configurationElement.getNamespaceIdentifier();
	}

	public Object getParent() throws InvalidRegistryObjectException {
		if (_configurationElement == null) {
			return null;
		}

		return _configurationElement.getParent();
	}

	public String getValue() throws InvalidRegistryObjectException {
		if (_configurationElement == null) {
			return null;
		}

		return _configurationElement.getValue();
	}

	public String getValue(String locale) throws InvalidRegistryObjectException {
		if (_configurationElement == null) {
			return null;
		}

		return _configurationElement.getValue(locale);
	}

	public String getValueAsIs() throws InvalidRegistryObjectException {
		if (_configurationElement == null) {
			return null;
		}

		return _configurationElement.getValueAsIs();
	}

	public int hashCode() {
		if (_configurationElement == null) {
			return -1;
		}

		return _configurationElement.hashCode();
	}

	public boolean isValid() {
		if (_configurationElement == null) {
			return false;
		}

		return _configurationElement.isValid();
	}

	public IConfigurationElement toEquinox() {
		return null;
	}

	public String toString() {
		if (_configurationElement == null) {
			return "delegateConfigurationElement: NULL";
		}

		return _configurationElement.toString();
	}

	private final IConfigurationElement _configurationElement;

}