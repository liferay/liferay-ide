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

package com.liferay.ide.server.ui;

import java.net.URL;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * @author Gregory Amerson
 */
public class LiferayServerUI extends AbstractUIPlugin {

	public static final String IMG_NOTIFICATION = "imgNotification";

	public static final String IMG_WIZ_RUNTIME = "imgWizRuntime";

	public static final String PLUGIN_ID = "com.liferay.ide.server.ui";

	public static IStatus createErrorStatus(Exception ex) {
		return new Status(IStatus.ERROR, PLUGIN_ID, ex.getMessage(), ex);
	}

	public static IStatus createErrorStatus(String msg) {
		return new Status(IStatus.ERROR, PLUGIN_ID, msg, null);
	}

	public static IStatus createErrorStatus(String msg, Exception ex) {
		return new Status(IStatus.ERROR, PLUGIN_ID, msg, ex);
	}

	public static LiferayServerUI getDefault() {
		return _plugin;
	}

	public static ImageDescriptor getImageDescriptor(String key) {
		try {
			getDefault().getImageRegistry();

			return (ImageDescriptor)getDefault().imageDescriptors.get(key);
		}
		catch (Exception e) {
			return null;
		}
	}

	public static void logError(Exception ex) {
		ILog log = getDefault().getLog();

		log.log(createErrorStatus(ex));
	}

	public static void logError(String msg, Exception ex) {
		ILog log = getDefault().getLog();

		log.log(createErrorStatus(msg, ex));
	}

	public static IStatus logInfo(String msg, IStatus status) {
		return new Status(IStatus.INFO, PLUGIN_ID, msg, status.getException());
	}

	public LiferayServerUI() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);

		_plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		_plugin = null;

		super.stop(context);
	}

	protected ImageRegistry createImageRegistry() {
		ImageRegistry registry = new ImageRegistry();

		String[] pluginTypes = {"portlet", "hook", "ext", "layouttpl", "theme", "web"};

		for (String type : pluginTypes) {
			_registerImage(registry, type, "/icons/e16/" + type + ".png");
		}

		_registerImage(registry, IMG_WIZ_RUNTIME, "wizban/liferay_wiz.png");
		_registerImage(registry, IMG_NOTIFICATION, "e16/liferay_logo_16.png");

		return registry;
	}

	protected Map<String, ImageDescriptor> imageDescriptors = new HashMap<>();

	private void _registerImage(ImageRegistry registry, String key, String partialURL) {
		if (_iconBaseUrl == null) {
			String pathSuffix = "icons/";

			Bundle bundle = _plugin.getBundle();

			_iconBaseUrl = bundle.getEntry(pathSuffix);
		}

		try {
			ImageDescriptor id = ImageDescriptor.createFromURL(new URL(_iconBaseUrl, partialURL));

			registry.put(key, id);

			imageDescriptors.put(key, id);
		}
		catch (Exception e) {
			ILog log = _plugin.getLog();

			log.log(new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage()));
		}
	}

	private static URL _iconBaseUrl;
	private static LiferayServerUI _plugin;

}