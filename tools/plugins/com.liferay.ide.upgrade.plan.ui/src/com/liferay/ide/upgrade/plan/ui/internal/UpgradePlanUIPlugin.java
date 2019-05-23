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

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogSettings;
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

	public static final String CHECKED_IMAGE_ID = "checked.image";

	public static final String ITEM_SKIP_IMAGE = "ITEM_SKIP";

	public static final String NO_STEPS_IMAGE = "NO_STEPS";

	public static final String PLUGIN_ID = "com.liferay.ide.upgrade.plan.ui";

	public static final String STEP_COMPLETE_IMAGE = "STEP_COMPLETE_IMAGE";

	public static final String STEP_COMPLETE_OVERLAY_IMAGE = "STEP_COMPLETE_OVERLAY_IMAGE";

	public static final String STEP_FAILED_OVERLAY_IMAGE = "STEP_FAILED_OVERLAY_IMAGE";

	public static final String STEP_PERFORM_IMAGE = "STEP_PERFORM_IMAGE";

	public static final String STEP_PERVIEW_IMAGE = "STEP_PERVIEW_IMAGE";

	public static final String STEP_RESTART_IMAGE = "STEP_RESTART_IMAGE";

	public static final String STEP_SKIP_IMAGE = "STEP_SKIP_IMAGE";

	public static final String STEP_SKIP_OVERLAY_IMAGE = "STEP_SKIP_OVERLAY_IMAGE";

	public static final String UNCHECKED_IMAGE_ID = "unchecked.image";

	public static IStatus createErrorStatus(String msg) {
		return createErrorStatus(msg, null);
	}

	public static IStatus createErrorStatus(String msg, Throwable throwable) {
		return new Status(IStatus.ERROR, PLUGIN_ID, msg, throwable);
	}

	public static Image getImage(String key) {
		UpgradePlanUIPlugin upgradePlanUIPlugin = getInstance();

		ImageRegistry imageRegistry = upgradePlanUIPlugin.getImageRegistry();

		return imageRegistry.get(key);
	}

	public static ImageDescriptor getImageDescriptor(String key) {
		UpgradePlanUIPlugin upgradePlanUIPlugin = getInstance();

		ImageRegistry imageRegistry = upgradePlanUIPlugin.getImageRegistry();

		return imageRegistry.getDescriptor(key);
	}

	public static UpgradePlanUIPlugin getInstance() {
		return _plugin;
	}

	public static IDialogSettings getUpgradePlanSettings() {
		return _plugin.getDialogSettings();
	}

	public static void logError(Exception e) {
		ILog log = _plugin.getLog();

		log.log(createErrorStatus(e.getMessage(), e));
	}

	public static void logError(String msg, Exception e) {
		ILog log = _plugin.getLog();

		log.log(createErrorStatus(msg, e));
	}

	public static void logError(String msg, Throwable t) {
		ILog log = _plugin.getLog();

		log.log(createErrorStatus(msg, t));
	}

	public static void saveUpgradePlanSettings() {
		_plugin.saveDialogSettings();
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

		IPath path = _ICONS_PATH.append("skip_status.gif");

		ImageDescriptor imageDescriptor = _createImageDescriptor(bundle, path);

		imageRegistry.put(ITEM_SKIP_IMAGE, imageDescriptor);

		path = _ICONS_PATH.append("start_ccs_task.gif");

		imageDescriptor = _createImageDescriptor(bundle, path);

		imageRegistry.put(STEP_PERFORM_IMAGE, imageDescriptor);

		path = _ICONS_PATH.append("failed_status.gif");

		imageDescriptor = _createImageDescriptor(bundle, path);

		imageRegistry.put(STEP_FAILED_OVERLAY_IMAGE, imageDescriptor);

		path = _ICONS_PATH.append("complete_status.gif");

		imageDescriptor = _createImageDescriptor(bundle, path);

		imageRegistry.put(STEP_COMPLETE_OVERLAY_IMAGE, imageDescriptor);

		path = _ICONS_PATH.append("skip_status.gif");

		imageDescriptor = _createImageDescriptor(bundle, path);

		imageRegistry.put(STEP_SKIP_OVERLAY_IMAGE, imageDescriptor);

		path = _ICONS_PATH.append("complete_task.gif");

		imageDescriptor = _createImageDescriptor(bundle, path);

		imageRegistry.put(STEP_COMPLETE_IMAGE, imageDescriptor);

		path = _ICONS_PATH.append("skip_action.gif");

		imageDescriptor = _createImageDescriptor(bundle, path);

		imageRegistry.put(STEP_SKIP_IMAGE, imageDescriptor);

		path = _ICONS_PATH.append("task_restart.gif");

		imageDescriptor = _createImageDescriptor(bundle, path);

		imageRegistry.put(STEP_RESTART_IMAGE, imageDescriptor);

		path = _ICONS_PATH.append("information.gif");

		imageDescriptor = _createImageDescriptor(bundle, path);

		imageRegistry.put(NO_STEPS_IMAGE, imageDescriptor);

		path = _ICONS_PATH.append("category_code.png");

		imageDescriptor = _createImageDescriptor(bundle, path);

		imageRegistry.put(CATEGORY_CODE_IMAGE, imageDescriptor);

		path = _ICONS_PATH.append("category_config.png");

		imageDescriptor = _createImageDescriptor(bundle, path);

		imageRegistry.put(CATEGORY_CONFIG_IMAGE, imageDescriptor);

		path = _ICONS_PATH.append("category_database.png");

		imageDescriptor = _createImageDescriptor(bundle, path);

		imageRegistry.put(CATEGORY_DATABASE_IMAGE, imageDescriptor);

		path = _ICONS_PATH.append("preview.gif");

		imageDescriptor = _createImageDescriptor(bundle, path);

		imageRegistry.put(STEP_PERVIEW_IMAGE, imageDescriptor);

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

	private static UpgradePlanUIPlugin _plugin;

}