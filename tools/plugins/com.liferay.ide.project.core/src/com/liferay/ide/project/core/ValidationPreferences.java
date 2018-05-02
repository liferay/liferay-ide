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

package com.liferay.ide.project.core;

import com.liferay.ide.core.util.CoreUtil;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;

import org.osgi.service.prefs.BackingStoreException;

/**
 * @author Kuo Zhang
 * @author Terry Jia
 */
public class ValidationPreferences {

	public static final String LIFERAY_DISPLAY_XML_REFERENCE_NOT_FOUND = "liferay-display-xml-reference-not-found";

	// validation preferences of liferay-display.xml

	public static final String LIFERAY_DISPLAY_XML_RESOURCE_NOT_FOUND = "liferay-display-xml-resource-not-found";

	public static final String LIFERAY_DISPLAY_XML_SYNTAX_INVALID = "liferay-display-xml-syntax-invalid";

	public static final String LIFERAY_DISPLAY_XML_TYPE_HIERARCHY_INCORRECT =
		"liferay-display-xml-type-hierarchy-incorrect";

	public static final String LIFERAY_DISPLAY_XML_TYPE_NOT_FOUND = "liferay-display-xml-type-not-found";

	public static final String LIFERAY_HOOK_XML_REFERENCE_NOT_FOUND = "liferay-hook-xml-reference-not-found";

	// validation preferences of liferay-hook.xml

	public static final String LIFERAY_HOOK_XML_RESOURCE_NOT_FOUND = "liferay-hook-xml-resource-not-found";

	public static final String LIFERAY_HOOK_XML_SYNTAX_INVALID = "liferay-hook-xml-syntax-invalid";

	public static final String LIFERAY_HOOK_XML_TYPE_HIERARCHY_INCORRECT = "liferay-hook-xml-type-hierarchy-incorrect";

	public static final String LIFERAY_HOOK_XML_TYPE_NOT_FOUND = "liferay-hook-xml-type-not-found";

	public static final String LIFERAY_JSP_METHOD_NOT_FOUND = "liferay-jsp-method-not-found";

	// validation preferences of liferay-layouttpl.xml

	public static final String LIFERAY_JSP_PROPERTY_NOT_FOUND = "liferay-jsp--property-not-found";

	public static final String LIFERAY_JSP_REFERENCE_NOT_FOUND = "liferay-jsp-reference-not-found";

	public static final String LIFERAY_JSP_RESOURCE_NOT_FOUND = "liferya-jsp-resource-not-found";

	public static final String LIFERAY_JSP_STATIC_VALUE_UNDEFINED = "liferay-jsp-static-value-undefined";

	public static final String LIFERAY_JSP_SYNTAX_INVALID = "liferay-jsp-syntax-value_invalid";

	// validation preferences of liferay-portlet.xml

	public static final String LIFERAY_JSP_TYPE_HIERARCHY_INCORRECT = "liferay-jsp-hierarchy-incorrect";

	public static final String LIFERAY_JSP_TYPE_NOT_FOUND = "liferay-jsp-type-not-found";

	public static final String LIFERAY_LAYOUTTPL_XML_REFERENCE_NOT_FOUND =
		"liferay-layout-templates-xml-reference-not-found";

	public static final String LIFERAY_LAYOUTTPL_XML_RESOURCE_NOT_FOUND =
		"liferay-layout-templates-xml-resource-not-found";

	public static final String LIFERAY_LAYOUTTPL_XML_SYNTAX_INVALID = "liferay-layout-templates-xml-syntax-invalid";

	// validation preferences of portlet.xml

	public static final String LIFERAY_LAYOUTTPL_XML_TYPE_HIERARCHY_INCORRECT =
		"liferay-layout-templates-xml-type-hierarchy-incorrect";

	public static final String LIFERAY_LAYOUTTPL_XML_TYPE_NOT_FOUND = "liferay-layout-templates-xml-type-not-found";

	public static final String LIFERAY_PORTLET_XML_REFERENCE_NOT_FOUND = "liferay-portlet-xml-reference-not-found";

	public static final String LIFERAY_PORTLET_XML_RESOURCE_NOT_FOUND = "liferay-portlet-xml-resource-not-found";

	public static final String LIFERAY_PORTLET_XML_SYNTAX_INVALID = "liferay-portlet-xml-syntax-invalid";

	// validation preferences of service.xml

	public static final String LIFERAY_PORTLET_XML_TYPE_HIERARCHY_INCORRECT =
		"liferay-portlet-xml-type-hierarchy-incorrect";

	public static final String LIFERAY_PORTLET_XML_TYPE_NOT_FOUND = "liferay-portlet-xml-type-not-found";

	public static final String PORTLET_XML_REFERENCE_NOT_FOUND = "portlet-xml-reference-not-found";

	public static final String PORTLET_XML_RESOURCE_NOT_FOUND = "portlet-xml-resource-not-found";

	public static final String PORTLET_XML_SYNTAX_INVALID = "portlet-xml-syntax-invalid";

	// validation preferences of liferay jsp files

	public static final String PORTLET_XML_TYPE_HIERARCHY_INCORRECT = "portlet-xml-type-hierarchy-incorrect";

	public static final String PORTLET_XML_TYPE_NOT_FOUND = "portlet-xml-type-not-found";

	public static final String SERVICE_XML_REFERENCE_NOT_FOUND = "service-xml-reference-not-found";

	public static final String SERVICE_XML_RESOURCE_NOT_FOUND = "service-xml-resource-not-found";

	public static final String SERVICE_XML_SYNTAX_INVALID = "service-xml-syntax-invalid";

	public static final String SERVICE_XML_TYPE_HIERARCHY_INCORRECT = "service-xml-type-hierarchy-incorrect";

	public static final String SERVICE_XML_TYPE_NOT_FOUND = "service-xml-type-not-found";

	public static boolean containsKey(String liferayPluginValidationType) {
		return _preferenceKeys.contains(liferayPluginValidationType);
	}

	public static String getValidationPreferenceKey(String descriptorFileName, ValidationType type) {
		StringBuilder retval = new StringBuilder();

		if (!CoreUtil.isNullOrEmpty(descriptorFileName)) {
			String fileName = descriptorFileName.replace(".", "-").toLowerCase();

			retval.append(fileName);

			retval.append("-");
		}

		if (type != null) {
			String t = type.toString().toLowerCase();

			retval.append(t.replace("_", "-"));
		}

		return retval.toString();
	}

	/**
	 * Levels: IGNORE: -1, ERROR: 1, WARNNING: 2
	 */
	public static void setInstanceScopeValidationLevel(String liferayPluginValidationType, int validationLevel) {
		if (_preferenceKeys.contains(liferayPluginValidationType) &&
			((validationLevel == -1) || (validationLevel == 1) || (validationLevel == 2))) {

			IEclipsePreferences node = InstanceScope.INSTANCE.getNode(ProjectCore.PLUGIN_ID);

			node.putInt(liferayPluginValidationType, validationLevel);

			try {
				node.flush();
			}
			catch (BackingStoreException bse) {
				ProjectCore.logError("Error setting validation preferences", bse);
			}
		}
	}

	public static void setProjectScopeValidationLevel(
		IProject project, String liferayPluginValidationType, int validationLevel) {

		IEclipsePreferences node = new ProjectScope(project).getNode(ProjectCore.PLUGIN_ID);

		if (_preferenceKeys.contains(liferayPluginValidationType) &&
			((validationLevel == -1) || (validationLevel == 1) || (validationLevel == 2))) {

			node.putBoolean(ProjectCore.USE_PROJECT_SETTINGS, true);

			node.putInt(liferayPluginValidationType, validationLevel);

			try {
				node.flush();
			}
			catch (BackingStoreException bse) {
				ProjectCore.logError("Error setting validation preferences", bse);
			}
		}
	}

	public enum ValidationType {

		METHOD_NOT_FOUND, PROPERTY_NOT_FOUND, REFERENCE_NOT_FOUND, RESOURCE_NOT_FOUND, STATIC_VALUE_UNDEFINED,
		SYNTAX_INVALID, TYPE_NOT_FOUND, TYPE_HIERARCHY_INCORRECT

	}

	private static Set<String> _preferenceKeys;

	static {
		_preferenceKeys = new HashSet<>();

		_preferenceKeys.add(LIFERAY_DISPLAY_XML_TYPE_HIERARCHY_INCORRECT);
		_preferenceKeys.add(LIFERAY_DISPLAY_XML_TYPE_NOT_FOUND);
		_preferenceKeys.add(LIFERAY_DISPLAY_XML_REFERENCE_NOT_FOUND);
		_preferenceKeys.add(LIFERAY_DISPLAY_XML_RESOURCE_NOT_FOUND);
		_preferenceKeys.add(LIFERAY_DISPLAY_XML_SYNTAX_INVALID);

		_preferenceKeys.add(LIFERAY_HOOK_XML_TYPE_HIERARCHY_INCORRECT);
		_preferenceKeys.add(LIFERAY_HOOK_XML_TYPE_NOT_FOUND);
		_preferenceKeys.add(LIFERAY_HOOK_XML_REFERENCE_NOT_FOUND);
		_preferenceKeys.add(LIFERAY_HOOK_XML_RESOURCE_NOT_FOUND);
		_preferenceKeys.add(LIFERAY_HOOK_XML_SYNTAX_INVALID);

		_preferenceKeys.add(LIFERAY_LAYOUTTPL_XML_TYPE_HIERARCHY_INCORRECT);
		_preferenceKeys.add(LIFERAY_LAYOUTTPL_XML_TYPE_NOT_FOUND);
		_preferenceKeys.add(LIFERAY_LAYOUTTPL_XML_REFERENCE_NOT_FOUND);
		_preferenceKeys.add(LIFERAY_LAYOUTTPL_XML_RESOURCE_NOT_FOUND);
		_preferenceKeys.add(LIFERAY_LAYOUTTPL_XML_SYNTAX_INVALID);

		_preferenceKeys.add(LIFERAY_PORTLET_XML_TYPE_HIERARCHY_INCORRECT);
		_preferenceKeys.add(LIFERAY_PORTLET_XML_TYPE_NOT_FOUND);
		_preferenceKeys.add(LIFERAY_PORTLET_XML_REFERENCE_NOT_FOUND);
		_preferenceKeys.add(LIFERAY_PORTLET_XML_RESOURCE_NOT_FOUND);
		_preferenceKeys.add(LIFERAY_PORTLET_XML_SYNTAX_INVALID);

		_preferenceKeys.add(PORTLET_XML_TYPE_HIERARCHY_INCORRECT);
		_preferenceKeys.add(PORTLET_XML_TYPE_NOT_FOUND);
		_preferenceKeys.add(PORTLET_XML_REFERENCE_NOT_FOUND);
		_preferenceKeys.add(PORTLET_XML_RESOURCE_NOT_FOUND);
		_preferenceKeys.add(PORTLET_XML_SYNTAX_INVALID);

		_preferenceKeys.add(SERVICE_XML_TYPE_HIERARCHY_INCORRECT);
		_preferenceKeys.add(SERVICE_XML_TYPE_NOT_FOUND);
		_preferenceKeys.add(SERVICE_XML_REFERENCE_NOT_FOUND);
		_preferenceKeys.add(SERVICE_XML_RESOURCE_NOT_FOUND);
		_preferenceKeys.add(SERVICE_XML_SYNTAX_INVALID);

		_preferenceKeys.add(LIFERAY_JSP_METHOD_NOT_FOUND);
		_preferenceKeys.add(LIFERAY_JSP_PROPERTY_NOT_FOUND);
		_preferenceKeys.add(LIFERAY_JSP_REFERENCE_NOT_FOUND);
		_preferenceKeys.add(LIFERAY_JSP_RESOURCE_NOT_FOUND);
		_preferenceKeys.add(LIFERAY_JSP_STATIC_VALUE_UNDEFINED);
		_preferenceKeys.add(LIFERAY_JSP_SYNTAX_INVALID);
		_preferenceKeys.add(LIFERAY_JSP_TYPE_NOT_FOUND);
		_preferenceKeys.add(LIFERAY_JSP_TYPE_HIERARCHY_INCORRECT);
	}

}