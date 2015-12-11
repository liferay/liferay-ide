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
 * @author Li Lu
 */
public interface ProjectBuildAction extends UIBase
{

    public final String DELETE_FROM_DISK = "Delete project contents on disk (cannot be undone)";
    public final String DELETE_RESOURCE = "Delete Resources";

    public final String MENU_LIFERAY = "Liferay";
    public final String MENU_MAVEN = "Maven";
    public final String MENU_SDK = "SDK";

    public final String MENU_FIND_LIFERAY7_API_CHANGES = "Find Liferay 7 breaking API changes...";
    public final String MENU_BUILD_SERVICE = "Build Services";
    public final String MENU_BUILD_WSDD = "Build WSDD";

    public final String MENU_ALL = "all";
    public final String MENU_BUILD_CLIENT = "build-client";
    public final String MENU_BUILD_CSS = "build-css";
    public final String MENU_BUILD_DB = "build-db";
    public final String MENU_BUILD_LANG = "build-lang";
    public final String MENU_BUILD_SERVICE_GOAL = "build-service";
    public final String MENU_BUILD_WSDD_GOAL = "build-wsdd";
    public final String MENU_CLEAN = "clean";
    public final String MENU_COMPILE = "compile";
    public final String MENU_COMPILE_TEST = "compile-test";
    public final String MENU_DEPLOY = "deploy";
    public final String MENU_DIRECT_DEPLOY = "direct-deploy";
    public final String MENU_FORMAT_SOURCE = "format-source";
    public final String MENU_MERGE = "merge";
    public final String MENU_TEST = "test";
    public final String MENU_WAR = "war";

    public final String MENU_SELECT_MAVEN_PROFILES = "Select Maven Profiles...";
    public final String MENU_UPDATE_PROJECT = "Update Project...";
}
