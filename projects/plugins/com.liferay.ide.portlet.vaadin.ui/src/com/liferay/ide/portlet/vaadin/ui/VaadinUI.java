package com.liferay.ide.portlet.vaadin.ui;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plugin life cycle
 */
public class VaadinUI extends Plugin {

	// The plugin ID
	public static final String PLUGIN_ID = "com.liferay.ide.vaadin.ui"; //$NON-NLS-1$

	// The shared instance
	private static VaadinUI plugin;
	
	/**
	 * The constructor
	 */
	public VaadinUI() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static VaadinUI getDefault() {
		return plugin;
	}

}
