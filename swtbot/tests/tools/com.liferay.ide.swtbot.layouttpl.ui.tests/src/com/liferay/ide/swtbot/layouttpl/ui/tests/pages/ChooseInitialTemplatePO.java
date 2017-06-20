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

package com.liferay.ide.swtbot.layouttpl.ui.tests.pages;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.layouttpl.ui.tests.CreateLayouttplWizard;
import com.liferay.ide.swtbot.ui.tests.page.RadioPO;
import com.liferay.ide.swtbot.ui.tests.page.WizardPO;

/**
 * @author Li Lu
 */
public class ChooseInitialTemplatePO extends WizardPO implements CreateLayouttplWizard
{

    public ChooseInitialTemplatePO( SWTBot bot )
    {
        this( bot, TEXT_BLANK, BUTTON_CANCEL, BUTTON_FINISH, BUTTON_BACK, BUTTON_NEXT, INDEX_LAYOUTTPL_VALIDATION_MESSAGE2 );
    }

    public ChooseInitialTemplatePO(
        SWTBot bot, String title, String cancelButtonText, String finishButtonText, String backButtonText,
        String nextButtonText, int validationMessageIndex )
    {
        super( bot, title, cancelButtonText, finishButtonText, backButtonText, nextButtonText, validationMessageIndex );
    }

    public boolean isRadioSelected( int index )
    {
        return new RadioPO( bot, index ).isSelected();
    }

    public boolean isRadioSelected( String label )
    {
        return new RadioPO( bot, label ).isSelected();
    }

    public void selectRadio( int index )
    {
        new RadioPO( bot, index ).click();
    }

    public void selectRadio( String label )
    {
        new RadioPO( bot, label ).click();
    }
}
