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

import com.liferay.ide.swtbot.liferay.ui.LiferayPortletWizardUI;
import com.liferay.ide.swtbot.liferay.ui.WizardUI;
import com.liferay.ide.swtbot.ui.page.CheckBox;
import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.Wizard;

/**
 * @author Ashley Yuan
 */
public class PortletDeploymentDescriptorWizard extends Wizard implements LiferayPortletWizardUI, WizardUI
{

    private CheckBox about;
    private CheckBox config;
    private CheckBox createJspFiles;
    private CheckBox createResourceBundleFile;
    private Text displayName;
    private CheckBox edit;
    private CheckBox editDefaults;
    private CheckBox editGuest;
    private CheckBox help;
    private Text jspFolder;
    private Text portletName;
    private Text portletTitle;
    private CheckBox preview;
    private CheckBox print;
    private Text resourceBundleFilePath;
    private CheckBox view;

    public PortletDeploymentDescriptorWizard( SWTBot bot )
    {
        this( bot, INDEX_SPECIFY_PORTLET_DEPLOYMENT_DESCRIPTOR_PAGE );
    }

    public PortletDeploymentDescriptorWizard( SWTBot bot, int validationMsgIndex )
    {
        this( bot, TEXT_BLANK, validationMsgIndex );
    }

    public PortletDeploymentDescriptorWizard( SWTBot bot, String title )
    {
        this( bot, title, INDEX_DEFAULT_VALIDATION_MESSAGE );
    }

    public PortletDeploymentDescriptorWizard( SWTBot bot, String title, int validationMsgIndex )
    {
        super( bot, title, validationMsgIndex );

        portletName = new Text( bot, LABEL_NAME );
        displayName = new Text( bot, LABEL_DISPLAY_NAME );
        portletTitle = new Text( bot, LABEL_TITLE );
        view = new CheckBox( bot, CHECKBOX_PORTLET_MODE_VIEW );
        edit = new CheckBox( bot, CHECKBOX_PORTLET_MODE_EDIT );
        help = new CheckBox( bot, CHECKBOX_PORTLET_MODE_HELP );
        about = new CheckBox( bot, CHECKBOX_LIFERAY_PORTLET_MODE_ABOUT );
        config = new CheckBox( bot, CHECKBOX_LIFERAY_PORTLET_MODE_CONFIG );
        editDefaults = new CheckBox( bot, CHECKBOX_LIFERAY_PORTLET_MODE_EDITDEFAULTS );
        editGuest = new CheckBox( bot, CHECKBOX_LIFERAY_PORTLET_MODE_EDITGUEST );
        preview = new CheckBox( bot, CHECKBOX_LIFERAY_PORTLET_MODE_PREVIEW );
        print = new CheckBox( bot, CHECKBOX_LIFERAY_PORTLET_MODE_PRINT );
        createJspFiles = new CheckBox( bot, CHECKBOX_CREATE_JSP_FILES );
        jspFolder = new Text( bot, LABEL_JSP_FOLDER );
        createResourceBundleFile = new CheckBox( bot, CHECKBOX_CREATE_RESOURCE_BUNDLE_FILE );
        resourceBundleFilePath = new Text( bot, LABEL_RESOURCE_BUNDLE_FILE_PATH );
    }

    public CheckBox getAbout()
    {
        return about;
    }

    public CheckBox getConfig()
    {
        return config;
    }

    public CheckBox getCreateJspFiles()
    {
        return createJspFiles;
    }

    public CheckBox getCreateResourceBundleFile()
    {
        return createResourceBundleFile;
    }

    public Text getDisplayName()
    {
        return displayName;
    }

    public CheckBox getEdit()
    {
        return edit;
    }

    public CheckBox getEditDefaults()
    {
        return editDefaults;
    }

    public CheckBox getEditGuest()
    {
        return editGuest;
    }

    public CheckBox getHelp()
    {
        return help;
    }

    public Text getJspFolder()
    {
        return jspFolder;
    }

    public Text getPortletName()
    {
        return portletName;
    }

    public Text getPortletTitle()
    {
        return portletTitle;
    }

    public CheckBox getPreview()
    {
        return preview;
    }

    public CheckBox getPrint()
    {
        return print;
    }

    public Text getResourceBundleFilePath()
    {
        return resourceBundleFilePath;
    }

    public CheckBox getView()
    {
        return view;
    }

    public void specifyResources(
        boolean createJspFilesValue, String jspFolderValue, boolean createResourceBundleFileValue,
        String resourceBundleFilePathValue )
    {

        if( createJspFilesValue )
        {
            createJspFiles.select();
            if( jspFolder != null )
            {
                jspFolder.setText( jspFolderValue );
            }
        }
        else
        {
            createJspFiles.deselect();
        }

        if( createResourceBundleFileValue )
        {
            createResourceBundleFile.select();
            if( resourceBundleFilePath != null )
            {
                resourceBundleFilePath.setText( resourceBundleFilePathValue );
            }
        }
        else
        {
            createResourceBundleFile.deselect();
        }
    }

    public void speficyLiferayPortletModes(
        boolean aboutLiferayMode, boolean configLiferayMode, boolean editDefaultsLiferayMode,
        boolean editGuestLiferayMode, boolean previewLiferayMode, boolean printLiferayMode )
    {
        if( aboutLiferayMode )
        {
            about.select();
        }
        else
        {
            about.deselect();
        }

        if( configLiferayMode )
        {
            config.select();
        }
        else
        {
            config.deselect();
        }

        if( editDefaultsLiferayMode )
        {
            editDefaults.select();
        }
        else
        {
            editDefaults.deselect();
        }

        if( editGuestLiferayMode )
        {
            editGuest.select();
        }
        else
        {
            editGuest.deselect();
        }

        if( previewLiferayMode )
        {
            preview.select();
        }
        else
        {
            preview.deselect();
        }

        if( printLiferayMode )
        {
            print.select();
        }
        else
        {
            print.deselect();
        }
    }

    public void speficyPortletInfo( String portletNameValue, String displayNameValue, String portletTitleValue )
    {
        if( portletNameValue != null )
        {
            portletName.setText( portletNameValue );
        }

        if( displayName != null )
        {
            displayName.setText( displayNameValue );
        }

        if( portletTitle != null )
        {
            portletTitle.setText( portletTitleValue );
        }
    }

    public void speficyPortletModes( boolean editMode, boolean helpMode )
    {
        if( editMode )
        {
            edit.select();
        }
        else
        {
            edit.deselect();
        }

        if( helpMode )
        {
            help.select();
        }
        else
        {
            help.deselect();
        }
    }

}
