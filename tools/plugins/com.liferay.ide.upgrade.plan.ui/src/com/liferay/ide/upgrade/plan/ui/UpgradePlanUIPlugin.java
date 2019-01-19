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

package com.liferay.ide.upgrade.plan.ui;

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
 * @author Gregory Amerson
 */
public class UpgradePlanUIPlugin extends AbstractUIPlugin {

	public static final String CATEGORY_CODE_IMAGE = "CATEGORY_CODE";

	public static final String CATEGORY_CONFIG_IMAGE = "CATEGORY_CONFIG";

	public static final String CATEGORY_DATABASE_IMAGE = "CATEGORY_DATABASE";

	public static final String COMPOSITE_TASK_START_IMAGE = "COMPOSITE_TASK_START";

	public static final String ITEM_COMPLETE_IMAGE = "ITEM_COMPLETE";

	public static final String ITEM_SKIP_IMAGE = "ITEM_SKIP";

	public static final String NO_TASKS_IMAGE = "NO_TASKS";

	public static final String PLUGIN_ID = "com.liferay.ide.upgrade.plan.ui";

	public static IStatus createErrorStatus(String msg) {
		return createErrorStatus(msg, null);
	}

	public static IStatus createErrorStatus(String msg, Exception e) {
		return new Status(IStatus.ERROR, PLUGIN_ID, msg, e);
	}

	public static Image getImage(String key) {
		UpgradePlanUIPlugin upgradePlanUIPlugin = getInstance();

		ImageRegistry imageRegistry = upgradePlanUIPlugin.getImageRegistry();

		return imageRegistry.get(key);
	}

	public static UpgradePlanUIPlugin getInstance() {
		return _plugin;
	}

	public static void logError(Exception e) {
		ILog log = _plugin.getLog();

		log.log(createErrorStatus(e.getMessage(), e));
	}

	public static void logError(String msg, Exception e) {
		ILog log = _plugin.getLog();

		log.log(createErrorStatus(msg, e));
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);

		_plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		_plugin = null;

		super.stop(context);
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry imageRegistry) {
		Bundle bundle = _plugin.getBundle();

		IPath path = _ICONS_PATH.append("complete_status.gif");

		ImageDescriptor imageDescriptor = _createImageDescriptor(bundle, path);

		imageRegistry.put(ITEM_COMPLETE_IMAGE, imageDescriptor);

		path = _ICONS_PATH.append("skip_status.gif");

		imageDescriptor = _createImageDescriptor(bundle, path);

		imageRegistry.put(ITEM_SKIP_IMAGE, imageDescriptor);

		path = _ICONS_PATH.append("start_ccs_task.gif");

		imageDescriptor = _createImageDescriptor(bundle, path);

		imageRegistry.put(COMPOSITE_TASK_START_IMAGE, imageDescriptor);

		path = _ICONS_PATH.append("information.gif");

		imageDescriptor = _createImageDescriptor(bundle, path);

		imageRegistry.put(NO_TASKS_IMAGE, imageDescriptor);

		path = _ICONS_PATH.append("category_code.png");

		imageDescriptor = _createImageDescriptor(bundle, path);

		imageRegistry.put(CATEGORY_CODE_IMAGE, imageDescriptor);

		path = _ICONS_PATH.append("category_config.png");

		imageDescriptor = _createImageDescriptor(bundle, path);

		imageRegistry.put(CATEGORY_CONFIG_IMAGE, imageDescriptor);

		path = _ICONS_PATH.append("category_database.png");

		imageDescriptor = _createImageDescriptor(bundle, path);

		imageRegistry.put(CATEGORY_DATABASE_IMAGE, imageDescriptor);
	}

	private static ImageDescriptor _createImageDescriptor(Bundle bundle, IPath path) {
		return ImageDescriptor.createFromURL(FileLocator.find(bundle, path, null));
	}

	private static final IPath _ICONS_PATH = new Path("icons/");

	private static UpgradePlanUIPlugin _plugin;

}