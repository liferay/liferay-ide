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

package com.liferay.ide.project.ui.tests.page;

import com.liferay.ide.ui.tests.UIBase;

/**
 * @author Ashley Yuan
 * @author Li Lu
 */
public interface CreateLiferayPortletWizard extends UIBase
{

    public final int INDEX_DEFAULT_VALIDATION_PORTLET_MESSAGE = 3;
    public final int INDEX_VALIDATION_PORTLET_MESSAGE2 = 5;
    public final int INDEX_VALIDATION_PORTLET_MESSAGE3 = 6;
    public final int INDEX_VALIDATION_PORTLET_MESSAGE4 = 0;

    String LABEL_JAVA_PACKAGE = "Java package:";
    String LABEL_PORTLET_CLASS = "Portlet class:";
    String LABEL_PORTLET_PLUGIN_PROJECT = "Portlet plugin project:";
    String LABEL_SOURCE_FOLDER = "Source folder:";
    String LABEL_SUPERCLASS = "Superclass:";

    String RADIO_CREATE_NEW_PORTLET = "Create new portlet";
    String RADIO_USE_DEFAULT_PORTLET = "Use default portlet (MVCPortlet)";

    public final String LABEL_NAME = "Name:";
    public final String LABEL_PORTLET_DISPLAY_NAME = "Display name:";
    public final String LABEL_TITLE = "Title:";
    public final String LABEL_VIEW = "View";
    public final String LABEL_EDIT = "Edit";
    public final String LABEL_HELP = "Help";
    public final String LABEL_ABOUT = "About";
    public final String LABEL_CONFIG = "Config";
    public final String LABEL_EDIT_DEFAULTS = "Edit Defaults";
    public final String LABEL_EDIT_GUEST = "Edit Guest";
    public final String LABEL_PREVIEW = "Priview";
    public final String LABEL_PRINT = "Print";
    public final String LABEL_CREATE_JSP_FILES = "Create JSP files";
    public final String LABEL_JSP_FOLDER = "JSP folder:";
    public final String LABEL_CREATE_RESOURCE_BUNDLER_FILE = "Create resource bundle file";
    public final String LABEL_RESOURCE_BUNDLE_FILE_PATH = "Resource bundle file path:";
    public final String LABEL_ICON = "Icon:";
    public final String LABEL_ALLOW_MULTIPLE_INSTANCES = "Allow multiple instances";
    public final String LABEL_CSS = "CSS:";
    public final String LABEL_JAVASCRIPT = "JavaScript:";
    public final String LABEL_CSS_CLASS_WRAPPER = "CSS class wrapper:";

    public final String LABEL_DISPLAY_CATEGORY = "Display Category:";
    public final String LABEL_ADD_TO_CONTROL_PANEL = "Add to Control Panel";
    public final String LABEL_ENTRY_CATEGORY = "Entry Category:";
    public final String LABEL_ENTRY_WEIGHT = "Entry Weight:";
    public final String LABEL_CREATE_ENTRY_CLASS = "Create Entry Class";
    public final String LABEL_ENTRY_CLASS = "Entry Class:";

    public final String LABEL_PUBLIC = "public";
    public final String LABEL_ABSTRACT = "abstract";
    public final String LABEL_FINAL = "final";
    public final String LABEL_INTERFACES = "Interfaces:";
    public final String LABEL_CONSTRUCTORS_FROM_SUPERCLASS = "Constructors from superclass";
    public final String LABEL_INHERITED_ABSTRACT_METHODS = "Inherited abstract methods";
    public final String LABEL_INIT = "init";
    public final String LABEL_DESTORY = "destory";
    public final String LABEL_DO_VIEW = "doView";
    public final String LABEL_DO_EDIT = "doEdit";
    public final String LABEL_DO_HELP = "doHelp";
    public final String LABEL_DO_ABOUT = "doAbout";
    public final String LABEL_DO_CONFIG = "doConfig";
    public final String LABEL_DO_EDIT_DEFAULTS = "doEditDefaults";
    public final String LABEL_DO_EDIT_GUEST = "doEditGuest";
    public final String LABEL_DO_PREEVIEW = "doPreview";
    public final String LABEL_DO_PRINT = "doPrint";
    public final String LABEL_PROCESS_ACTION = "processAction";
    public final String LABEL_SERVE_RESOURCE = "serveResource";

}
