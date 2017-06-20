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

package com.liferay.ide.swtbot.gradle.ui.tests.page;

import com.liferay.ide.swtbot.ui.tests.page.CheckBoxPO;
import com.liferay.ide.swtbot.ui.tests.page.ComboBoxPO;
import com.liferay.ide.swtbot.ui.tests.page.TextPO;
import com.liferay.ide.swtbot.ui.tests.page.ToolbarButtonWithTooltipPO;
import com.liferay.ide.swtbot.ui.tests.page.WizardPO;
import com.liferay.ide.swtbot.gradle.tests.LiferayWorkspaceProjectWizard;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Vicky Wang
 * @author Ying Xu
 */
public class CreateWorkspaceProjectWizardPO extends WizardPO implements LiferayWorkspaceProjectWizard
{

    private TextPO _workspaceNameText;
    private TextPO _serverNameText;
    private TextPO _bundleUrlText;
    private TextPO _location;
    private ComboBoxPO _buildType;
    private CheckBoxPO _useDefaultLocation;
    private CheckBoxPO _downloadLiferayBundleCheckBox;
    private ToolbarButtonWithTooltipPO _saveToolbar;

    public CreateWorkspaceProjectWizardPO( SWTBot bot )
    {
        this( bot, INDEX_DEFAULT_VALIDATION_MESSAGE );
    }

    public CreateWorkspaceProjectWizardPO( SWTBot bot, int index )
    {
        this( bot, TEXT_BLANK, index );
    }

    public CreateWorkspaceProjectWizardPO( SWTBot bot, String title )
    {
        this( bot, title, INDEX_DEFAULT_VALIDATION_MESSAGE );
    }

    public CreateWorkspaceProjectWizardPO( SWTBot bot, String title, int validationMessageIndex )
    {
        super( bot, title, BUTTON_CANCEL, BUTTON_FINISH, BUTTON_BACK, BUTTON_NEXT, validationMessageIndex );

        _workspaceNameText = new TextPO( bot, LABEL_WORKSPACE_NAME );
        _serverNameText = new TextPO( bot, LABEL_SERVER_NAME );
        _bundleUrlText = new TextPO( bot, LABEL_BUNDLE_URL );
        _location = new TextPO( bot, LABEL_LOCATION );
        _buildType = new ComboBoxPO( bot, LABEL_BUILD_TYPE );
        _downloadLiferayBundleCheckBox = new CheckBoxPO( bot, LABEL_DOWNLOAD_LIFERAY_BUNDLE );
        _useDefaultLocation = new CheckBoxPO( bot, LABEL_USE_DEFAULT_LOCATION );
        _saveToolbar = new ToolbarButtonWithTooltipPO( bot, TOOLBAR_SAVE );
    }

    public String getWorkspaceNameText()
    {
        return _workspaceNameText.getText();
    }

    public void setWorkspaceName( String workspaceName )
    {
        this._workspaceNameText.setText( workspaceName );
    }

    public TextPO getServerNameText()
    {
        return _serverNameText;
    }

    public void setServerName( String serverName )
    {
        this._serverNameText.setText( serverName );
    }

    public TextPO getBundleUrlText()
    {
        return _bundleUrlText;
    }

    public void setBundleUrlText( TextPO bundleUrlText )
    {
        this._bundleUrlText = bundleUrlText;
    }

    public TextPO getLocation()
    {
        return _location;
    }

    public void setLocation( TextPO _location )
    {
        this._location = _location;
    }

    public ComboBoxPO getBuildType()
    {
        return _buildType;
    }

    public void setBuildType( ComboBoxPO buildType )
    {
        this._buildType = buildType;
    }

    public CheckBoxPO getUseDefaultLocation()
    {
        return _useDefaultLocation;
    }

    public void setUseDefaultLocation( CheckBoxPO _useDefaultLocation )
    {
        this._useDefaultLocation = _useDefaultLocation;
    }

    public CheckBoxPO getDownloadLiferayBundleCheckbox()
    {
        return _downloadLiferayBundleCheckBox;
    }

    public void setDownloadLiferayBundleCheckBox( CheckBoxPO downloadLiferayBundleCheckBox )
    {
        this._downloadLiferayBundleCheckBox = downloadLiferayBundleCheckBox;
    }

    public boolean isDownloadLiferayBundleChecked()
    {
        return _downloadLiferayBundleCheckBox.isChecked();
    }

    public ToolbarButtonWithTooltipPO getSaveToolbar()
    {
        return _saveToolbar;
    }

}
