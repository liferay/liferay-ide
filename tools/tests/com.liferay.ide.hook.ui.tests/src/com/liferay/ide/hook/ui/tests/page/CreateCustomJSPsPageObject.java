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

package com.liferay.ide.hook.ui.tests.page;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.hook.ui.tests.HookConfigurationWizard;
import com.liferay.ide.project.ui.tests.page.ProjectWizard;
import com.liferay.ide.ui.tests.swtbot.page.ButtonPageObject;
import com.liferay.ide.ui.tests.swtbot.page.CheckBoxPageObject;
import com.liferay.ide.ui.tests.swtbot.page.TablePageObject;
import com.liferay.ide.ui.tests.swtbot.page.TextPageObject;
import com.liferay.ide.ui.tests.swtbot.page.WizardPageObject;

/**
 * @author Vicky Wang
 */
public class CreateCustomJSPsPageObject<T extends SWTBot> extends WizardPageObject<T>
    implements HookConfigurationWizard, ProjectWizard
{

    ButtonPageObject<SWTBot> add;
    ButtonPageObject<SWTBot> addFromLiferay;
    ButtonPageObject<SWTBot> browse;
    ButtonPageObject<SWTBot> edit;
    ButtonPageObject<SWTBot> remove;

    CheckBoxPageObject<SWTBot> disableJSPsyntaxValidation;

    TextPageObject<SWTBot> customJSPfolder;
    TablePageObject<SWTBot> jspFilesToOverride;

    TextPageObject<SWTBot> selectedProject;
    TextPageObject<SWTBot> webRootFolder;

    public CreateCustomJSPsPageObject( T bot, String title, int indexCustomJSPsValidationMessage )
    {
        super( bot, title, BUTTON_CANCEL, BUTTON_FINISH, BUTTON_BACK, BUTTON_NEXT, indexCustomJSPsValidationMessage );
        selectedProject = new TextPageObject<SWTBot>( bot, LABLE_SELECTED_PROJECT );
        webRootFolder = new TextPageObject<SWTBot>( bot, LABLE_WEB_ROOT_FOLDER );
        customJSPfolder = new TextPageObject<SWTBot>( bot, LABLE_CUSTOM_JSP_FOLDER );
        jspFilesToOverride = new TablePageObject<SWTBot>( bot, LABLE_JSP_FILES_TO_OVERRIDE );

        disableJSPsyntaxValidation = new CheckBoxPageObject<SWTBot>( bot, LABLE_DISABLE_JSP_SYNTAX_VALIDATION );

        browse = new ButtonPageObject<SWTBot>( bot, BUTTON_BROWSE );
        addFromLiferay = new ButtonPageObject<SWTBot>( bot, BUTTON_ADD_FROM_LIFERAY );
        add = new ButtonPageObject<SWTBot>( bot, BUTTON_ADD );
        edit = new ButtonPageObject<SWTBot>( bot, BUTTON_EDIT );
        remove = new ButtonPageObject<SWTBot>( bot, BUTTON_REMOVE );
    }

    public ButtonPageObject<SWTBot> getAdd()
    {
        return add;
    }

    public ButtonPageObject<SWTBot> getAddFromLiferay()
    {
        return addFromLiferay;
    }

    public ButtonPageObject<SWTBot> getBrowse()
    {
        return browse;
    }

    public ButtonPageObject<SWTBot> getEdit()
    {
        return edit;
    }

    public TablePageObject<SWTBot> getJspFilesToOverride()
    {
        return jspFilesToOverride;
    }

    public ButtonPageObject<SWTBot> getRemove()
    {
        return remove;
    }

    public void setCustomJSPfolder( String text )
    {
        this.customJSPfolder.setText( text );
    }

    public TextPageObject<SWTBot> getSelectedProject()
    {
        return selectedProject;
    }

    
    public TextPageObject<SWTBot> getCustomJSPfolder()
    {
        return customJSPfolder;
    }

    public TextPageObject<SWTBot> getWebRootFolder()
    {
        return webRootFolder;
    }

}
