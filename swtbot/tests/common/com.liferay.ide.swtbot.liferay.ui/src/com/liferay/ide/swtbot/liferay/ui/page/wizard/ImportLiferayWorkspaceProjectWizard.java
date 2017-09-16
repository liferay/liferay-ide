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

import com.liferay.ide.swtbot.ui.page.CheckBox;
import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.Wizard;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Terry Jia
 */
public class ImportLiferayWorkspaceProjectWizard extends Wizard
{

    private CheckBox addProjectToWorkingSet;
    private Text buildType;
    private Text bundleUrl;
    private CheckBox downloadLiferaybundle;
    private Text location;
    private Text serverName;

    public ImportLiferayWorkspaceProjectWizard( SWTWorkbenchBot bot )
    {
        super( bot, IMPORT_LIFERAY_WORKSPACE, 2 );

        buildType = new Text( bot, BUILD_TYPE );
        location = new Text( bot, WORKSPACE_LOCATION );
        downloadLiferaybundle = new CheckBox( bot, DOWNLOAD_LIFERAY_BUNDLE );
        serverName = new Text( bot, SERVER_NAME );
        bundleUrl = new Text( bot, BUNDLE_URL );
        addProjectToWorkingSet = new CheckBox( bot, ADD_PROJECT_TO_WORKING_SET );
    }

    public CheckBox getAddProjectToWorkingSet()
    {
        return addProjectToWorkingSet;
    }

    public Text getBuildTypeText()
    {
        return buildType;
    }

    public Text getBundleUrl()
    {
        return bundleUrl;
    }

    public CheckBox getDownloadLiferaybundle()
    {
        return downloadLiferaybundle;
    }

    public Text getServerName()
    {
        return serverName;
    }

    public Text getWorkspaceLocation()
    {
        return location;
    }

}
