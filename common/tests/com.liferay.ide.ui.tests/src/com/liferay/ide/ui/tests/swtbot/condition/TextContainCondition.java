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
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;

/**
 * ICondition implementation to wait for the content of a text control to contain or not to contain a specific string
 * value This is useful when the content change of a text takes a while after the initiating user action has been
 * carried out
 *
 * @author Kay-Uwe Graw &lt;kugraw [at] web [dot] de&gt;
 */
public class TextContainCondition implements ICondition
{

    private final String content;
    private final SWTBotText text;
    private final boolean contain;

    public TextContainCondition( SWTBotText text, String content, boolean contain )
    {
        this.content = content;
        this.text = text;
        this.contain = contain;
    }

    public String getFailureMessage()
    {
        if( contain )
        {
            return "wait for text content contains " + content + " failed"; //$NON-NLS-1$
        }
        else
        {
            return "wait for text content contains not " + content + " failed"; //$NON-NLS-1$
        }
    }

    public void init( SWTBot bot )
    {
    }

    public boolean test() throws Exception
    {
        return text.getText().contains( content ) == contain;
    }
}
