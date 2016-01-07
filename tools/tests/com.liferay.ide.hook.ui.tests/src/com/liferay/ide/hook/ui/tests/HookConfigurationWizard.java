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

package com.liferay.ide.hook.ui.tests;

import com.liferay.ide.ui.tests.UIBase;

/**
 * @author Vicky Wang
 */
public interface HookConfigurationWizard extends UIBase
{

    String BUTTON_ADD = "Add...";
    String BUTTON_ADD_FROM_LIFERAY = "Add from Liferay...";
    String BUTTON_CREATE = "Create";
    String BUTTON_EDIT = "Edit...";
    String BUTTON_NEW = "New...";
    String BUTTON_REMOVE = "Remove...";
    String BUTTON_SELECT = "Select...";

    int INDEX_CUSTOM_JSPS_VALIDATION_MESSAGE = 3;
    int INDEX_LANGUAGE_PROPERTIES_VALIDATION_MESSAGE = 1;
    int INDEX_PORTAL_PROPERTIES_VALIDATION_MESSAGE = 1;
    int INDEX_SERVICES_MESSAGE = 0;
    int INDEX_VALIDATION_MESSAGE = 2;

    String LABEL_BUILD_TYPE = "Build type:";
    String LABEL_DISPLAY_NAME = "Display name:";
    String LABEL_PLUGIN_TYPE = "Plugin type:";
    String LABEL_PROJECT_NAME = "Project name:";
    String LABEL_SDK_LOCATION = "SDK Location:";
    String LABLE_CHOOSE_SUPERCLASS = "Choose a superclass:";
    String LABLE_CLASS = "Class:";
    String LABLE_CLASS_NAME = "Classname:";
    String LABLE_CONTENT_FOLDER = "Content folder:";
    String LABLE_CUSTOM_JSP_FOLDER = "Custom JSP folder:";
    String LABLE_CUSTOM_JSPS = "Custom JSPs";
    String LABLE_DEFINE_ACTIONS = "Define actions to be executed on portal events:";
    String LABLE_DEFINE_PORTAL_SERVICES = "Define portal services to extend:";
    String LABLE_DISABLE_JSP_SYNTAX_VALIDATION = "Disable JSP syntax validation for custom JSP folder (recommended).";
    String LABLE_EVENT = "Event:";
    String LABLE_IMPL_CLASS = "Impl Class:";
    String LABLE_JAVA_PACKAGE = "Java package:";
    String LABLE_JSP_FILE_PATH = "JSP File Path";
    String LABLE_JSP_FILES_TO_OVERRIDE = "JSP files to override";
    String LABLE_LANGUAGE_PROPERTIES = "Language properties";
    String LABLE_LANGUAGE_PROPERTY_FILE = "Language property file:";
    String LABLE_LANGUAGE_PROPERTY_FILES = "Language property files:";
    String LABLE_LANGUAGE_PROPTERTIES = "Language properties";
    String LABLE_PLEASE_SELECT_A_PROPERTY = "Please select a property:";
    String LABLE_PORTAL_PROPERTIES = "Portal properties";
    String LABLE_PORTAL_PROPERTIES_FILE = "Portal properties file:";
    String LABLE_PROPERTY = "Property:";
    String LABLE_SELECT_A_PROPERTY = "Please select a property";
    String LABLE_SELECT_AN_EVENT_ACTION = "Select an event action:";
    String LABLE_SELECTED_PROJECT = "Selected project:";
    String LABLE_SERVICE_TYPE = "Service Type:";
    String LABLE_SERVICES = "Services";
    String LABLE_SPECIFY_PROPERTIES = "Specify properties to override:";
    String LABLE_VALUE = "Value:";
    String LABLE_WEB_ROOT_FOLDER = "Web root folder:";

    String MENU_HOOK = "Hook";

    String TOOLTIP_CREATE_LIFERAY_PROJECT = "Create a new Liferay Plugin Project";
    String TOOLTIP_MENU_ITEM_NEW_LIFERAY_HOOK_CONFIGURATION = "New Liferay Hook Configuration";
    String TOOLTIP_MENU_ITEM_NEW_LIFERAY_PROJECT = "New Liferay Plugin Project";

    String WINDOW_ADD_EVENT_ACTION = "Add Event Action";
    String WINDOW_ADD_PROPERTY_OVERRIDE = "Add Property Override";
    String WINDOW_ADD_SERVICE = "Add Service";
    String WINDOW_ADD_SERVICE_WRAPPER = "Add Service Wrapper";
    String WINDOW_NEW_LIFERAY_HOOK_CONFIGURATION = "New Liferay Hook Configuration";

}
