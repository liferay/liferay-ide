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

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IPluginRegistry;
import org.eclipse.core.runtime.Platform;

import org.osgi.framework.Bundle;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("deprecation")
public abstract class RegistryReader {

	public static boolean canCreateExecutableExtension(IConfigurationElement element) {
		if (Platform.isRunning() && (getSystemBundle().getState() != Bundle.STOPPING)) {
			return true;
		}

		return false;
	}

	/**
	 * Utility method to get the plugin id of a configuation element
	 *
	 * @param configurationElement
	 * @return plugin id of configuration element
	 * @since 1.0.0
	 */
	public static String getPluginId(IConfigurationElement configurationElement) {
		if (configurationElement == null) {
			return null;
		}

		IExtension extension = configurationElement.getDeclaringExtension();

		if (extension != null) {
			return extension.getContributor().getName();
		}

		return null;
	}

	public RegistryReader(IPluginRegistry registry, String pluginID, String extensionPoint) {
		this(pluginID, extensionPoint);
	}

	public RegistryReader(String pluginID, String extensionPoint) {
		pluginId = pluginID;

		extensionPointId = extensionPoint;
	}

	/**
	 * Implement this method to read element attributes. If this element has
	 * subelements, the reader will recursively cycle through them and call this
	 * method so don't do it here.
	 */
	public abstract boolean readElement(IConfigurationElement element);

	public void readRegistry() {
		IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint(pluginId, extensionPointId);

		if (point != null) {
			for (IConfigurationElement element : point.getConfigurationElements()) {
				_internalReadElement(element);
			}
		}

		// the following code is to handle the contributions to the deprecated org.eclipse.jem.util extensions

		if (_JEM_PLUGIN_ID.equals(pluginId)) {
			return;
		}

		point = Platform.getExtensionRegistry().getExtensionPoint(_JEM_PLUGIN_ID, extensionPointId);

		if (point == null) {
			return;
		}

		for (IConfigurationElement element : point.getConfigurationElements()) {
			_internalReadElement(element);
		}
	}

	public String extensionPointId;
	public String pluginId;

	protected static Bundle getSystemBundle() {
		if (_systemBundle == null) {
			_systemBundle = Platform.getBundle("org.eclipse.osgi");
		}

		return _systemBundle;
	}

	/**
	 * Logs the error in the desktop log using the provided text and the information
	 * in the configuration element.
	 */
	protected void logError(IConfigurationElement element, String text) {
		IExtension extension = element.getDeclaringExtension();

		StringBuffer sb = new StringBuffer();

		sb.append("Plugin ");
		sb.append(extension.getContributor().getName());
		sb.append(", extension ");
		sb.append(extension.getExtensionPointUniqueIdentifier());
		sb.append("\n");
		sb.append(text);

		LiferayCore.logError(sb.toString());
	}

	/**
	 * Logs a very common registry error when a required attribute is missing.
	 */
	protected void logMissingAttribute(IConfigurationElement element, String attributeName) {
		logError(element, "Required attribute '" + attributeName + "' not defined");
	}

	private void _internalReadElement(IConfigurationElement element) {
		boolean recognized = readElement(element);

		if (recognized) {
			return;
		}

		logError(element, "Error processing extension: " + element);
	}

	private static final String _JEM_PLUGIN_ID = "org.eclipse.jem.util";

	private static Bundle _systemBundle;

}