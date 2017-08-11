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

import com.liferay.ide.swtbot.ui.page.ComboBox;
import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.ToolbarButtonWithTooltip;
import com.liferay.ide.swtbot.ui.page.Wizard;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Ying Xu
 */
public class NewLiferayComponentWizard extends Wizard
{

    private ToolbarButtonWithTooltip browseBtn;
    private Text componentClassName;
    private ComboBox componentClassTemplates;
    private Text modelClassName;
    private ToolbarButtonWithTooltip packageBrowseBtn;
    private Text packageName;
    private ComboBox projectNames;
    private Text serviceName;

    public NewLiferayComponentWizard( SWTBot bot )
    {
        this( bot, -1 );
    }

    public NewLiferayComponentWizard( SWTBot bot, int validationMsgIndex )
    {
        super( bot, NEW_LIFERAY_COMPONENT, 4 );

        packageName = new Text( bot, PACKAGE_NAME );
        componentClassName = new Text( bot, COMPONENT_CLASS_NAME );
        serviceName = new Text( bot, SERVICE_NAME );
        modelClassName = new Text( bot, MODEL_CLASS );
        projectNames = new ComboBox( bot, PROJECT_NAME );
        componentClassTemplates = new ComboBox( bot, COMPONENT_CLASS_TEMPLATE );
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
