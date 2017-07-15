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

import com.liferay.ide.swtbot.liferay.ui.DialogUI;
import com.liferay.ide.swtbot.liferay.ui.WizardUI;
import com.liferay.ide.swtbot.ui.page.Button;
import com.liferay.ide.swtbot.ui.page.CheckBox;
import com.liferay.ide.swtbot.ui.page.Table;
import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.Wizard;

/**
 * @author Vicky Wang
 */
public class CreateCustomJSPsWizard extends Wizard implements DialogUI, WizardUI
{

    private Button addBtn;
    private Button addFromLiferayBtn;
    private Button browseBtn;
    private Text customJSPfolder;
    private CheckBox disableJspSyntaxValidation;
    private Button editBtn;
    private Table jspFilesToOverride;
    private Button removeBtn;
    private Text selectedProjects;
    private Text webRootFolder;

    public CreateCustomJSPsWizard( SWTBot bot, int indexCustomJSPsValidationMsg )
    {
        this( bot, TEXT_BLANK, indexCustomJSPsValidationMsg );
    }

    public CreateCustomJSPsWizard( SWTBot bot, String title, int indexCustomJSPsValidationMsg )
    {
        super( bot, title, indexCustomJSPsValidationMsg );

        selectedProjects = new Text( bot, LABLE_SELECTED_PROJECT );
        webRootFolder = new Text( bot, LABLE_WEB_ROOT_FOLDER );
        customJSPfolder = new Text( bot, LABLE_CUSTOM_JSP_FOLDER );
        jspFilesToOverride = new Table( bot, LABLE_JSP_FILES_TO_OVERRIDE );
        browseBtn = new Button( bot, BROWSE_WITH_THREE_DOT );
        addFromLiferayBtn = new Button( bot, BUTTON_ADD_FROM_LIFERAY );
        addBtn = new Button( bot, ADD_WITH_THREE_DOT );
        editBtn = new Button( bot, BUTTON_EDIT );
        removeBtn = new Button( bot, REMOVE_WITH_THREE_DOT );
        disableJspSyntaxValidation = new CheckBox( bot, CHECKBOX_DISABLE_JSP_SYNTAX_VALIDATION );
    }

    public Button getAddBtn()
    {
        return addBtn;
    }

    public Button getAddFromLiferayBtn()
    {
        return addFromLiferayBtn;
    }

    public Button getBrowseBtn()
    {
        return browseBtn;
    }

    public Text getCustomJSPfolder()
    {
        return customJSPfolder;
    }

    public CheckBox getDisableJspSyntaxValidation()
    {
        return disableJspSyntaxValidation;
    }

    public Button getEditBtn()
    {
        return editBtn;
    }

    public Table getJspFilesToOverride()
    {
        return jspFilesToOverride;
    }

    public Button getRemoveBtn()
    {
        return removeBtn;
    }

    public Text getSelectedProject()
    {
        return selectedProjects;
    }

    public Text getWebRootFolder()
    {
        return webRootFolder;
    }

}
