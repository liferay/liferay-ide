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

import com.liferay.ide.swtbot.liferay.ui.WizardUI;
import com.liferay.ide.swtbot.ui.page.Button;
import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.ToolbarButtonWithTooltip;
import com.liferay.ide.swtbot.ui.page.Wizard;

/**
 * @author Li Lu
 * @author Ying Xu
 */
public class LiferayProjectFromExistSourceWizard extends Wizard implements WizardUI
{

    private ToolbarButtonWithTooltip browseSdkDirectoryBtn;
    private Button deselectAllBtn;
    private Button refreshBtn;
    private Text sdkDirectory;
    private Text sdkVersion;
    private Button selectAllBtn;

    public LiferayProjectFromExistSourceWizard( SWTBot bot )
    {
        this( bot, TITLE_NEW_LIFERAY_PROJECT_EXIS_SOURCE, BACK_WITH_LEFT_BRACKET, NEXT_WITH_BRACKET, FINISH, CANCEL, INDEX_DEFAULT_WIZARD_VALIDATION_MESSAGE );
    }

    public LiferayProjectFromExistSourceWizard(
        SWTBot bot, String title, String cancelButtonText, String finishBtnText, String backBtnText,
        String nextButtonText, int validationMsgIndex )
    {
        super( bot, title, cancelButtonText, finishBtnText, backBtnText, nextButtonText, validationMsgIndex );
        sdkDirectory = new Text( bot, LABEL_SDK_DIRECTORY );
        sdkVersion = new Text( bot, LABLE_SDK_VERSION );
        browseSdkDirectoryBtn = new ToolbarButtonWithTooltip( bot, BROWSE );
        selectAllBtn = new Button( bot, BUTTON_SELECT_ALL );
        deselectAllBtn = new Button( bot, BUTTON_DESELECT_ALL );
        refreshBtn = new Button( bot, BUTTON_REFRESH );

    }

    public ToolbarButtonWithTooltip getBrowseSdkDirectoryBtn()
    {
        return browseSdkDirectoryBtn;
    }

    public Button getDeselectAllBtn()
    {
        return deselectAllBtn;
    }

    public Button getRefreshBtn()
    {
        return refreshBtn;
    }

    public Text getSdkDirectory()
    {
        return sdkDirectory;
    }

    public Text getSdkVersion()
    {
        return sdkVersion;
    }

    public Button getSelectAllBtn()
    {
        return selectAllBtn;
    }

    public void importProject( String path )
    {
        sdkDirectory.setText( path );

        finish();
    }

}
