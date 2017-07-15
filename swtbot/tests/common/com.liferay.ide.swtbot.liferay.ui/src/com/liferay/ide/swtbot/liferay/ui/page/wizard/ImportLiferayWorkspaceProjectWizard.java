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

import com.liferay.ide.swtbot.liferay.ui.ImportLiferayWorkspaceProjectUI;
import com.liferay.ide.swtbot.ui.page.CheckBox;
import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.Wizard;

/**
 * @author Terry Jia
 */
public class ImportLiferayWorkspaceProjectWizard extends Wizard implements ImportLiferayWorkspaceProjectUI
{

    private CheckBox addProjectToWorkingSet;
    private Text buildType;
    private Text bundleUrl;
    private CheckBox downloadLiferaybundle;
    private Text serverName;
    private Text workspaceLocation;

    public ImportLiferayWorkspaceProjectWizard( SWTBot bot, String title, int validationMsgIndex )
    {

        super( bot, title, validationMsgIndex );
        buildType = new Text( bot, LABEL_BUILD_TYPE );
        workspaceLocation = new Text( bot, TEXT_WORKSPACE_LOCATION );
        downloadLiferaybundle = new CheckBox( bot, CHECKBOX_DOWNLOAD_LIFERAY_BUNDLE );
        serverName = new Text( bot, TEXT_SERVER_NAME );
        bundleUrl = new Text( bot, TEXT_BUNDLE_URL );
        addProjectToWorkingSet = new CheckBox( bot, CHECKBOX_ADD_PROJECT_TO_WORKING_SET );
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
        return workspaceLocation;
    }

    public void setBuildTypeText( String buildTypeText )
    {
        this.buildType.setText( buildTypeText );
    }

    public void setBundleUrl( String bundleUrl )
    {
        this.bundleUrl.setText( bundleUrl );
    }

    public void setDownloadLiferaybundle( CheckBox downloadLiferaybundle )
    {
        this.downloadLiferaybundle = downloadLiferaybundle;
    }

    public void setServerName( String serverName )
    {
        this.serverName.setText( serverName );
    }

    public void setWorkspaceLocation( String workspaceLocation )
    {
        this.workspaceLocation.setText( workspaceLocation );;
    }

}
