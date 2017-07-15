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

import com.liferay.ide.swtbot.liferay.ui.ServiceBuilderUI;
import com.liferay.ide.swtbot.ui.page.Button;
import com.liferay.ide.swtbot.ui.page.CheckBox;
import com.liferay.ide.swtbot.ui.page.ComboBox;
import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.Wizard;

/**
 * @author Ying Xu
 */
public class ServiceBuilderWizard extends Wizard implements ServiceBuilderUI
{

    private Text author;
    private Button browseButton;
    private CheckBox includeSampleEntity;
    private Text namespace;
    private Text packagePath;
    private ComboBox pluginProjects;
    private Text serviceFile;

    public ServiceBuilderWizard( SWTBot bot )
    {
        this( bot, TEXT_BLANK );
    }

    public ServiceBuilderWizard( SWTBot bot, String title )
    {
        this( bot, title, INDEX_VALIDATION_MESSAGE );
    }

    public ServiceBuilderWizard( SWTBot bot, String title, int validationMsgIndex )
    {
        super( bot, title, validationMsgIndex );

        packagePath = new Text( bot, LABEL_PACKAGE_PATH );
        namespace = new Text( bot, LABEL_NAMESPACE );
        author = new Text( bot, LABEL_AUTHOR );
        serviceFile = new Text( bot, LABEL_SERVICE_FILE );
        includeSampleEntity = new CheckBox( bot, CHECKBOX_INCLUDE_SAMPLE_ENTITY );
        pluginProjects = new ComboBox( bot, COMBOBOX_PLUGIN_PROJECT );
        browseButton = new Button( bot, BROWSE_WITH_THREE_DOT );
    }

    public void createServiceBuilder( String packagePathText, String namespaceText )
    {
        createServiceBuilder( packagePathText, namespaceText, true );
    }

    public void createServiceBuilder( String packagePathText, String namespaceText, boolean includeSampleEntityValue )
    {
        packagePath.setText( packagePathText );
        namespace.setText( namespaceText );

        if( includeSampleEntityValue )
        {
            includeSampleEntity.select();
        }
        else
        {
            includeSampleEntity.deselect();
        }
    }

    public Text getAuthor()
    {
        return author;
    }

    public Button getBrowseButton()
    {
        return browseButton;
    }

    public CheckBox getIncludeSampleEntity()
    {
        return includeSampleEntity;
    }

    public Text getNamespace()
    {
        return namespace;
    }

    public Text getPackagePath()
    {
        return packagePath;
    }

    public ComboBox getPluginProjects()
    {
        return pluginProjects;
    }

    public Text getServiceFile()
    {
        return serviceFile;
    }

}
