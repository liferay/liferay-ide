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
 * @author Li Lu
 * @author Ying Xu
 */
public interface LiferayProjectFromExistSourceWizard extends UIBase
{

    public final String TITLE_NEW_LIFERAY_PROJECT_EXIS_SOURCE = "New Liferay Project from existing source";
    public final String MENU_LIFERAY_PROJECT_EXIS_SOURCE = "Liferay Project from Existing Source";

    public final int INDEX_DEFAULT_WIZARD_VALIDATION_MESSAGE = 2;

    public final String LABEL_SDK_DIRECTORY = "SDK Directory:";
    public final String LABLE_SDK_VERSION = "SDK Version:";
    public final String BUTTON_SELECT_ALL = "Select All";
    public final String BUTTON_DESELECT_ALL = "Deselect All";
    public final String BUTTON_REFRESH = "Refresh";

    public final String MESSAGE_PROJECT_NAME_EXSIT = " Project name already exists.";
    public final String MESSAGE_PROJECT_HAS_DIFF_SDK = " This project has different sdk than current workspace sdk";
    public final String MESSAGE_COULD_NOT_DETER_SDK = " Could not determine SDK from project location";
    public final String MESSAGE_INVALID_PROJECT_LOCATION = " SDK does not exist.";
    public final String MESSAGE_DEFAULT = "Please select at least one project to import.";
    public final String MESSAGE_MUST_SPECIFY_ONE_PROJECT = " At least one project must be specified.";

}
