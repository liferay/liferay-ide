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

package com.liferay.ide.swtbot.hook.ui.tests.page;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.hook.ui.tests.HookConfigurationWizard;
import com.liferay.ide.swtbot.project.ui.tests.ProjectWizard;
import com.liferay.ide.swtbot.ui.tests.page.ButtonPO;
import com.liferay.ide.swtbot.ui.tests.page.CheckBoxPO;
import com.liferay.ide.swtbot.ui.tests.page.TablePO;
import com.liferay.ide.swtbot.ui.tests.page.TextPO;
import com.liferay.ide.swtbot.ui.tests.page.WizardPO;

/**
 * @author Vicky Wang
 */
public class CreateCustomJSPsPO extends WizardPO implements HookConfigurationWizard, ProjectWizard
{

    private ButtonPO _addButton;
    private ButtonPO _addFromLiferayButton;
    private ButtonPO _browseButton;
    private ButtonPO _editButton;
    private ButtonPO _removeButton;
    private TextPO _customJSPfolderText;
    private TablePO _jspFilesToOverrideTable;
    private TextPO _selectedProjectText;
    private TextPO _webRootFolderText;
    private CheckBoxPO _disableJspSyntaxValidation;

    public CreateCustomJSPsPO( SWTBot bot, int indexCustomJSPsValidationMessage )
    {
        this( bot, TEXT_BLANK, indexCustomJSPsValidationMessage );
    }

    public CreateCustomJSPsPO( SWTBot bot, String title, int indexCustomJSPsValidationMessage )
    {
        super( bot, title, BUTTON_CANCEL, BUTTON_FINISH, BUTTON_BACK, BUTTON_NEXT, indexCustomJSPsValidationMessage );

        _selectedProjectText = new TextPO( bot, LABLE_SELECTED_PROJECT );
        _webRootFolderText = new TextPO( bot, LABLE_WEB_ROOT_FOLDER );
        _customJSPfolderText = new TextPO( bot, LABLE_CUSTOM_JSP_FOLDER );
        _jspFilesToOverrideTable = new TablePO( bot, LABLE_JSP_FILES_TO_OVERRIDE );
        _browseButton = new ButtonPO( bot, BUTTON_BROWSE_WITH_DOT );
        _addFromLiferayButton = new ButtonPO( bot, BUTTON_ADD_FROM_LIFERAY );
        _addButton = new ButtonPO( bot, BUTTON_ADD );
        _editButton = new ButtonPO( bot, BUTTON_EDIT );
        _removeButton = new ButtonPO( bot, BUTTON_REMOVE_WITH_DOT );
        _disableJspSyntaxValidation = new CheckBoxPO( bot, CHECKBOX_DISABLE_JSP_SYNTAX_VALIDATION );
    }

    public ButtonPO getAddButton()
    {
        return _addButton;
    }

    public ButtonPO getAddFromLiferayButton()
    {
        return _addFromLiferayButton;
    }

    public ButtonPO getBrowseButton()
    {
        return _browseButton;
    }

    public ButtonPO getEditButton()
    {
        return _editButton;
    }

    public TablePO getJspFilesToOverride()
    {
        return _jspFilesToOverrideTable;
    }

    public ButtonPO getRemoveButton()
    {
        return _removeButton;
    }

    public void setCustomJSPfolder( String text )
    {
        this._customJSPfolderText.setText( text );
    }

    public TextPO getSelectedProject()
    {
        return _selectedProjectText;
    }

    public TextPO getCustomJSPfolder()
    {
        return _customJSPfolderText;
    }

    public TextPO getWebRootFolder()
    {
        return _webRootFolderText;
    }

    public CheckBoxPO getDisableJspSyntaxValidation()
    {
        return _disableJspSyntaxValidation;
    }

    public void setDisableJspSyntaxValidation( CheckBoxPO disableJspSyntaxValidation )
    {
        this._disableJspSyntaxValidation = disableJspSyntaxValidation;
    }

}
