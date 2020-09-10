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

package com.liferay.ide.upgrade.commands.ui.internal;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * @author Terry Jia
 */
public class UpgradeCommandsUIPlugin extends AbstractUIPlugin {

	public static final String CHECKED_IMAGE_ID = "checked.image";

	public static final String PLUGIN_ID = "com.liferay.ide.upgrade.commands.ui";

	public static final String UNCHECKED_IMAGE_ID = "unchecked.image";

	public static IStatus createErrorStatus(String msg) {
		return createErrorStatus(msg, null);
	}

	public static IStatus createErrorStatus(String msg, Exception e) {
		return new Status(IStatus.ERROR, PLUGIN_ID, msg, e);
	}

	public static Image getImage(String key) {
		UpgradeCommandsUIPlugin upgradeCommandsUIPlugin = getInstance();

		ImageRegistry imageRegistry = upgradeCommandsUIPlugin.getImageRegistry();

		return imageRegistry.get(key);
	}

	public static UpgradeCommandsUIPlugin getInstance() {
		return _instance;
	}

	public static void log(IStatus status) {
		ILog log = _instance.getLog();

		log.log(status);
	}

	public static void logError(String msg) {
		ILog log = _instance.getLog();

		log.log(createErrorStatus(msg));
	}

	public static void logError(String msg, Exception e) {
		ILog log = _instance.getLog();

		log.log(createErrorStatus(msg, e));
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);

		_instance = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		_instance = null;

		super.stop(context);
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry imageRegistry) {
		Bundle bundle = _instance.getBundle();

		IPath path = _ICONS_PATH.append("skip_status.gif");

		ImageDescriptor imageDescriptor = _createImageDescriptor(bundle, path);

		path = _ICONS_PATH.append("checked.png");

		imageDescriptor = _createImageDescriptor(bundle, path);

		imageRegistry.put(CHECKED_IMAGE_ID, imageDescriptor);

		path = _ICONS_PATH.append("unchecked.png");

		imageDescriptor = _createImageDescriptor(bundle, path);

		imageRegistry.put(UNCHECKED_IMAGE_ID, imageDescriptor);
	}

	private static ImageDescriptor _createImageDescriptor(Bundle bundle, IPath path) {
		return ImageDescriptor.createFromURL(FileLocator.find(bundle, path, null));
	}

	private static final IPath _ICONS_PATH = new Path("icons/");

	private static UpgradeCommandsUIPlugin _instance;

}