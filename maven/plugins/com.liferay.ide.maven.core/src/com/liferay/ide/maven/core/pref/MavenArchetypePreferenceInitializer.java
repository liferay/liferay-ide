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

package com.liferay.ide.maven.core.pref;

import com.liferay.ide.maven.core.LiferayMavenCore;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

/**
 * @author Simon Jiang
 */
public class MavenArchetypePreferenceInitializer extends AbstractPreferenceInitializer {

	public MavenArchetypePreferenceInitializer() {
	}

	@Override
	public void initializeDefaultPreferences() {
		IEclipsePreferences defaultPrefs = LiferayMavenCore.getDefaultPrefs();

		defaultPrefs.put(
			LiferayMavenCore.PREF_ARCHETYPE_GAV_EXT, "com.liferay.maven.archetypes:liferay-ext-archetype:6.2.5");
		defaultPrefs.put(
			LiferayMavenCore.PREF_ARCHETYPE_GAV_LIFERAY_FACES_ALLOY,
			"com.liferay.maven.archetypes:liferay-portlet-liferay-faces-alloy-archetype:6.2.5");
		defaultPrefs.put(
			LiferayMavenCore.PREF_ARCHETYPE_GAV_HOOK, "com.liferay.maven.archetypes:liferay-hook-archetype:6.2.5");
		defaultPrefs.put(
			LiferayMavenCore.PREF_ARCHETYPE_GAV_ICEFACES,
			"com.liferay.maven.archetypes:liferay-portlet-icefaces-archetype:6.2.5");
		defaultPrefs.put(
			LiferayMavenCore.PREF_ARCHETYPE_GAV_JSF,
			"com.liferay.maven.archetypes:liferay-portlet-jsf-archetype:6.2.5");
		defaultPrefs.put(
			LiferayMavenCore.PREF_ARCHETYPE_GAV_LAYOUTTPL,
			"com.liferay.maven.archetypes:liferay-layouttpl-archetype:6.2.5");
		defaultPrefs.put(
			LiferayMavenCore.PREF_ARCHETYPE_GAV_MVC, "com.liferay.maven.archetypes:liferay-portlet-archetype:6.2.5");
		defaultPrefs.put(
			LiferayMavenCore.PREF_ARCHETYPE_GAV_PRIMEFACES,
			"com.liferay.maven.archetypes:liferay-portlet-primefaces-archetype:6.2.5");
		defaultPrefs.put(
			LiferayMavenCore.PREF_ARCHETYPE_GAV_RICHFACES,
			"com.liferay.maven.archetypes:liferay-portlet-richfaces-archetype:6.2.5");
		defaultPrefs.put(
			LiferayMavenCore.PREF_ARCHETYPE_GAV_SPRING_MVC,
			"com.liferay.maven.archetypes:liferay-portlet-spring-mvc-archetype:6.2.5");
		defaultPrefs.put(
			LiferayMavenCore.PREF_ARCHETYPE_GAV_SERVICEBUILDER,
			"com.liferay.maven.archetypes:liferay-servicebuilder-archetype:6.2.5");
		defaultPrefs.put(
			LiferayMavenCore.PREF_ARCHETYPE_GAV_THEME, "com.liferay.maven.archetypes:liferay-theme-archetype:6.2.5");
		defaultPrefs.put(
			LiferayMavenCore.PREF_ARCHETYPE_GAV_VAADIN, "com.vaadin:vaadin-archetype-liferay-portlet:7.4.0.alpha2");
		defaultPrefs.put(
			LiferayMavenCore.PREF_ARCHETYPE_GAV_WEB, "com.liferay.maven.archetypes:liferay-web-archetype:6.2.5");
	}

}