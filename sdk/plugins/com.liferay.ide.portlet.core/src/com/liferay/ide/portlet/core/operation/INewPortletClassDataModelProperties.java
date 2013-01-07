/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.portlet.core.operation;

import org.eclipse.jst.j2ee.internal.common.operations.INewJavaClassDataModelProperties;

/**
 * @author Greg Amerson
 * @author Cindy Li
 */
@SuppressWarnings("restriction")
public interface INewPortletClassDataModelProperties extends INewJavaClassDataModelProperties 
{
	// public static final String PORTLET_CLASS =
	// "INewPortletClassDataModelProperties.PORTLET_CLASS";

	// public static final String CREATE_CUSTOM_PORTLET_CLASS =
	// "INewPortletClassDataModelProperties.CREATE_CUSTOM_PORTLET_CLASS";

	// liferay portlet view modes
    final String[] initNames60 =
    {
        "view-jsp", //$NON-NLS-1$
        "edit-jsp", //$NON-NLS-1$
        "help-jsp", //$NON-NLS-1$
        "about-jsp", //$NON-NLS-1$
        "config-jsp", //$NON-NLS-1$
        "edit-defaults-jsp", //$NON-NLS-1$
        "edit-guest-jsp", //$NON-NLS-1$
        "preview-jsp", //$NON-NLS-1$
        "print-jsp" //$NON-NLS-1$
    };

    final String[] initNames61 =
    {
        "view-template", //$NON-NLS-1$
        "edit-template", //$NON-NLS-1$
        "help-template", //$NON-NLS-1$
        "about-template", //$NON-NLS-1$
        "config-template", //$NON-NLS-1$
        "edit-defaults-template", //$NON-NLS-1$
        "edit-guest-template", //$NON-NLS-1$
        "preview-template", //$NON-NLS-1$
        "print-template" //$NON-NLS-1$
    };

    final String[] initValues =
    {
        "/view.jsp", //$NON-NLS-1$
        "/edit.jsp", //$NON-NLS-1$
        "/help.jsp", //$NON-NLS-1$
        "/about.jsp", //$NON-NLS-1$
        "/config.jsp", //$NON-NLS-1$
        "/edit-defaults.jsp", //$NON-NLS-1$
        "/edit-guest.jsp", //$NON-NLS-1$
        "/preview.jsp", //$NON-NLS-1$
        "/print.jsp" //$NON-NLS-1$
    };

    public static final String ABOUT_MODE = "INewPortletClassDataModelProperties.ABOUT_MODE"; //$NON-NLS-1$

    public static final String ABOUT_MODE_TEMPLATE = "com.liferay.ide.templates.portlet.aboutjsp"; //$NON-NLS-1$

	public static final String ADD_TO_CONTROL_PANEL = "INewPortletClassDataModelProperties.ADD_TO_CONTROL_PANEL"; //$NON-NLS-1$
	
	public static final String[] ALL_PORTLET_MODES = 
    {
		"INewPortletClassDataModelProperties.VIEW_MODE",  //$NON-NLS-1$
		"INewPortletClassDataModelProperties.EDIT_MODE", //$NON-NLS-1$
		"INewPortletClassDataModelProperties.HELP_MODE",  //$NON-NLS-1$
		ABOUT_MODE, 
		"INewPortletClassDataModelProperties.CONFIG_MODE", //$NON-NLS-1$
		"INewPortletClassDataModelProperties.EDITDEFAULTS_MODE",  //$NON-NLS-1$
		"INewPortletClassDataModelProperties.EDITGUEST_MODE", //$NON-NLS-1$
		"INewPortletClassDataModelProperties.PREVIEW_MODE",  //$NON-NLS-1$
		"INewPortletClassDataModelProperties.PRINT_MODE" //$NON-NLS-1$
	};

	public static final String ALLOW_MULTIPLE = "INewPortletClassDataModelProperties.ALLOW_MULTIPLE"; //$NON-NLS-1$

	public static final String CATEGORY = "INewPortletClassDataModelProperties.CATEGORY"; //$NON-NLS-1$

	public static final String CONFIG_MODE = "INewPortletClassDataModelProperties.CONFIG_MODE"; //$NON-NLS-1$

	public static final String CONFIG_MODE_TEMPLATE = "com.liferay.ide.templates.portlet.configjsp"; //$NON-NLS-1$

	public static final String CONTEXT_TYPE = "INewPortletClassDataModelProperties.CONTEXT_TYPE"; //$NON-NLS-1$
	
	public static final String CREATE_ENTRY_CLASS = "INewPortletClassDataModelProperties.CREATE_ENTRY_CLASS"; //$NON-NLS-1$

	public static final String CREATE_JSPS = "INewPortletClassDataModelProperties.CREATE_JSPS"; //$NON-NLS-1$
	
	public static final String CREATE_JSPS_FOLDER = "INewPortletClassDataModelProperties.CREATE_JSPS_FOLDER"; //$NON-NLS-1$

	public static final String CREATE_NEW_PORTLET_CLASS =
		"INewPortletClassDataModelProperties.CREATE_NEW_PORTLET_CLASS"; //$NON-NLS-1$

	public static final String CREATE_RESOURCE_BUNDLE_FILE =
		"INewPortletClassDataModelProperties.CREATE_RESOURCE_BUNDLE_FILE"; //$NON-NLS-1$

	public static final String CREATE_RESOURCE_BUNDLE_FILE_PATH =
		"INewPortletClassDataModelProperties.CREATE_RESOURCE_BUNDLE_FILE_PATH"; //$NON-NLS-1$

	public static final String CSS_CLASS_WRAPPER = "INewPortletClassDataModelProperties.CSS_CLASS_WRAPPER"; //$NON-NLS-1$

	public static final String CSS_FILE = "INewPortletClassDataModelProperties.CSS_FILE"; //$NON-NLS-1$

	public static final String[] DEFAULT_SECURITY_ROLE_NAMES = 
    {
		"administrator", "guest", "power-user", "user" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	};

	public static final String DESTROY_OVERRIDE = "INewPortletClassDataModelProperties.DESTROY_OVERRIDE"; //$NON-NLS-1$

	public static final String DISPLAY_NAME = "INewPortletClassDataModelProperties.DISPLAY_NAME"; //$NON-NLS-1$

	public static final String DOABOUT_OVERRIDE = "INewPortletClassDataModelProperties.DOABOUT_OVERRIDE"; //$NON-NLS-1$

	public static final String DOCONFIG_OVERRIDE = "INewPortletClassDataModelProperties.DOCONFIG_OVERRIDE"; //$NON-NLS-1$

	public static final String DOEDIT_OVERRIDE = "INewPortletClassDataModelProperties.DOEDIT_OVERRIDE"; //$NON-NLS-1$

	public static final String DOEDITDEFAULTS_OVERRIDE = "INewPortletClassDataModelProperties.DOEDITDEFAULTS_OVERRIDE"; //$NON-NLS-1$
	
	public static final String DOEDITGUEST_OVERRIDE = "INewPortletClassDataModelProperties.DOEDITGUEST_OVERRIDE"; //$NON-NLS-1$

	public static final String DOHELP_OVERRIDE = "INewPortletClassDataModelProperties.DOHELP_OVERRIDE"; //$NON-NLS-1$

	public static final String DOPREVIEW_OVERRIDE = "INewPortletClassDataModelProperties.DOPREVIEW_OVERRIDE"; //$NON-NLS-1$

	public static final String DOPRINT_OVERRIDE = "INewPortletClassDataModelProperties.DOPRINT_OVERRIDE"; //$NON-NLS-1$

	public static final String DOVIEW_OVERRIDE = "INewPortletClassDataModelProperties.DOVIEW_OVERRIDE"; //$NON-NLS-1$

	public static final String EDIT_MODE = "INewPortletClassDataModelProperties.EDIT_MODE"; //$NON-NLS-1$

	public static final String EDIT_MODE_TEMPLATE = "com.liferay.ide.templates.portlet.editjsp"; //$NON-NLS-1$

	public static final String EDITDEFAULTS_MODE = "INewPortletClassDataModelProperties.EDITDEFAULTS_MODE"; //$NON-NLS-1$

	public static final String EDITDEFAULTS_MODE_TEMPLATE = "com.liferay.ide.templates.portlet.editdefaultsjsp"; //$NON-NLS-1$

	public static final String EDITGUEST_MODE = "INewPortletClassDataModelProperties.EDITGUEST_MODE"; //$NON-NLS-1$

	public static final String EDITGUEST_MODE_TEMPLATE = "com.liferay.ide.templates.portlet.editguestjsp"; //$NON-NLS-1$

	public static final String ENTRY_CATEGORY = "INewPortletClassDataModelProperties.ENTRY_CATEGORY"; //$NON-NLS-1$

    public static final String ENTRY_CLASS_NAME = "INewPortletClassDataModelProperties.ENTRY_CLASS_NAME"; //$NON-NLS-1$

    public static final String ENTRY_WEIGHT = "INewPortletClassDataModelProperties.ENTRY_WEIGHT"; //$NON-NLS-1$

    public static final String HELP_MODE = "INewPortletClassDataModelProperties.HELP_MODE"; //$NON-NLS-1$

	public static final String HELP_MODE_TEMPLATE = "com.liferay.ide.templates.portlet.helpjsp"; //$NON-NLS-1$

	public static final String ICON_FILE = "INewPortletClassDataModelProperties.ICON_FILE"; //$NON-NLS-1$

	public static final String ID = "INewPortletClassDataModelProperties.ID"; //$NON-NLS-1$

	public static final String INIT_OVERRIDE = "INewPortletClassDataModelProperties.INIT_OVERRIDE"; //$NON-NLS-1$

	public static final String INIT_PARAMETER_NAME = "INewPortletClassDataModelProperties.INIT_PARAMETER_NAME"; //$NON-NLS-1$

	public static final String INIT_PARAMS = "INewPortletClassDataModelProperties.INIT_PARAMS"; //$NON-NLS-1$

	public static final String JAVASCRIPT_FILE = "INewPortletClassDataModelProperties.JAVASCRIPT_FILE"; //$NON-NLS-1$

	public static final String KEYWORDS = "INewPortletClassDataModelProperties.KEYWORDS"; //$NON-NLS-1$

	public static final String LIFERAY_PORTLET_NAME = "INewPortletClassDataModelProperties.LIFERAY_PORTLET_NAME"; //$NON-NLS-1$

	public static final String PORTLET_NAME = "INewPortletClassDataModelProperties.PORTLET_NAME"; //$NON-NLS-1$

	public static final String PREVIEW_MODE = "INewPortletClassDataModelProperties.PREVIEW_MODE"; //$NON-NLS-1$

	public static final String PREVIEW_MODE_TEMPLATE = "com.liferay.ide.templates.portlet.previewjsp"; //$NON-NLS-1$

	public static final String PRINT_MODE = "INewPortletClassDataModelProperties.PRINT_MODE"; //$NON-NLS-1$

	public static final String PRINT_MODE_TEMPLATE = "com.liferay.ide.templates.portlet.printjsp"; //$NON-NLS-1$

	public static final String PROCESSACTION_OVERRIDE = "INewPortletClassDataModelProperties.PROCESSACTION_OVERRIDE"; //$NON-NLS-1$

	public static final String QUALIFIED_GENERIC_PORTLET = "javax.portlet.GenericPortlet"; //$NON-NLS-1$

	public static final String QUALIFIED_LIFERAY_PORTLET = "com.liferay.portal.kernel.portlet.LiferayPortlet"; //$NON-NLS-1$

	public static final String QUALIFIED_MVC_PORTLET = "com.liferay.util.bridges.mvc.MVCPortlet"; //$NON-NLS-1$

	public static final String SERVERESOURCE_OVERRIDE = "INewPortletClassDataModelProperties.SERVERESOURCE_OVERRIDE"; //$NON-NLS-1$

	public static final String SHORT_TITLE = "INewPortletClassDataModelProperties.SHORT_TITLE"; //$NON-NLS-1$

	public static final String SHOW_NEW_CLASS_OPTION = "INewPortletClassDataModelProperties.SHOW_NEW_CLASS_OPTION"; //$NON-NLS-1$

	public static final String TEMPLATE_STORE = "INewPortletClassDataModelProperties.TEMPLATE_STORE"; //$NON-NLS-1$

    public static final String TITLE = "INewPortletClassDataModelProperties.TITLE"; //$NON-NLS-1$

    public static final String USE_DEFAULT_PORTLET_CLASS = "INewPortletClassDataModelProperties.USE_DEFAULT_PORTLET_CLASS"; //$NON-NLS-1$

	// portlet view modes
	public static final String VIEW_MODE = "INewPortletClassDataModelProperties.VIEW_MODE"; //$NON-NLS-1$

	public static final String VIEW_MODE_TEMPLATE = "com.liferay.ide.templates.portlet.viewjsp"; //$NON-NLS-1$

}
