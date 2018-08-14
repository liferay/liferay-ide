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

package com.liferay.ide.portlet.vaadin.ui;

import org.eclipse.core.runtime.Plugin;

import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plugin life cycle
 *
 * @author Gregory Amerson
 */
public class VaadinUI extends Plugin {

	// The plugin ID

	public static final String PLUGIN_ID = "com.liferay.ide.vaadin.ui";

	// The shared instance

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static VaadinUI getDefault() {
		return _plugin;
	}

	/**
	 * The constructor
	 */
	public VaadinUI() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);

		_plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		_plugin = null;
		super.stop(context);
	}

	private static VaadinUI _plugin;

}