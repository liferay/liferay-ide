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

package com.liferay.ide.portlet.core.operation;

import org.eclipse.jst.j2ee.internal.common.operations.INewJavaClassDataModelProperties;

/**
 * @author Greg Amerson
 * @author Cindy Li
 */
@SuppressWarnings("restriction")
public interface INewPortletClassDataModelProperties extends INewJavaClassDataModelProperties {

	// public static final String PORTLET_CLASS =
	// "INewPortletClassDataModelProperties.PORTLET_CLASS";

	// public static final String CREATE_CUSTOM_PORTLET_CLASS =
	// "INewPortletClassDataModelProperties.CREATE_CUSTOM_PORTLET_CLASS";

	// liferay portlet view modes

	public static final String ABOUT_MODE = "INewPortletClassDataModelProperties.ABOUT_MODE";

	public static final String ABOUT_MODE_TEMPLATE = "com.liferay.ide.templates.portlet.aboutjsp";

	public static final String ADD_TO_CONTROL_PANEL = "INewPortletClassDataModelProperties.ADD_TO_CONTROL_PANEL";

	public static final String[] ALL_PORTLET_MODES = {
		"INewPortletClassDataModelProperties.VIEW_MODE", "INewPortletClassDataModelProperties.EDIT_MODE",
		"INewPortletClassDataModelProperties.HELP_MODE", ABOUT_MODE, "INewPortletClassDataModelProperties.CONFIG_MODE",
		"INewPortletClassDataModelProperties.EDITDEFAULTS_MODE", "INewPortletClassDataModelProperties.EDITGUEST_MODE",
		"INewPortletClassDataModelProperties.PREVIEW_MODE", "INewPortletClassDataModelProperties.PRINT_MODE"
	};

	public static final String ALLOW_MULTIPLE = "INewPortletClassDataModelProperties.ALLOW_MULTIPLE";

	public static final String CATEGORY = "INewPortletClassDataModelProperties.CATEGORY";

	public static final String CONFIG_MODE = "INewPortletClassDataModelProperties.CONFIG_MODE";

	public static final String CONFIG_MODE_TEMPLATE = "com.liferay.ide.templates.portlet.configjsp";

	public static final String CONTEXT_TYPE = "INewPortletClassDataModelProperties.CONTEXT_TYPE";

	public static final String CREATE_ENTRY_CLASS = "INewPortletClassDataModelProperties.CREATE_ENTRY_CLASS";

	public static final String CREATE_JSPS = "INewPortletClassDataModelProperties.CREATE_JSPS";

	public static final String CREATE_JSPS_FOLDER = "INewPortletClassDataModelProperties.CREATE_JSPS_FOLDER";

	public static final String CREATE_NEW_PORTLET_CLASS =
		"INewPortletClassDataModelProperties.CREATE_NEW_PORTLET_CLASS";

	public static final String CREATE_RESOURCE_BUNDLE_FILE =
		"INewPortletClassDataModelProperties.CREATE_RESOURCE_BUNDLE_FILE";

	public static final String CREATE_RESOURCE_BUNDLE_FILE_PATH =
		"INewPortletClassDataModelProperties.CREATE_RESOURCE_BUNDLE_FILE_PATH";

	public static final String CSS_CLASS_WRAPPER = "INewPortletClassDataModelProperties.CSS_CLASS_WRAPPER";

	public static final String CSS_FILE = "INewPortletClassDataModelProperties.CSS_FILE";

	public static final String[] DEFAULT_SECURITY_ROLE_NAMES = {"administrator", "guest", "power-user", "user"};

	public static final String DESTROY_OVERRIDE = "INewPortletClassDataModelProperties.DESTROY_OVERRIDE";

	public static final String DISPLAY_NAME = "INewPortletClassDataModelProperties.DISPLAY_NAME";

	public static final String DOABOUT_OVERRIDE = "INewPortletClassDataModelProperties.DOABOUT_OVERRIDE";

	public static final String DOCONFIG_OVERRIDE = "INewPortletClassDataModelProperties.DOCONFIG_OVERRIDE";

	public static final String DOEDIT_OVERRIDE = "INewPortletClassDataModelProperties.DOEDIT_OVERRIDE";

	public static final String DOEDITDEFAULTS_OVERRIDE = "INewPortletClassDataModelProperties.DOEDITDEFAULTS_OVERRIDE";

	public static final String DOEDITGUEST_OVERRIDE = "INewPortletClassDataModelProperties.DOEDITGUEST_OVERRIDE";

	public static final String DOHELP_OVERRIDE = "INewPortletClassDataModelProperties.DOHELP_OVERRIDE";

	public static final String DOPREVIEW_OVERRIDE = "INewPortletClassDataModelProperties.DOPREVIEW_OVERRIDE";

	public static final String DOPRINT_OVERRIDE = "INewPortletClassDataModelProperties.DOPRINT_OVERRIDE";

	public static final String DOVIEW_OVERRIDE = "INewPortletClassDataModelProperties.DOVIEW_OVERRIDE";

	public static final String EDIT_MODE = "INewPortletClassDataModelProperties.EDIT_MODE";

	public static final String EDIT_MODE_TEMPLATE = "com.liferay.ide.templates.portlet.editjsp";

	public static final String EDITDEFAULTS_MODE = "INewPortletClassDataModelProperties.EDITDEFAULTS_MODE";

	public static final String EDITDEFAULTS_MODE_TEMPLATE = "com.liferay.ide.templates.portlet.editdefaultsjsp";

	public static final String EDITGUEST_MODE = "INewPortletClassDataModelProperties.EDITGUEST_MODE";

	public static final String EDITGUEST_MODE_TEMPLATE = "com.liferay.ide.templates.portlet.editguestjsp";

	public static final String ENTRY_CATEGORY = "INewPortletClassDataModelProperties.ENTRY_CATEGORY";

	public static final String ENTRY_CLASS_NAME = "INewPortletClassDataModelProperties.ENTRY_CLASS_NAME";

	public static final String ENTRY_WEIGHT = "INewPortletClassDataModelProperties.ENTRY_WEIGHT";

	public static final String HELP_MODE = "INewPortletClassDataModelProperties.HELP_MODE";

	public static final String HELP_MODE_TEMPLATE = "com.liferay.ide.templates.portlet.helpjsp";

	public static final String ICON_FILE = "INewPortletClassDataModelProperties.ICON_FILE";

	public static final String ID = "INewPortletClassDataModelProperties.ID";

	public static final String INIT_OVERRIDE = "INewPortletClassDataModelProperties.INIT_OVERRIDE";

	public static final String INIT_PARAMETER_NAME = "INewPortletClassDataModelProperties.INIT_PARAMETER_NAME";

	public static final String INIT_PARAMS = "INewPortletClassDataModelProperties.INIT_PARAMS";

	public static final String JAVASCRIPT_FILE = "INewPortletClassDataModelProperties.JAVASCRIPT_FILE";

	public static final String KEYWORDS = "INewPortletClassDataModelProperties.KEYWORDS";

	public static final String LIFERAY_PORTLET_NAME = "INewPortletClassDataModelProperties.LIFERAY_PORTLET_NAME";

	public static final String PORTLET_NAME = "INewPortletClassDataModelProperties.PORTLET_NAME";

	public static final String PREVIEW_MODE = "INewPortletClassDataModelProperties.PREVIEW_MODE";

	public static final String PREVIEW_MODE_TEMPLATE = "com.liferay.ide.templates.portlet.previewjsp";

	public static final String PRINT_MODE = "INewPortletClassDataModelProperties.PRINT_MODE";

	public static final String PRINT_MODE_TEMPLATE = "com.liferay.ide.templates.portlet.printjsp";

	public static final String PROCESSACTION_OVERRIDE = "INewPortletClassDataModelProperties.PROCESSACTION_OVERRIDE";

	public static final String QUALIFIED_GENERIC_PORTLET = "javax.portlet.GenericPortlet";

	public static final String QUALIFIED_LIFERAY_PORTLET = "com.liferay.portal.kernel.portlet.LiferayPortlet";

	public static final String QUALIFIED_MVC_PORTLET = "com.liferay.util.bridges.mvc.MVCPortlet";

	public static final String SERVERESOURCE_OVERRIDE = "INewPortletClassDataModelProperties.SERVERESOURCE_OVERRIDE";

	public static final String SHORT_TITLE = "INewPortletClassDataModelProperties.SHORT_TITLE";

	public static final String SHOW_NEW_CLASS_OPTION = "INewPortletClassDataModelProperties.SHOW_NEW_CLASS_OPTION";

	public static final String TEMPLATE_STORE = "INewPortletClassDataModelProperties.TEMPLATE_STORE";

	public static final String TITLE = "INewPortletClassDataModelProperties.TITLE";

	public static final String USE_DEFAULT_PORTLET_CLASS =
		"INewPortletClassDataModelProperties.USE_DEFAULT_PORTLET_CLASS";

	public static final String VIEW_MODE = "INewPortletClassDataModelProperties.VIEW_MODE";

	public static final String VIEW_MODE_TEMPLATE = "com.liferay.ide.templates.portlet.viewjsp";

	public String[] initNames60 = {
		"view-jsp", "edit-jsp", "help-jsp", "about-jsp", "config-jsp", "edit-defaults-jsp", "edit-guest-jsp",
		"preview-jsp", "print-jsp"
	};

	// portlet view modes

	public String[] initNames61 = {
		"view-template", "edit-template", "help-template", "about-template", "config-template",
		"edit-defaults-template", "edit-guest-template", "preview-template", "print-template"
	};
	public String[] initValues = {
		"/view.jsp", "/edit.jsp", "/help.jsp", "/about.jsp", "/config.jsp", "/edit-defaults.jsp", "/edit-guest.jsp",
		"/preview.jsp", "/print.jsp"
	};

}