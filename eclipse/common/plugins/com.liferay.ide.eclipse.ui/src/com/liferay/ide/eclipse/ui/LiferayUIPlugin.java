/*******************************************************************************
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
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
 *
 *******************************************************************************/

package com.liferay.ide.eclipse.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 * 
 * @author Greg Amerson
 */
public class LiferayUIPlugin extends AbstractUIPlugin {

	public static final String IMG_LIFERAY_ICON_SMALL = "IMG_LIFERAY_ICON_SMALL";

	// The plug-in ID
	public static final String PLUGIN_ID = "com.liferay.ide.eclipse.ui";

	// The shared instance
	private static LiferayUIPlugin plugin;

	public static IWorkbenchPage getActivePage() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static LiferayUIPlugin getDefault() {
		return plugin;
	}

	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	public static void logError(Exception e) {
		getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage(), e));
	}

	protected TextFileDocumentProvider fTextFileDocumentProvider;

	protected Map<String, ImageDescriptor> imageDescriptors = new HashMap<String, ImageDescriptor>();

	/**
	 * The constructor
	 */
	public LiferayUIPlugin() {
	}

	public Image getImage(String key) {
		return getImageRegistry().get(key);
	}

	public ImageDescriptor getImageDescriptor(String key) {
		getImageRegistry();

		return imageDescriptors.get(key);
	}

	public synchronized IDocumentProvider getTextFileDocumentProvider() {
		if (fTextFileDocumentProvider == null) {
			fTextFileDocumentProvider = new TextFileDocumentProvider();
		}

		return fTextFileDocumentProvider;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context)
		throws Exception {

		super.start(context);

		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context)
		throws Exception {

		plugin = null;

		super.stop(context);
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		registerImage(reg, IMG_LIFERAY_ICON_SMALL, "/icons/e16/liferay.png");
	}

	protected void registerImage(ImageRegistry registry, String key, String path) {
		try {
			ImageDescriptor id = ImageDescriptor.createFromURL(getBundle().getEntry(path));

			imageDescriptors.put(key, id);

			registry.put(key, id);
		}
		catch (Exception e) {
		}
	}

	// public synchronized IDocumentProvider
	// getPluginPropertiesFileDocumentProvider() {
	// if (fPluginPropertiesFileDocumentProvider == null) {
	// fPluginPropertiesFileDocumentProvider = new
	// PluginPropertiesFileDocumentProvider();
	// }
	// return fPluginPropertiesFileDocumentProvider;
	// }
}
