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

package com.liferay.ide.swtbot.project.ui.tests.page;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.project.ui.tests.NewLiferayComponentWizard;
import com.liferay.ide.swtbot.ui.tests.page.ComboBoxPO;
import com.liferay.ide.swtbot.ui.tests.page.TextPO;
import com.liferay.ide.swtbot.ui.tests.page.ToolbarButtonWithTooltipPO;
import com.liferay.ide.swtbot.ui.tests.page.WizardPO;

/**
 * @author Ying Xu
 */
public class NewLiferayComponentWizardPO extends WizardPO implements NewLiferayComponentWizard
{

    private TextPO _packageName;
    private TextPO _componentClassName;
    private TextPO _modelClass;
    private TextPO _serviceName;

    private ToolbarButtonWithTooltipPO _browseButton;
    private ToolbarButtonWithTooltipPO _packageBrowseButton;

    private ComboBoxPO _projectName;
    private ComboBoxPO _componentClassTemplate;

    public NewLiferayComponentWizardPO( SWTBot bot, String title )
    {
        this( bot, title, INDEX_VALIDATION_MESSAGE );
    }

    public NewLiferayComponentWizardPO( SWTBot bot, String title, int validationMessageIndex )
    {
        super( bot, title, BUTTON_CANCEL, BUTTON_FINISH, BUTTON_BACK, BUTTON_NEXT, validationMessageIndex );
        _packageName = new TextPO( bot, LABEL_PACKAGE_NAME );
        _componentClassName = new TextPO( bot, LABEL_COMPONENT_CLASS_NAME );
        _serviceName = new TextPO( bot, LABEL_SERVICE_NAME );
        _modelClass = new TextPO( bot, LABEL_MODEL_CLASS );
        _projectName = new ComboBoxPO( bot, COMBOBOX_PROJECT_NAME );
        _componentClassTemplate = new ComboBoxPO( bot, COMBOBOX_COMPONENT_CLASS_TEMPLATE );
        _browseButton = new ToolbarButtonWithTooltipPO( bot, BUTTON_BROWSE, 1 );
        _packageBrowseButton = new ToolbarButtonWithTooltipPO( bot, BUTTON_BROWSE, 0 );
    }

    public void selectProject( String projectName )
    {
        _projectName.setSelection( projectName );
    }

    public void selectTemplate( String componentClassTemplate )
    {
        _componentClassTemplate.setSelection( componentClassTemplate );
    }

    public TextPO getPackageName()
    {
        return _packageName;
    }

    public void setPackageName( TextPO _packageName )
    {
        this._packageName = _packageName;
    }

    public TextPO getComponentClassName()
    {
        return _componentClassName;
    }

    public void setComponentClassName( TextPO _componentClassName )
    {
        this._componentClassName = _componentClassName;
    }

    public TextPO getModelClass()
    {
        return _modelClass;
    }

    public void setModelClassName( String modelClassNameText )
    {
        _modelClass.setText( modelClassNameText );
    }

    public TextPO getServiceName()
    {
        return _serviceName;
    }

    public void setServiceName( String serviceNameText )
    {
        _serviceName.setText( serviceNameText );
    }

    public ToolbarButtonWithTooltipPO getBrowseButton()
    {
        return _browseButton;
    }

    public ToolbarButtonWithTooltipPO getPackageBrowseButton()
    {
        return _packageBrowseButton;
    }

    public ComboBoxPO getProjectName()
    {
        return _projectName;
    }

    public ComboBoxPO getComponentClassTemplate()
    {
        return _componentClassTemplate;
    }

}
