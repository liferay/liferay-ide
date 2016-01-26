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

package com.liferay.ide.portlet.ui.tests.swtbot.pages;

import com.liferay.ide.project.ui.tests.swtbot.CreateLiferayPortletWizard;
import com.liferay.ide.project.ui.tests.swtbot.ProjectWizard;
import com.liferay.ide.ui.tests.swtbot.page.CheckBoxPageObject;
import com.liferay.ide.ui.tests.swtbot.page.TextPageObject;
import com.liferay.ide.ui.tests.swtbot.page.WizardPageObject;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Li Lu
 */
public class SpecifyPortletDetailsOnePageObject<T extends SWTBot> extends WizardPageObject<T>
    implements CreateLiferayPortletWizard, ProjectWizard
{

    CheckBoxPageObject<SWTBot> about;
    CheckBoxPageObject<SWTBot> config;
    CheckBoxPageObject<SWTBot> createJSPFiles;
    CheckBoxPageObject<SWTBot> createResourceBundleFile;
    CheckBoxPageObject<SWTBot> edit;
    CheckBoxPageObject<SWTBot> editDefaults;
    CheckBoxPageObject<SWTBot> editGuest;
    CheckBoxPageObject<SWTBot> help;
    TextPageObject<SWTBot> jspFolder;
    TextPageObject<SWTBot> name;
    TextPageObject<SWTBot> portletDisplayName;
    TextPageObject<SWTBot> portletTitle;
    CheckBoxPageObject<SWTBot> preview;
    TextPageObject<SWTBot> resourceBundleFilePath;
    CheckBoxPageObject<SWTBot> view;

    public SpecifyPortletDetailsOnePageObject( T bot )
    {
        this( bot, TEXT_BLANK, BUTTON_CANCEL, BUTTON_FINISH, BUTTON_BACK, BUTTON_NEXT, INDEX_VALIDATION_PORTLET_MESSAGE2 );
    }

    public SpecifyPortletDetailsOnePageObject(
        T bot, String title, String cancelButtonText, String finishButtonText, String backButtonText,
        String nextButtonText, int validationMessageIndex )
    {
        super( bot, title, cancelButtonText, finishButtonText, backButtonText, nextButtonText, validationMessageIndex );
        name = new TextPageObject<SWTBot>( bot, LABEL_NAME );
        portletDisplayName = new TextPageObject<SWTBot>( bot, LABEL_PORTLET_DISPLAY_NAME );

        portletTitle = new TextPageObject<SWTBot>( bot, LABEL_TITLE );
        view = new CheckBoxPageObject<SWTBot>( bot, LABEL_VIEW );

        edit = new CheckBoxPageObject<SWTBot>( bot, LABEL_EDIT );
        help = new CheckBoxPageObject<SWTBot>( bot, LABEL_HELP );
        about = new CheckBoxPageObject<SWTBot>( bot, LABEL_ABOUT );
        config = new CheckBoxPageObject<SWTBot>( bot, LABEL_CONFIG );
        editDefaults = new CheckBoxPageObject<SWTBot>( bot, LABEL_EDIT_DEFAULTS );
        editGuest = new CheckBoxPageObject<SWTBot>( bot, LABEL_EDIT_GUEST );
        preview = new CheckBoxPageObject<SWTBot>( bot, LABEL_PREVIEW );

        createJSPFiles = new CheckBoxPageObject<SWTBot>( bot, LABEL_CREATE_JSP_FILES );
        jspFolder = new TextPageObject<SWTBot>( bot, LABEL_JSP_FOLDER );
        createResourceBundleFile = new CheckBoxPageObject<SWTBot>( bot, LABEL_CREATE_RESOURCE_BUNDLER_FILE );
        resourceBundleFilePath = new TextPageObject<SWTBot>( bot, LABEL_RESOURCE_BUNDLE_FILE_PATH );
    }

    public CheckBoxPageObject<SWTBot> getAbout()
    {
        return about;
    }

    public CheckBoxPageObject<SWTBot> getConfig()
    {
        return config;
    }

    public CheckBoxPageObject<SWTBot> getCreateJSPFiles()
    {
        return createJSPFiles;
    }

    public CheckBoxPageObject<SWTBot> getCreateResourceBundleFile()
    {
        return createResourceBundleFile;
    }

    public CheckBoxPageObject<SWTBot> getEdit()
    {
        return edit;
    }

    public CheckBoxPageObject<SWTBot> getEditDefaults()
    {
        return editDefaults;
    }

    public CheckBoxPageObject<SWTBot> getEditGuest()
    {
        return editGuest;
    }

    public CheckBoxPageObject<SWTBot> getHelp()
    {
        return help;
    }

    public TextPageObject<SWTBot> getJspFolder()
    {
        return jspFolder;
    }

    public TextPageObject<SWTBot> getName()
    {
        return name;
    }

    public TextPageObject<SWTBot> getPortletDisplayName()
    {
        return portletDisplayName;
    }

    public TextPageObject<SWTBot> getPortletTitle()
    {
        return portletTitle;
    }

    public CheckBoxPageObject<SWTBot> getPreview()
    {
        return preview;
    }

    public TextPageObject<SWTBot> getResourceBundleFilePath()
    {
        return resourceBundleFilePath;
    }

    public CheckBoxPageObject<SWTBot> getView()
    {
        return view;
    }
}
