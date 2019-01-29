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

package com.liferay.ide.project.ui;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plugin life cycle
 *
 * @author Gregory Amerson
 * @author Lovett Li
 */
public class ProjectUI extends AbstractUIPlugin {

	public static final String CHECKED_IMAGE_ID = "checked.image";

	public static final String LAST_SDK_IMPORT_LOCATION_PREF = "last.sdk.import.location";

	public static final String LIFERAY_LOGO_IMAGE_ID = "liferay.logo.image";

	public static final String MIGRATION_TASKS_IMAGE_ID = "migration.tasks.image";

	public static final String PLUGIN_ID = "com.liferay.ide.project.ui";

	public static final String PROPERTIES_IMAGE_ID = "properties";

	public static final String UNCHECKED_IMAGE_ID = "unchecked.image";

	public static final String WAR_IMAGE_ID = "war.image";

	public static IStatus createErrorStatus(String msg) {
		return createErrorStatus(msg, null);
	}

	public static IStatus createErrorStatus(String msg, Exception e) {
		return new Status(IStatus.ERROR, PLUGIN_ID, msg, e);
	}

	public static IStatus createInfoStatus(String msg) {
		return new Status(IStatus.INFO, PLUGIN_ID, msg, null);
	}

	public static ProjectUI getDefault() {
		return _plugin;
	}

	public static Bundle getPluginBundle() {
		return _plugin.getBundle();
	}

	public static ImageRegistry getPluginImageRegistry() {
		return _plugin.getImageRegistry();
	}

	public static IPath getPluginStateLocation() {
		return _plugin.getStateLocation();
	}

	public static void logError(Exception e) {
		ILog log = _plugin.getLog();

		log.log(createErrorStatus(e.getMessage(), e));
	}

	public static void logError(String msg, Exception e) {
		ILog log = _plugin.getLog();

		log.log(createErrorStatus(msg, e));
	}

	public static void logInfo(String msg) {
		ILog log = _plugin.getLog();

		log.log(createInfoStatus(msg));
	}

	public ProjectUI() {
	}

	public Image getImage(String imageName) {
		ImageDescriptor descriptor = ImageDescriptor.createFromURL(getBundle().getEntry("icons/e16/" + imageName));

		return descriptor.createImage();
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
	protected void initializeImageRegistry(ImageRegistry registry) {
		Bundle bundle = Platform.getBundle(PLUGIN_ID);

		IPath checked = new Path("icons/e16/checked.png");

		URL checkedurl = FileLocator.find(bundle, checked, null);

		ImageDescriptor checkeddesc = ImageDescriptor.createFromURL(checkedurl);

		registry.put(CHECKED_IMAGE_ID, checkeddesc);

		IPath unchecked = new Path("icons/e16/unchecked.png");

		URL uncheckedurl = FileLocator.find(bundle, unchecked, null);

		ImageDescriptor uncheckeddesc = ImageDescriptor.createFromURL(uncheckedurl);

		registry.put(UNCHECKED_IMAGE_ID, uncheckeddesc);

		IPath migrationtasks = new Path("icons/e16/migration-tasks.png");

		URL migrationtasksurl = FileLocator.find(bundle, migrationtasks, null);

		ImageDescriptor migrationtasksdesc = ImageDescriptor.createFromURL(migrationtasksurl);

		registry.put(MIGRATION_TASKS_IMAGE_ID, migrationtasksdesc);

		URL warPicUrl = FileLocator.find(bundle, new Path("icons/e16/war.gif"), null);

		registry.put(WAR_IMAGE_ID, ImageDescriptor.createFromURL(warPicUrl));

		URL propertiesPicUrl = FileLocator.find(bundle, new Path("icons/e16/properties.png"), null);

		registry.put(PROPERTIES_IMAGE_ID, ImageDescriptor.createFromURL(propertiesPicUrl));

		URL liferayLogoPicUrl = FileLocator.find(bundle, new Path("icons/e16/liferay.png"), null);

		registry.put(LIFERAY_LOGO_IMAGE_ID, ImageDescriptor.createFromURL(liferayLogoPicUrl));
	}

	private static ProjectUI _plugin;

}