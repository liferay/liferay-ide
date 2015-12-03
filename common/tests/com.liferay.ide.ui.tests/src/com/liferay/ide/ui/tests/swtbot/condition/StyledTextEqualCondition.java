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
 * ICondition implementation to wait for the content of styled text control to become equal or unequal to a specific
 * string value This is useful when the content change of a styled text takes a while after the initiating user action
 * has been carried out
 *
 * @author Kay-Uwe Graw &lt;kugraw [at] web [dot] de&gt;
 */
public class StyledTextEqualCondition implements ICondition
{

    private final String content;
    private final SWTBotStyledText styledText;
    private final boolean equal;

    public StyledTextEqualCondition( SWTBotStyledText styledText, String content, boolean equal )
    {
        this.content = content;
        this.styledText = styledText;
        this.equal = equal;
    }

    public String getFailureMessage()
    {
        if( equal )
        {
            return "wait for styled text equals " + content + " failed"; //$NON-NLS-1$
        }
        else
        {
            return "wait for styled text not equals " + content + " failed"; //$NON-NLS-1$
        }
    }

    public void init( SWTBot bot )
    {
    }

    public boolean test() throws Exception
    {
        return styledText.getText().equals( content ) == equal;
    }
}
