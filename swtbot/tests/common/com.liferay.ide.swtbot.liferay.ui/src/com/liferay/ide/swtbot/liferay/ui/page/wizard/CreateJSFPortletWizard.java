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

import com.liferay.ide.swtbot.liferay.ui.JSFPortletWizardUI;
import com.liferay.ide.swtbot.ui.page.CheckBox;
import com.liferay.ide.swtbot.ui.page.Radio;
import com.liferay.ide.swtbot.ui.page.Text;

/**
 * @author Li Lu
 */
public class CreateJSFPortletWizard extends CreateLiferayPortletWizard implements JSFPortletWizardUI
{

    private CheckBox createViewFiles;
    private Radio iceFaces;
    private Radio liferayFacesAlloy;
    private Radio primeFaces;
    private Radio richFaces;
    private Radio standardJSF;
    private Text viewFolder;

    public CreateJSFPortletWizard( SWTBot bot )
    {
        this( bot, INDEX__JSF_VALIDATION_MESSAGE1 );
    }

    public CreateJSFPortletWizard( SWTBot bot, int validationMsgIndex )
    {
        super( bot, TEXT_BLANK, validationMsgIndex );

        createViewFiles = new CheckBox( bot, CREATE_VIRW_FILES );
        viewFolder = new Text( bot, LABEL_VIEW_FOLDER );
        standardJSF = new Radio( bot, LABEL_STANDARD_JSF );
        iceFaces = new Radio( bot, LABEL_ICE_FACES );
        liferayFacesAlloy = new Radio( bot, LABEL_LIFERAY_FACES_ALLOY );
        primeFaces = new Radio( bot, LABEL_PRIME_FACES );
        richFaces = new Radio( bot, LABEL_RICH_FACES );
    }

    public boolean _isLiferayFacesAlloySelected()
    {
        return liferayFacesAlloy.isSelected();
    }

    public boolean _isStandardJSFSelected()
    {
        return standardJSF.isSelected();
    }

    public void deSelectCreateViewFiles()
    {
        createViewFiles.deselect();
    }

    public String getViewFolderText()
    {
        return viewFolder.getText();
    }

    public boolean isCreateViewFilesChecked()
    {
        return createViewFiles.isChecked();
    }

    public boolean isICEfacesEnabled()
    {
        return iceFaces.isEnabled();
    }

    public boolean isICEfacesSelected()
    {
        return iceFaces.isSelected();
    }

    public boolean isliferayFacesAlloyEnbled()
    {
        return liferayFacesAlloy.isEnabled();
    }

    public boolean isPrimeFacesEnbled()
    {
        return primeFaces.isEnabled();
    }

    public boolean isPrimeFacesSelected()
    {
        return primeFaces.isSelected();
    }

    public boolean isRichFacesEnbled()
    {
        return richFaces.isEnabled();
    }

    public boolean isRichFacesSelected()
    {
        return richFaces.isSelected();
    }

    public boolean isStandardJSFEnabled()
    {
        return standardJSF.isEnabled();
    }

    public boolean isViewFolderEnabled()
    {
        return viewFolder.isEnabled();
    }

    public void selectCreateViewFiles()
    {
        createViewFiles.select();
    }

    public void selectViewTemplate( String lable )
    {
        switch( lable )
        {
        case LABEL_STANDARD_JSF:
            standardJSF.click();
        case LABEL_ICE_FACES:
            iceFaces.click();
        case LABEL_LIFERAY_FACES_ALLOY:
            liferayFacesAlloy.click();
        case LABEL_PRIME_FACES:
            primeFaces.click();
        case LABEL_RICH_FACES:
            richFaces.click();
        }
    }

    public void setViewFolderText( String text )
    {
        viewFolder.setText( text );
    }

}
