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

package com.liferay.ide.eclipse.server.tomcat.core;

import com.liferay.ide.eclipse.core.CorePlugin;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.IStartup;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 * 
 * @author Greg Amerson
 */
public class PortalTomcatPlugin extends CorePlugin implements IStartup {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.liferay.ide.eclipse.server.tomcat.core";

	// The shared instance
	private static PortalTomcatPlugin plugin;

	private static PortalSourcePartListener portalSourcePartListener;

	public static IStatus createErrorStatus(String msg) {
		return createErrorStatus(PLUGIN_ID, msg);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static PortalTomcatPlugin getDefault() {
		return plugin;
	}

	public static void logError(String msg, Exception e) {
		getDefault().getLog().log(createErrorStatus(PLUGIN_ID, msg, e));
	}

	/**
	 * The constructor
	 */
	public PortalTomcatPlugin() {
	}

	public void earlyStartup() {
		// no-op
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

		// portalSourcePartListener = new PortalSourcePartListener();
		//
		// PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
		//
		// public void run() {
		// IWorkbenchWindow workbenchWindow =
		// PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		//
		// if (workbenchWindow != null) {
		// workbenchWindow.getPartService().addPartListener((IPartListener2)
		// portalSourcePartListener);
		// }
		// }
		// });
		//
		// PlatformUI.getWorkbench().addWindowListener(new IWindowListener() {
		//
		// public void windowActivated(IWorkbenchWindow window) {
		// }
		//
		// public void windowClosed(IWorkbenchWindow window) {
		// }
		//
		// public void windowDeactivated(IWorkbenchWindow window) {
		// }
		//
		// public void windowOpened(IWorkbenchWindow window) {
		// window.getPartService().addPartListener((IPartListener2)
		// portalSourcePartListener);
		// }
		// });
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
}
