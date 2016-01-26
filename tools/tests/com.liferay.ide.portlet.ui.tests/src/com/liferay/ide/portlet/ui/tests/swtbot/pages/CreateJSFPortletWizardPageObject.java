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

import com.liferay.ide.portlet.ui.tests.swtbot.JSFPortletWizard;
import com.liferay.ide.project.ui.tests.page.CreateLiferayPortletWizardPageObject;
import com.liferay.ide.ui.tests.swtbot.page.CheckBoxPageObject;
import com.liferay.ide.ui.tests.swtbot.page.RadioPageObject;
import com.liferay.ide.ui.tests.swtbot.page.TextPageObject;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Li Lu
 */
public class CreateJSFPortletWizardPageObject<T extends SWTBot> extends CreateLiferayPortletWizardPageObject<T>
    implements JSFPortletWizard
{

    CheckBoxPageObject<SWTBot> createViewFiles;
    RadioPageObject<SWTBot> iCEfaces;
    RadioPageObject<SWTBot> liferayFacesAlloy;
    RadioPageObject<SWTBot> primeFaces;
    RadioPageObject<SWTBot> richFaces;
    RadioPageObject<SWTBot> standardJSF;
    TextPageObject<SWTBot> viewFolder;

    public CreateJSFPortletWizardPageObject( T bot )
    {
        this( bot, INDEX__JSF_VALIDATION_MESSAGE1 );
    }

    public CreateJSFPortletWizardPageObject( T bot, int validationMessageIndex )
    {
        super( bot, TEXT_BLANK, validationMessageIndex );

        createViewFiles = new CheckBoxPageObject<SWTBot>( bot, CREATE_VIRW_FILES );
        viewFolder = new TextPageObject<SWTBot>( bot, LABEL_VIEW_FOLDER );

        standardJSF = new RadioPageObject<SWTBot>( bot, LABEL_STANDARD_JSF );
        iCEfaces = new RadioPageObject<SWTBot>( bot, ICE_FACES );
        liferayFacesAlloy = new RadioPageObject<SWTBot>( bot, LABEL_LIFERAY_FACES_ALLOY );
        primeFaces = new RadioPageObject<SWTBot>( bot, LABEL_PRIME_FACES );
        richFaces = new RadioPageObject<SWTBot>( bot, LABEL_RICH_FACES );
    }

    public CheckBoxPageObject<SWTBot> getCreateViewFiles()
    {
        return createViewFiles;
    }

    public RadioPageObject<SWTBot> getiCEfaces()
    {
        return iCEfaces;
    }

    public RadioPageObject<SWTBot> getICEfaces()
    {
        return iCEfaces;
    }

    public RadioPageObject<SWTBot> getLiferayFacesAlloy()
    {
        return liferayFacesAlloy;
    }

    public RadioPageObject<SWTBot> getPrimeFaces()
    {
        return primeFaces;
    }

    public RadioPageObject<SWTBot> getRichFaces()
    {
        return richFaces;
    }

    public RadioPageObject<SWTBot> getStandardJSF()
    {
        return standardJSF;
    }

    public TextPageObject<SWTBot> getViewFolder()
    {
        return viewFolder;
    }

}
