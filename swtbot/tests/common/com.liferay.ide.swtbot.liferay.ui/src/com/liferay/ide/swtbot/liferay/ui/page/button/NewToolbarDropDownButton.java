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

package com.liferay.ide.swtbot.liferay.ui.page.button;

import com.liferay.ide.swtbot.ui.page.MenuItem;
import com.liferay.ide.swtbot.ui.page.ToolbarDropDownButton;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Ying Xu
 */
public class NewToolbarDropDownButton extends ToolbarDropDownButton
{

    private MenuItem liferayPluginProject;
    private MenuItem liferayModuleProject;
    private MenuItem liferayPortlet;
    private MenuItem liferayJSFPortlet;
    private MenuItem liferayVaadinPortlet;
    private MenuItem liferayHookConfiguration;
    private MenuItem liferayServiceBuilder;
    private MenuItem liferayLayoutTemplate;
    private MenuItem newPackage;

    public NewToolbarDropDownButton( SWTWorkbenchBot bot )
    {
        super( bot, NEW );

        liferayPluginProject = new MenuItem( bot, this, LIFERAY_PLUGIN_PROJECT );
        liferayModuleProject = new MenuItem( bot, this, LIFERAY_MODULE_PROJECT );
        liferayPortlet = new MenuItem( bot, this, LIFERAY_PORTLET );
        liferayJSFPortlet = new MenuItem( bot, this, LIFERAY_JSF_PORTLET );
        liferayVaadinPortlet = new MenuItem( bot, this, LIFERAY_VAADIN_PORTLET );
        liferayHookConfiguration = new MenuItem( bot, this, LIFERAY_HOOK_CONFIGURATION );
        liferayServiceBuilder = new MenuItem( bot, this, LIFERAY_SERVICE_BUILDER );
        liferayLayoutTemplate = new MenuItem( bot, this, LIFERAY_LAYOUT_TEMPLATE );
        newPackage = new MenuItem( bot, this, PACKAGE );
    }

    public MenuItem getLiferayPluginProject()
    {
        return liferayPluginProject;
    }

    public MenuItem getLiferayModuleProject()
    {
        return liferayModuleProject;
    }

    public MenuItem getLiferayPortlet()
    {
        return liferayPortlet;
    }

    public MenuItem getLiferayJSFPortlet()
    {
        return liferayJSFPortlet;
    }

    public MenuItem getLiferayVaadinPortlet()
    {
        return liferayVaadinPortlet;
    }

    public MenuItem getLiferayHookConfiguration()
    {
        return liferayHookConfiguration;
    }

    public MenuItem getLiferayServiceBuilder()
    {
        return liferayServiceBuilder;
    }

    public MenuItem getLiferayLayoutTemplate()
    {
        return liferayLayoutTemplate;
    }

    public MenuItem getNewPackage()
    {
        return newPackage;
    }

}
