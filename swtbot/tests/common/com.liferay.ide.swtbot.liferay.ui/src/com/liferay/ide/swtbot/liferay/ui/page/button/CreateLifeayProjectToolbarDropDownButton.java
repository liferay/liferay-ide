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

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.ui.page.MenuItem;
import com.liferay.ide.swtbot.ui.page.ToolbarDropDownButton;

/**
 * @author Terry Jia
 * @author Vicky Wang
 * @author Ying Xu
 * @author Li Lu
 */
public class CreateLifeayProjectToolbarDropDownButton extends ToolbarDropDownButton
{

    private MenuItem newLiferayHookConfiguration;
    private MenuItem newLiferayJSFPortlet;
    private MenuItem newLiferayPluginProject;
    private MenuItem newLiferayPortlet;
    private MenuItem newLiferayServer;
    private MenuItem newLiferayServiceBuilder;
    private MenuItem newLiferayVaadinPortlet;
    private MenuItem newLiferayLayouTemplate;
    private MenuItem newLiferayModuleProjectFragment;
    private MenuItem newLiferayWorkspaceProject;
    private MenuItem newLiferayComponentClass;
    private MenuItem newLiferayModuleProject;
    private MenuItem newLiferayJSFProject;

    public CreateLifeayProjectToolbarDropDownButton( SWTBot bot )
    {
        super( bot, TOOLBAR_CREATE_LIFERAY_PROJECT );

        newLiferayPluginProject = new MenuItem( bot, this, LABEL_NEW_LIFERAY_PLUGIN_PROJECT );
        newLiferayPortlet = new MenuItem( bot, this, LABEL_NEW_LIFERAY_PORTLET );
        newLiferayHookConfiguration = new MenuItem( bot, this, LABEL_NEW_LIFERAY_HOOK_CONFIGURATION );
        newLiferayServer = new MenuItem( bot, this, LABEL_NEW_LIFERAY_SERVER );
        newLiferayServiceBuilder = new MenuItem( bot, this, LABEL_NEW_LIFERAY_SERVICE_BUILDER );
        newLiferayJSFPortlet = new MenuItem( bot, this, LABEL_NEW_LIFERAY_JSF_PORTLET );
        newLiferayVaadinPortlet = new MenuItem( bot, this, LABEL_NEW_LIFERAY_VAADIN_PORTLET );
        newLiferayLayouTemplate = new MenuItem( bot, this, LABEL_NEW_LIFERAY_LAYOUT_TMEPLATE );
        newLiferayModuleProjectFragment = new MenuItem( bot, this, LABEL_NEW_LIFERAY_MODULE_PROJECT_FRAGMENT );
        newLiferayWorkspaceProject = new MenuItem( bot, this, LABEL_NEW_LIFERAY_WORPSPACE_PROJECT );
        newLiferayComponentClass = new MenuItem( bot, this, LABEL_NEW_LIFERAY_COMPONENT_CLASS );
        newLiferayModuleProject = new MenuItem( bot, this, LABEL_NEW_LIFERAY_MODULE_PROJECT );
        newLiferayJSFProject = new MenuItem( bot, this, LABEL_NEW_LIFERAY_JSF_PROJECT );
    }

    public MenuItem getNewLiferayHookConfiguration()
    {
        return newLiferayHookConfiguration;
    }

    public MenuItem getNewLiferayJSFPortlet()
    {
        return newLiferayJSFPortlet;
    }

    public MenuItem getNewLiferayPluginProject()
    {
        return newLiferayPluginProject;
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

    public MenuItem getNewLiferayLayoutTemplate()
    {
        return newLiferayLayouTemplate;
    }

    public MenuItem getNewLiferayVaadinPortlet()
    {
        return newLiferayVaadinPortlet;
    }

    public MenuItem getNewLiferayModuleFragmentProject()
    {
        return newLiferayModuleProjectFragment;
    }

    public MenuItem getNewLiferayWorkspaceProject()
    {
        return newLiferayWorkspaceProject;
    }

    public MenuItem getNewLiferayComponentClass()
    {
        return newLiferayComponentClass;
    }

    public MenuItem getNewLiferayModuleProject()
    {
        return newLiferayModuleProject;
    }

    public MenuItem getNewLiferayJSFProject()
    {
        return newLiferayJSFProject;
    }

}
