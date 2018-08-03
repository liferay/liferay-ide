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

package com.liferay.ide.maven.core;

import com.liferay.ide.core.util.MultiStatusBuilder;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

import org.osgi.framework.BundleContext;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 * @author Eric Min
 * @author Terry Jia
 */
public class LiferayMavenCore extends Plugin {

	// The shared instance

	public static final String PLUGIN_ID = "com.liferay.ide.maven.core";

	// set maven project context root with suffix

	public static final String PREF_ADD_MAVEN_PLUGIN_SUFFIX = "add-maven-plugin-suffix";

	public static final String PREF_ARCHETYPE_GAV_EXT = "archetype-gav-ext";

	public static final String PREF_ARCHETYPE_GAV_HOOK = "archetype-gav-hook";

	public static final String PREF_ARCHETYPE_GAV_ICEFACES = "archetype-gav-icefaces";

	public static final String PREF_ARCHETYPE_GAV_JSF = "archetype-gav-jsf";

	public static final String PREF_ARCHETYPE_GAV_LAYOUTTPL = "archetype-gav-layouttpl";

	public static final String PREF_ARCHETYPE_GAV_LIFERAY_FACES_ALLOY = "archetype-gav-liferay-faces-alloy";

	public static final String PREF_ARCHETYPE_GAV_MVC = "archetype-gav-mvc";

	public static final String PREF_ARCHETYPE_GAV_PRIMEFACES = "archetype-gav-primefaces";

	public static final String PREF_ARCHETYPE_GAV_RICHFACES = "archetype-gav-richfaces";

	public static final String PREF_ARCHETYPE_GAV_SERVICEBUILDER = "archetype-gav-servicebuilder";

	public static final String PREF_ARCHETYPE_GAV_SPRING_MVC = "archetype-gav-spring-mvc";

	public static final String PREF_ARCHETYPE_GAV_THEME = "archetype-gav-theme";

	public static final String PREF_ARCHETYPE_GAV_VAADIN = "archetype-gav-vaadin";

	public static final String PREF_ARCHETYPE_GAV_WEB = "archetype-gav-web";

	public static final String PREF_DISABLE_CUSTOM_JSP_VALIDATION = "disable-custom-jsp-validation";

	// The key of disable customJspValidation checking

	public static Status createErrorStatus(String msg) {
		return new Status(IStatus.ERROR, PLUGIN_ID, msg, null);
	}

	public static Status createErrorStatus(String msg, Throwable t) {
		return new Status(IStatus.ERROR, PLUGIN_ID, msg, t);
	}

	public static IStatus createErrorStatus(Throwable throwable) {
		return createErrorStatus(throwable.getMessage(), throwable);
	}

	public static IStatus createMultiStatus(int severity, IStatus[] statuses) {
		return new MultiStatus(
			LiferayMavenCore.PLUGIN_ID, severity, statuses, statuses[0].getMessage(), statuses[0].getException());
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static LiferayMavenCore getDefault() {
		return _plugin;
	}

	public static IEclipsePreferences getDefaultPrefs() {
		return DefaultScope.INSTANCE.getNode(PLUGIN_ID);
	}

	public static boolean getPreferenceBoolean(String key) {
		IPreferencesService preferencesService = Platform.getPreferencesService();

		return preferencesService.getBoolean(PLUGIN_ID, key, false, _scopes);
	}

	public static String getPreferenceString(final String key, final String defaultValue) {
		IPreferencesService preferencesService = Platform.getPreferencesService();

		return preferencesService.getString(PLUGIN_ID, key, defaultValue, _scopes);
	}

	public static void log(IStatus status) {
		ILog iLog = getDefault().getLog();

		iLog.log(status);
	}

	public static void logError(String msg, Throwable t) {
		log(createErrorStatus(msg, t));
	}

	public static void logError(Throwable t) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, t.getMessage(), t));
	}

	public static MultiStatusBuilder newMultiStatus() {
		return new MultiStatusBuilder(PLUGIN_ID);
	}

	public LiferayMavenCore() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);

		_plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		_plugin = null;
		super.stop(context);
	}

	// The plug-in ID

	private static LiferayMavenCore _plugin;
	private static final IScopeContext[] _scopes = {InstanceScope.INSTANCE, DefaultScope.INSTANCE};

}