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

import com.liferay.ide.swtbot.ui.page.Button;
import com.liferay.ide.swtbot.ui.page.CheckBox;
import com.liferay.ide.swtbot.ui.page.Table;
import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.Wizard;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Vicky Wang
 */
public class CreateCustomJSPsWizard extends Wizard
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

    public CreateCustomJSPsWizard( SWTBot bot )
    {
        super( bot, 3 );

        selectedProjects = new Text( bot, SELECTED_PROJECT );
        webRootFolder = new Text( bot, WEB_ROOT_FOLDER );
        customJSPfolder = new Text( bot, CUSTOM_JSP_FOLDER );
        jspFilesToOverride = new Table( bot, JSP_FILES_TO_OVERRIDE );
        browseBtn = new Button( bot, BROWSE_WITH_DOT );
        addFromLiferayBtn = new Button( bot, ADD_FROM_LIFERAY );
        addBtn = new Button( bot, ADD_WITH_DOT );
        editBtn = new Button( bot, EDIT_WITH_DOT );
        removeBtn = new Button( bot, REMOVE_WITH_DOT );
        disableJspSyntaxValidation = new CheckBox( bot, DISABLE_JSP_SYNTAX );
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
