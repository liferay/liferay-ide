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
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.IProgressService;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plugin life cycle
 *
 * @author Gregory Amerson
 * @author Lovett Li
 */
public class ProjectUI extends AbstractUIPlugin {

	// Shared images

	public static final String CHECKED_IMAGE_ID = "checked.image";

	public static final String LAST_SDK_IMPORT_LOCATION_PREF = "last.sdk.import.location";

	public static final String MIGRATION_TASKS_IMAGE_ID = "migration.tasks.image";

	public static final String MODULE_DEPENDENCY_IAMGE_ID = "module.dependency.image";

	public static final String PLUGIN_ID = "com.liferay.ide.project.ui";

	public static final String PROPERTIES_IMAGE_ID = "properties";

	public static final String UNCHECKED_IMAGE_ID = "unchecked.image";

	// The shared instance

	public static final String WAR_IMAGE_ID = "war.image";

	// The plugin ID

	public static IStatus createErrorStatus(String msg) {
		return createErrorStatus(msg, null);
	}

	public static IStatus createErrorStatus(String msg, Exception e) {
		return new Status(IStatus.ERROR, PLUGIN_ID, msg, e);
	}

	public static IStatus createInfoStatus(String msg) {
		return new Status(IStatus.INFO, PLUGIN_ID, msg, null);
	}

	public static IWorkbenchWindow getActiveWindow() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static ProjectUI getDefault() {
		return _plugin;
	}

	public static Bundle getPluginBundle() {
		return _plugin.getBundle();
	}

	public static ImageRegistry getPluginImageRegistry() {
		return _plugin.getImageRegistry();
	}

	public static ISharedImages getPluginSharedImages() {
		return PlatformUI.getWorkbench().getSharedImages();
	}

	public static IPath getPluginStateLocation() {
		return _plugin.getStateLocation();
	}

	public static IProgressService getProgressService() {
		return PlatformUI.getWorkbench().getProgressService();
	}

	public static void logError(Exception e) {
		_plugin.getLog().log(createErrorStatus(e.getMessage(), e));
	}

	public static void logError(String msg, Exception e) {
		_plugin.getLog().log(createErrorStatus(msg, e));
	}

	public static void logInfo(String msg) {
		_plugin.getLog().log(createInfoStatus(msg));
	}

	// private static IConfigurationElement[] pluginWizardFragmentElements;

	// public static IPluginWizardFragment getPluginWizardFragment(String
	// pluginFacetId) {
	// if (CoreUtil.isNullOrEmpty(pluginFacetId)) {
	// return null;
	// }

	//

	// IConfigurationElement[] fragmentElements =
	// getPluginWizardFragmentsElements();

	//

	// for (IConfigurationElement fragmentElement : fragmentElements) {
	// if (pluginFacetId.equals(fragmentElement.getAttribute("facetId"))) {
	// try {
	// Object o = fragmentElement.createExecutableExtension("class");

	//

	// if (o instanceof IPluginWizardFragment) {
	// IPluginWizardFragment fragment = (IPluginWizardFragment) o;
	// fragment.setFragment(true);
	// return fragment;
	// }
	// }
	// catch (CoreException e) {
	// ProjectUIPlugin.logError("Could not load plugin wizard fragment for " +
	// pluginFacetId, e);
	// }
	// }
	// }

	//

	// return null;
	// }

	// public static IConfigurationElement[] getPluginWizardFragmentsElements() {
	// if (pluginWizardFragmentElements == null) {
	// pluginWizardFragmentElements =
	// Platform.getExtensionRegistry().getConfigurationElementsFor(IPluginWizardFragment.ID);
	// }

	//

	// return pluginWizardFragmentElements;
	// }

	/**
	 * The constructor
	 */
	public ProjectUI() {
	}

	public Image getImage(String imageName) {
		Image image = ImageDescriptor.createFromURL(getBundle().getEntry("icons/e16/" + imageName)).createImage();

		return image;
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

		URL dependencyPicUrl = FileLocator.find(bundle, new Path("icons/e16/new_module_dependency.png"), null);

		registry.put(MODULE_DEPENDENCY_IAMGE_ID, ImageDescriptor.createFromURL(dependencyPicUrl));
	}

	private static ProjectUI _plugin;

}