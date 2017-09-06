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

package com.liferay.ide.swtbot.liferay.ui.page.wizard.project;

import com.liferay.ide.swtbot.ui.page.Radio;
import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.Wizard;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Ashley Yuan
 * @author Li Lu
 */
public class SelectPortletFrameworkWizard extends Wizard
{

    private final Text displayName;
    private final Radio jsf;
    private final Radio liferayMvc;
    private final Text portletName;
    private final Radio springMvc;
    private final Radio vaadin;

    public SelectPortletFrameworkWizard( final SWTWorkbenchBot bot )
    {
        super( bot );

        liferayMvc = new Radio( bot, LIFERAY_MVC );
        jsf = new Radio( bot, JSF_2_X );
        springMvc = new Radio( bot, SPRING_MVC );
        vaadin = new Radio( bot, VAADIN );
        portletName = new Text( bot, PORTLET_NAME );
        displayName = new Text( bot, DISPLAY_NAME );
    }

    public Text getDisplayName()
    {
        return displayName;
    }

    public Radio getJsf()
    {
        return jsf;
    }

    public Radio getLiferayMvc()
    {
        return liferayMvc;
    }

    public Text getPortletName()
    {
        return portletName;
    }

    public Radio getSpringMvc()
    {
        return springMvc;
    }

    public Radio getVaadin()
    {
        return vaadin;
    }

}
