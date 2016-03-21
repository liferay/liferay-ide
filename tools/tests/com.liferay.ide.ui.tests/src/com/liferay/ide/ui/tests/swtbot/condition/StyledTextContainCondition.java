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

package com.liferay.ide.ui.tests.swtbot.condition;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.ICondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotStyledText;

/**
 * ICondition implementation to wait for the content of a styled text control to contain or not to contain a specific
 * string value This is useful when the content change of a styled text takes a while after the initiating user action
 * has been carried out
 *
 * @author Kay-Uwe Graw &lt;kugraw [at] web [dot] de&gt;
 */
public class StyledTextContainCondition implements ICondition
{

    private final String content;
    private final SWTBotStyledText styledText;
    private final boolean contain;

    public StyledTextContainCondition( SWTBotStyledText styledText, String content, boolean contain )
    {
        this.content = content;
        this.styledText = styledText;
        this.contain = contain;
    }

    public String getFailureMessage()
    {
        if( contain )
        {
            return "wait for styled text to contain " + content + " failed"; //$NON-NLS-1$
        }
        else
        {
            return "wait for styled text not to contain " + content + " failed"; //$NON-NLS-1$
        }
    }

    public void init( SWTBot bot )
    {
    }

    public boolean test() throws Exception
    {
        return styledText.getText().contains( content ) == contain;
    }
}
