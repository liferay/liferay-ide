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

package com.liferay.ide.swtbot.ui.tests.eclipse.page;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.ui.tests.UIBase;
import com.liferay.ide.swtbot.ui.tests.page.MenuItemPO;
import com.liferay.ide.swtbot.ui.tests.page.ToolbarDropDownButtonPO;

/**
 * @author Ying Xu
 */
public class NewToolbarDropDownButton extends ToolbarDropDownButtonPO implements UIBase
{

    private MenuItemPO _liferayPluginProject;
    private MenuItemPO _liferayModuleProject;
    private MenuItemPO _liferayPortlet;
    private MenuItemPO _liferayJSFPortlet;
    private MenuItemPO _liferayVaadinPortlet;
    private MenuItemPO _liferayHookConfiguration;
    private MenuItemPO _liferayServiceBuilder;
    private MenuItemPO _liferayLayoutTemplate;
    private MenuItemPO _newPackage;

    public NewToolbarDropDownButton( SWTBot bot )
    {
        super( bot, TOOLBAR_NEW );

        _liferayPluginProject = new MenuItemPO( bot, this, LABEL_LIFERAY_PLUGIN_PROJECT );
        _liferayModuleProject = new MenuItemPO( bot, this, LABEL_LIFERAY_MODULE_PROJECT );
        _liferayPortlet = new MenuItemPO( bot, this, LABEL_LIFERAY_PORTLET );
        _liferayJSFPortlet = new MenuItemPO( bot, this, LABEL_LIFERAY_JSF_PORTLET );
        _liferayVaadinPortlet = new MenuItemPO( bot, this, LABEL_LIFERAY_VAADIN_PORTLET );
        _liferayHookConfiguration = new MenuItemPO( bot, this, LABEL_LIFERAY_HOOK_CONFIGURATION );
        _liferayServiceBuilder = new MenuItemPO( bot, this, LABEL_LIFERAY_SERVICE_BUILDER );
        _liferayLayoutTemplate = new MenuItemPO( bot, this, LABEL_LIFERAY_LAYOUT_TEMPLATE );
        _newPackage = new MenuItemPO( bot, this, TOOLBAR_PACKAGE );
    }

    public MenuItemPO getLiferayPluginProject()
    {
        return _liferayPluginProject;
    }

    public MenuItemPO getLiferayModuleProject()
    {
        return _liferayModuleProject;
    }

    public MenuItemPO getLiferayPortlet()
    {
        return _liferayPortlet;
    }

    public MenuItemPO getLiferayJSFPortlet()
    {
        return _liferayJSFPortlet;
    }

    public MenuItemPO getLiferayVaadinPortlet()
    {
        return _liferayVaadinPortlet;
    }

    public MenuItemPO getLiferayHookConfiguration()
    {
        return _liferayHookConfiguration;
    }

    public MenuItemPO getLiferayServiceBuilder()
    {
        return _liferayServiceBuilder;
    }

    public MenuItemPO getLiferayLayoutTemplate()
    {
        return _liferayLayoutTemplate;
    }

    public MenuItemPO getNewPackage()
    {
        return _newPackage;
    }

}
