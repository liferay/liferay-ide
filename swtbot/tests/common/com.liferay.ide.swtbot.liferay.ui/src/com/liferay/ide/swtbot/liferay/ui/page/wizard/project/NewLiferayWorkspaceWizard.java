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

import com.liferay.ide.swtbot.ui.page.CheckBox;
import com.liferay.ide.swtbot.ui.page.Text;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Vicky Wang
 * @author Ying Xu
 */
public class NewLiferayWorkspaceWizard extends NewProjectWizard
{

    private Text bundleUrl;
    private CheckBox downloadLiferayBundle;
    private Text serverName;

    public NewLiferayWorkspaceWizard( SWTWorkbenchBot bot )
    {
        super( bot, 2 );

        serverName = new Text( bot, SERVER_NAME );
        bundleUrl = new Text( bot, BUNDLE_URL );
        downloadLiferayBundle = new CheckBox( bot, DOWNLOAD_LIFERAY_BUNDLE );
    }

    public Text getBundleUrl()
    {
        return bundleUrl;
    }

    public CheckBox getDownloadLiferayBundle()
    {
        return downloadLiferayBundle;
    }

    public Text getServerName()
    {
        return serverName;
    }

}
