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

package com.liferay.ide.swtbot.ui.page;

import com.liferay.ide.swtbot.ui.UI;
import com.liferay.ide.swtbot.ui.util.StringPool;

import org.apache.log4j.Logger;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public class BasePageObject implements UI
{

    protected SWTWorkbenchBot bot;

    private long DEFAULT_SLEEP_MILLIS = 1000;

    protected int index = -1;
    protected String label = "";

    protected Logger log;

    public BasePageObject( SWTWorkbenchBot bot )
    {
        this.bot = bot;

        log = Logger.getLogger( this.getClass() );
    }

    public BasePageObject( final SWTWorkbenchBot bot, final int index )
    {
        this( bot );

        this.index = index;
    }

    public BasePageObject( final SWTWorkbenchBot bot, final String label )
    {
        this( bot );

        this.label = label;
    }

    public BasePageObject( final SWTWorkbenchBot bot, final String label, final int index )
    {
        this( bot );

        this.label = label;
        this.index = index;
    }

    protected boolean hasIndex()
    {
        return index >= 0;
    }

    protected boolean isLabelNull()
    {
        return label.equals( StringPool.BLANK );
    }

    public void sleep()
    {
        sleep( DEFAULT_SLEEP_MILLIS );
    }

    public void sleep( final long millis )
    {
        bot.sleep( millis );
    }

}
