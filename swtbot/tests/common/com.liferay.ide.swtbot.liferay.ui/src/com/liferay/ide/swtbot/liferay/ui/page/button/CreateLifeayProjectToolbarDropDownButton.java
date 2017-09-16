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
 * @author Terry Jia
 * @author Vicky Wang
 * @author Ying Xu
 * @author Li Lu
 */
public class CreateLifeayProjectToolbarDropDownButton extends ToolbarDropDownButton
{

    private MenuItem newLiferayComponentClass;
    private MenuItem newLiferayHookConfiguration;
    private MenuItem newLiferayJSFPortlet;
    private MenuItem newLiferayJSFProject;
    private MenuItem newLiferayLayouTemplate;
    private MenuItem newLiferayModule;
    private MenuItem newLiferayModuleFragment;
    private MenuItem newLiferayPlugin;
    private MenuItem newLiferayPluginProjectFromExistingSource;
    private MenuItem newLiferayPortlet;
    private MenuItem newLiferayServer;
    private MenuItem newLiferayServiceBuilder;
    private MenuItem newLiferayVaadinPortlet;
    private MenuItem newLiferayWorkspaceProject;

    public CreateLifeayProjectToolbarDropDownButton( SWTWorkbenchBot bot )
    {
        super( bot, CREATE_A_NEW_LIFERAY_MODULE_PROJECT );

        newLiferayPlugin = new MenuItem( bot, this, NEW_LIFERAY_PLUGIN_PROJECT );
        newLiferayPluginProjectFromExistingSource =
            new MenuItem( bot, this, NEW_LIFERAY_PLUGIN_PROJECTS_FROM_EXISING_SOURCE );
        newLiferayPortlet = new MenuItem( bot, this, NEW_LIFERAY_PORTLET );
        newLiferayHookConfiguration = new MenuItem( bot, this, NEW_LIFERAY_HOOK_CONFIGURATION );
        newLiferayServer = new MenuItem( bot, this, NEW_LIFERAY_SERVER );
        newLiferayServiceBuilder = new MenuItem( bot, this, NEW_LIFERAY_SERVICE_BUILDER );
        newLiferayJSFPortlet = new MenuItem( bot, this, NEW_LIFERAY_JSF_PORTLET );
        newLiferayVaadinPortlet = new MenuItem( bot, this, NEW_LIFERAY_VAADIN_PORTLET );
        newLiferayLayouTemplate = new MenuItem( bot, this, NEW_LIFERAY_LAYOUT_TMEPLATE );
        newLiferayModuleFragment = new MenuItem( bot, this, NEW_LIFERAY_MODULE_PROJECT_FRAGMENT );
        newLiferayWorkspaceProject = new MenuItem( bot, this, NEW_LIFERAY_WORPSPACE_PROJECT );
        newLiferayComponentClass = new MenuItem( bot, this, NEW_LIFERAY_COMPONENT_CLASS );
        newLiferayModule = new MenuItem( bot, this, NEW_LIFERAY_MODULE_PROJECT );
        newLiferayJSFProject = new MenuItem( bot, this, NEW_LIFERAY_JSF_PROJECT );
    }

    public MenuItem getNewLiferayComponentClass()
    {
        return newLiferayComponentClass;
    }

    public MenuItem getNewLiferayHookConfiguration()
    {
        return newLiferayHookConfiguration;
    }

    public MenuItem getNewLiferayJSFPortlet()
    {
        return newLiferayJSFPortlet;
    }

    public MenuItem getNewLiferayJSFProject()
    {
        return newLiferayJSFProject;
    }

    public MenuItem getNewLiferayLayoutTemplate()
    {
        return newLiferayLayouTemplate;
    }

    public MenuItem getNewLiferayModuleFragment()
    {
        return newLiferayModuleFragment;
    }

    public MenuItem getNewLiferayModule()
    {
        return newLiferayModule;
    }

    public MenuItem getNewLiferayPlugin()
    {
        return newLiferayPlugin;
    }

    public MenuItem getNewLiferayPluginProjectFromExistingSource()
    {
        return newLiferayPluginProjectFromExistingSource;
    }

    public MenuItem getNewLiferayPortlet()
    {
        return newLiferayPortlet;
    }

    public MenuItem getNewLiferayServer()
    {
        return newLiferayServer;
    }

    public MenuItem getNewLiferayServiceBuilder()
    {
        return newLiferayServiceBuilder;
    }

    public MenuItem getNewLiferayVaadinPortlet()
    {
        return newLiferayVaadinPortlet;
    }

    public MenuItem getNewLiferayWorkspaceProject()
    {
        return newLiferayWorkspaceProject;
    }

}
