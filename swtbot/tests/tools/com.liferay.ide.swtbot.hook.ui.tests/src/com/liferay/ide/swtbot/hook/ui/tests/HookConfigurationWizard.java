/*******************************************************************************
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
 *
 *******************************************************************************/

package com.liferay.ide.swtbot.hook.ui.tests;

import com.liferay.ide.swtbot.ui.tests.UIBase;

/**
 * @author Vicky Wang
 * @author Ying Xu
 */
public interface HookConfigurationWizard extends UIBase
{

    public final String BUTTON_ADD_FROM_LIFERAY = "Add from Liferay...";
    public final String BUTTON_CREATE = "Create";
    public final String BUTTON_EDIT = "Edit...";
    public final String BUTTON_NEW = "New...";
    public final String BUTTON_SELECT = "Select...";

    public final int INDEX_CUSTOM_JSPS_VALIDATION_MESSAGE = 3;
    public final int INDEX_LANGUAGE_PROPERTIES_VALIDATION_MESSAGE = 1;
    public final int INDEX_LANGUAGE_PROPERTIES_WITH_SDK7_VALIDATION_MESSAGE = 0;
    public final int INDEX_PORTAL_PROPERTIES_VALIDATION_MESSAGE = 1;
    public final int INDEX_SERVICES_MESSAGE = 0;
    public final int INDEX_VALIDATION_MESSAGE = 2;
    public final int INDEX_NEW_LIFERAY_HOOK_VALIDATION_MESSAGE = 0;

    public final String LABLE_CHOOSE_SUPERCLASS = "Choose a superclass:";
    public final String LABLE_CLASS = "Class:";
    public final String LABLE_CLASS_NAME = "Classname:";
    public final String LABLE_CONTENT_FOLDER = "Content folder:";
    public final String LABLE_CUSTOM_JSP_FOLDER = "Custom JSP folder:";
    public final String LABLE_CUSTOM_JSPS = "Custom JSPs";
    public final String LABLE_DEFINE_ACTIONS = "Define actions to be executed on portal events:";
    public final String LABLE_DEFINE_PORTAL_SERVICES = "Define portal services to extend:";
    public final String LABLE_DISABLE_JSP_SYNTAX_VALIDATION =
        "Disable JSP syntax validation for custom JSP folder (recommended).";
    public final String LABLE_EVENT = "Event:";
    public final String LABLE_IMPL_CLASS = "Impl Class:";
    public final String LABLE_JAVA_PACKAGE = "Java package:";
    public final String LABLE_JSP_FILE_PATH = "JSP File Path";
    public final String LABLE_JSP_FILES_TO_OVERRIDE = "JSP files to override";
    public final String LABLE_LANGUAGE_PROPERTIES = "Language properties";
    public final String LABLE_LANGUAGE_PROPERTY_FILE = "Language property file:";
    public final String LABLE_LANGUAGE_PROPERTY_FILES = "Language property files:";
    public final String LABLE_PLEASE_SELECT_A_PROPERTY = "Please select a property:";
    public final String LABLE_PORTAL_PROPERTIES = "Portal properties";
    public final String LABLE_PORTAL_PROPERTIES_FILE = "Portal properties file:";
    public final String LABLE_PROPERTY = "Property:";
    public final String LABLE_SELECT_AN_EVENT_ACTION = "Select an event action:";
    public final String LABLE_SELECTED_PROJECT = "Selected project:";
    public final String LABLE_SERVICE_TYPE = "Service Type:";
    public final String LABLE_SERVICES = "Services";
    public final String LABLE_SPECIFY_PROPERTIES = "Specify properties to override:";
    public final String LABLE_VALUE = "Value:";
    public final String LABLE_WEB_ROOT_FOLDER = "Web root folder:";
    public final String LABLE_LANGUAGE_PROPERTIES_IN_SDK7_IS_NOT_SUPPORTED =
        " Modifying Language properties in Plugins SDK 7.0 is not supported, use Liferay module instead.";
    public final String LABEL_SELECT_A_JSP_TO_CUSTOMIZE = "Select a JSP to customize:";

    public final String COMBOBOX_HOOK_PLUGIN_PROJECT = "Hook plugin project:";
    public final String COMBOBOX_SUPERCLASS = "Superclass:";
    public final String CHECKBOX_DISABLE_JSP_SYNTAX_VALIDATION =
        "Disable JSP syntax validation for custom JSP folder (recommended).";

    public final String MENU_HOOK = "Hook";
    public final String WINDOW_ADD_EVENT_ACTION = "Add Event Action";
    public final String WINDOW_ADD_PROPERTY_OVERRIDE = "Add Property Override";
    public final String WINDOW_ADD_SERVICE = "Add Service";
    public final String WINDOW_ADD_SERVICE_WRAPPER = "Add Service Wrapper";

}
