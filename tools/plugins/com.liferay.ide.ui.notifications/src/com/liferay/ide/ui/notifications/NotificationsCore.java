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

package com.liferay.ide.ui.notifications;

import java.util.Collections;
import java.util.Date;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.mylyn.commons.notifications.core.AbstractNotification;
import org.eclipse.mylyn.commons.notifications.ui.AbstractUiNotification;
import org.eclipse.mylyn.commons.notifications.ui.NotificationsUi;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;

/**
 * @author Andy Wu
 */
@SuppressWarnings("restriction")
public class NotificationsCore extends AbstractUIPlugin implements IStartup {

	public static final String PLUGIN_ID = "com.liferay.ide.ui.notifications";

	public static final String SHOULD_SHOW_NOTIFICATIONS = "SHOULD_SHOW_NOTIFICATIONS";

	public static NotificationsCore getDefault() {
		return _plugin;
	}

	public static void logError(Exception e) {
		NotificationsCore plugin = getDefault();

		plugin.getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage(), e));
	}

	@Override
	public void earlyStartup() {
		if (_shouldShowNotifications() && !_matchRequiredJavaVersion()) {
			NotificationsUi.getService().notify(Collections.singletonList(_createJava8RequiredNotification()));
		}
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);

		_plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		_plugin = null;
		super.stop(context);
	}

	private AbstractNotification _createJava8RequiredNotification() {
		Date date = new Date();

		return new AbstractUiNotification("com.liferay.ide.ui.notifications.java8required") {

			@SuppressWarnings({"rawtypes", "unchecked"})
			public Object getAdapter(Class adapter) {
				return null;
			}

			@Override
			public Date getDate() {
				return date;
			}

			@Override
			public String getDescription() {
				return "This Eclipse instance is running on java " + _getCurrentJavaVersion() + "\n" +
					"Liferay IDE needs at least Java 1.8 to run, please launch Eclipse with 1.8 and try again.";
			}

			@Override
			public String getLabel() {
				return "Java 8 Required";
			}

			@Override
			public Image getNotificationImage() {
				return null;
			}

			@Override
			public Image getNotificationKindImage() {
				return null;
			}

			@Override
			public void open() {
			}

		};
	}

	private String _getCurrentJavaVersion() {
		return System.getProperty("java.specification.version");
	}

	private boolean _matchRequiredJavaVersion() {
		String javaVersion = _getCurrentJavaVersion();

		Version currentVersion = new Version(javaVersion);

		Version requiredVersion = new Version("1.8");

		if (currentVersion.compareTo(requiredVersion) < 0) {
			return false;
		}
		else {
			return true;
		}
	}

	private boolean _shouldShowNotifications() {
		IScopeContext[] scopes = {ConfigurationScope.INSTANCE, InstanceScope.INSTANCE};

		return Platform.getPreferencesService().getBoolean(PLUGIN_ID, SHOULD_SHOW_NOTIFICATIONS, true, scopes);
	}

	private static NotificationsCore _plugin;

}