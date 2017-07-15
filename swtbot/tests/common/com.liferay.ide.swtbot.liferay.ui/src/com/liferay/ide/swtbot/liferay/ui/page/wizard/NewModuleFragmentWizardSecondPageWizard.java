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
import com.liferay.ide.swtbot.ui.page.ToolbarButtonWithTooltip;
import com.liferay.ide.swtbot.ui.page.Wizard;

/**
 * @author Ying Xu
 */
public class NewModuleFragmentWizardSecondPageWizard extends Wizard implements WizardUI
{

    private ToolbarButtonWithTooltip addOverridFilesBtn;
    private ToolbarButtonWithTooltip browseOSGiBundleBtn;

    public NewModuleFragmentWizardSecondPageWizard( SWTBot bot )
    {
        super( bot );

        browseOSGiBundleBtn = new ToolbarButtonWithTooltip( bot, BROWSE );
        addOverridFilesBtn = new ToolbarButtonWithTooltip( bot, ADD_FILES_TO_OVERRIDE );
    }

    public ToolbarButtonWithTooltip getAddOverridFilesBtn()
    {
        return addOverridFilesBtn;
    }

    public ToolbarButtonWithTooltip getBrowseOSGiBundleBtn()
    {
        return browseOSGiBundleBtn;
    }

}
