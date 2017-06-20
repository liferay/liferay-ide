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

package com.liferay.ide.swtbot.portlet.ui.tests.page;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.portlet.ui.tests.VaadinPortletWizard;
import com.liferay.ide.swtbot.ui.tests.page.ComboBoxPO;
import com.liferay.ide.swtbot.ui.tests.page.TextPO;

/**
 * @author Li Lu
 */
public class CreateVaadinPortletWizardPO extends CreateLiferayPortletWizardPO implements VaadinPortletWizard
{

    private TextPO _applicationClassText;

    private ComboBoxPO _vaadinPortletClassCombo;
    private ComboBoxPO _portletClassCombo;

    public CreateVaadinPortletWizardPO( SWTBot bot )
    {
        this( bot, INDEX_VAADIN_VALIDATION_MESSAGE1 );
    }

    public CreateVaadinPortletWizardPO( SWTBot bot, int validationMessageIndex )
    {
        super( bot, TEXT_BLANK, validationMessageIndex );

        _applicationClassText = new TextPO( bot, LABEL_APPLICATION_CLASS );
        _vaadinPortletClassCombo = new ComboBoxPO( bot, LABEL_PORTLET_CLASS );
        _portletClassCombo = new ComboBoxPO( bot, LABEL_PORTLET_CLASS );
    }

    public String getApplicationClassText()
    {
        return _applicationClassText.getText();
    }

    public String getVaadinPortletClassText()
    {
        return _vaadinPortletClassCombo.getText();
    }

    public String[] getVaadinPortletClassValues()
    {
        return _vaadinPortletClassCombo.getAvailableComboValues();
    }

    public void setApplicationClassText( String text )
    {
        _applicationClassText.setText( text );
    }

    public void setVaadinPortletClassText( String text )
    {
        _vaadinPortletClassCombo.setText( text );
    }

    public void setPortletClassText( String text )
    {
        _portletClassCombo.setText( text );
    }

    public String getPortletClassText()
    {
        return _portletClassCombo.getText();
    }

}
