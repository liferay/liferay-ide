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

import com.liferay.ide.swtbot.liferay.ui.WizardUI;
import com.liferay.ide.swtbot.ui.page.Radio;
import com.liferay.ide.swtbot.ui.page.Text;

/**
 * @author Li Lu
 */
public class ChoosePortletFrameworkWizard extends NewSdkProjectWizard implements WizardUI
{

    static int defaultvalidationMsgIndex = 3;
    private Text archetype;
    private Text displayName;
    private Radio jsf2x;
    private Radio liferayMvc;
    private Radio springMvc;
    private Radio vaadin;

    public ChoosePortletFrameworkWizard( SWTBot bot )
    {
        this( bot, TEXT_BLANK, defaultvalidationMsgIndex );

    }

    public ChoosePortletFrameworkWizard( SWTBot bot, String title, int validationMsgIndex )
    {
        super( bot, title, validationMsgIndex );

        liferayMvc = new Radio( bot, LABEL_LIFERAY_MVC_FRAMEWORK );
        jsf2x = new Radio( bot, LABEL_JSF_FRAMEWORK );
        springMvc = new Radio( bot, LABEL_SPRING_MVC_FRAMEWORK );
        vaadin = new Radio( bot, LABEL_VAADIN_FRAMEWORK );
        displayName = new Text( bot, LABEL_DISPLAY_NAME );
        archetype = new Text( bot, LABLE_ARCHETYPE );
    }

    public Text getArchetypeText()
    {
        return archetype;
    }

    public Text getDisplayNameText()
    {
        return displayName;
    }

    public void selectPortletFramework( String label )
    {
        switch( label )
        {
        case LABEL_LIFERAY_MVC_FRAMEWORK:

            liferayMvc.click();
            break;
        case LABEL_JSF_FRAMEWORK:
            jsf2x.click();
            break;

        case LABEL_SPRING_MVC_FRAMEWORK:
            springMvc.click();
            break;
        case LABEL_VAADIN_FRAMEWORK:
            vaadin.click();
        }
    }
}
