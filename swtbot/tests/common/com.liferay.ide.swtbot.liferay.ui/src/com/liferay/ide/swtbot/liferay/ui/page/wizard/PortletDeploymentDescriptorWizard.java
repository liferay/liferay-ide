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
 * @author Ashley Yuan
 */
public class PortletDeploymentDescriptorWizard extends Wizard
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
    private Text name;
    private CheckBox preview;
    private CheckBox print;
    private Text resourceBundleFilePath;
    private Text title;
    private CheckBox view;

    public PortletDeploymentDescriptorWizard( SWTWorkbenchBot bot )
    {
        super( bot, 5 );

        name = new Text( bot, NAME );
        displayName = new Text( bot, DISPLAY_NAME );
        title = new Text( bot, TITLE );
        view = new CheckBox( bot, VIEW );
        edit = new CheckBox( bot, EDIT );
        help = new CheckBox( bot, HELP );
        about = new CheckBox( bot, ABOUT );
        config = new CheckBox( bot, CONFIG );
        editDefaults = new CheckBox( bot, EDIT_DEFAULTS );
        editGuest = new CheckBox( bot, EDIT_GUEST );
        preview = new CheckBox( bot, PREVIEW );
        print = new CheckBox( bot, PRINT );
        createJspFiles = new CheckBox( bot, CREATE_JSP_FILES );
        jspFolder = new Text( bot, JSP_FOLDER );
        createResourceBundleFile = new CheckBox( bot, CREATE_RESOURCE_BUNDLE_FILE );
        resourceBundleFilePath = new Text( bot, RESOURCE_BUNDLE_FILE_PATH );
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
        return name;
    }

    public Text getPortletTitle()
    {
        return title;
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
            name.setText( portletNameValue );
        }

        if( displayName != null )
        {
            displayName.setText( displayNameValue );
        }

        if( title != null )
        {
            title.setText( portletTitleValue );
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
