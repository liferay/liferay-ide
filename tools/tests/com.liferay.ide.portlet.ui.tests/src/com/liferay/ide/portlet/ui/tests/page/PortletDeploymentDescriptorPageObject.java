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

package com.liferay.ide.portlet.ui.tests.page;

import com.liferay.ide.portlet.ui.tests.LiferayPortletWizard;
import com.liferay.ide.ui.tests.ProjectWizard;
import com.liferay.ide.ui.tests.swtbot.page.CheckBoxPageObject;
import com.liferay.ide.ui.tests.swtbot.page.TextPageObject;
import com.liferay.ide.ui.tests.swtbot.page.WizardPageObject;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Ashley Yuan
 */
public class PortletDeploymentDescriptorPageObject<T extends SWTBot> extends WizardPageObject<T>
    implements LiferayPortletWizard, ProjectWizard
{

    TextPageObject<SWTBot> nameText;
    TextPageObject<SWTBot> displayNameText;
    TextPageObject<SWTBot> titleText;

    CheckBoxPageObject<SWTBot> viewPortletModeCheckbox;
    CheckBoxPageObject<SWTBot> editPortletModeCheckbox;
    CheckBoxPageObject<SWTBot> helpPortletModeCheckbox;

    CheckBoxPageObject<SWTBot> aboutLiferayPortletModeCheckbox;
    CheckBoxPageObject<SWTBot> configLiferayPortletModeCheckbox;
    CheckBoxPageObject<SWTBot> editDefaultsLiferayPortletModeCheckbox;
    CheckBoxPageObject<SWTBot> editGuestLiferayPortletModeCheckbox;
    CheckBoxPageObject<SWTBot> previewLiferayPortletModeCheckbox;
    CheckBoxPageObject<SWTBot> printLiferayPortletModeCheckbox;

    CheckBoxPageObject<SWTBot> createJspFilesCheckbox;
    TextPageObject<SWTBot> jspFolderText;
    CheckBoxPageObject<SWTBot> createResourceBundleFileCheckbox;
    TextPageObject<SWTBot> resourceBundleFilePathText;

    public PortletDeploymentDescriptorPageObject( T bot )
    {
        this( bot, TEXT_BLANK, BUTTON_BACK, BUTTON_NEXT, BUTTON_FINISH, BUTTON_CANCEL, INDEX_DEFAULT_VALIDATION_MESSAGE );
    }

    public PortletDeploymentDescriptorPageObject( T bot, int validationMessageIndex )
    {
        this( bot, TEXT_BLANK, BUTTON_BACK, BUTTON_NEXT, BUTTON_FINISH, BUTTON_CANCEL, validationMessageIndex );
    }

    public PortletDeploymentDescriptorPageObject( T bot, String title )
    {
        this( bot, title, BUTTON_BACK, BUTTON_NEXT, BUTTON_FINISH, BUTTON_CANCEL, INDEX_DEFAULT_VALIDATION_MESSAGE );
    }

    public PortletDeploymentDescriptorPageObject(
        T bot, String title, String cancelButtonText, String finishButtonText, String backButtonText,
        String nextButtonText, int validationMessageIndex )
    {
        super( bot, title, BUTTON_BACK, BUTTON_NEXT, BUTTON_FINISH, BUTTON_CANCEL, validationMessageIndex );

        nameText = new TextPageObject<SWTBot>( bot, LABEL_NAME );
        displayNameText = new TextPageObject<SWTBot>( bot, LABEL_DISPLAY_NAME );
        titleText = new TextPageObject<SWTBot>( bot, LABEL_TITLE );

        viewPortletModeCheckbox = new CheckBoxPageObject<SWTBot>( bot, CHECKBOX_PORTLET_MODE_VIEW );
        editPortletModeCheckbox = new CheckBoxPageObject<SWTBot>( bot, CHECKBOX_PORTLET_MODE_EDIT );
        helpPortletModeCheckbox = new CheckBoxPageObject<SWTBot>( bot, CHECKBOX_PORTLET_MODE_HELP );

        aboutLiferayPortletModeCheckbox = new CheckBoxPageObject<SWTBot>( bot, CHECKBOX_LIFERAY_PORTLET_MODE_ABOUT );
        configLiferayPortletModeCheckbox = new CheckBoxPageObject<SWTBot>( bot, CHECKBOX_LIFERAY_PORTLET_MODE_CONFIG );
        editDefaultsLiferayPortletModeCheckbox =
            new CheckBoxPageObject<SWTBot>( bot, CHECKBOX_LIFERAY_PORTLET_MODE_EDITDEFAULTS );
        editGuestLiferayPortletModeCheckbox =
            new CheckBoxPageObject<SWTBot>( bot, CHECKBOX_LIFERAY_PORTLET_MODE_EDITGUEST );
        previewLiferayPortletModeCheckbox = new CheckBoxPageObject<SWTBot>( bot, CHECKBOX_LIFERAY_PORTLET_MODE_PREVIEW );
        printLiferayPortletModeCheckbox = new CheckBoxPageObject<SWTBot>( bot, CHECKBOX_LIFERAY_PORTLET_MODE_PRINT );

        createJspFilesCheckbox = new CheckBoxPageObject<SWTBot>( bot, CHECKBOX_CREATE_JSP_FILES );
        jspFolderText = new TextPageObject<SWTBot>( bot, LABEL_JSP_FOLDER );
        createResourceBundleFileCheckbox = new CheckBoxPageObject<SWTBot>( bot, CHECKBOX_CREATE_RESOURCE_BUNDLE_FILE );
        resourceBundleFilePathText = new TextPageObject<SWTBot>( bot, LABEL_RESOURCE_BUNDLE_FILE_PATH );
    }

    public String getDisplayName()
    {
        return displayNameText.getText();
    }

    public String getJspFolder()
    {
        return jspFolderText.getText();
    }

    public String getPortletName()
    {
        return nameText.getText();
    }

    public String getPortletTitle()
    {
        return titleText.getText();
    }

    public String getResourceBundleFilePath()
    {
        return resourceBundleFilePathText.getText();
    }

    public boolean isBundleFilePathEnabled()
    {
        return this.resourceBundleFilePathText.isEnabled();
    }

    public boolean isCheckboxEnabled( String label )
    {
        CheckBoxPageObject<SWTBot> checkbox = new CheckBoxPageObject<SWTBot>( bot, label );
        return checkbox.isEnabled();
    }

    public boolean isCreateJspFilesChecked()
    {
        return createJspFilesCheckbox.isChecked();
    }

    public boolean isCreateResourceBundleFileChecked()
    {
        return createResourceBundleFileCheckbox.isChecked();
    }

    public boolean isViewPortletModeChecked()
    {
        return viewPortletModeCheckbox.isChecked();
    }

    public void setDisplayName( String displayName )
    {
        this.displayNameText.setText( displayName );
    }

    public void setJspFolder( String jspFolder )
    {
        this.jspFolderText.setText( jspFolder );
    }

    public void setPortletName( String nameText )
    {
        this.nameText.setText( nameText );
    }

    public void setPortletTitle( String portletTitle )
    {
        this.titleText.setText( portletTitle );
    }

    public void setResourceBundleFilePath( String resourceBundleFilePath )
    {
        this.resourceBundleFilePathText.setText( resourceBundleFilePath );
    }

    public void specifyResources(
        boolean createJspFiles, String jspFolder, boolean createResourceBundleFile, String resourceBundleFilePath )
    {

        if( createJspFiles )
        {
            createJspFilesCheckbox.select();
            if( jspFolder != null )
            {
                jspFolderText.setText( jspFolder );
            }
        }
        else
        {
            createJspFilesCheckbox.deselect();
        }

        if( createResourceBundleFile )
        {
            createResourceBundleFileCheckbox.select();
            if( resourceBundleFilePath != null )
            {
                resourceBundleFilePathText.setText( resourceBundleFilePath );
            }
        }
        else
        {
            createResourceBundleFileCheckbox.deselect();
        }
    }

    public void speficyLiferayPortletModes(
        boolean aboutLiferayMode, boolean configLiferayMode, boolean editDefaultsLiferayMode,
        boolean editGuestLiferayMode, boolean previewLiferayMode, boolean printLiferayMode )
    {
        if( aboutLiferayMode )
        {
            aboutLiferayPortletModeCheckbox.select();
        }
        else
        {
            aboutLiferayPortletModeCheckbox.deselect();
        }

        if( configLiferayMode )
        {
            configLiferayPortletModeCheckbox.select();
        }
        else
        {
            configLiferayPortletModeCheckbox.deselect();
        }

        if( editDefaultsLiferayMode )
        {
            editDefaultsLiferayPortletModeCheckbox.select();
        }
        else
        {
            editDefaultsLiferayPortletModeCheckbox.deselect();
        }

        if( editGuestLiferayMode )
        {
            editGuestLiferayPortletModeCheckbox.select();
        }
        else
        {
            editGuestLiferayPortletModeCheckbox.deselect();
        }

        if( previewLiferayMode )
        {
            previewLiferayPortletModeCheckbox.select();
        }
        else
        {
            previewLiferayPortletModeCheckbox.deselect();
        }

        if( printLiferayMode )
        {
            printLiferayPortletModeCheckbox.select();
        }
        else
        {
            printLiferayPortletModeCheckbox.deselect();
        }
    }

    public void speficyPortletInfo( String portletName, String displayName, String portletTitle )
    {
        if( portletName != null )
        {
            nameText.setText( portletName );
        }

        if( displayName != null )
        {
            displayNameText.setText( displayName );
        }
        if( portletTitle != null )
        {
            titleText.setText( portletTitle );
        }
    }

    public void speficyPortletModes( boolean editMode, boolean helpMode )
    {
        if( editMode )
        {
            editPortletModeCheckbox.select();
        }
        else
        {
            editPortletModeCheckbox.deselect();
        }

        if( helpMode )
        {
            helpPortletModeCheckbox.select();
        }
        else
        {
            helpPortletModeCheckbox.deselect();
        }
    }
}
