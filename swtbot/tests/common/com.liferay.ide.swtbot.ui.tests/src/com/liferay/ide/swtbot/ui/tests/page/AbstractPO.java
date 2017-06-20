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

import org.apache.log4j.Logger;
import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public abstract class AbstractPO
{

    protected final Logger log;

    protected final SWTBot bot;

    private final long DEFAULT_SLEEP_MILLIS = 1000;

    public AbstractPO( SWTBot bot )
    {
        this.bot = bot;

        log = Logger.getLogger( this.getClass() );
    }

    public void sleep()
    {
        sleep( DEFAULT_SLEEP_MILLIS );
    }

    public void sleep( long millis )
    {
        bot.sleep( millis );
    }

}
