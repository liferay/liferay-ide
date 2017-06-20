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

package com.liferay.ide.swtbot.module.fragment.tests;

import com.liferay.ide.swtbot.ui.tests.UIBase;

/**
 * @author Vicky Wang
 */
public interface ModuleFragmentProjectWizard extends UIBase
{
    public final String BUTTON_ADD_FILE_PATH = "Add Override File Path";

    public final int INDEX_VALIDATION_MESSAGE2 = 1;
    public final int INDEX_VALIDATION_PAGE_MESSAGE3 = 2;

    public final String LABEL_OSGI_BUNDLE = "Host OSGi Bundle:";
    public final String LABEL_RUNTIME_NAME = "Liferay runtime name:";
    public final String LABLE_OVERRIDDEN_FILES = "Overridden files:";
    public final String LABLE_SELECT_OVERRIDE_FILE_PATH = "Select Override File Path:";

    public final String TEXT_INVALID_GRADLE_PROJECT = "The project name is invalid.";
    public final String TEXT_OSGI_BUNDLE_BLANK = " Host OSGi Bundle must be specified";
    public final String TEXT_LIFERAY_RUNTIME_MUST_BE_CONFIGURED = " Liferay runtime must be configured.";

}
