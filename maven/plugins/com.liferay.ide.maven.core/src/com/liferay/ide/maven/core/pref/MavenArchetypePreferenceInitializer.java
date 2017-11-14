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

		// new project template archetypes

		defaultPrefs.put(
			LiferayMavenCore.PREF_ARCHETYPE_PROJECT_TEMPLATE_ACTIVATOR,
			"com.liferay:com.liferay.project.templates.activator:1.0.3");
		defaultPrefs.put(
			LiferayMavenCore.PREF_ARCHETYPE_PROJECT_TEMPLATE_API,
			"com.liferay:com.liferay.project.templates.api:1.0.4");
		defaultPrefs.put(
			LiferayMavenCore.PREF_ARCHETYPE_PROJECT_TEMPLATE_CONTENT_TARGETING_REPORT,
			"com.liferay:com.liferay.project.templates.content.targeting.report:1.0.3");
		defaultPrefs.put(
			LiferayMavenCore.PREF_ARCHETYPE_PROJECT_TEMPLATE_CONTENT_TARGETING_RULE,
			"com.liferay:com.liferay.project.templates.content.targeting.rule:1.0.3");
		defaultPrefs.put(
			LiferayMavenCore.PREF_ARCHETYPE_PROJECT_TEMPLATE_CONTENT_TARGETING_TRACKING_ACTION,
			"com.liferay:com.liferay.project.templates.content.targeting.tracking.action:1.0.3");
		defaultPrefs.put(
			LiferayMavenCore.PREF_ARCHETYPE_PROJECT_TEMPLATE_CONTROL_MENU_ENTRY,
			"com.liferay:com.liferay.project.templates.control.menu.entry:1.0.3");
		defaultPrefs.put(
			LiferayMavenCore.PREF_ARCHETYPE_PROJECT_TEMPLATE_FORM_FIELD,
			"com.liferay:com.liferay.project.templates.form.field:1.0.1");
		defaultPrefs.put(
			LiferayMavenCore.PREF_ARCHETYPE_PROJECT_TEMPLATE_FRAGMENT,
			"com.liferay:com.liferay.project.templates.fragment:1.0.3");
		defaultPrefs.put(
			LiferayMavenCore.PREF_ARCHETYPE_PROJECT_TEMPLATE_FREEMERKER_PORTLET,
			"com.liferay:com.liferay.project.templates.freemarker.portlet:1.0.0");
		defaultPrefs.put(
			LiferayMavenCore.PREF_ARCHETYPE_PROJECT_TEMPLATE_LAYOUT_TEMPLATE,
			"com.liferay:com.liferay.project.templates.layout.template:1.0.1");
		defaultPrefs.put(
			LiferayMavenCore.PREF_ARCHETYPE_PROJECT_TEMPLATE_MVC_PORTLET,
			"com.liferay:com.liferay.project.templates.mvc.portlet:1.0.3");
		defaultPrefs.put(
			LiferayMavenCore.PREF_ARCHETYPE_PROJECT_TEMPLATE_PANEL_APP,
			"com.liferay:com.liferay.project.templates.panel.app:1.0.3");
		defaultPrefs.put(
			LiferayMavenCore.PREF_ARCHETYPE_PROJECT_TEMPLATE_PORTLET,
			"com.liferay:com.liferay.project.templates.portlet:1.0.3");
		defaultPrefs.put(
			LiferayMavenCore.PREF_ARCHETYPE_PROJECT_TEMPLATE_PORTLET_CONFIGURATION_ICON,
			"com.liferay:com.liferay.project.templates.portlet.configuration.icon:1.0.3");
		defaultPrefs.put(
			LiferayMavenCore.PREF_ARCHETYPE_PROJECT_TEMPLATE_PORTLET_PROVIDER,
			"com.liferay:com.liferay.project.templates.portlet.provider:1.0.3");
		defaultPrefs.put(
			LiferayMavenCore.PREF_ARCHETYPE_PROJECT_TEMPLATE_PORTLET_TOOLBAR_CONTRIBUTOR,
			"com.liferay:com.liferay.project.templates.portlet.toolbar.contributor:1.0.3");
		defaultPrefs.put(
			LiferayMavenCore.PREF_ARCHETYPE_PROJECT_TEMPLATE_REST,
			"com.liferay:com.liferay.project.templates.rest:1.0.2");
		defaultPrefs.put(
			LiferayMavenCore.PREF_ARCHETYPE_PROJECT_TEMPLATE_SERVICE,
			"com.liferay:com.liferay.project.templates.service:1.0.3");
		defaultPrefs.put(
			LiferayMavenCore.PREF_ARCHETYPE_PROJECT_TEMPLATE_SERVICE_BUILDER,
			"com.liferay:com.liferay.project.templates.service.builder:1.0.4");
		defaultPrefs.put(
			LiferayMavenCore.PREF_ARCHETYPE_PROJECT_TEMPLATE_SERVICE_WRAPPER,
			"com.liferay:com.liferay.project.templates.service.wrapper:1.0.3");
		defaultPrefs.put(
			LiferayMavenCore.PREF_ARCHETYPE_PROJECT_TEMPLATE_SIMULATION_PANEL_ENTRY,
			"com.liferay:com.liferay.project.templates.simulation.panel.entry:1.0.3");
		defaultPrefs.put(
			LiferayMavenCore.PREF_ARCHETYPE_PROJECT_TEMPLATE_SOY_PORTLET,
			"com.liferay:com.liferay.project.templates.soy.portlet:1.0.0");
		defaultPrefs.put(
			LiferayMavenCore.PREF_ARCHETYPE_PROJECT_TEMPLATE_SPRING_MVC_PORTLET,
			"com.liferay:com.liferay.project.templates.spring.mvc.portlet:1.0.0");
		defaultPrefs.put(
			LiferayMavenCore.PREF_ARCHETYPE_PROJECT_TEMPLATE_TEMPLATE_CONTEXT_CONTRIBUTOR,
			"com.liferay:com.liferay.project.templates.template.context.contributor:1.0.3");
		defaultPrefs.put(
			LiferayMavenCore.PREF_ARCHETYPE_PROJECT_TEMPLATE_THEME,
			"com.liferay:com.liferay.project.templates.theme:1.0.4");
		defaultPrefs.put(
			LiferayMavenCore.PREF_ARCHETYPE_PROJECT_TEMPLATE_THEME_CONTRIBUTOR,
			"com.liferay:com.liferay.project.templates.theme.contributor:1.0.2");
		defaultPrefs.put(
			LiferayMavenCore.PREF_ARCHETYPE_PROJECT_TEMPLATE_WORKSPACE,
			"com.liferay:com.liferay.project.templates.workspace:1.0.4");
	}

}