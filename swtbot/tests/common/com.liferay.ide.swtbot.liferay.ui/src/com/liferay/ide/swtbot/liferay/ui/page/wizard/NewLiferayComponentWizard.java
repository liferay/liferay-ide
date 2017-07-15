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

import com.liferay.ide.swtbot.liferay.ui.NewLiferayComponentWizardUI;
import com.liferay.ide.swtbot.ui.page.ComboBox;
import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.ToolbarButtonWithTooltip;
import com.liferay.ide.swtbot.ui.page.Wizard;

/**
 * @author Ying Xu
 */
public class NewLiferayComponentWizard extends Wizard implements NewLiferayComponentWizardUI
{

    private ToolbarButtonWithTooltip browseBtn;
    private Text componentClassName;
    private ComboBox componentClassTemplates;
    private Text modelClassName;
    private ToolbarButtonWithTooltip packageBrowseBtn;
    private Text packageName;
    private ComboBox projectNames;
    private Text serviceName;

    public NewLiferayComponentWizard( SWTBot bot, String title )
    {
        this( bot, title, INDEX_VALIDATION_MESSAGE );
    }

    public NewLiferayComponentWizard( SWTBot bot, String title, int validationMsgIndex )
    {
        super( bot, title, BACK_WITH_LEFT_BRACKET, NEXT_WITH_BRACKET, FINISH, CANCEL, validationMsgIndex );
        packageName = new Text( bot, LABEL_PACKAGE_NAME );
        componentClassName = new Text( bot, LABEL_COMPONENT_CLASS_NAME );
        serviceName = new Text( bot, LABEL_SERVICE_NAME );
        modelClassName = new Text( bot, LABEL_MODEL_CLASS );
        projectNames = new ComboBox( bot, COMBOBOX_PROJECT_NAME );
        componentClassTemplates = new ComboBox( bot, COMBOBOX_COMPONENT_CLASS_TEMPLATE );
        browseBtn = new ToolbarButtonWithTooltip( bot, BROWSE, 1 );
        packageBrowseBtn = new ToolbarButtonWithTooltip( bot, BROWSE, 0 );
    }

    public ToolbarButtonWithTooltip getBrowseBtn()
    {
        return browseBtn;
    }

    public Text getComponentClassName()
    {
        return componentClassName;
    }

    public ComboBox getComponentClassTemplates()
    {
        return componentClassTemplates;
    }

    public Text getModelClassName()
    {
        return modelClassName;
    }

    public ToolbarButtonWithTooltip getPackageBrowseBtn()
    {
        return packageBrowseBtn;
    }

    public Text getPackageName()
    {
        return packageName;
    }

    public ComboBox getProjectNames()
    {
        return projectNames;
    }

    public Text getServiceName()
    {
        return serviceName;
    }

}
