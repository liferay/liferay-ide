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

import com.liferay.ide.swtbot.project.ui.tests.LiferayProjectFromExistSourceWizard;
import com.liferay.ide.swtbot.ui.tests.page.ButtonPO;
import com.liferay.ide.swtbot.ui.tests.page.TextPO;
import com.liferay.ide.swtbot.ui.tests.page.ToolbarButtonWithTooltipPO;
import com.liferay.ide.swtbot.ui.tests.page.WizardPO;

/**
 * @author Li Lu
 * @author Ying Xu
 */
public class LiferayProjectFromExistSourceWizardPO extends WizardPO implements LiferayProjectFromExistSourceWizard
{

    private ToolbarButtonWithTooltipPO _browseSDKDirectory;

    private ButtonPO _selectAllButton;
    private ButtonPO _deselectAllButton;
    private ButtonPO _refreshButton;

    private TextPO _sdkDirectoryText;
    private TextPO _sdkVersionText;

    public LiferayProjectFromExistSourceWizardPO( SWTBot bot )
    {
        this( bot, TITLE_NEW_LIFERAY_PROJECT_EXIS_SOURCE, BUTTON_CANCEL, BUTTON_FINISH, BUTTON_BACK, BUTTON_NEXT, INDEX_DEFAULT_WIZARD_VALIDATION_MESSAGE );
    }

    public LiferayProjectFromExistSourceWizardPO(
        SWTBot bot, String title, String cancelButtonText, String finishButtonText, String backButtonText,
        String nextButtonText, int validationMessageIndex )
    {
        super( bot, title, cancelButtonText, finishButtonText, backButtonText, nextButtonText, validationMessageIndex );
        _sdkDirectoryText = new TextPO( bot, LABEL_SDK_DIRECTORY );
        _sdkVersionText = new TextPO( bot, LABLE_SDK_VERSION );
        _browseSDKDirectory = new ToolbarButtonWithTooltipPO( bot, BUTTON_BROWSE );
        _selectAllButton = new ButtonPO( bot, BUTTON_SELECT_ALL );
        _deselectAllButton = new ButtonPO( bot, BUTTON_DESELECT_ALL );
        _refreshButton = new ButtonPO( bot, BUTTON_REFRESH );

    }

    public ToolbarButtonWithTooltipPO getBrowseSdkDirectory()
    {
        return _browseSDKDirectory;
    }

    public ButtonPO getSelectAllButton()
    {
        return _selectAllButton;
    }

    public ButtonPO getDeselectAllButton()
    {
        return _deselectAllButton;
    }

    public ButtonPO getRefreshButton()
    {
        return _refreshButton;
    }

    public TextPO getSdkDirectoryText()
    {
        return _sdkDirectoryText;
    }

    public TextPO getSdkVersionText()
    {
        return _sdkVersionText;
    }

    public void importProject( String path )
    {
        getSdkDirectoryText().setText( path );

        finish();
        waitForPageToClose();
    }
}
