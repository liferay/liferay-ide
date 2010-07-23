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

package com.liferay.ide.eclipse.project.ui;

import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.project.ui.wizard.IPluginWizardFragment;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 * 
 * @author Greg Amerson
 */
public class ProjectUIPlugin extends AbstractUIPlugin {

	public static final String LAST_SDK_IMPORT_LOCATION_PREF = "last.sdk.import.location";

	// The plug-in ID
	public static final String PLUGIN_ID = "com.liferay.ide.eclipse.project.ui";

	// The shared instance
	private static ProjectUIPlugin plugin;

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static ProjectUIPlugin getDefault() {
		return plugin;
	}

	public static void logError(Exception e) {
		getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage(), e));
	}

	public static void logError(String msg, Exception e) {
		getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, msg, e));
	}

	/**
	 * The constructor
	 */
	public ProjectUIPlugin() {
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


	private static IConfigurationElement[] pluginWizardFragmentElements;

	public static IPluginWizardFragment getPluginWizardFragment(String pluginFacetId) {
		if (CoreUtil.isNullOrEmpty(pluginFacetId)) {
			return null;
		}

		IConfigurationElement[] fragmentElements = getPluginWizardFragmentsElements();

		for (IConfigurationElement fragmentElement : fragmentElements) {
			if (pluginFacetId.equals(fragmentElement.getAttribute("facetId"))) {
				try {
					Object o = fragmentElement.createExecutableExtension("class");

					if (o instanceof IPluginWizardFragment) {
						IPluginWizardFragment fragment = (IPluginWizardFragment) o;
						fragment.setFragment(true);
						return fragment;
					}
				}
				catch (CoreException e) {
					ProjectUIPlugin.logError("Could not load plugin wizard fragment for " + pluginFacetId, e);
				}
			}
		}

		return null;
	}

	public static IConfigurationElement[] getPluginWizardFragmentsElements() {
		if (pluginWizardFragmentElements == null) {
			pluginWizardFragmentElements =
				Platform.getExtensionRegistry().getConfigurationElementsFor(IPluginWizardFragment.ID);
		}

		return pluginWizardFragmentElements;
	}

}
