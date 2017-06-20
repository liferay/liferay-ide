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

import com.liferay.ide.swtbot.project.ui.tests.LiferayModuleFragementWizard;
import com.liferay.ide.swtbot.ui.tests.page.ToolbarButtonWithTooltipPO;
import com.liferay.ide.swtbot.ui.tests.page.WizardPO;

/**
 * @author Ying Xu
 */
public class NewModuleFragmentWizardSecondPagePO extends WizardPO implements LiferayModuleFragementWizard
{

    private ToolbarButtonWithTooltipPO _addOverridFilesButton;
    private ToolbarButtonWithTooltipPO _browseOSGiBundleButton;

    public NewModuleFragmentWizardSecondPagePO( SWTBot bot )
    {
        super( bot, TEXT_BLANK, BUTTON_CANCEL, BUTTON_FINISH, BUTTON_BACK, BUTTON_NEXT );
        _browseOSGiBundleButton = new ToolbarButtonWithTooltipPO( bot, BUTTON_BROWSE );
        _addOverridFilesButton = new ToolbarButtonWithTooltipPO( bot, BUTTON_ADD_FILES_TO_OVERRIDE );
    }

    public ToolbarButtonWithTooltipPO getAddOverridFilesButton()
    {
        return _addOverridFilesButton;
    }

    public ToolbarButtonWithTooltipPO getOSGiBundleButton()
    {
        return _browseOSGiBundleButton;
    }

}
