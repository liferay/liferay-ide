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
 * @author Li Lu
 * @author Terry Jia
 */
public interface ServerAction extends UIBase
{

    public final String BUTTON_ADD_PROJECT = "Add >";
    public final String BUTTON_ADD_ALL = "Add All >>";
    public final String BUTTON_REMOVE_PROJECT = "< Remove";
    public final String BUTTON_REMOVE_ALL = "<< Remove All";
    public final String CHECKBOX_PUBLISH_CHANGES = "If server is started, publish changes immediately";
    public final String MESAGE_SERVER_STOP = "Destroying ProtocolHandler";
    public final String MENU_ADD_AND_REMOVE = "Add and Remove...";
    public final String MENU_CREATE_DB_CONN_PROFILE = "Create a Database Connection Profile";
    public final String MENU_CREATE_PORTAL_SETTINGS_FILE = "Create Portal Settings File";
    public final String MENU_DEBUG = "Debug";
    public final String MENU_OPEN_LIFERAY_HOME_FOLDER = "Open Liferay Home Folder";
    public final String MENU_OPEN_LIFERAY_PORTAL_HOME = "Open Liferay Portal Home";
    public final String MENU_START = "Start";
    public final String MENU_STOP = "Stop";
    public final String MENU_TEST_JASON_WEB_SERVICE = "Test JSON Web Services";
    public final String MENU_TEST_LIFERAY_WEB_SERVICE = "Test Liferay Web Services";
    // public final String DELETE_SERVER = "Delete Server";
    public final String TITLE_DELETE_SERVER_WIZARD = "Delete Server";

}
