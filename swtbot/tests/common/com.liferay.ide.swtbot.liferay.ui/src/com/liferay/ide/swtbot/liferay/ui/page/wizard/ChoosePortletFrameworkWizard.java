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

import com.liferay.ide.swtbot.ui.page.Radio;
import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.Wizard;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Li Lu
 */
public class ChoosePortletFrameworkWizard extends Wizard
{

    private Text archetype;
    private Text displayName;
    private Radio jsf2x;
    private Radio liferayMvc;
    private Radio springMvc;
    private Radio vaadin;

    public ChoosePortletFrameworkWizard( SWTBot bot )
    {
        super( bot, 3 );

        liferayMvc = new Radio( bot, LIFERAY_MVC );
        jsf2x = new Radio( bot, JSF_2_X );
        springMvc = new Radio( bot, SPRING_MVC );
        vaadin = new Radio( bot, VAADIN );
        displayName = new Text( bot, DISPLAY_NAME );
        archetype = new Text( bot, ARCHETYPE );
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
        case LIFERAY_MVC:

            liferayMvc.click();
            break;
        case JSF_2_X:
            jsf2x.click();
            break;

        case SPRING_MVC:
            springMvc.click();
            break;
        case VAADIN:
            vaadin.click();
        }
    }
}
