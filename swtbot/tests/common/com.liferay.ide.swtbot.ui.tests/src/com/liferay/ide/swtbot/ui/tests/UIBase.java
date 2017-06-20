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

package com.liferay.ide.swtbot.ui.tests;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 * @author Ying Xu
 * @author Vicky Wang
 * @author Li Lu
 */
public interface UIBase
{

    public final String BUTTON_ADD = "Add...";
    public final String BUTTON_BACK = "< Back";
    public final String BUTTON_BROWSE = "Browse";
    public final String BUTTON_BROWSE_WITH_DOT = "Browse...";
    public final String BUTTON_CANCEL = "Cancel";
    public final String BUTTON_CONTINUE = "Continue";
    public final String BUTTON_DELETE = "Delete";
    public final String BUTTON_FINISH = "Finish";
    public final String BUTTON_HELP = "Help";
    public final String BUTTON_NEXT = "Next >";
    public final String BUTTON_NO = "No";
    public final String BUTTON_OK = "OK";
    public final String BUTTON_REMOVE = "Remove";
    public final String BUTTON_REMOVE_WITH_DOT = "Remove...";
    public final String BUTTON_YES = "Yes";
    public final String BUTTON_MORE_WITH_DOT = "More...";
    public final String BUTTON_SHOW_ALL_PAGES = "Show All Pages";
    public final String BUTTON_RESTART_UPGRADE = "Restart Upgrade";

    public final String CHECKBOX_INCLUDE_SAMPLE_CODE = "Include sample code";
    public final String CHECKBOX_ADD_PROJECT_TO_WORKING_SET = "Add project to working set";
    public final String COMBOBOX_WORKING_SET = "Working set:";

    public final int INDEX_DEFAULT_START = 0;
    public final int INDEX_DEFAULT_VALIDATION_MESSAGE = -1;
    public final int INDEX_CREATE_A_PROJECT_THROUGH_NEW_WIZARD_PAGE = 1;

    public final String LABEL_CLEAR_LOG_VIEWER = "Clear Log Viewer";
    public final String LABEL_DELETE_FROM_DISK = "Delete project contents on disk (cannot be undone)";
    public final String LABEL_DELETE_RESOURCE = "Delete Resources";
    public final String LABEL_ERROR_LOG = "Error Log";
    public final String LABEL_ERROR_LOG_FULL = "Error Log (Workspace Log)";
    public final String LABEL_LIFERAY_PLUGINS = "Liferay Plugins";
    public final String LABEL_LIFERAY_WORKSPACE = "Liferay Workspace";
    public final String LABEL_OTHER = "Other...";
    public final String LABEL_PACKAGE_EXPLORER = "Package Explorer";
    public final String LABEL_PROGRESS = "Progress";
    public final String LABEL_SHOW_VIEW = "Show View";
    public final String LABEL_WELCOME = "Welcome";
    public final String LABEL_WINDOW = "Window";
    public final String LABEL_IMPORT = "Import...";
    public final String LABEL_PROJECT_EXPLORER = "Project Explorer";
    public final String LABEL_LIFERAY_CODE_UPGRADE = "Liferay Code Upgrade";
    public final String LABEL_RESTART_CODE_UPGRADE = "Restart code upgrade?";
    public final String LABEL_BUILD_TYPE = "Build type:";
    public final String LABEL_PREFERENCES = "Preferences";

    public final String LABEL_SOURCE_FOLDER = "Source folder:";
    public final String LABEL_NAME = "Name:";
    public final String LABEL_TYPE = "Type";

    public final String MENU_DELETE = "Delete";
    public final String MENU_FILE = "File";
    public final String MENU_PROJECT = "Project...";
    public final String MENU_NEW = "New";
    public final String TEXT_BLANK = "";
    public final String TEXT_NO_OPERTAIONS = "No operations to display at this time.";

    public final String TOOLBAR_CREATE_LIFERAY_PROJECT = "Create a new Liferay Module Project";

    public final String LABEL_NEW_LIFERAY_PLUGIN_PROJECT = "New Liferay Plugin Project";
    public final String LABEL_NEW_LIFERAY_MODULE_PROJECT = "New Liferay Module Project";
    public final String LABEL_NEW_LIFERAY_HOOK_CONFIGURATION = "New Liferay Hook Configuration";
    public final String LABEL_NEW_LIFERAY_SERVER = "New Liferay Server";
    public final String LABEL_NEW_LIFERAY_SERVICE_BUILDER = "New Liferay Service Builder";
    public final String LABEL_NEW_LIFERAY_LAYOUT_TMEPLATE = "New Liferay Layout Template";
    public final String LABEL_NEW_LIFERAY_JSF_PORTLET = "New Liferay JSF Portlet";
    public final String LABEL_NEW_LIFERAY_VAADIN_PORTLET = "New Liferay Vaadin Portlet";
    public final String LABEL_NEW_LIFERAY_PORTLET = "New Liferay Portlet";
    public final String LABEL_NEW_LIFERAY_MODULE_PROJECT_FRAGMENT = "New Liferay Module Project Fragment";
    public final String LABEL_NEW_LIFERAY_WORPSPACE_PROJECT = "New Liferay Workspace Project";
    public final String LABEL_NEW_LIFERAY_COMPONENT_CLASS = "New Liferay Component Class";
    public final String LABEL_NEW_LIFERAY_JSF_PROJECT = "New Liferay JSF Project";

    public final String LABEL_LIFERAY_PLUGIN_PROJECT = "Liferay Plugin Project";
    public final String LABEL_LIFERAY_MODULE_PROJECT = "Liferay Module Project";
    public final String LABEL_LIFERAY_PORTLET = "Liferay Portlet";
    public final String LABEL_LIFERAY_JSF_PORTLET = "Liferay JSF Portlet";
    public final String LABEL_LIFERAY_VAADIN_PORTLET = "Liferay Vaadin Portlet";
    public final String LABEL_LIFERAY_HOOK_CONFIGURATION = "Liferay Hook Configuration";
    public final String LABEL_LIFERAY_SERVICE_BUILDER = "Liferay Service Builder";
    public final String LABEL_LIFERAY_LAYOUT_TEMPLATE = "Liferay Layout Template";
    public final String LABEL_LIFERAY_WORKSPACE_PROJECT = "Liferay Workspace Project";
    public final String LABEL_LIFERAY_COMPONENT_CLASS = "Liferay Component Class";

    public final String TOOLBAR_NEW = "New";
    public final String TOOLBAR_PACKAGE = "Package";
    public final String TOOLBAR_SAVE = "Save (Ctrl+S)";

    public final String MENU_NEW_LIFERAY_PROJECT_EXIS_SOURCE = "New Liferay Plugin Projects from Existing Source";
    public final String MENU_LIFERAY = "Liferay";

    public final String TITLE_DELETE_RESOURCES = "Delete Resources";
    public final String TITLE_NEW_LAYOUT_TEMPLATE = "New Layout Template";
    public final String TITLE_NEW_SERVICE_BUILDER = "New Service Builder";
    public final String TITLE_NEW_LIFERAY_PORTLET = "New Liferay Portlet";
    public final String TITLE_NEW_LIFERAY_HOOK = "New Liferay Hook";

    public final String TEXT_IS_NOT_AN_ABSOLUTE_PATH = " is not an absolute path.";
    public final String TEXT_DIRECTORY_DOESNT_EXIST = " Directory doesn't exist.";
    public final String TEXT_IS_NOT_A_VALID_PATH = " is not a valid path";

    public final String TEXT_PLEASE_SELECT_THE_WORKSPACE_LOCATION = "Please select the workspace location.";

    public final String TITLE_NEW_LIFERAY_VAADIN_PORTLET = "New Liferay Vaadin Portlet";

    public final String TEXT_PLEASE_ENTER_A_PROJECT_NAME = "Please enter a project name.";
    public final String TEXT_ENTER_A_PROJECT_NAME = " Enter a project name.";
    public final String TEXT_THE_PROJECT_NAME_INVALID = " The project name is invalid.";
    public final String TEXT_INVALID_CHARACTER_IN_RESOURCE_NAME = " is an invalid character in resource name ";
    public final String TEXT_PROJECT_NAME_MUST_BE_SPECIFIED = " Project name must be specified";
    public final String TEXT_INVALID_NAME_ON_PLATFORM = " is an invalid name on this platform.";
    public final String TEXT_PROJECT_ALREADY_EXISTS = " A project with that name already exists.";
    public final String TEXT_INVALID_CLASS_NAME = " Invalid class name";

    public final String LABEL_WORKSPACE_NAME = "Workspace name:";
    public final String LABEL_SERVICE_NAME = "Service Name:";
    public final String LABEL_SERVER_NAME = "Server name:";
    public final String LABEL_PROJECT_NAME = "Project name:";
    public final String LABEL_PACKAGE_NAME = "Package name:";
    public final String LABEL_PACKAGE_NAME_UPPERCASE = "Package Name:";
    public final String LABEL_PLUGIN_TYPE = "Plugin type:";
    public final String LABEL_LOCATION = "Location:";
    public final String LABEL_SDK_LOCATION = "SDK Location:";
    public final String LABLE_SELECT_OSGI_BUNDLE = "Select Host OSGi Bundle:";
    public final String LABEL_SERVER_LOCATION = "Liferay Portal Bundle Directory";
    public final String LABEL_USE_DEFAULT_LOCATION = "Use default location";
    public final String LABEL_BUNDLE_URL = "Bundle url:";
    public final String LABEL_SERVER_HOST_NAME = "Server's host name:";

    public final String BUTTON_ADD_FILES_TO_OVERRIDE = "Add Files From OSGi Bundle to Override...";
    public final String SERVER_STARTUP_MESSAGE = "Server startup in";
    public final String SERVER_STOP_MESSAGE = "stop Stopping ProtocolHandler";

    public final String TEXT_BUILD_TYPE_GRADLE = "Gradle";
    public final String TEXT_BUILD_TYPE_MAVEN = "Maven";

    public final String NODE_LIFERAY_7X = "Liferay 7.x";
    public final String NODE_LIFERAY_INC = "Liferay, Inc.";
    public final String SERVER_START_BUTTON = "Start the server (Ctrl+Alt+R)";
    public final String SERVER_STOP_BUTTON = "Stop the server (Ctrl+Alt+S)";
    public final String SERVER_DEBUG_BUTTON = "Start the server in debug mode (Ctrl+Alt+D)";
}
