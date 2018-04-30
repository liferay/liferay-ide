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

package com.liferay.ide.portlet.ui;

import com.liferay.ide.portlet.ui.jsf.JSFPortletTemplateContextTypeIds;
import com.liferay.ide.portlet.ui.template.PortletTemplateContextTypeIds;

import java.io.IOException;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plugin life cycle
 *
 * @author Gregory Amerson
 * @author Kamesh Sampath [IDE-405] updated the ImageRegistry
 */
public class PortletUIPlugin extends AbstractUIPlugin {

	// The plugin ID

	public static final String PLUGIN_ID = "com.liferay.ide.portlet.ui";

	// The shared instance

	public static IStatus createErrorStatus(Exception e) {
		return new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage(), e);
	}

	// Image KEYS

	public static IStatus createErrorStatus(String string) {
		return new Status(IStatus.ERROR, PLUGIN_ID, string);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static PortletUIPlugin getDefault() {
		return _plugin;
	}

	public static void logError(Exception e) {
		logError(e.getMessage(), e);
	}

	public static void logError(String msg, Exception e) {
		ILog log = getDefault().getLog();

		log.log(new Status(IStatus.ERROR, PLUGIN_ID, msg, e));
	}

	/**
	 * The constructor
	 */
	public PortletUIPlugin() {
	}

	public ContextTypeRegistry getTemplateContextRegistry() {
		if (_fContextTypeRegistry == null) {
			ContributionContextTypeRegistry registry = new ContributionContextTypeRegistry();

			registry.addContextType(PortletTemplateContextTypeIds.NEW);
			registry.addContextType(JSFPortletTemplateContextTypeIds.NEW);

			_fContextTypeRegistry = registry;
		}

		return _fContextTypeRegistry;
	}

	public TemplateStore getTemplateStore() {
		if (_fTemplateStore == null) {
			_fTemplateStore = new ContributionTemplateStore(
				getTemplateContextRegistry(), getPreferenceStore(), "com.liferay.ide.portlet.ui.custom_templates");

			try {
				_fTemplateStore.load();
			}
			catch (IOException ioe) {
				logError(ioe);
			}
		}

		return _fTemplateStore;
	}

	/**
	 * (non-Javadoc)
	 *
	 * @see
	 * AbstractUIPlugin#start(BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);

		_plugin = this;
	}

	/**
	 * (non-Javadoc)
	 *
	 * @see
	 * AbstractUIPlugin#stop(BundleContext
	 * )
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		_plugin = null;
		super.stop(context);
	}

	/**
	 * (non-Javadoc)
	 *
	 * @see
	 * AbstractUIPlugin#initializeImageRegistry(org.eclipse.
	 * jface.resource.ImageRegistry)
	 */
	@Override
	protected void initializeImageRegistry(ImageRegistry imageRegistry) {
	}

	private static PortletUIPlugin _plugin;

	private ContributionContextTypeRegistry _fContextTypeRegistry;
	private ContributionTemplateStore _fTemplateStore;

}