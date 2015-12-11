/*******************************************************************************
 * Copyright (c) 2008 Ketan Padegaonkar and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Kay-Uwe Graw - initial API and implementation

 *******************************************************************************/

package com.liferay.ide.ui.tests.swtbot.page;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;

/**
 * base class for page objects representing a wizard
 *
 * @author Kay-Uwe Graw &lt;kugraw [at] web [dot] de&gt;
 */
public class WizardPageObject<T extends SWTBot> extends CancelPageObject<T>
{

    private final String finishButtonText;
    private final String backButtonText;
    private final String nextButtonText;
    private int validationMessageIndex = -1;

    public WizardPageObject(
        T bot, String title, String cancelButtonText, String finishButtonText, String backButtonText,
        String nextButtonText )
    {
        super( bot, title, cancelButtonText );

        this.finishButtonText = finishButtonText;

        this.backButtonText = backButtonText;

        this.nextButtonText = nextButtonText;
    }

    public WizardPageObject(
        T bot, String title, String cancelButtonText, String finishButtonText, String backButtonText,
        String nextButtonText, int validationMessageIndex )
    {
        super( bot, title, cancelButtonText );

        this.finishButtonText = finishButtonText;

        this.backButtonText = backButtonText;

        this.nextButtonText = nextButtonText;

        this.validationMessageIndex = validationMessageIndex;
    }

    public void finish()
    {
        clickClosingButton( finishButton() );
    }

    protected SWTBotButton finishButton()
    {
        return bot.button( finishButtonText );
    }

    public void back()
    {
        clickButton( backButton() );
    }

    protected SWTBotButton backButton()
    {
        return bot.button( backButtonText );
    }

    public void next()
    {
        clickButton( nextButton() );
    }

    protected SWTBotButton nextButton()
    {
        return bot.button( nextButtonText );
    }

    public void setTitle( String title )
    {
        this.title = title;
    }

    public String getValidationMessage()
    {
        sleep();

        if( validationMessageIndex < 0 )
        {
            log.error( "Validation Message Index error" );

            return null;
        }

        return bot.text( validationMessageIndex ).getText();
    }
}
