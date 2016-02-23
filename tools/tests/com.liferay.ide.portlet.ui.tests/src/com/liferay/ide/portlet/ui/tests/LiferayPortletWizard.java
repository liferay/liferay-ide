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

package com.liferay.ide.portlet.ui.tests;

import com.liferay.ide.ui.tests.UIBase;

/**
 * @author Ashley Yuan
 */
public interface LiferayPortletWizard extends UIBase
{

    public final String LABEL_PORTLET_PLUGIN_PROJECT = "Portlet plugin project:";
    public final String LABEL_SOURCE_FOLDER = "Source folder:";
    public final String LABEL_PORTLET_CLASS = "Portlet class:";
    public final String LABEL_JAVA_PACKAGE = "Java package:";
    public final String LABEL_SUPERCLASS = "Superclass:";

    public final String RADIO_CREATE_NEW_PORTLET = "Create new portlet";
    public final String RADIO_USE_DEFAULT_PORTLET = "Use default portlet (MVCPortlet)";

    public final String LABEL_NAME = "Name:";
    // public final String LABEL_DISPLAY_NAME = "Display name:";
    public final String LABEL_TITLE = "Title:";

    public final String CHECKBOX_PORTLET_MODE_VIEW = "View";
    public final String CHECKBOX_PORTLET_MODE_EDIT = "Edit";
    public final String CHECKBOX_PORTLET_MODE_HELP = "Help";

    public final String CHECKBOX_LIFERAY_PORTLET_MODE_ABOUT = "About";
    public final String CHECKBOX_LIFERAY_PORTLET_MODE_CONFIG = "Config";
    public final String CHECKBOX_LIFERAY_PORTLET_MODE_EDITDEFAULTS = "Edit Defaults";
    public final String CHECKBOX_LIFERAY_PORTLET_MODE_EDITGUEST = "Edit Guest";
    public final String CHECKBOX_LIFERAY_PORTLET_MODE_PREVIEW = "Preview";
    public final String CHECKBOX_LIFERAY_PORTLET_MODE_PRINT = "Print";

    public final String CHECKBOX_CREATE_JSP_FILES = "Create JSP files";
    public final String LABEL_JSP_FOLDER = "JSP folder:";
    public final String CHECKBOX_CREATE_RESOURCE_BUNDLE_FILE = "Create resource bundle file";
    public final String LABEL_RESOURCE_BUNDLE_FILE_PATH = "Resource bundle file path:";

    public final String LABEL_ICON = "Icon:";
    public final String CHECKBOX_ALLOW_MULTIPLE_INSTANCES = "Allow multiple instances";
    public final String LABEL_CSS = "CSS:";
    public final String LABEL_JAVASCRIPT = "JavaScript:";
    public final String LABEL_CSS_CLASS_WRAPPER = "CSS class wrapper:";
    public final String LABEL_DISPLAY_CATEGORY = "Display Category:";
    public final String CHECKBOX_ADD_TO_CONTROL_PANEL = "Add to Control Panel";
    public final String LABEL_ENTRY_CATEGORY = "Entry Category:";
    public final String LABEL_ENTRY_WEIGHT = "Entry Weight:";
    public final String CHECKBOX_CREATE_ENTRY_CLASS = "Create Entry Class";
    public final String LABEL_ENTRY_CLASS = "Entry Class:";

    public final String LABEL_PUBLIC = "public";
    public final String LABEL_ABSTRACT = "abstract";
    public final String LABEL_FINAL = "final";
    public final String LABEL_CONSTRUCTORS_FROM_SUPERCLASS = "Constructors from superclass";
    public final String LABEL_INHERITED_ABSTRACT_METHODS = "Inherited abstract methods";
    public final String LABEL_INIT = "init";
    public final String LABEL_DESTROY = "destroy";
    public final String LABEL_DOVIEW = "doView";
    public final String LABEL_DOEDIT = "doEdit";
    public final String LABEL_DOHELP = "doHelp";
    public final String LABEL_DOABOUT = "doAbout";
    public final String LAEBL_DOCONFIG = "doConfig";
    public final String LABEL_DOEDITDEFAULTS = "doEditDefaults";
    public final String LABEL_DOEDITGUEST = "doEditGuest";
    public final String LABEL_DOPREVIEW = "doPreview";
    public final String LABEL_DOPRINT = "doPrint";
    public final String LABEL_PROCESSACTION = "processAction";
    public final String LABEL_SERVERESOURCE = "serveResource";
    public final String LABEL_INTERFACES = "Interfaces:";

    public final String TOOLTIP_MENU_ITEM_NEW_LIFERAY_PORTLET = "New Liferay Portlet";

    public final String TEXT_ENTER_A_PROJECT_NAME = " Enter a project name.";
    public final String TEXT_CREATE_A_PORTLET_CLASS = "Create a portlet class.";
    public final String TEXT_SOURCE_FOLDER_CANNOT_BE_EMPTY = " The source folder cannot be empty.";
    public final String TEXT_SOUCCE_FOLDER_MUST_BE_ABSOLUTE_PATH =
        " Source folder path must be a workspace relative absolute path.";
    public final String TEXT_CREATE_A_NEW_SOURCE_FOLDER = "Create a new source folder.";
    public final String TEXT_CREATE_A_PROJECT_FOR_DEVELOPING_LIFERAY_PLUGINS =
        "Create a project for developing Liferay Plugins to deploy to Liferay Portal Server";
    public final String TEXT_NOT_A_JAVA_SOURCE_FOLDER = " Not a Java source folder.";
    public final String TEXT_CLASS_NAME_CANNOT_BE_EMPTY = " The class name cannot be empty.";
    public final String TEXT_INVALID_JAVA_CLASS_NAME = " Invalid Java class name: The type name ";
    public final String TEXT_INVALID_JAVA_PACKAGE_NAME = " Invalid Java package name: ";
    public final String TEXT_NOT_A_VALID_IDENTIFIER = " is not a valid identifier";
    public final String TEXT_NOT_A_VALID_JAVA_IDENTIFIER = " is not a valid Java identifier";
    public final String TEXT_JAVA_TYPE_START_WITH_AN_UPPERCASE_LETTER =
        " Warning: By convention, Java type names usually start with an uppercase letter";
    public final String TEXT_PACKAGE_NAME_CANNOT_END_WITH_DOT = "A package name cannot start or end with a dot";
    public final String TEXT_JAVA_PACKAGE_START_WITH_AN_UPPERCASE_LETTER =
        " Warning: By convention, package names usually start with a lowercase letter";
    public final String TEXT_MUST_SPECIFY_A_PORTLET_SUPERCLASS = " Must specify a portlet superclass.";
    public final String TEXT_SUPERCLASS_MUST_BE_VALID = " Portlet superclass must be a valid portlet class.";
    public final String TEXT_ALREADY_EXISTS = " already exists.";

    public final String TEXT_SPECIFY_PORTLET_DEPLOYMENT_DESCRIPTOR_DETAILS =
        "Specify portlet deployment descriptor details.";
    public final String TEXT_PORTLET_NAME_ALREADY_EXISTS = " Portlet name already exists.";
    public final String TEXT_VIEW_JSP_EXSITS_AND_OVERWRITTEN = " View jsp already exists and will be overwritten.";
    public final String TEXT_PORTLET_NAME_IS_EMPTY = " Portlet name is empty.";
    public final String TEXT_JSP_FOLDER_CANNOT_EMPTY = " JSP folder cannot be empty.";
    public final String TEXT_FOLDER_VALUE_IS_INVALID = " Folder value is invalid.";
    public final String TEXT_RESOURCE_BUNDLE_FILE_END_WITH_PROPERTIES =
        " Resource bundle file path should end with .properties";
    public final String TEXT_RESOURCE_BUNDLE_FILE_MUST_VALID_PATH = " Resource bundle file path must be a valid path.";

    public final String TEXT_SPECIFY_LIFERAY_PORTLET_DEPLOYMENT_DESCRIPTOR_DETAILS =
        "Specify Liferay portlet deployment descriptor details.";
    public final String TEXT_CATEGORY_NAME_IS_EMPTY = " Category name is empty.";
    public final String TEXT_MUST_SPECIFY_VALID_ENTRY_WEIGHT = " Must specify a valid double for entry weight.";
    public final String TEXT_DONOT_USE_QUALIDIED_CLASS_NAME = " Do not use qualified class name.";

    public final String TEXT_SPECIFY_STUBS_TO_GENERATE_IN_PORTLET_CLASS =
        "Specify modifiers, interfaces, and method stubs to generate in Portlet class.";

    public String[] availableSuperclasses = { "com.liferay.util.bridges.mvc.MVCPortlet",
        "com.liferay.portal.kernel.portlet.LiferayPortlet", "javax.portlet.GenericPortlet" };

    public String[] availableDisplayCategories = { "OpenSocial", "Portal", "Tools", "Content", "Social",
        "Collaboration", "Google", "Sun", "Shopping", "Workflow", "Finance", "Community", "World of Liferay", "WSRP",
        "Christianity", "Spring", "Content Management", "Sandbox", "Test", "Gadgets", "Marketplace", "Library",
        "Entertainment", "Alfresco", "Knowledge Base", "Religion", "Wiki", "Admin", "Undefined", "Development",
        "Sample", "Science", "Web Content Management", "News" };

    public String[] availableEntryCategories =
        { "Control Panel - Apps", "Control Panel - Configuration", "My Account Administration",
            "Site Administration - Configuration", "Site Administration - Content", "Site Administration - Pages",
            "Site Administration - Users", "Control Panel - Sites", "Control Panel - Users" };

}
