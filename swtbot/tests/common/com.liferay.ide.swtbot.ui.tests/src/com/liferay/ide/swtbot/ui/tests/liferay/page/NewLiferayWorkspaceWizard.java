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

package com.liferay.ide.swtbot.ui.tests.liferay.page;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.ui.tests.page.CheckBoxPO;
import com.liferay.ide.swtbot.ui.tests.page.ComboBoxPO;
import com.liferay.ide.swtbot.ui.tests.page.TextPO;
import com.liferay.ide.swtbot.ui.tests.page.ToolbarButtonWithTooltipPO;
import com.liferay.ide.swtbot.ui.tests.page.WizardPO;

/**
 * @author Vicky Wang
 * @author Ying Xu
 */
public class NewLiferayWorkspaceWizard extends WizardPO
{

    private ComboBoxPO buildTypes;
    private TextPO bundleUrl;
    private CheckBoxPO downloadLiferayBundle;
    private TextPO location;
    private ToolbarButtonWithTooltipPO saveToolbar;
    private TextPO serverName;
    private CheckBoxPO useDefaultLocation;
    private TextPO workspaceName;

    public NewLiferayWorkspaceWizard( SWTBot bot )
    {
        this( bot, INDEX_DEFAULT_VALIDATION_MESSAGE );
    }

    public NewLiferayWorkspaceWizard( SWTBot bot, int index )
    {
        this( bot, TEXT_BLANK, index );
    }

    public NewLiferayWorkspaceWizard( SWTBot bot, String title )
    {
        this( bot, title, INDEX_DEFAULT_VALIDATION_MESSAGE );
    }

    public NewLiferayWorkspaceWizard( SWTBot bot, String title, int validationMessageIndex )
    {
        super( bot, title, BUTTON_CANCEL, BUTTON_FINISH, BUTTON_BACK, BUTTON_NEXT, validationMessageIndex );

        workspaceName = new TextPO( bot, LABEL_WORKSPACE_NAME );
        serverName = new TextPO( bot, LABEL_SERVER_NAME );
        bundleUrl = new TextPO( bot, LABEL_BUNDLE_URL );
        location = new TextPO( bot, LABEL_LOCATION );
        buildTypes = new ComboBoxPO( bot, LABEL_BUILD_TYPE );
        downloadLiferayBundle = new CheckBoxPO( bot, LABEL_DOWNLOAD_LIFERAY_BUNDLE );
        useDefaultLocation = new CheckBoxPO( bot, LABEL_USE_DEFAULT_LOCATION );
        saveToolbar = new ToolbarButtonWithTooltipPO( bot, TOOLBAR_SAVE );
    }

    public ComboBoxPO getBuildTypes()
    {
        return buildTypes;
    }

    public TextPO getBundleUrl()
    {
        return bundleUrl;
    }

    public CheckBoxPO getDownloadLiferayBundle()
    {
        return downloadLiferayBundle;
    }

    public TextPO getLocation()
    {
        return location;
    }

    public ToolbarButtonWithTooltipPO getSaveToolbar()
    {
        return saveToolbar;
    }

    public TextPO getServerName()
    {
        return serverName;
    }

    public CheckBoxPO getUseDefaultLocation()
    {
        return useDefaultLocation;
    }

    public String getWorkspaceName()
    {
        return workspaceName.getText();
    }

    public boolean isDownloadLiferayBundleChecked()
    {
        return downloadLiferayBundle.isChecked();
    }

    public void setBuildTypes( ComboBoxPO buildType )
    {
        this.buildTypes = buildType;
    }

    public void setBundleUrlText( TextPO bundleUrl )
    {
        this.bundleUrl = bundleUrl;
    }

    public void setDownloadLiferayBundle( CheckBoxPO downloadLiferayBundle )
    {
        this.downloadLiferayBundle = downloadLiferayBundle;
    }

    public void setLocation( TextPO location )
    {
        this.location = location;
    }

    public void setServerName( String serverName )
    {
        this.serverName.setText( serverName );
    }

    public void setUseDefaultLocation( CheckBoxPO useDefaultLocation )
    {
        this.useDefaultLocation = useDefaultLocation;
    }

    public void setWorkspaceName( String workspaceName )
    {
        this.workspaceName.setText( workspaceName );
    }

}
