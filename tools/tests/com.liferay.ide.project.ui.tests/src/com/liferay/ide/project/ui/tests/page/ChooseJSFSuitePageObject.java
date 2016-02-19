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

package com.liferay.ide.project.ui.tests.page;

import com.liferay.ide.project.ui.tests.swtbot.ProjectWizard;
import com.liferay.ide.ui.tests.swtbot.page.RadioPageObject;
import com.liferay.ide.ui.tests.swtbot.page.TextPageObject;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Li Lu
 */
public class ChooseJSFSuitePageObject<T extends SWTBot> extends CreateLiferayPortletWizardPageObject<T>
    implements ProjectWizard
{

    static int defaultValidationMessageIndex = 1;

    TextPageObject<SWTBot> archetype;

    RadioPageObject<SWTBot> iCEfaces;

    RadioPageObject<SWTBot> jSFstandard;

    RadioPageObject<SWTBot> liferayFacesAlloy;

    RadioPageObject<SWTBot> primeFaces;

    RadioPageObject<SWTBot> richFaces;

    TextPageObject<SWTBot> viewFolder;

    public ChooseJSFSuitePageObject( T bot )
    {
        this( bot, TEXT_BLANK, defaultValidationMessageIndex );
    }

    public ChooseJSFSuitePageObject( T bot, String title, int validationMessageIndex )
    {
        super( bot, TEXT_BLANK, validationMessageIndex );
        jSFstandard = new RadioPageObject<SWTBot>( bot, LABEL_JSF_STANDARD );
        iCEfaces = new RadioPageObject<SWTBot>( bot, LABLE_ICE_FACES );
        liferayFacesAlloy = new RadioPageObject<SWTBot>( bot, LABLE_LIFERAY_FACES_ALLOY );
        primeFaces = new RadioPageObject<SWTBot>( bot, LABLE_PRIME_FACES );
        richFaces = new RadioPageObject<SWTBot>( bot, LABLE_RICH_FACES );
    }

    public TextPageObject<SWTBot> getArchetype()
    {
        return archetype;
    }

    public RadioPageObject<SWTBot> getiCEfaces()
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
        return jSFstandard;
    }

    public TextPageObject<SWTBot> getViewFolder()
    {
        return viewFolder;
    }

}
