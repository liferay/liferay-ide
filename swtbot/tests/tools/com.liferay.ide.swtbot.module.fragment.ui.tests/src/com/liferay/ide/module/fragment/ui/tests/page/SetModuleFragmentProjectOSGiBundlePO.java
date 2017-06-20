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

package com.liferay.ide.swtbot.module.fragment.ui.tests.page;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.module.fragment.tests.ModuleFragmentProjectWizard;
import com.liferay.ide.swtbot.ui.tests.swtbot.page.TablePO;
import com.liferay.ide.swtbot.ui.tests.swtbot.page.ToolbarButtonWithTooltipPO;
import com.liferay.ide.swtbot.ui.tests.swtbot.page.WizardPO;

/**
 * @author Vicky Wang
 */
public class SetModuleFragmentProjectOSGiBundlePO extends WizardPO implements ModuleFragmentProjectWizard
{

    private ToolbarButtonWithTooltipPO _addOverriddenFilesButton;
    private ToolbarButtonWithTooltipPO _addOverrideFilePathButton;
    private TablePO _overriddenFilesTable;
    private ToolbarButtonWithTooltipPO _selectOSGiBundleButton;

    public SetModuleFragmentProjectOSGiBundlePO( SWTBot bot, int index )
    {
        this( bot, TEXT_OSGI_BUNDLE_BLANK, index );
    }

    public SetModuleFragmentProjectOSGiBundlePO( SWTBot bot, String title, int validationMessageIndex )
    {
        super( bot, title, BUTTON_CANCEL, BUTTON_FINISH, BUTTON_BACK, BUTTON_NEXT, validationMessageIndex );

        _selectOSGiBundleButton = new ToolbarButtonWithTooltipPO( bot, BUTTON_BROWSE );
        _addOverriddenFilesButton = new ToolbarButtonWithTooltipPO( bot, BUTTON_ADD_FILES_TO_OVERRIDE );
        _addOverrideFilePathButton = new ToolbarButtonWithTooltipPO( bot, BUTTON_ADD_FILE_PATH );
        _overriddenFilesTable = new TablePO( bot, LABLE_OVERRIDDEN_FILES );
    }

    public ToolbarButtonWithTooltipPO getAddOverriddenFilesButton()
    {
        return _addOverriddenFilesButton;
    }

    public ToolbarButtonWithTooltipPO getAddOverrideFilePathButton()
    {
        return _addOverrideFilePathButton;
    }

    public TablePO getOverriddenFiles()
    {
        return _overriddenFilesTable;
    }

    public ToolbarButtonWithTooltipPO getSelectOSGiBundleButton()
    {
        return _selectOSGiBundleButton;
    }

}
