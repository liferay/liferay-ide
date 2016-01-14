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

package com.liferay.ide.server.core.tests.swtbot;

import com.liferay.ide.ui.tests.UIBase;

/**
 * @author Li Lu
 * @author Terry Jia
 */
public interface ServerAction extends UIBase
{

    public final String ADD_AND_REMOVE = "Add and Remove...";
    public final String BUTTON_ADD = "Add >";
    public final String BUTTON_ADD_ALL = "Add All >>";
    public final String BUTTON_REMOVE = "< Remove";
    public final String BUTTON_REMOVE_ALL = "<< Remove All";
    public final String CREATE_DB_CONN_PROFILE = "Create a Database Connection Profile";
    public final String CREATE_PORTAL_SETTINGS_FILE = "Create Portal Settings File";
    public final String DEBUG = "Debug";
    public final String DELETE = "Delete";
    public final String DELETE_SERVER = "Delete Server";
    public final String DELETE_SERVER_WIZARD_TITLE = "Delete Server";
    public final String OPEN_LIFERAY_HOME_FOLDER = "Open Liferay Home Folder";

    public final String OPEN_LIFERAY_PORTAL_HOME = "Open Liferay Portal Home";
    public final String PUBLISH_CHANGES = "If server is started, publish changes immediately";
    public final String SERVER_STARTUP_MESSAGE = "Server startup in";
    public final String SERVER_STOP_MESSAGE = "Destroying ProtocolHandler";
    public final String START = "Start";

    public final String STOP = "Stop";
    public final String TEST_JASON_WEB_SERVICE = "Test JSON Web Services";

    public final String TEST_LIFERAY_WEB_SERVICE = "Test Liferay Web Services";

}
