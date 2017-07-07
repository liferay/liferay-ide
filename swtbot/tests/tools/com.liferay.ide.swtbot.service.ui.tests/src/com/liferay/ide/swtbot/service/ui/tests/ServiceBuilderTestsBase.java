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

package com.liferay.ide.swtbot.service.ui.tests;

import com.liferay.ide.swtbot.ui.tests.UIBase;

/**
 * @author Ying Xu
 */
public interface ServiceBuilderTestsBase extends UIBase
{

    public final String LABEL_PACKAGE_PATH = "Package path:";
    public final String LABEL_NAMESPACE = "Namespace:";
    public final String LABEL_AUTHOR = "Author";
    public final String LABEL_SERVICE_FILE = "Service file:";
    public final String LABEL_ENTITY_NAME = "Name:";
    public final String LABEL_CHOOSE_PACKAGE = "Choose a package:";

    public final String CHECKBOX_INCLUDE_SAMPLE_ENTITY = "Include sample entity in new file.";
    public final String COMBOBOX_PLUGIN_PROJECT = "Plugin project:";

    public final String TEXT_SERVICE_FILE_VALUE = "service.xml";
    public final String TEXT_DEFAULT_PLUGIN_PROJECT_VALUE = "";
    public final String TEXT_DEFAULT_PACKAGE_PAHT_VALUE = "";
    public final String TEXT_DEFAULT_NAMESPACE_VALUE = "";
    public final String TEXT_PACKAGE_PATH_EMPTY_MESSAGE = " Package path cannot be empty.";
    public final String TEXT_NAMESPACE_EMPTY_MESSAGE = " Namespace cannot be empty.";
    public final String TEXT_NEW_SERVICE_BUILDER_XML_FILE = "Create a new service builder xml file in a project.";
    public final String TEXT_ALREADY_HAS_SERVICE_BUILDER_XML_FILE_MESSAGE =
        " Project already contains service.xml file, please select another project.";
    public final String TEXT_VALIDATION_NAMESPACE_MESSAGE = " The namespace element must be a valid keyword.";
    public final String TEXT_VALIDATION_PACKAGE_PATH_MESSAGE = " Invalid Java package name: ";

    public final String SERVICE_BUILDER_DTD_VERSION = "7.0.0";
    public final int INDEX_VALIDATION_MESSAGE = 4;
}
