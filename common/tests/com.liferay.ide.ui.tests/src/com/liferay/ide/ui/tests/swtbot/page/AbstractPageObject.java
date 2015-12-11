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

import org.apache.log4j.Logger;
import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * base class for implementing the page object pattern with swtbot
 *
 * @author Kay-Uwe Graw &lt;kugraw [at] web [dot] de&gt;
 * @param <T>
 *            - the bot class
 */
public abstract class AbstractPageObject<T extends SWTBot>
{

    protected final Logger log;

    protected final T bot;

    private final long DEFAULT_SLEEP_MILLIS = 1000;

    public AbstractPageObject( T bot )
    {
        this.bot = bot;

        log = Logger.getLogger( this.getClass() );
    }

    protected void sleep()
    {
        sleep( DEFAULT_SLEEP_MILLIS );
    }

    protected void sleep( long millis )
    {
        bot.sleep( millis );
    }
}
