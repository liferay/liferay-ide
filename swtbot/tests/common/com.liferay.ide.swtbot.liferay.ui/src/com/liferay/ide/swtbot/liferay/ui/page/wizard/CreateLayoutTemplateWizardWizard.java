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

import com.liferay.ide.swtbot.liferay.ui.CreateLayouttplWizard;
import com.liferay.ide.swtbot.ui.page.Button;
import com.liferay.ide.swtbot.ui.page.ComboBox;
import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.Wizard;

/**
 * @author Li Lu
 */
public class CreateLayoutTemplateWizardWizard extends Wizard implements CreateLayouttplWizard
{

    private Text id;
    private ComboBox layoutPluginProjects;
    private Text name;
    private Text templateFile;
    private Text thumbnailFile;
    private Text wapTemplateFile;

    public CreateLayoutTemplateWizardWizard( SWTBot bot )
    {
        super( bot, TEXT_BLANK, INDEX_LAYOUTTPL_VALIDATION_MESSAGE1 );

        layoutPluginProjects = new ComboBox( bot, LABEL_LAYOUT_PLUGIN_PROJECT );
        name = new Text( bot, LABEL_NAME );
        id = new Text( bot, LABEL_ID );
        templateFile = new Text( bot, LABEL_TEMPLATE_FILE );
        wapTemplateFile = new Text( bot, LABEL_WAP_TEMPLATE_FILE );
        thumbnailFile = new Text( bot, LABEL_THUMBNAIL_FILE );
    }

    public void clickBrowseButton( int index )
    {
        new Button( bot, index ).click();
    }

    public Text getId()
    {
        return id;
    }

    public ComboBox getLayoutPluginProjects()
    {
        return layoutPluginProjects;
    }

    public Text getName()
    {
        return name;
    }

    public Text getTemplateFile()
    {
        return templateFile;
    }

    public Text getThumbnailFile()
    {
        return thumbnailFile;
    }

    public Text getWapTemplateFile()
    {
        return wapTemplateFile;
    }

}
