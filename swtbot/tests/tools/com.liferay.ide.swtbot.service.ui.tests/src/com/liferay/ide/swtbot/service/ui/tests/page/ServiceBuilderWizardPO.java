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

package com.liferay.ide.swtbot.service.ui.tests.page;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.service.ui.tests.ServiceBuilderWizard;
import com.liferay.ide.swtbot.ui.tests.page.ButtonPO;
import com.liferay.ide.swtbot.ui.tests.page.CheckBoxPO;
import com.liferay.ide.swtbot.ui.tests.page.ComboBoxPO;
import com.liferay.ide.swtbot.ui.tests.page.TextPO;
import com.liferay.ide.swtbot.ui.tests.page.WizardPO;

/**
 * @author Ying Xu
 */
public class ServiceBuilderWizardPO extends WizardPO implements ServiceBuilderWizard
{

    private TextPO _author;
    private TextPO _serviceFile;
    private TextPO _namespace;
    private TextPO _packagePath;

    private ButtonPO _browseButton;

    private ComboBoxPO _pluginProjectComboBox;
    private CheckBoxPO _includeSampleEntityCheckBox;

    public ServiceBuilderWizardPO( SWTBot bot )
    {
        this( bot, TEXT_BLANK );
    }

    public ServiceBuilderWizardPO( SWTBot bot, String title )
    {
        this( bot, title, INDEX_VALIDATION_MESSAGE );
    }

    public ServiceBuilderWizardPO( SWTBot bot, String title, int validationMessageIndex )
    {
        super( bot, title, BUTTON_CANCEL, BUTTON_FINISH, BUTTON_BROWSE_WITH_DOT, TEXT_BLANK, validationMessageIndex );
        _packagePath = new TextPO( bot, LABEL_PACKAGE_PATH );
        _namespace = new TextPO( bot, LABEL_NAMESPACE );
        _author = new TextPO( bot, LABEL_AUTHOR );
        _serviceFile = new TextPO( bot, LABEL_SERVICE_FILE );
        _includeSampleEntityCheckBox = new CheckBoxPO( bot, CHECKBOX_INCLUDE_SAMPLE_ENTITY );
        _pluginProjectComboBox = new ComboBoxPO( bot, COMBOBOX_PLUGIN_PROJECT );
        _browseButton = new ButtonPO( bot, BUTTON_BROWSE_WITH_DOT );

    }

    public ButtonPO getBrowseButton()
    {
        return _browseButton;
    }

    public TextPO getAuthorText()
    {
        return _author;
    }

    public CheckBoxPO getIncludeSampleEntityCheckBox()
    {
        return _includeSampleEntityCheckBox;
    }

    public TextPO getNamespaceText()
    {
        return _namespace;
    }

    public TextPO getPackagePathText()
    {
        return _packagePath;
    }

    public ComboBoxPO getPluginProjectComboBox()
    {
        return _pluginProjectComboBox;
    }

    public TextPO getServiceFileText()
    {
        return _serviceFile;
    }

    public void NewServiceBuilder( String packagePathText, String namespaceText )
    {
        NewServiceBuilder( packagePathText, namespaceText, true );
    }

    public void NewServiceBuilder( String packagePathText, String namespaceText, boolean includeSampleEntity )
    {
        _packagePath.setText( packagePathText );
        _namespace.setText( namespaceText );

        if( includeSampleEntity )
        {
            _includeSampleEntityCheckBox.select();
        }
        else
        {
            _includeSampleEntityCheckBox.deselect();
        }
    }

}
