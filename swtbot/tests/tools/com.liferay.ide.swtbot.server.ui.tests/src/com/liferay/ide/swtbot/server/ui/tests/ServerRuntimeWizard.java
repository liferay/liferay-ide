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

package com.liferay.ide.swtbot.server.ui.tests;

import com.liferay.ide.swtbot.ui.tests.UIBase;

/**
 * @author Vicky Wang
 * @author Ying Xu
 */
public interface ServerRuntimeWizard extends UIBase
{

    public final String LABEL_BUNDLE_TYPE = "Detected portal bundle type";
    public final String LABEL_RUNTIME_NAME = "Name";
    public final String LABEL_RUNTIEME_ENVIRONMENTS = "Server runtime environments:";
    public final String LABEL_ECLIPSE_WORKSPACE = "Workspace:";
    public final String TITLE_NEW_SERVER = "New Server";
    public final String TITLE_NEW_SERVER_RUNTIME_ENVIRONMENT = "New Server Runtime Environmet";

    public final String TEXT_CHOOSE_THE_SERVER_TYPE = "Choose the type of server to create";
    public final String TEXT_SERVER_HOST_NAME_MUST_BE_SET = " Server's host name must be set.";
    public final String TEXT_DEFAULT_SERVER_HOST_NAME = "localhost";
    public final String TEXT_DEFAULT_SERVER_NAME = "Liferay 7.x at localhost";
    public final String TEXT_SPECIFY_PORTAL_BUNDLE_LOCATION =
        "Specify the installation directory of the portal bundle.";
    public final String TEXT_ENTER_RUNTIME_ENVIRONMENT_NAME = " Enter a name for the runtime environment.";
    public final String TEXT_PORTAL_BUNDLE_DOES_NOT_EXIST = " Portal bundle does not exist";

    public final String BUTTON_RESET_DEFAULT = "Rese&t default";

    public final int CHOOSE_SERVER_TYPE_INDEX = 3;
    public final int SPECIFY_PORTAL_BUNDLE_LOCATION_INDEX = 3;

}
