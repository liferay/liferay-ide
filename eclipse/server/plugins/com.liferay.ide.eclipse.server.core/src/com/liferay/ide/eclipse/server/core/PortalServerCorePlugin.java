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

package com.liferay.ide.eclipse.server.core;

import com.liferay.ide.eclipse.core.CorePlugin;
import com.liferay.ide.eclipse.core.util.CoreUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 * 
 * @author Greg Amerson
 */
public class PortalServerCorePlugin extends CorePlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.liferay.ide.eclipse.server.core";

	// The shared instance
	private static PortalServerCorePlugin plugin;

	private static PluginPackageResourceListener pluginPackageResourceListener;

	// private static HashMap<String, ServerInstallSpec> serverInstallSpecs =
	// null;

	// private PortalBundle[] portalBundles;

	public static IStatus createErrorStatus(String msg) {
		return createErrorStatus(PLUGIN_ID, msg);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static PortalServerCorePlugin getDefault() {
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
	public PortalServerCorePlugin() {
		pluginPackageResourceListener = new PluginPackageResourceListener();
	}

	/*
	 * public static ServerInstallSpec[] readServerInstallSpecs() { if
	 * (serverInstallSpecs == null) { serverInstallSpecs = new HashMap<String,
	 * ServerInstallSpec>(); try { URL url =
	 * getDefault().getBundle().getEntry("server.install.properties"); url =
	 * FileLocator.resolve(url); Properties props = new Properties();
	 * props.load(url.openStream()); String[] bundles =
	 * props.getProperty("bundles").split(","); for (String bundle : bundles) {
	 * ServerInstallSpec spec = new
	 * ServerInstallSpec(props.getProperty(bundle+".id")); spec.setJREPath(new
	 * Path(props.getProperty(bundle+".jre"))); spec.setServerPath(new
	 * Path(props.getProperty(bundle+".server"))); String files =
	 * props.getProperty(bundle+".verifyFiles"); StringTokenizer tokenizer = new
	 * StringTokenizer(files, ","); while (tokenizer.hasMoreTokens()) {
	 * spec.addFile(tokenizer.nextToken()); } spec.setStatus(Status.OK_STATUS);
	 * serverInstallSpecs.put(bundle, spec); } } catch (Exception e) {
	 * getDefault
	 * ().getLog().log(PortalServerUtil.createErrorStatus(e.getMessage())); } }
	 * return serverInstallSpecs.values().toArray(new ServerInstallSpec[0]); }
	 * public PortalBundle[] getPortalBundleExtensions() { if (portalBundles ==
	 * null) { IExtensionPoint portalBundlesExtensions =
	 * Platform.getExtensionRegistry().getExtensionPoint(PLUGIN_ID,
	 * "portalBundles"); ArrayList<PortalBundle> portalBundlesList = new
	 * ArrayList<PortalBundle>(); for (IConfigurationElement portalBundleElement
	 * : portalBundlesExtensions.getConfigurationElements()) { PortalBundle
	 * portalBundle = new PortalBundle();
	 * portalBundle.setId(portalBundleElement.getAttribute("id"));
	 * portalBundle.setRuntimeTypeId
	 * (portalBundleElement.getAttribute("runtimeTypeId")); for
	 * (IConfigurationElement bundledServletContainerElement :
	 * portalBundleElement.getChildren()) { BundledServletContainer
	 * bundledServletContainer = new BundledServletContainer(portalBundle);
	 * bundledServletContainer
	 * .setDisplayName(bundledServletContainerElement.getAttribute
	 * ("displayName"));
	 * bundledServletContainer.setBundledPath(bundledServletContainerElement
	 * .getAttribute("bundledPath"));
	 * bundledServletContainer.setBundledRuntimeTypeId
	 * (bundledServletContainerElement.getAttribute("bundledRuntimeTypeId"));
	 * portalBundle.addBundledServletContainer(bundledServletContainer); }
	 * portalBundlesList.add(portalBundle); } portalBundles =
	 * portalBundlesList.toArray(new PortalBundle[0]); } return portalBundles; }
	 * public PortalBundle getPortalBundleForRuntimeTypeId(String id) { for
	 * (PortalBundle portalBundle : getPortalBundleExtensions()) { if
	 * (portalBundle.getRuntimeTypeId().equals(id)) { return portalBundle; } }
	 * return null; }
	 */

	public IPath getPortalSourcePath(IPath entryPath) {
		IPath portalSourcePath = getStateLocation().append("portal-source").append(entryPath);
		
		portalSourcePath.toFile().mkdirs();
		
		return portalSourcePath;
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

	private static IPluginDeployer[] pluginDeployers = null;

	public static IPluginDeployer getPluginDeployer(String facetId) {
		if (CoreUtil.isNullOrEmpty(facetId)) {
			return null;
		}

		IPluginDeployer retval = null;
		IPluginDeployer[] deployers = getPluginDeployers();

		if (deployers != null && deployers.length > 0) {
			for (IPluginDeployer deployer : deployers) {
				if (deployer != null && facetId.equals(deployer.getFacetId())) {
					retval = deployer;
					break;
				}
			}
		}

		return retval;
	}

	public static IPluginDeployer[] getPluginDeployers() {
		if (pluginDeployers == null) {
			IConfigurationElement[] elements =
				Platform.getExtensionRegistry().getConfigurationElementsFor(IPluginDeployer.ID);

			try {
				List<IPluginDeployer> deployers = new ArrayList<IPluginDeployer>();

				for (IConfigurationElement element : elements) {
					final Object o = element.createExecutableExtension("class");

					if (o instanceof AbstractPluginDeployer) {
						AbstractPluginDeployer pluginDeployer = (AbstractPluginDeployer) o;
						pluginDeployer.setFacetId(element.getAttribute("facetId"));
						deployers.add(pluginDeployer);
					}
				}

				pluginDeployers = deployers.toArray(new IPluginDeployer[0]);
			}
			catch (Exception e) {
				logError("Unable to get plugin deployer extensions", e);
			}
		}

		return pluginDeployers;
	}
}
