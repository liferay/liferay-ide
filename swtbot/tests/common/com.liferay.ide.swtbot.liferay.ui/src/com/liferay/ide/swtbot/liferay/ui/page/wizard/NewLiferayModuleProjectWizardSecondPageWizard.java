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

package com.liferay.ide.swtbot.liferay.ui.page.wizard;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.liferay.ui.ModuleWizardUI;
import com.liferay.ide.swtbot.ui.page.Table;
import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.ToolbarButtonWithTooltip;
import com.liferay.ide.swtbot.ui.page.Wizard;

/**
 * @author Ying Xu
 */
public class NewLiferayModuleProjectWizardSecondPageWizard extends Wizard implements ModuleWizardUI
{

    private ToolbarButtonWithTooltip addPropertyKeyBtn;
    private ToolbarButtonWithTooltip browseBtn;
    private Text componentClassName;
    private ToolbarButtonWithTooltip deleteBtn;
    private ToolbarButtonWithTooltip moveDownBtn;
    private ToolbarButtonWithTooltip moveUpBtn;
    private Text packageName;
    private Table properties;
    private Text serviceName;

    public NewLiferayModuleProjectWizardSecondPageWizard( SWTBot bot )
    {
        this( bot, TEXT_BLANK );
    }

    public NewLiferayModuleProjectWizardSecondPageWizard( SWTBot bot, int validationMsgIndex )
    {
        this( bot, TEXT_BLANK, validationMsgIndex );
    }

    public NewLiferayModuleProjectWizardSecondPageWizard( SWTBot bot, String title )
    {
        this( bot, title, INDEX_DEFAULT_VALIDATION_MESSAGE );
    }

    public NewLiferayModuleProjectWizardSecondPageWizard( SWTBot bot, String title, int validationMsgIndex )
    {
        super( bot, title, validationMsgIndex );
        componentClassName = new Text( bot, MODULE_COMPONENT_CLASS_NAME );
        packageName = new Text( bot, LABEL_PACKAGE_NAME_UPPERCASE );
        serviceName = new Text( bot, LABEL_SERVICE_NAME );
        properties = new Table( bot, MODULE_PROPERTIES );
        browseBtn = new ToolbarButtonWithTooltip( bot, BROWSE );
        addPropertyKeyBtn = new ToolbarButtonWithTooltip( bot, ADD_PROPERTY_KEY );
        moveUpBtn = new ToolbarButtonWithTooltip( bot, MOVE_UP );
        moveDownBtn = new ToolbarButtonWithTooltip( bot, MOVE_DOWN );
        deleteBtn = new ToolbarButtonWithTooltip( bot, DELETE );
    }

    public ToolbarButtonWithTooltip getAddPropertyKeyBtn()
    {
        return addPropertyKeyBtn;
    }

    public ToolbarButtonWithTooltip getBrowseBtn()
    {
        return browseBtn;
    }

    public Text getComponentClassName()
    {
        return componentClassName;
    }

    public ToolbarButtonWithTooltip getDeleteBtn()
    {
        return deleteBtn;
    }

    public ToolbarButtonWithTooltip getMoveDownBtn()
    {
        return moveDownBtn;
    }

    public ToolbarButtonWithTooltip getMoveUpBtn()
    {
        return moveUpBtn;
    }

    public Text getPackageName()
    {
        return packageName;
    }

    public Table getProperties()
    {
        return properties;
    }

    public Text getServiceName()
    {
        return serviceName;
    }

}
