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

package com.liferay.ide.swtbot.ui.tests.liferay.page;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.ui.tests.UIBase;
import com.liferay.ide.swtbot.ui.tests.page.MenuItemPO;
import com.liferay.ide.swtbot.ui.tests.page.ToolbarDropDownButtonPO;

/**
 * @author Terry Jia
 * @author Vicky Wang
 * @author Ying Xu
 * @author Li Lu
 */
public class CreateLifeayProjectToolbarDropDownButtonPO extends ToolbarDropDownButtonPO implements UIBase
{

    private MenuItemPO _newLiferayHookConfiguration;
    private MenuItemPO _newLiferayJSFPortlet;
    private MenuItemPO _newLiferayPluginProject;
    private MenuItemPO _newLiferayPortlet;
    private MenuItemPO _newLiferayServer;
    private MenuItemPO _newLiferayServiceBuilder;
    private MenuItemPO _newLiferayVaadinPortlet;
    private MenuItemPO _newLiferayLayouTemplate;
    private MenuItemPO _newLiferayModuleProjectFragment;
    private MenuItemPO _newLiferayWorkspaceProject;
    private MenuItemPO _newLiferayComponentClass;
    private MenuItemPO _newLiferayModuleProject;
    private MenuItemPO _newLiferayJSFProject;

    public CreateLifeayProjectToolbarDropDownButtonPO( SWTBot bot )
    {
        super( bot, TOOLBAR_CREATE_LIFERAY_PROJECT );

        _newLiferayPluginProject = new MenuItemPO( bot, this, LABEL_NEW_LIFERAY_PLUGIN_PROJECT );
        _newLiferayPortlet = new MenuItemPO( bot, this, LABEL_NEW_LIFERAY_PORTLET );
        _newLiferayHookConfiguration = new MenuItemPO( bot, this, LABEL_NEW_LIFERAY_HOOK_CONFIGURATION );
        _newLiferayServer = new MenuItemPO( bot, this, LABEL_NEW_LIFERAY_SERVER );
        _newLiferayServiceBuilder = new MenuItemPO( bot, this, LABEL_NEW_LIFERAY_SERVICE_BUILDER );
        _newLiferayJSFPortlet = new MenuItemPO( bot, this, LABEL_NEW_LIFERAY_JSF_PORTLET );
        _newLiferayVaadinPortlet = new MenuItemPO( bot, this, LABEL_NEW_LIFERAY_VAADIN_PORTLET );
        _newLiferayLayouTemplate = new MenuItemPO( bot, this, LABEL_NEW_LIFERAY_LAYOUT_TMEPLATE );
        _newLiferayModuleProjectFragment = new MenuItemPO( bot, this, LABEL_NEW_LIFERAY_MODULE_PROJECT_FRAGMENT );
        _newLiferayWorkspaceProject = new MenuItemPO( bot, this, LABEL_NEW_LIFERAY_WORPSPACE_PROJECT );
        _newLiferayComponentClass = new MenuItemPO( bot, this, LABEL_NEW_LIFERAY_COMPONENT_CLASS );
        _newLiferayModuleProject = new MenuItemPO( bot, this, LABEL_NEW_LIFERAY_MODULE_PROJECT );
        _newLiferayJSFProject = new MenuItemPO( bot, this, LABEL_NEW_LIFERAY_JSF_PROJECT );

    }

    public MenuItemPO getNewLiferayHookConfiguration()
    {
        return _newLiferayHookConfiguration;
    }

    public MenuItemPO getNewLiferayJSFPortlet()
    {
        return _newLiferayJSFPortlet;
    }

    public MenuItemPO getNewLiferayPluginProject()
    {
        return _newLiferayPluginProject;
    }

    public MenuItemPO getNewLiferayPortlet()
    {
        return _newLiferayPortlet;
    }

    public MenuItemPO getNewLiferayServer()
    {
        return _newLiferayServer;
    }

    public MenuItemPO getNewLiferayServiceBuilder()
    {
        return _newLiferayServiceBuilder;
    }

    public MenuItemPO getNewLiferayLayoutTemplate()
    {
        return _newLiferayLayouTemplate;
    }

    public MenuItemPO getNewLiferayVaadinPortlet()
    {
        return _newLiferayVaadinPortlet;
    }

    public MenuItemPO getNewLiferayModuleFragmentProject()
    {
        return _newLiferayModuleProjectFragment;
    }

    public MenuItemPO getNewLiferayWorkspaceProject()
    {
        return _newLiferayWorkspaceProject;
    }

    public MenuItemPO getNewLiferayComponentClass()
    {
        return _newLiferayComponentClass;
    }

    public MenuItemPO getNewLiferayModuleProject()
    {
        return _newLiferayModuleProject;
    }

    public MenuItemPO getNewLiferayJSFProject()
    {
        return _newLiferayJSFProject;
    }

}
