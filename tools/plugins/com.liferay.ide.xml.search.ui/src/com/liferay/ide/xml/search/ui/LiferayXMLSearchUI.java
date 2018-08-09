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

import com.liferay.ide.xml.search.ui.editor.ServiceXmlContextType;

import java.io.IOException;

import java.net.URL;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 *
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
public class LiferayXMLSearchUI extends AbstractUIPlugin {

	// The shared instance

	public static final String PLUGIN_ID = "com.liferay.ide.xml.search.ui";

	public static final String PREF_KEY_IGNORE_PROJECTS_LIST = "ignore-projects-list";

	public static final String SERVICE_XML_TEMPLATES_KEY = PLUGIN_ID + ".service_xml_templates";

	public static String portletImg = "portlet";

	// The plug-in ID

	public static IStatus createErrorStatus(Exception e) {
		return new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage(), e);
	}

	public static IStatus createErrorStatus(String msg) {
		return new Status(IStatus.ERROR, PLUGIN_ID, msg);
	}

	public static IStatus createErrorStatus(String msg, Exception e) {
		return new Status(IStatus.ERROR, PLUGIN_ID, msg, e);
	}

	public static IStatus createWarningStatus(String msg) {
		return new Status(IStatus.WARNING, PLUGIN_ID, msg);
	}

	public static LiferayXMLSearchUI getDefault() {
		return _plugin;
	}

	public static IEclipsePreferences getInstancePrefs() {
		return InstanceScope.INSTANCE.getNode(PLUGIN_ID);
	}

	public static void logError(String msg, Exception e) {
		ILog log = getDefault().getLog();

		log.log(createErrorStatus(msg, e));
	}

	public static void logError(Throwable t) {
		ILog log = getDefault().getLog();

		log.log(new Status(IStatus.ERROR, PLUGIN_ID, t.getMessage(), t));
	}

	public LiferayXMLSearchUI() {
	}

	// add context type for templates of service.xml

	public ContextTypeRegistry getContextTypeRegistry() {
		if (_contextTypeRegistry == null) {
			ContributionContextTypeRegistry registry = new ContributionContextTypeRegistry();

			registry.addContextType(ServiceXmlContextType.ID_SERVICE_XML_TAG);

			_contextTypeRegistry = registry;
		}

		return _contextTypeRegistry;
	}

	public TemplateStore getServiceXmlTemplateStore() {
		if (_templateStore == null) {
			_templateStore = new ContributionTemplateStore(
				getContextTypeRegistry(), getPreferenceStore(), SERVICE_XML_TEMPLATES_KEY);

			try {
				_templateStore.load();
			}
			catch (IOException ioe) {
				logError("Error loading template store.", ioe);
			}
		}

		return _templateStore;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);

		_plugin = this;

		URL baseIconsURL = getBundle().getEntry("icons/");

		ImageDescriptor portletImage = ImageDescriptor.createFromURL(new URL(baseIconsURL, "portlet.png"));

		getImageRegistry().put(portletImg, portletImage);
	}

	public void stop(BundleContext context) throws Exception {
		_plugin = null;
		super.stop(context);
	}

	private static LiferayXMLSearchUI _plugin;

	private ContextTypeRegistry _contextTypeRegistry;
	private TemplateStore _templateStore;

}