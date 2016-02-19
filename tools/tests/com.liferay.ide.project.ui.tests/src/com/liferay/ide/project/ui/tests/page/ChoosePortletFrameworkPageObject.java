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
public class ChoosePortletFrameworkPageObject<T extends SWTBot> extends CreateLiferayPortletWizardPageObject<T>
    implements ProjectWizard
{

    static int defaultvalidationMessageIndex = 3;
    TextPageObject<SWTBot> archetype;
    TextPageObject<SWTBot> displayName;
    RadioPageObject<SWTBot> jsf2x;
    RadioPageObject<SWTBot> liferayMVC;
    TextPageObject<SWTBot> portletName;
    RadioPageObject<SWTBot> springMVC;

    RadioPageObject<SWTBot> vaadin;

    public ChoosePortletFrameworkPageObject( T bot )
    {
        this( bot, TEXT_BLANK, defaultvalidationMessageIndex );

    }

    public ChoosePortletFrameworkPageObject( T bot, String title, int validationMessageIndex )
    {
        super( bot, title, validationMessageIndex );
        liferayMVC = new RadioPageObject<SWTBot>( bot, TEXT_LIFERAY_MVC_FRAMEWORK );
        jsf2x = new RadioPageObject<SWTBot>( bot, TEXT_JSF_FRAMEWORK );
        springMVC = new RadioPageObject<SWTBot>( bot, TEXT_SPRING_MVC_FRAMEWORK );
        vaadin = new RadioPageObject<SWTBot>( bot, TEXT_VAADIN_FRAMEWORK );
        portletName = new TextPageObject<SWTBot>( bot, LABEL_PORTLET_NAME );
        displayName = new TextPageObject<SWTBot>( bot, LABEL_DISPLAY_NAME );
        archetype = new TextPageObject<SWTBot>( bot, LABLE_ARCHETYPE );
    }

    public TextPageObject<SWTBot> getArchetype()
    {
        return archetype;
    }

    public TextPageObject<SWTBot> getDisplayName()
    {
        return displayName;
    }

    public RadioPageObject<SWTBot> getJsf2x()
    {
        return jsf2x;
    }

    public RadioPageObject<SWTBot> getLiferayMVC()
    {
        return liferayMVC;
    }

    public TextPageObject<SWTBot> getPortletName()
    {
        return portletName;
    }

    public RadioPageObject<SWTBot> getSpringMVC()
    {
        return springMVC;
    }

    public RadioPageObject<SWTBot> getVaadin()
    {
        return vaadin;
    }

}
