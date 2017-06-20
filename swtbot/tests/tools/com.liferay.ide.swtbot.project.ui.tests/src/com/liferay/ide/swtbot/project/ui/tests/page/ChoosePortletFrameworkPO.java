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

package com.liferay.ide.swtbot.project.ui.tests.page;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.project.ui.tests.ProjectWizard;
import com.liferay.ide.swtbot.ui.tests.page.RadioPO;
import com.liferay.ide.swtbot.ui.tests.page.TextPO;

/**
 * @author Li Lu
 */
public class ChoosePortletFrameworkPO extends CreateProjectWizardPO implements ProjectWizard
{

    static int defaultvalidationMessageIndex = 3;
    private TextPO _archetypeText;
    private TextPO _displayNameText;
    private RadioPO _jsf2xRadio;
    private RadioPO _liferayMVCRadio;
    private RadioPO _springMVCRadio;
    private RadioPO _vaadinRadio;

    RadioPO vaadin;

    public ChoosePortletFrameworkPO( SWTBot bot )
    {
        this( bot, TEXT_BLANK, defaultvalidationMessageIndex );

    }

    public ChoosePortletFrameworkPO( SWTBot bot, String title, int validationMessageIndex )
    {
        super( bot, title, validationMessageIndex );
        _liferayMVCRadio = new RadioPO( bot, LABEL_LIFERAY_MVC_FRAMEWORK );
        _jsf2xRadio = new RadioPO( bot, LABEL_JSF_FRAMEWORK );
        _springMVCRadio = new RadioPO( bot, LABEL_SPRING_MVC_FRAMEWORK );
        _vaadinRadio = new RadioPO( bot, LABEL_VAADIN_FRAMEWORK );
        _displayNameText = new TextPO( bot, LABEL_DISPLAY_NAME );
        _archetypeText = new TextPO( bot, LABLE_ARCHETYPE );
    }

    public TextPO getArchetypeText()
    {
        return _archetypeText;
    }

    public TextPO getDisplayNameText()
    {
        return _displayNameText;
    }

    public void selectPortletFramework( String label )
    {
        switch( label )
        {
        case LABEL_LIFERAY_MVC_FRAMEWORK:

            _liferayMVCRadio.click();
            break;
        case LABEL_JSF_FRAMEWORK:
            _jsf2xRadio.click();
            break;

        case LABEL_SPRING_MVC_FRAMEWORK:
            _springMVCRadio.click();
            break;
        case LABEL_VAADIN_FRAMEWORK:
            _vaadinRadio.click();
        }
    }
}
