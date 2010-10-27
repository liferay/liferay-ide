package com.liferay.ide.eclipse.portlet.vaadin.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class VaadinCore extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.liferay.ide.eclipse.portlet.vaadin.core"; //$NON-NLS-1$

	// The shared instance
	private static VaadinCore plugin;
	
	public static IStatus createErrorStatus(Exception e) {
		return new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage(), e);
	}

	public static IStatus createErrorStatus(String message) {
		return new Status(IStatus.ERROR, PLUGIN_ID, message);
	}

	public static IStatus createWarningStatus(String message) {
		return new Status(IStatus.WARNING, PLUGIN_ID, message);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static VaadinCore getDefault() {
		return plugin;
	}

	public static void logError(Exception e) {
		getDefault().getLog().log(createErrorStatus(e));
	}

	/**
	 * The constructor
	 */
	public VaadinCore() {
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

}
