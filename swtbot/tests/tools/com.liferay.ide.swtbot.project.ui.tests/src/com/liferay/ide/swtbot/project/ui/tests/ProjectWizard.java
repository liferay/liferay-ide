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
 * @author Terry Jia
 * @author Ashley Yuan
 * @author Ying Xu
 * @author Vicky Wang
 * @author Li Lu
 */
public interface ProjectWizard extends UIBase
{

    public final int INDEX_VALIDATION_MESSAGE1 = 0;
    public final int INDEX_VALIDATION_MESSAGE2 = 1;
    public final int INDEX_NEW_LIFERAY_PLUGIN_PROJECT_VALIDATION_MESSAGE = 2;
    public final int INDEX_NEW_LIFERAY_PLUGIN_PROJECT_WIZARD = 2;
    public final int INDEX_SELECT_A_WIZARD_VALIDATION_MESSAGE = 1;
    public final int INDEX_VALIDATION_MESSAGE4 = 3;
    public final int INDEX_VALIDATION_MESSAGE6 = 5;
    public final int INDEX_VALIDATION_MESSAGE7 = 6;
    public final int INDEX_SDK_SETTING_VALIDATION_MESSAGE = 1;
    public final int INDEX_THEME_PARENT_AND_FRAMEWORK_VALIDATIONG_MESSAGE = 0;

    public final String LABEL_ACTIVE_PROFILES = "Active profiles:";
    public final String LABEL_ARTIFACT_VERSION = "Artifact version:";
    public final String LABEL_GROUP_ID = "Group id:";
    public final String LABEL_DISPLAY_NAME = "Display name:";
    public final String LABEL_FOLDER_NAME = "Folder name:";

    public final String LABEL_IGNORE_OPTIONAL_COMPILE_PROBLEMS = "Ignore optional compile problems";
    public final String LABEL_JAVA_PROJECT = "Java Project";
    public final String LABEL_LAUNCH_NEW_PORTLET_WIZARD_AFTER_PROJECT =
        "Launch New Portlet Wizard after project is created";
    public final String LABEL_PORTLET_NAME = "Portlet name:";

    public final String LABEL_UPDATE_EXCLUSION_FILTERS =
        "Update exclusion filters in other source folders to solve nesting";

    public final String MENU_BUILD_TYPE_ANT = "Ant (liferay-plugins-sdk)";
    public final String MENU_BUILD_TYPE_MAVEN = "Maven (liferay-maven-plugin)";
    public final String MENU_EXT = "Ext";
    public final String MENU_HOOK = "Hook";
    public final String MENU_LAYOUT_TEMPLATE = "Layout Template";
    public final String MENU_PORTLET = "Portlet";
    public final String MENU_SERVICE_BUILDER_PORTLET = "Service Builder Portlet";
    public final String MENU_WEB = "Web";
    public final String MENU_THEME = "Theme";
    public final String MENU_THEME_FRAMEWORK_FREEMARKER = "Freemarker";
    public final String MENU_THEME_FRAMEWORK_JSP = "JSP";
    public final String MENU_THEME_FRAMEWORK_VELOCITY = "Velocity";
    public final String MENU_THEME_PARENT_CLASSIC = "classic";
    public final String MENU_THEME_PARENT_STYLED = "_styled";
    public final String MENU_THEME_PARENT_UNSTYLED = "_unstyled";

    public final String TEXT_ADDITIONAL_PORTLET_OPTIONS = "Additional portlet options";
    public final String TEXT_CHOOSE_AVAILABLE_PORTLET_FRAMEWORKS =
        "Choose from available portlet frameworks depending on which technology is most appropriate for this project.";
    public final String TEXT_CHOOSE_PLUGINS_SDK_AND_OPEN = "Choose Plugins SDK and Open in Workspace";

    public final String TEXT_CREATE_A_JAVA_PROJECT = "Create a Java project";
    public final String TEXT_CREATE_A_JAVA_PROJECT_IN_WORKSPACE =
        "Create a Java project in the workspace or in an external location.";
    public final String TEXT_CREATE_A_NEW_PROJECT_RESOURCE = "Create a new project resource";
    public final String TEXT_CREATE_NEW_PROJECT_AS_LIFERAY_PLUGIN =
        "Create a new project configured as a Liferay plugin";

    public final String TEXT_PORTLET_FRAMEWORK_REQUIRES_SDK =
        "Selected portlet framework requires SDK version at least 7.0.0";
    public final String TEXT_PROJECT_EXISTS_IN_LOCATION =
        " is not valid because a project already exists at that location.";

    public final String TEXT_PROJECT_NAME_MUST_SPECIFIED = "Project name must be specified";
    public final String TEXT_SDK_LOCATION_EMPTY = " This sdk location is empty ";
    public final String TEXT_USE_DEFAULT_MVC_PORTLET = "Use default portlet (MVCPortlet)";

    public final String TEXT_WEB_SDK_62_ERRORR_MESSAGE =
        " The selected Plugins SDK does not support creating new web type plugins.  Please configure version 7.0.0 or greater.";
    public final String THEME_FARMEWORK_TYPE = "Theme framework:";
    public final String THEME_PARENT_TYPE = "Theme parent:";
    public final String THEME_DEFAULT_MESSAGE = "Select options for creating new theme project.";

    public final String THEME_DONOT_SUPPORT_MESSAGE = "Select options for creating new theme project.";

    public final String THEME_WARNING_MESSAGE = " For advanced theme developers only.";

    public final String TOOLTIP_LEARN_MORE = "Learn More...";

    public final String MESAGE_SDK_NOT_SUPPORT =
        " The selected Plugins SDK does not support creating ext type plugins.  Please configure version 6.2 or less.";

    public final String LABEL_JSF_FRAMEWORK = "JSF 2.x";
    public final String LABEL_LIFERAY_MVC_FRAMEWORK = "Liferay MVC";
    public final String LABEL_SPRING_MVC_FRAMEWORK = "Spring MVC";
    public final String LABEL_VAADIN_FRAMEWORK = "Vaadin";

    public final String LABLE_ARCHETYPE = "Archetype:";
    public final String LABEL_JSF_STANDARD = "JSF standard";
    public final String LABLE_ICE_FACES = "ICEfaces";
    public final String LABLE_LIFERAY_FACES_ALLOY = "Liferay Faces Alloy";
    public final String LABLE_PRIME_FACES = "PrimeFaces";
    public final String LABLE_RICH_FACES = "RichFaces";

}
