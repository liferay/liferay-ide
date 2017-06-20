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

import com.liferay.ide.swtbot.project.ui.tests.ImportLiferayModuleProject;
import com.liferay.ide.swtbot.ui.tests.page.TextPO;
import com.liferay.ide.swtbot.ui.tests.page.ToolbarButtonWithTooltipPO;
import com.liferay.ide.swtbot.ui.tests.page.WizardPO;

/**
 * @author Ashley Yuan
 */
public class ImportLiferayModuleProjectPO extends WizardPO implements ImportLiferayModuleProject
{

    private TextPO _locationText;
    private ToolbarButtonWithTooltipPO _browseButton;
    private TextPO _buildTypeText;

    public ImportLiferayModuleProjectPO( SWTBot bot, String title, int validationMessageIndex )
    {
        super( bot, title, BUTTON_CANCEL, BUTTON_FINISH, BUTTON_BACK, BUTTON_NEXT, validationMessageIndex );
        _locationText = new TextPO( bot, LABEL_LOCATION );
        _buildTypeText = new TextPO( bot, LABEL_BUILD_TYPE );
        _browseButton = new ToolbarButtonWithTooltipPO( bot, BUTTON_BROWSE );
    }

    public TextPO get_locationText()
    {
        return _locationText;
    }

    public ToolbarButtonWithTooltipPO get_browseButton()
    {
        return _browseButton;
    }

    public TextPO get_buildTypeText()
    {
        return _buildTypeText;
    }

}
