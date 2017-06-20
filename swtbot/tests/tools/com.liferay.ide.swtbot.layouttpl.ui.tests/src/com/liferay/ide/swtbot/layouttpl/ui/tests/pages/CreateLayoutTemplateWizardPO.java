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

package com.liferay.ide.swtbot.layouttpl.ui.tests.pages;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.layouttpl.ui.tests.CreateLayouttplWizard;
import com.liferay.ide.swtbot.ui.tests.page.ButtonPO;
import com.liferay.ide.swtbot.ui.tests.page.ComboBoxPO;
import com.liferay.ide.swtbot.ui.tests.page.TextPO;
import com.liferay.ide.swtbot.ui.tests.page.WizardPO;

/**
 * @author Li Lu
 */
public class CreateLayoutTemplateWizardPO extends WizardPO implements CreateLayouttplWizard
{

    private TextPO _idText;
    private ComboBoxPO _layoutPluginProjectCheckBox;
    private TextPO _nameText;
    private TextPO _templateFileText;
    private TextPO _thumbnailFileText;
    private TextPO _wapTemplateFileText;

    public CreateLayoutTemplateWizardPO( SWTBot bot )
    {
        this( bot, TEXT_BLANK, BUTTON_CANCEL, BUTTON_FINISH, BUTTON_BACK, BUTTON_NEXT, INDEX_LAYOUTTPL_VALIDATION_MESSAGE1 );
    }

    public CreateLayoutTemplateWizardPO(
        SWTBot bot, String title, String cancelButtonText, String finishButtonText, String backButtonText,
        String nextButtonText, int validationMessageIndex )
    {
        super( bot, title, cancelButtonText, finishButtonText, backButtonText, nextButtonText, validationMessageIndex );
        _layoutPluginProjectCheckBox = new ComboBoxPO( bot, LABEL_LAYOUT_PLUGIN_PROJECT );
        _nameText = new TextPO( bot, LABEL_NAME );
        _idText = new TextPO( bot, LABEL_ID );
        _templateFileText = new TextPO( bot, LABEL_TEMPLATE_FILE );
        _wapTemplateFileText = new TextPO( bot, LABEL_WAP_TEMPLATE_FILE );
        _thumbnailFileText = new TextPO( bot, LABEL_THUMBNAIL_FILE );
    }

    public void clickBrowseButton( int index )
    {
        new ButtonPO( bot, index ).click();
    }

    public String getIdText()
    {
        return _idText.getText();
    }

    public String getLayoutPluginProjectText()
    {
        return _layoutPluginProjectCheckBox.getText();
    }

    public String getNameText()
    {
        return _nameText.getText();
    }

    public String getTemplateFileText()
    {
        return _templateFileText.getText();
    }

    public String getThumbnailFileText()
    {
        return _thumbnailFileText.getText();
    }

    public String getWapTemplateFileText()
    {
        return _wapTemplateFileText.getText();
    }

    public void setIdText( String text )
    {
        _idText.setText( text );
    }

    public void setLayoutPluginProjectText( String text )
    {
        _layoutPluginProjectCheckBox.setText( text );
    }

    public void setNameText( String text )
    {
        _nameText.setText( text );
    }

    public void setTemplateFileText( String text )
    {
        _templateFileText.setText( text );
    }

    public void setThumbnailFileText( String text )
    {
        _thumbnailFileText.setText( text );
    }

    public void setWapTemplateFileText( String text )
    {
        _wapTemplateFileText.setText( text );
    }
}
