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

import com.liferay.ide.portlet.ui.tests.swtbot.VaadinPortletWizard;
import com.liferay.ide.project.ui.tests.page.CreateLiferayPortletWizardPageObject;
import com.liferay.ide.ui.tests.swtbot.page.ComboBoxPageObject;
import com.liferay.ide.ui.tests.swtbot.page.TextPageObject;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Li Lu
 */
public class CreateVaadinPortletWizardPageObject<T extends SWTBot> extends CreateLiferayPortletWizardPageObject<T>
    implements VaadinPortletWizard
{

    TextPageObject<SWTBot> applicationClass;

    ComboBoxPageObject<SWTBot> vaadinPortletClass;

    public CreateVaadinPortletWizardPageObject( T bot )
    {
        this( bot, INDEX__VAADIN_VALIDATION_MESSAGE1 );
    }

    public CreateVaadinPortletWizardPageObject( T bot, int validationMessageIndex )
    {
        super( bot, TEXT_BLANK, validationMessageIndex );

        applicationClass = new TextPageObject<SWTBot>( bot, LABEL_APPLICATION_CLASS );
        vaadinPortletClass = new ComboBoxPageObject<SWTBot>( bot, LABEL_PORTLET_CLASS );
    }

    public TextPageObject<SWTBot> getApplicationClass()
    {
        return applicationClass;
    }

    public ComboBoxPageObject<SWTBot> getVaadinPortletClass()
    {
        return vaadinPortletClass;
    }

}
