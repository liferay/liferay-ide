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

package com.liferay.ide.swtbot.ui.tests.page;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public class WizardPO extends CancelPO
{

    private final String finishButtonText;
    private final String backButtonText;
    private final String nextButtonText;
    private int validationMessageIndex = -1;

    public WizardPO(
        SWTBot bot, String title, String cancelButtonText, String finishButtonText, String backButtonText,
        String nextButtonText )
    {
        super( bot, title, cancelButtonText );

        this.finishButtonText = finishButtonText;

        this.backButtonText = backButtonText;

        this.nextButtonText = nextButtonText;

    }

    public WizardPO(
        SWTBot bot, String title, String cancelButtonText, String finishButtonText, String backButtonText,
        String nextButtonText, int validationMessageIndex )
    {
        super( bot, title, cancelButtonText );

        this.finishButtonText = finishButtonText;

        this.backButtonText = backButtonText;

        this.nextButtonText = nextButtonText;

        this.validationMessageIndex = validationMessageIndex;
    }

    public void back()
    {
        clickButton( backButton() );
    }

    public SWTBotButton backButton()
    {
        return bot.button( backButtonText );
    }

    public void finish()
    {
        clickClosingButton( finishButton() );

        sleep(5000);
    }

    public SWTBotButton finishButton()
    {
        return bot.button( finishButtonText );
    }

    public String getValidationMessage()
    {
        if( validationMessageIndex < 0 )
        {
            log.error( "Validation Message Index error" );

            return null;
        }

        return bot.text( validationMessageIndex ).getText();
    }

    public void next()
    {
        clickButton( nextButton() );
    }

    public SWTBotButton nextButton()
    {
        return bot.button( nextButtonText );
    }

    public void setTitle( String title )
    {
        this.title = title;
    }

}
