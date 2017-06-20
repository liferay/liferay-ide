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
 * @author Ashley Yuan
 */
public interface ImportLiferayModuleProject extends UIBase
{

    public final String LABEL_IMPORT_MODULE_PROJECTS = "Liferay Module Project(s)";

    public final String TEXT_SELECT_IMPORT_LIFERAY_MODULE_PROJECTS_VALIDATION_MESSAGE =
        "Import existing liferay module project(s)";
    public final String TEXT_SELECT_LOCATION_OF_MODULE_PROJECT_DIRECTORY =
        "Select location of Liferay module project or parent project directory.";
    public final String TEXT_LOCATION_NOT_RECOGNIZED_AS_A_VALID_PROJECT_TYPE =
        " Location is not recognized as a valid project type.";
    public final String TEXT_NOT_ROOT_LOCATION_OF_MULTI_MODULE_PROJECT =
        " Location is not the root location of a multi-module project.";

    public final String TITLE_IMPORT_LIFERAY_MODULE_PROJECT = "Import Liferay Module Project or Multi-Module Projects";
    public final int INDEX_SELECT_IMPORT_LIFERAY_MODULE_PROJECTS_VALIDATION_MESSAGE = 1;
    public final int INDEX_IMPORT_LIFERAY_WORKSPACE_LOCATION_VALIDATION_MESSAGE = 2;
}
