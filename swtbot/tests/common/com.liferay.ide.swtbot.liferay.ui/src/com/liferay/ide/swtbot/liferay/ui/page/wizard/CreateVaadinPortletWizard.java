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

import com.liferay.ide.swtbot.ui.page.ComboBox;
import com.liferay.ide.swtbot.ui.page.Text;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Li Lu
 */
public class CreateVaadinPortletWizard extends CreateLiferayPortletWizard
{

    private Text applicationClass;
    private ComboBox portletClasses;
    private ComboBox vaadinPortletClasses;

    public CreateVaadinPortletWizard( SWTBot bot )
    {
        super( bot, 3 );

        applicationClass = new Text( bot, APPLICATION_CLASS );
        vaadinPortletClasses = new ComboBox( bot, PORTLET_CLASS );
        portletClasses = new ComboBox( bot, PORTLET_CLASS );
    }

    public Text getApplicationClass()
    {
        return applicationClass;
    }

    public ComboBox getPortletClasses()
    {
        return portletClasses;
    }

    public ComboBox getVaadinPortletClasses()
    {
        return vaadinPortletClasses;
    }

}
