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

import com.liferay.ide.swtbot.portlet.ui.tests.JSFPortletWizard;
import com.liferay.ide.swtbot.ui.tests.page.CheckBoxPO;
import com.liferay.ide.swtbot.ui.tests.page.RadioPO;
import com.liferay.ide.swtbot.ui.tests.page.TextPO;

/**
 * @author Li Lu
 */
public class CreateJSFPortletWizardPO extends CreateLiferayPortletWizardPO implements JSFPortletWizard
{

    private CheckBoxPO _createViewFilesCheckBox;
    private RadioPO _iCEfacesRadio;
    private RadioPO _liferayFacesAlloyRadio;
    private RadioPO _primeFacesRadio;
    private RadioPO _richFacesRadio;
    private RadioPO _standardJSFRadio;
    private TextPO _viewFolderText;

    public CreateJSFPortletWizardPO( SWTBot bot )
    {
        this( bot, INDEX__JSF_VALIDATION_MESSAGE1 );
    }

    public CreateJSFPortletWizardPO( SWTBot bot, int validationMessageIndex )
    {
        super( bot, TEXT_BLANK, validationMessageIndex );

        _createViewFilesCheckBox = new CheckBoxPO( bot, CREATE_VIRW_FILES );
        _viewFolderText = new TextPO( bot, LABEL_VIEW_FOLDER );

        _standardJSFRadio = new RadioPO( bot, LABEL_STANDARD_JSF );
        _iCEfacesRadio = new RadioPO( bot, LABEL_ICE_FACES );
        _liferayFacesAlloyRadio = new RadioPO( bot, LABEL_LIFERAY_FACES_ALLOY );
        _primeFacesRadio = new RadioPO( bot, LABEL_PRIME_FACES );
        _richFacesRadio = new RadioPO( bot, LABEL_RICH_FACES );
    }

    public boolean _isLiferayFacesAlloySelected()
    {
        return _liferayFacesAlloyRadio.isSelected();
    }

    public boolean _isStandardJSFSelected()
    {
        return _standardJSFRadio.isSelected();
    }

    public void deSelectCreateViewFiles()
    {
        _createViewFilesCheckBox.deselect();
    }

    public String getViewFolderText()
    {
        return _viewFolderText.getText();
    }

    public boolean isCreateViewFilesChecked()
    {
        return _createViewFilesCheckBox.isChecked();
    }

    public boolean isICEfacesEnabled()
    {
        return _iCEfacesRadio.isEnabled();
    }

    public boolean isICEfacesSelected()
    {
        return _iCEfacesRadio.isSelected();
    }

    public boolean isliferayFacesAlloyEnbled()
    {
        return _liferayFacesAlloyRadio.isEnabled();
    }

    public boolean isPrimeFacesEnbled()
    {
        return _primeFacesRadio.isEnabled();
    }

    public boolean isPrimeFacesSelected()
    {
        return _primeFacesRadio.isSelected();
    }

    public boolean isRichFacesEnbled()
    {
        return _richFacesRadio.isEnabled();
    }

    public boolean isRichFacesSelected()
    {
        return _richFacesRadio.isSelected();
    }

    public boolean isStandardJSFEnabled()
    {
        return _standardJSFRadio.isEnabled();
    }

    public boolean isViewFolderEnabled()
    {
        return _viewFolderText.isEnabled();
    }

    public void selectCreateViewFiles()
    {
        _createViewFilesCheckBox.select();
    }

    public void selectViewTemplate( String lable )
    {
        switch( lable )
        {
        case LABEL_STANDARD_JSF:
            _standardJSFRadio.click();
        case LABEL_ICE_FACES:
            _iCEfacesRadio.click();
        case LABEL_LIFERAY_FACES_ALLOY:
            _liferayFacesAlloyRadio.click();
        case LABEL_PRIME_FACES:
            _primeFacesRadio.click();
        case LABEL_RICH_FACES:
            _richFacesRadio.click();
        }
    }

    public void setViewFolderText( String text )
    {
        _viewFolderText.setText( text );
    }

}
