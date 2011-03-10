/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.eclipse.project.core;

import com.liferay.ide.eclipse.core.CorePlugin;
import com.liferay.ide.eclipse.core.util.CoreUtil;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plugin life cycle
 * 
 * @author Greg Amerson
 */
public class ProjectCorePlugin extends CorePlugin {

	// The plugin ID
	public static final String PLUGIN_ID = "com.liferay.ide.eclipse.project.core";

	public static final String USE_PROJECT_SETTINGS = "use-project-settings";

	// The shared instance
	private static ProjectCorePlugin plugin;

	private static PluginPackageResourceListener pluginPackageResourceListener;

	private static IPortletFramework[] portletFrameworks;

	private static IProjectDefinition[] projectDefinitions = null;

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static ProjectCorePlugin getDefault() {
		return plugin;
	}

	public static IPortletFramework[] getPortletFrameworks() {
		if (portletFrameworks == null) {
			IConfigurationElement[] elements =
				Platform.getExtensionRegistry().getConfigurationElementsFor(IPortletFramework.EXTENSION_ID);

			if (!CoreUtil.isNullOrEmpty(elements)) {
				List<IPortletFramework> frameworks = new ArrayList<IPortletFramework>();

				for (IConfigurationElement element : elements) {
					String id = element.getAttribute(IPortletFramework.ID);
					String shortName = element.getAttribute(IPortletFramework.SHORT_NAME);
					String displayName = element.getAttribute(IPortletFramework.DISPLAY_NAME);
					String description = element.getAttribute(IPortletFramework.DESCRIPTION);
					String requiredSDKVersion = element.getAttribute(IPortletFramework.REQUIRED_SDK_VERSION);
					boolean isDefault = Boolean.parseBoolean(element.getAttribute(IPortletFramework.DEFAULT));

					URL helpUrl = null;

					try {
						helpUrl = new URL(element.getAttribute(IPortletFramework.HELP_URL));
					}
					catch (Exception e1) {
					}

					try {
						AbstractPortletFramework framework =
							(AbstractPortletFramework) element.createExecutableExtension("class");
						framework.setId(id);
						framework.setShortName(shortName);
						framework.setDisplayName(displayName);
						framework.setDescription(description);
						framework.setRequiredSDKVersion(requiredSDKVersion);
						framework.setHelpUrl(helpUrl);
						framework.setDefault(isDefault);
						framework.setBundleId(element.getContributor().getName());

						frameworks.add(framework);
					}
					catch (Exception e) {
						logError("Could not create portlet framework.", e);
					}
				}

				portletFrameworks = frameworks.toArray(new IPortletFramework[0]);

				// sort the array so that the default template is first
				Arrays.sort(portletFrameworks, 0, portletFrameworks.length, new Comparator<IPortletFramework>() {

					public int compare(IPortletFramework o1, IPortletFramework o2) {
						if (o1.isDefault() && (!o2.isDefault())) {
							return -1;
						}
						else if ((!o1.isDefault()) && o2.isDefault()) {
							return 1;
						}

						return o1.getShortName().compareTo(o2.getShortName());
					}

				});
			}
		}

		return portletFrameworks;
	}

	public static IProjectDefinition getProjectDefinition(IProjectFacet projectFacet) {
		IProjectDefinition[] definitions = getProjectDefinitions();

		if (definitions != null) {
			for (IProjectDefinition def : definitions) {
				if (def != null && def.getFacet() != null && def.getFacet().equals(projectFacet)) {
					return def;
				}
			}
		}

		return null;
	}

	public static IProjectDefinition getProjectDefinition(String type) {
		IProjectDefinition[] defs = getProjectDefinitions();

		if (defs != null && defs.length > 0) {
			for (IProjectDefinition def : defs) {
				if (def != null && def.getFacetId().equals(type)) {
					return def;
				}
			}
		}

		return null;
	}

	public static IProjectDefinition[] getProjectDefinitions() {
		if (projectDefinitions == null) {
			IConfigurationElement[] elements =
				Platform.getExtensionRegistry().getConfigurationElementsFor(IProjectDefinition.ID);

			try {
				List<IProjectDefinition> definitions = new ArrayList<IProjectDefinition>();

				for (IConfigurationElement element : elements) {
					final Object o = element.createExecutableExtension("class");

					if (o instanceof AbstractProjectDefinition) {
						AbstractProjectDefinition projectDefinition = (AbstractProjectDefinition) o;
						projectDefinition.setFacetId(element.getAttribute("facetId"));
						projectDefinition.setShortName(element.getAttribute("shortName"));
						projectDefinition.setDisplayName(element.getAttribute("displayName"));
						projectDefinition.setFacetedProjectTemplateId(element.getAttribute("facetedProjectTemplateId"));

						int menuIndex = Integer.MAX_VALUE;

						try {
							String intVal = element.getAttribute("menuIndex");

							if (intVal != null) {
								menuIndex = Integer.parseInt(intVal);
							}
						}
						catch (Exception e) {
							ProjectCorePlugin.logError("Error reading project definition.", e);
						}

						projectDefinition.setMenuIndex(menuIndex);

						definitions.add(projectDefinition);
					}
				}

				projectDefinitions = definitions.toArray(new IProjectDefinition[0]);
			}
			catch (Exception e) {
				logError(e);
			}
		}

		return projectDefinitions;
	}

	public static void logError(String msg, Exception e) {
		getDefault().getLog().log(createErrorStatus(msg, e));
	}

	/**
	 * The constructor
	 */
	public ProjectCorePlugin() {
		pluginPackageResourceListener = new PluginPackageResourceListener();
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

		ResourcesPlugin.getWorkspace().addResourceChangeListener(
			pluginPackageResourceListener, IResourceChangeEvent.POST_CHANGE);
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

		if (pluginPackageResourceListener != null) {
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(pluginPackageResourceListener);
		}
	}


}
