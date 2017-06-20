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

package com.liferay.ide.swtbot.layouttpl.ui.tests;

import com.liferay.ide.swtbot.ui.tests.UIBase;

/**
 * @author Li Lu
 */
public interface CreateLayouttplWizard extends UIBase
{

    final int DEFAULT_FILE_SELECTION_DIALOG_INDEX = 0;
    final int INDEX_LAYOUTTPL_VALIDATION_MESSAGE1 = 5;
    final int INDEX_LAYOUTTPL_VALIDATION_MESSAGE2 = 0;
    final String LABEL_ID = "Id:";
    final String LABEL_LAYOUT_PLUGIN_PROJECT = "Layout plugin project:";
    final String LABEL_STANDARD_JSF = "Standard JSF";
    final String LABEL_TEMPLATE_FILE = "Template file:";
    final String LABEL_THUMBNAIL_FILE = "Thumbnail file:";
    final String LABEL_VIEW_FOLDER = "View folder:";
    final String LABEL_WAP_TEMPLATE_FILE = "WAP template file:";
    final String TEXT_CHOOSE_VALID_PROJECT_FILE = "Choose a valid project file";
    final String TEXT_DEFAULT_MESSAGE = "Create a Liferay layout template.";
    final String TEXT_ID_CANNT_BE_EMPTY = " Id can't be empty.";
    final String TEXT_ID_INVALID = " Template id is invalid.";
    final String TEXT_TEMPLATE_FILE_EXIST = " Template file already exists and will be overwritten.";
    final String TEXT_TEMPLATE_FILE_INVALID = " Template file name is invalid.";
    final String TEXT_THUMBNAIL_FILE_EXIST = " Thumbnail file already exists and will be overwritten.";
    final String TEXT_THUMBNAIL_FILE_INVALID = " Thumbnail file name is invalid.";
    final String TEXT_VIEW_FILE_OEVERWRITTEN = " View file already exists and will be overwritten.";
    final String TEXT_WAP_TEMPLATE_FILE_EXIST = " WAP template file already exists and will be overwritten.";
    final String TEXT_WAP_TEMPLATE_FILE_INVALID = " WAP template file name is invalid.";
    final String TITLE_TEMPLATE_FILE_SELECTION = "Template file selection";
    final String TITLE_THUMBNAIL_FILE_SELECTION = "Thumbnail file selection";
    final String TITLE_WAP_TEMPLATE_FILE_SELECTION = "WAP template file selection";
}
