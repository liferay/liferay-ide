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

package com.liferay.ide.gradle.ui;

import com.liferay.ide.core.Event;
import com.liferay.ide.core.EventListener;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.ListenerRegistry;
import com.liferay.ide.core.workspace.ProjectCreatedEvent;
import com.liferay.ide.core.workspace.ProjectDeletedEvent;
import com.liferay.ide.ui.util.UIUtil;

import java.net.URL;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class LiferayGradleUI extends AbstractUIPlugin {

	// The plug-in ID

	public static final String IMG_WIZ_RUNTIME = "imgWizRuntime";

	public static final String LIFERAY_STANDALONE_WATCH_JOB_FAMILY = "standalone";

	public static final String LIFERAY_WATCH_DECORATOR_ID = "com.liferay.ide.gradle.ui.liferayWatchDecorator";

	public static final String PLUGIN_ID = "com.liferay.ide.gradle.ui";

	public static IStatus createErrorStatus(String msg) {
		return createErrorStatus(msg, null);
	}

	public static IStatus createErrorStatus(String msg, Exception e) {
		return new Status(IStatus.ERROR, PLUGIN_ID, msg, e);
	}

	// The shared instance

	public static IStatus createInfoStatus(String msg) {
		return new Status(IStatus.INFO, PLUGIN_ID, msg, null);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static LiferayGradleUI getDefault() {
		return _plugin;
	}

	public static Bundle getDefaultBundle() {
		return _plugin.getBundle();
	}

	public static ImageDescriptor getImageDescriptor(String key) {
		try {
			getDefault().getImageRegistry();

			return (ImageDescriptor)getDefault().imageDescriptors.get(key);
		}
		catch (Exception e) {
			return null;
		}
	}

	public static void logError(String msg, Throwable t) {
		ILog log = getDefault().getLog();

		log.log(new Status(IStatus.ERROR, PLUGIN_ID, msg, t));
	}

	public static void logError(Throwable t) {
		logError(t.getMessage(), t);
	}

	/**
	 * The constructor
	 */
	public LiferayGradleUI() {
	}

	/**
	 * (non-Javadoc)
	 *
	 * @see AbstractUIPlugin#start(BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);

		_plugin = this;

		ConsolePlugin plugin = ConsolePlugin.getDefault();

		IConsoleManager consoleManager = plugin.getConsoleManager();

		consoleManager.addConsoleListener(new RemoveOldWatchConsoleListener(consoleManager));

		_projectListener = new EventListener() {

			@Override
			public void onEvent(Event event) {
				if (event instanceof ProjectCreatedEvent || event instanceof ProjectDeletedEvent) {
					UIUtil.async(
						new Runnable() {

							@Override
							public void run() {
								IViewPart viewPart = UIUtil.findView("org.eclipse.wst.server.ui.ServersView");

								if ((viewPart != null) && (viewPart instanceof CommonNavigator)) {
									CommonNavigator commandNavigator = (CommonNavigator)viewPart;

									CommonViewer commonViewer = commandNavigator.getCommonViewer();

									commonViewer.refresh();
								}
							}

						});
				}
			}

		};

		ListenerRegistry listenerRegistry = LiferayCore.listenerRegistry();

		listenerRegistry.addEventListener(_projectListener);
	}

	/**
	 * (non-Javadoc)
	 *
	 * @see AbstractUIPlugin#stop(BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		_plugin = null;

		ListenerRegistry listenerRegistry = LiferayCore.listenerRegistry();

		listenerRegistry.removeEventListener(_projectListener);

		super.stop(context);
	}

	protected ImageRegistry createImageRegistry() {
		ImageRegistry registry = new ImageRegistry();

		_registerImage(registry, IMG_WIZ_RUNTIME, "wizban/liferay_wiz.png");

		return registry;
	}

	protected Map<String, ImageDescriptor> imageDescriptors = new HashMap<>();

	private void _registerImage(ImageRegistry registry, String key, String partialURL) {
		if (_iconBaseUrl == null) {
			String pathSuffix = "icons/";

			Bundle bundle = _plugin.getBundle();

			_iconBaseUrl = bundle.getEntry(pathSuffix);
		}

		try {
			ImageDescriptor id = ImageDescriptor.createFromURL(new URL(_iconBaseUrl, partialURL));

			registry.put(key, id);

			imageDescriptors.put(key, id);
		}
		catch (Exception e) {
			ILog log = _plugin.getLog();

			log.log(new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage()));
		}
	}

	private static URL _iconBaseUrl;
	private static LiferayGradleUI _plugin;

	private EventListener _projectListener;

}