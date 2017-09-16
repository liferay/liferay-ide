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

package com.liferay.ide.swtbot.liferay.ui.page.wizard.project;

import com.liferay.ide.swtbot.ui.page.Table;
import com.liferay.ide.swtbot.ui.page.ToolbarButtonWithTooltip;
import com.liferay.ide.swtbot.ui.page.Wizard;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Ying Xu
 */
public class NewModuleFragmentInfoWizard extends Wizard
{

    private ToolbarButtonWithTooltip addOverrideFilePathBtn;
    private ToolbarButtonWithTooltip addOverrideFilesBtn;
    private ToolbarButtonWithTooltip browseOsgiBtn;
    private Table files;

    public NewModuleFragmentInfoWizard( SWTWorkbenchBot bot )
    {
        super( bot, 1 );

        browseOsgiBtn = new ToolbarButtonWithTooltip( bot, BROWSE );
        addOverrideFilesBtn = new ToolbarButtonWithTooltip( bot, ADD_FILES_FROM_OSGI_TO_OVERRIDE );
        files = new Table( bot, OVERRIDDEN_FILES );
        addOverrideFilePathBtn = new ToolbarButtonWithTooltip( bot, ADD_FILE_PATH );
    }

    public ToolbarButtonWithTooltip getAddOverrideFilePathBtn()
    {
        return addOverrideFilePathBtn;
    }

    public ToolbarButtonWithTooltip getAddOverrideFilesBtn()
    {
        return addOverrideFilesBtn;
    }

    public ToolbarButtonWithTooltip getBrowseOsgiBtn()
    {
        return browseOsgiBtn;
    }

    public Table getFiles()
    {
        return files;
    }

}
