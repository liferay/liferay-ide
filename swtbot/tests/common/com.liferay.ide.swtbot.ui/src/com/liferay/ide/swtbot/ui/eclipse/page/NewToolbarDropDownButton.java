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

package com.liferay.ide.swtbot.ui.eclipse.page;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.ui.UI;
import com.liferay.ide.swtbot.ui.page.MenuItem;
import com.liferay.ide.swtbot.ui.page.ToolbarDropDownButton;

/**
 * @author Ying Xu
 */
public class NewToolbarDropDownButton extends ToolbarDropDownButton implements UI
{

    private MenuItem _liferayPluginProject;
    private MenuItem _liferayModuleProject;
    private MenuItem _liferayPortlet;
    private MenuItem _liferayJSFPortlet;
    private MenuItem _liferayVaadinPortlet;
    private MenuItem _liferayHookConfiguration;
    private MenuItem _liferayServiceBuilder;
    private MenuItem _liferayLayoutTemplate;
    private MenuItem _newPackage;

    public NewToolbarDropDownButton( SWTBot bot )
    {
        super( bot, TOOLBAR_NEW );

        _liferayPluginProject = new MenuItem( bot, this, LABEL_LIFERAY_PLUGIN_PROJECT );
        _liferayModuleProject = new MenuItem( bot, this, LABEL_LIFERAY_MODULE_PROJECT );
        _liferayPortlet = new MenuItem( bot, this, LABEL_LIFERAY_PORTLET );
        _liferayJSFPortlet = new MenuItem( bot, this, LABEL_LIFERAY_JSF_PORTLET );
        _liferayVaadinPortlet = new MenuItem( bot, this, LABEL_LIFERAY_VAADIN_PORTLET );
        _liferayHookConfiguration = new MenuItem( bot, this, LABEL_LIFERAY_HOOK_CONFIGURATION );
        _liferayServiceBuilder = new MenuItem( bot, this, LABEL_LIFERAY_SERVICE_BUILDER );
        _liferayLayoutTemplate = new MenuItem( bot, this, LABEL_LIFERAY_LAYOUT_TEMPLATE );
        _newPackage = new MenuItem( bot, this, TOOLBAR_PACKAGE );
    }

    public MenuItem getLiferayPluginProject()
    {
        return _liferayPluginProject;
    }

    public MenuItem getLiferayModuleProject()
    {
        return _liferayModuleProject;
    }

    public MenuItem getLiferayPortlet()
    {
        return _liferayPortlet;
    }

    public MenuItem getLiferayJSFPortlet()
    {
        return _liferayJSFPortlet;
    }

    public MenuItem getLiferayVaadinPortlet()
    {
        return _liferayVaadinPortlet;
    }

    public MenuItem getLiferayHookConfiguration()
    {
        return _liferayHookConfiguration;
    }

    public MenuItem getLiferayServiceBuilder()
    {
        return _liferayServiceBuilder;
    }

    public MenuItem getLiferayLayoutTemplate()
    {
        return _liferayLayoutTemplate;
    }

    public MenuItem getNewPackage()
    {
        return _newPackage;
    }

}
