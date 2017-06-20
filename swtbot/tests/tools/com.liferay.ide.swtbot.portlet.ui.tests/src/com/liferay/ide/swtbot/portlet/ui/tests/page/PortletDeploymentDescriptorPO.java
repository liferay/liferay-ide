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

package com.liferay.ide.swtbot.portlet.ui.tests.page;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.portlet.ui.tests.LiferayPortletWizard;
import com.liferay.ide.swtbot.project.ui.tests.ProjectWizard;
import com.liferay.ide.swtbot.ui.tests.page.CheckBoxPO;
import com.liferay.ide.swtbot.ui.tests.page.TextPO;
import com.liferay.ide.swtbot.ui.tests.page.WizardPO;

/**
 * @author Ashley Yuan
 */
public class PortletDeploymentDescriptorPO extends WizardPO implements LiferayPortletWizard, ProjectWizard
{

    private TextPO _nameText;
    private TextPO _displayNameText;
    private TextPO _titleText;
    private CheckBoxPO _viewPortletModeCheckbox;
    private CheckBoxPO _editPortletModeCheckbox;
    private CheckBoxPO _helpPortletModeCheckbox;
    private CheckBoxPO _aboutLiferayPortletModeCheckbox;
    private CheckBoxPO _configLiferayPortletModeCheckbox;
    private CheckBoxPO _editDefaultsLiferayPortletModeCheckbox;
    private CheckBoxPO _editGuestLiferayPortletModeCheckbox;
    private CheckBoxPO _previewLiferayPortletModeCheckbox;
    private CheckBoxPO _printLiferayPortletModeCheckbox;
    private CheckBoxPO _createJspFilesCheckbox;
    private TextPO _jspFolderText;
    private CheckBoxPO _createResourceBundleFileCheckbox;
    private TextPO _resourceBundleFilePathText;

    public PortletDeploymentDescriptorPO( SWTBot bot )
    {
        this( bot, INDEX_SPECIFY_PORTLET_DEPLOYMENT_DESCRIPTOR_PAGE );
    }

    public PortletDeploymentDescriptorPO( SWTBot bot, int validationMessageIndex )
    {
        this( bot, TEXT_BLANK, validationMessageIndex );
    }

    public PortletDeploymentDescriptorPO( SWTBot bot, String title )
    {
        this( bot, title, INDEX_DEFAULT_VALIDATION_MESSAGE );
    }

    public PortletDeploymentDescriptorPO( SWTBot bot, String title, int validationMessageIndex )
    {
        super( bot, title, BUTTON_BACK, BUTTON_NEXT, BUTTON_FINISH, BUTTON_CANCEL, validationMessageIndex );

        _nameText = new TextPO( bot, LABEL_NAME );
        _displayNameText = new TextPO( bot, LABEL_DISPLAY_NAME );
        _titleText = new TextPO( bot, LABEL_TITLE );
        _viewPortletModeCheckbox = new CheckBoxPO( bot, CHECKBOX_PORTLET_MODE_VIEW );
        _editPortletModeCheckbox = new CheckBoxPO( bot, CHECKBOX_PORTLET_MODE_EDIT );
        _helpPortletModeCheckbox = new CheckBoxPO( bot, CHECKBOX_PORTLET_MODE_HELP );
        _aboutLiferayPortletModeCheckbox = new CheckBoxPO( bot, CHECKBOX_LIFERAY_PORTLET_MODE_ABOUT );
        _configLiferayPortletModeCheckbox = new CheckBoxPO( bot, CHECKBOX_LIFERAY_PORTLET_MODE_CONFIG );
        _editDefaultsLiferayPortletModeCheckbox = new CheckBoxPO( bot, CHECKBOX_LIFERAY_PORTLET_MODE_EDITDEFAULTS );
        _editGuestLiferayPortletModeCheckbox = new CheckBoxPO( bot, CHECKBOX_LIFERAY_PORTLET_MODE_EDITGUEST );
        _previewLiferayPortletModeCheckbox = new CheckBoxPO( bot, CHECKBOX_LIFERAY_PORTLET_MODE_PREVIEW );
        _printLiferayPortletModeCheckbox = new CheckBoxPO( bot, CHECKBOX_LIFERAY_PORTLET_MODE_PRINT );
        _createJspFilesCheckbox = new CheckBoxPO( bot, CHECKBOX_CREATE_JSP_FILES );
        _jspFolderText = new TextPO( bot, LABEL_JSP_FOLDER );
        _createResourceBundleFileCheckbox = new CheckBoxPO( bot, CHECKBOX_CREATE_RESOURCE_BUNDLE_FILE );
        _resourceBundleFilePathText = new TextPO( bot, LABEL_RESOURCE_BUNDLE_FILE_PATH );
    }

    public CheckBoxPO getAboutCheckBox()
    {
        return _aboutLiferayPortletModeCheckbox;
    }

    public CheckBoxPO getConfigCheckBox()
    {
        return _configLiferayPortletModeCheckbox;
    }

    public CheckBoxPO getEditDefaultsCheckBox()
    {
        return _editDefaultsLiferayPortletModeCheckbox;
    }

    public CheckBoxPO getEditGuestCheckBox()
    {
        return _configLiferayPortletModeCheckbox;
    }

    public CheckBoxPO getPreviewCheckBox()
    {
        return _previewLiferayPortletModeCheckbox;
    }

    public CheckBoxPO getPrintCheckBox()
    {
        return _printLiferayPortletModeCheckbox;
    }

    public String getDisplayName()
    {
        return _displayNameText.getText();
    }

    public String getJspFolder()
    {
        return _jspFolderText.getText();
    }

    public String getPortletName()
    {
        return _nameText.getText();
    }

    public String getPortletTitle()
    {
        return _titleText.getText();
    }

    public String getResourceBundleFilePath()
    {
        return _resourceBundleFilePathText.getText();
    }

    public CheckBoxPO get_viewPortletModeCheckbox()
    {
        return _viewPortletModeCheckbox;
    }

    public CheckBoxPO get_createJspFilesCheckbox()
    {
        return _createJspFilesCheckbox;
    }

    public CheckBoxPO get_createResourceBundleFileCheckbox()
    {
        return _createResourceBundleFileCheckbox;
    }

    public TextPO get_resourceBundleFilePathText()
    {
        return _resourceBundleFilePathText;
    }

    public boolean isViewPortletModeEnabled()
    {
        return _viewPortletModeCheckbox.isEnabled();
    }

    public boolean isEditPortletModeChecked()
    {
        return _editPortletModeCheckbox.isChecked();
    }

    public boolean isHelpPortletModeChecked()
    {
        return _helpPortletModeCheckbox.isChecked();
    }

    public void setDisplayName( String displayName )
    {
        this._displayNameText.setText( displayName );
    }

    public void setJspFolder( String jspFolder )
    {
        this._jspFolderText.setText( jspFolder );
    }

    public void setPortletName( String nameText )
    {
        this._nameText.setText( nameText );
    }

    public void setPortletTitle( String portletTitle )
    {
        this._titleText.setText( portletTitle );
    }

    public void setResourceBundleFilePath( String resourceBundleFilePath )
    {
        this._resourceBundleFilePathText.setText( resourceBundleFilePath );
    }

    public void specifyResources(
        boolean createJspFiles, String jspFolder, boolean createResourceBundleFile, String resourceBundleFilePath )
    {

        if( createJspFiles )
        {
            _createJspFilesCheckbox.select();
            if( jspFolder != null )
            {
                _jspFolderText.setText( jspFolder );
            }
        }
        else
        {
            _createJspFilesCheckbox.deselect();
        }

        if( createResourceBundleFile )
        {
            _createResourceBundleFileCheckbox.select();
            if( resourceBundleFilePath != null )
            {
                _resourceBundleFilePathText.setText( resourceBundleFilePath );
            }
        }
        else
        {
            _createResourceBundleFileCheckbox.deselect();
        }
    }

    public void speficyLiferayPortletModes(
        boolean aboutLiferayMode, boolean configLiferayMode, boolean editDefaultsLiferayMode,
        boolean editGuestLiferayMode, boolean previewLiferayMode, boolean printLiferayMode )
    {
        if( aboutLiferayMode )
        {
            _aboutLiferayPortletModeCheckbox.select();
        }
        else
        {
            _aboutLiferayPortletModeCheckbox.deselect();
        }

        if( configLiferayMode )
        {
            _configLiferayPortletModeCheckbox.select();
        }
        else
        {
            _configLiferayPortletModeCheckbox.deselect();
        }

        if( editDefaultsLiferayMode )
        {
            _editDefaultsLiferayPortletModeCheckbox.select();
        }
        else
        {
            _editDefaultsLiferayPortletModeCheckbox.deselect();
        }

        if( editGuestLiferayMode )
        {
            _editGuestLiferayPortletModeCheckbox.select();
        }
        else
        {
            _editGuestLiferayPortletModeCheckbox.deselect();
        }

        if( previewLiferayMode )
        {
            _previewLiferayPortletModeCheckbox.select();
        }
        else
        {
            _previewLiferayPortletModeCheckbox.deselect();
        }

        if( printLiferayMode )
        {
            _printLiferayPortletModeCheckbox.select();
        }
        else
        {
            _printLiferayPortletModeCheckbox.deselect();
        }
    }

    public void speficyPortletInfo( String portletName, String displayName, String portletTitle )
    {
        if( portletName != null )
        {
            _nameText.setText( portletName );
        }

        if( displayName != null )
        {
            _displayNameText.setText( displayName );
        }

        if( portletTitle != null )
        {
            _titleText.setText( portletTitle );
        }
    }

    public void speficyPortletModes( boolean editMode, boolean helpMode )
    {
        if( editMode )
        {
            _editPortletModeCheckbox.select();
        }
        else
        {
            _editPortletModeCheckbox.deselect();
        }

        if( helpMode )
        {
            _helpPortletModeCheckbox.select();
        }
        else
        {
            _helpPortletModeCheckbox.deselect();
        }
    }

}
