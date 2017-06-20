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
import com.liferay.ide.swtbot.project.ui.tests.ProjectWizard;
import com.liferay.ide.swtbot.ui.tests.swtbot.page.DialogPO;
import com.liferay.ide.swtbot.ui.tests.swtbot.page.TreePO;

/**
 * @author Vicky Wang
 */
public class AddFilesToOverridePO extends DialogPO implements ModuleFragmentProjectWizard, ProjectWizard
{

    private TreePO _addFilesFromOSGiBundleToOverride;

    public AddFilesToOverridePO( SWTBot bot )
    {
        super( bot, BUTTON_CANCEL, BUTTON_OK );

        _addFilesFromOSGiBundleToOverride = new TreePO( bot );
    }

    public void select( String... items )
    {
        _addFilesFromOSGiBundleToOverride.selectTreeItem( items );
    }

}