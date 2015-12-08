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

import com.liferay.ide.ui.tests.swtbot.condition.ViewVisibleCondition;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;

/**
 * base class for page objects representing an eclipse view
 *
 * @author Kay-Uwe Graw &lt;kugraw [at] web [dot] de&gt;
 */
public class ViewPageObject extends AbstractPageObject<SWTWorkbenchBot>
{

    private final String identifier;

    private final boolean isId;

    public ViewPageObject( SWTWorkbenchBot bot, String identifier, boolean isId )
    {
        super( bot );

        this.identifier = identifier;

        this.isId = isId;
    }

    public ViewPageObject( SWTWorkbenchBot bot, String viewIdentifier )
    {
        this( bot, viewIdentifier, true );
    }

    public void close()
    {
        getView().close();

        bot.waitUntil( new ViewVisibleCondition( identifier, false, isId ) );
    }

    public void waitForPageToOpen()
    {
        bot.waitUntil( new ViewVisibleCondition( identifier, true, isId ) );
    }

    protected SWTBotView getView()
    {
        if( isId )
        {
            return bot.viewById( identifier );
        }
        else
        {
            return bot.viewByTitle( identifier );
        }
    }
}
