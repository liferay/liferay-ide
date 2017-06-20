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

package com.liferay.ide.swtbot.project.ui.tests;

import com.liferay.ide.swtbot.ui.tests.UIBase;

/**
 * @author Ying Xu
 * @author Sunny Shi
 * @author Ashley Yuan
 */
public interface NewLiferayModuleProjectWizard extends UIBase
{

    public final String LABEL_MODULE_PROJECT_TEMPLATE_NAME = "Project Template Name:";
    public final String LABEL_MODULE_COMPONENT_CLASS_NAME = "Component Class Name:";
    public final String LABEL_MODULE_PROPERTIES = "Properties:";
    public final String LABEL_MODULE_SELECT_SERVICE_NAME = "Select Service Name:";

    public final String CHECKBOX_USE_DEFAULT_LOCATION = "Use default location";

    public final String TABLE_NAME = "Name";
    public final String TABLE_VALUE = "Value";

    public final String TOOLBARBOTTON_ADD_PROPERTY_KEY = "Add Property Key";
    public final String TOOLBARBOTTON_MOVE_UP = "Move Up";
    public final String TOOLBARBOTTON_MOVE_DOWN = "Move Down";
    public final String TOOLBARBOTTON_DELETE = "Delete";

    public final String MENU_MODULE_MVC_PORTLET = "mvc-portlet";
    public final String MENU_MODULE_ACTIVATOR = "activator";
    public final String MENU_MODULE_CONTENT_TARGETING_RULE = "content-targeting-rule";
    public final String MENU_MODULE_CONTENT_TARGETING_TRACKING_ACTION = "content-targeting-tracking-action";
    public final String MENU_MODULE_CONTROL_MENU_ENTRY = "control-menu-entry";
    public final String MENU_MODULE_PORTLET = "portlet";
    public final String MENU_MODULE_SERVICE = "service";
    public final String MENU_MODULE_SERVICE_BUILDER = "service-builder";
    public final String MENU_MODULE_SERVICE_WRAPPER = "service-wrapper";
    public final String MENU_MODULE_CONTENT_TARGETING_REPORT = "content-targeting-report";
    public final String MENU_MODULE_PANEL_APP = "panel-app";
    public final String MENU_MODULE_PORTLET_PROVIDER = "portlet-provider";
    public final String MENU_MODULE_API = "api";
    public final String MENU_MODULE_PORTLET_CONFIGURATION_ICON = "portlet-configuration-icon";
    public final String MENU_MODULE_PORTLET_TOOLBAR_CONTRIBUTOR = "portlet-toolbar-contributor";
    public final String MENU_MODULE_SIMULATION_PANEL_ENTRY = "simulation-panel-entry";
    public final String MENU_MODULE_TEMPLATE_CONTEXT_CONTRIBUTOR = "template-context-contributor";
    public final String MENU_MODULE_REST = "rest";
    public final String MENU_MODULE_THEME = "theme";
    public final String MENU_MODULE_THEME_CONTRIBUTOR = "theme-contributor";
    public final String MENU_MODULE_FORM_FIELD = "form-field";

    public final String TEXT_NEW_LIFERAY_MODULE_MESSAGE =
        "Enter a name and choose a template to use for a new Liferay module.";
    public final String TEXT_LOCATION_MUST_BE_SPECIFIED = " Location must be specified.";
    public final String TEXT_NAME_MUST_BE_SPECIFIED = " Name must be specified";
    public final String TEXT_VALUE_MUST_BE_SPECIFIED = " Value must be specified";
    public final String TEXT_INVALID_PACKAGE_NAME = " Invalid package name";
    public final String TEXT_CONFIGURE_COMPONENT_CLASS = "Configure Component Class";
    public final String TEXT_SERVICE_NAME_MUST_BE_SPECIFIED = " Service Name must be specified";

    public final int INDEX_NEW_LIFERAY_MODULE_PROJECT_VALIDATION_MESSAGE = 2;
    public final int INDEX_CONFIGURE_COMPONENT_CLASS_VALIDATION_MESSAGE = 2;
    public final int INDEX_SERVICE_BUILDER_CONFIGURE_COMPONENT_CLASS_VALIDATION_MESSAGE = 2;
    public final int INDEX_SERVICE_CONFIGURE_COMPONENT_CLASS_VALIDATION_MESSAGE = 3;
    public final int INDEX_SERVICE_WRAPPER_CONFIGURE_COMPONENT_CLASS_VALIDATION_MESSAGE = 3;

    String[] expectedModuleProjectTemplateItems = { MENU_MODULE_ACTIVATOR, MENU_MODULE_API,
        MENU_MODULE_CONTENT_TARGETING_REPORT, MENU_MODULE_CONTENT_TARGETING_RULE,
        MENU_MODULE_CONTENT_TARGETING_TRACKING_ACTION, MENU_MODULE_CONTROL_MENU_ENTRY, MENU_MODULE_FORM_FIELD,
        MENU_MODULE_MVC_PORTLET, MENU_MODULE_PANEL_APP, MENU_MODULE_PORTLET, MENU_MODULE_PORTLET_CONFIGURATION_ICON,
        MENU_MODULE_PORTLET_PROVIDER, MENU_MODULE_PORTLET_TOOLBAR_CONTRIBUTOR, MENU_MODULE_REST, MENU_MODULE_SERVICE,
        MENU_MODULE_SERVICE_BUILDER, MENU_MODULE_SERVICE_WRAPPER, MENU_MODULE_SIMULATION_PANEL_ENTRY,
        MENU_MODULE_TEMPLATE_CONTEXT_CONTRIBUTOR, MENU_MODULE_THEME, MENU_MODULE_THEME_CONTRIBUTOR };

    String[] templatesWithoutPropertyKeys = { MENU_MODULE_ACTIVATOR, MENU_MODULE_API,
        MENU_MODULE_CONTENT_TARGETING_REPORT, MENU_MODULE_CONTENT_TARGETING_RULE,
        MENU_MODULE_CONTENT_TARGETING_TRACKING_ACTION, MENU_MODULE_THEME_CONTRIBUTOR, MENU_MODULE_PORTLET_PROVIDER };

    String[] expectedBuildTypeItems = { TEXT_BUILD_TYPE_GRADLE, TEXT_BUILD_TYPE_MAVEN };

}
