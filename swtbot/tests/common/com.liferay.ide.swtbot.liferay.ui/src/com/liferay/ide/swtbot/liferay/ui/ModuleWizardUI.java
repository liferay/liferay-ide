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

package com.liferay.ide.swtbot.liferay.ui;

/**
 * @author Ying Xu
 * @author Sunny Shi
 * @author Ashley Yuan
 */
public interface ModuleWizardUI extends WizardUI
{

    public final String MODULE_PROJECT_TEMPLATE_NAME = "Project Template Name:";
    public final String MODULE_COMPONENT_CLASS_NAME = "Component Class Name:";
    public final String MODULE_PROPERTIES = "Properties:";
    public final String MODULE_SELECT_SERVICE_NAME = "Select Service Name:";

    public final String USE_DEFAULT_LOCATION = "Use default location";

    public final String NAME = "Name";
    public final String VALUE = "Value";

    public final String ADD_PROPERTY_KEY = "Add Property Key";
    public final String MOVE_UP = "Move Up";
    public final String MOVE_DOWN = "Move Down";

    public final String MODULE_MVC_PORTLET = "mvc-portlet";
    public final String MODULE_ACTIVATOR = "activator";
    public final String MODULE_CONTENT_TARGETING_RULE = "content-targeting-rule";
    public final String MODULE_CONTENT_TARGETING_TRACKING_ACTION = "content-targeting-tracking-action";
    public final String MODULE_CONTROL_MENU_ENTRY = "control-menu-entry";
    public final String MODULE_PORTLET = "portlet";
    public final String MODULE_SERVICE = "service";
    public final String MODULE_SERVICE_BUILDER = "service-builder";
    public final String MODULE_SERVICE_WRAPPER = "service-wrapper";
    public final String MODULE_CONTENT_TARGETING_REPORT = "content-targeting-report";
    public final String MODULE_PANEL_APP = "panel-app";
    public final String MODULE_PORTLET_PROVIDER = "portlet-provider";
    public final String MODULE_API = "api";
    public final String MODULE_PORTLET_CONFIGURATION_ICON = "portlet-configuration-icon";
    public final String MODULE_PORTLET_TOOLBAR_CONTRIBUTOR = "portlet-toolbar-contributor";
    public final String MODULE_SIMULATION_PANEL_ENTRY = "simulation-panel-entry";
    public final String MODULE_TEMPLATE_CONTEXT_CONTRIBUTOR = "template-context-contributor";
    public final String MODULE_REST = "rest";
    public final String MODULE_THEME = "theme";
    public final String MODULE_THEME_CONTRIBUTOR = "theme-contributor";
    public final String MODULE_FORM_FIELD = "form-field";
    public final String MODULE_FREEMARKER_PORTLET = "freemarker-portlet";
    public final String MODULE_LAYOUT_TEMPLATE = "layout-template";
    public final String MODULE_SOY_PORTLET = "soy-portlet";
    public final String MODULE_SPRING_MVC_PORTLET = "spring-mvc-portlet";

    public final String NEW_LIFERAY_MODULE_MESSAGE =
        "Enter a name and choose a template to use for a new Liferay module.";
    public final String LOCATION_MUST_BE_SPECIFIED = " Location must be specified.";
    public final String NAME_MUST_BE_SPECIFIED = " Name must be specified";
    public final String VALUE_MUST_BE_SPECIFIED = " Value must be specified";
    public final String INVALID_PACKAGE_NAME = " Invalid package name";
    public final String CONFIGURE_COMPONENT_CLASS = "Configure Component Class";
    public final String SERVICE_NAME_MUST_BE_SPECIFIED = " Service Name must be specified";

    public final int INDEX_NEW_LIFERAY_MODULE_PROJECT_VALIDATION_MESSAGE = 2;
    public final int INDEX_CONFIGURE_COMPONENT_CLASS_VALIDATION_MESSAGE = 2;
    public final int INDEX_SERVICE_BUILDER_CONFIGURE_COMPONENT_CLASS_VALIDATION_MESSAGE = 2;
    public final int INDEX_SERVICE_CONFIGURE_COMPONENT_CLASS_VALIDATION_MESSAGE = 3;
    public final int INDEX_SERVICE_WRAPPER_CONFIGURE_COMPONENT_CLASS_VALIDATION_MESSAGE = 3;

    String[] expectedModuleProjectTemplateItems = { MODULE_ACTIVATOR, MODULE_API, MODULE_CONTENT_TARGETING_REPORT,
        MODULE_CONTENT_TARGETING_RULE, MODULE_CONTENT_TARGETING_TRACKING_ACTION, MODULE_CONTROL_MENU_ENTRY,
        MODULE_FORM_FIELD, MODULE_FREEMARKER_PORTLET, MODULE_LAYOUT_TEMPLATE, MODULE_MVC_PORTLET, MODULE_PANEL_APP,
        MODULE_PORTLET, MODULE_PORTLET_CONFIGURATION_ICON, MODULE_PORTLET_PROVIDER, MODULE_PORTLET_TOOLBAR_CONTRIBUTOR,
        MODULE_REST, MODULE_SERVICE, MODULE_SERVICE_BUILDER, MODULE_SERVICE_WRAPPER, MODULE_SIMULATION_PANEL_ENTRY,
        MODULE_SOY_PORTLET, MODULE_SPRING_MVC_PORTLET, MODULE_TEMPLATE_CONTEXT_CONTRIBUTOR, MODULE_THEME,
        MODULE_THEME_CONTRIBUTOR };

    String[] templatesWithoutPropertyKeys =
        { MODULE_ACTIVATOR, MODULE_API, MODULE_CONTENT_TARGETING_REPORT, MODULE_CONTENT_TARGETING_RULE,
            MODULE_CONTENT_TARGETING_TRACKING_ACTION, MODULE_THEME_CONTRIBUTOR, MODULE_PORTLET_PROVIDER };

    String[] expectedBuildTypeItems = { TEXT_BUILD_TYPE_GRADLE, TEXT_BUILD_TYPE_MAVEN };

}
