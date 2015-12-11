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

package com.liferay.ide.project.ui.tests;

import com.liferay.ide.ui.tests.UIBase;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 * @author Ying Xu
 * @author Vicky Wang
 */
public interface ProjectWizard extends UIBase
{

    public final String LABEL_PROJECT_NAME = "Project name:";
    public final String LABEL_SDK_LOCATION = "SDK Location:";
    public final String LABEL_PLUGIN_TYPE = "Plugin type:";
    public final String LABEL_DISPLAY_NAME = "Display name:";
    public final String LABEL_PORTLET_NAME = "Portlet name:";
    public final String LABEL_BUILD_TYPE = "Build type:";
    public final String LABEL_INCLUDE_SAMPLE_CODE = "Include sample code";
    public final String LABEL_LAUNCH_NEW_PORTLET_WIZARD_AFTER_PROJECT =
        "Launch New Portlet Wizard after project is created";
    public final String LABEL_ARTIFACT_VERSION = "Artifact version:";
    public final String LABEL_GROUP_ID = "Group id:";
    public final String LABEL_ACTIVE_PROFILES = "Active profiles:";
    public final String LABEL_USE_DEFAULT_LOCATION = "Use default location";
    public final String LABEL_LOCATION = "Location:";

    public final String TEXT_BLANK = "";
    public final String TEXT_PROJECT_NAME_MUST_SPECIFIED = "Project name must be specified";
    public final String TEXT_ENTER_PROJECT_NAME = "Please enter a project name.";
    public final String TEXT_ADD_PROJECT_TO_WORKING_SET = "Add project to working set";
    public final String TEXT_WORKING_SET = "Working set:";
    public final String TEXT_CREATE_NEW_PROJECT_AS_LIFERAY_PLUGIN =
        "Create a new project configured as a Liferay plugin";
    public final String TEXT_SDK_LOCATION_EMPTY = " This sdk location is empty ";
    public final String TEXT_CHOOSE_PLUGINS_SDK_AND_OPEN = "Choose Plugins SDK and Open in Workspace";
    public final String TEXT_CHOOSE_AVAILABLE_PORTLET_FRAMEWORKS =
        "Choose from available portlet frameworks depending on which technology is most appropriate for this project.";
    public final String TEXT_PORTLET_FRAMEWORK_REQUIRES_SDK =
        "Selected portlet framework requires SDK version at least 7.0.0";
    public final String TEXT_ADDITIONAL_PORTLET_OPTIONS = "Additional portlet options";
    public final String TEXT_CREATE_NEW_PORTLET = "Create new portlet";
    public final String TEXT_USE_DEFAULT_MVC_PORTLET = "Use default portlet (MVCPortlet)";
    public final String TEXT_PROJECT_ALREADY_EXISTS = " A project with that name already exists.";
    public final String TEXT_PROJECT_EXISTS_IN_LOCATION =
        " is not valid because a project already exists at that location.";
    public final String TEXT_INVALID_CHARACTER_IN_RESOURCE_NAME = " is an invalid character in resource name ";
    public final String TEXT_INVALID_NAME_ON_PLATFORM = " is an invalid name on this platform.";
    public final String TEXT_LIFERAY_MVC_FRAMEWORK = "Liferay MVC";
    public final String TEXT_JSF_FRAMEWORK = "JSF 2.x";
    public final String TEXT_SPRING_MVC_FRAMEWORK = "Spring MVC";
    public final String TEXT_VAADIN_FRAMEWORK = "Vaadin";
    public final String TEXT_WEB_SDK_62_ERRORR_MESSAGE =
        " The selected Plugins SDK does not support creating new web type plugins.  Please configure version 7.0.0 or greater.";
    public final String TEXT_PROJECT_NAME_MUST_BE_SPECIFIED = " Project name must be specified";

    public final String TOOLTIP_CREATE_LIFERAY_PROJECT = "Create a new Liferay Plugin Project";
    public final String TOOLTIP_MENU_ITEM_NEW_LIFERAY_PROJECT = "New Liferay Plugin Project";
    public final String TOOLTIP_LEARN_MORE = "Learn More...";
    public final String TOOLTIP_BROWSE = "Browse";
    public final String TOOLTIP_NEW_LIFERAY_PORTLET = "New Liferay Portlet";

    public final int INDEX_VALIDATION_MESSAGE3 = 2;
    public final int INDEX_VALIDATION_MESSAGE2 = 1;
    public final int INDEX_VALIDATION_MESSAGE1 = 0;
    public final int INDEX_DEFAULT_VALIDATION_MESSAGE = -1;

    public final String MENU_PORTLET = "Portlet";
    public final String MENU_SERVICE_BUILDER_PORTLET = "Service Builder Portlet";
    public final String MENU_EXT = "Ext";
    public final String MENU_THEME = "Theme";
    public final String MENU_HOOK = "Hook";
    public final String MENU_LAYOUT_TEMPLATE = "Layout Template";
    public final String MENU_WEB = "Web";
    public final String MENU_BUILD_TYPE_ANT = "Ant (liferay-plugins-sdk)";
    public final String MENU_BUILD_TYPE_MAVEN = "Maven (liferay-maven-plugin)";

    public final String THEME_PARENT_TYPE = "Theme parent:";
    public final String THEME_FARMEWORK_TYPE = "Theme framework:";
    public final String MENU_THEME_PARENT_STYLED = "_styled";
    public final String MENU_THEME_PARENT_UNSTYLED = "_unstyled";
    public final String MENU_THEME_PARENT_CLASSIC = "classic";
    public final String MENU_THEME_FRAMEWORK_FREEMARKER = "Freemarker";
    public final String MENU_THEME_FRAMEWORK_VELOCITY = "Velocity";
    public final String MENU_THEME_FRAMEWORK_JSP = "JSP";

}
