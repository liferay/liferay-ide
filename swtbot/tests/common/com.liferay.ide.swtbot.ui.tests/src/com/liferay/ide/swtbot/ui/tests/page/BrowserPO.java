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
import org.eclipse.swtbot.swt.finder.widgets.SWTBotBrowser;

/**
 * @author Terry Jia
 */
public class BrowserPO extends AbstractWidgetPO
{

    public BrowserPO( SWTBot bot )
    {
        super( bot );
    }

    public BrowserPO( SWTBot bot, String label )
    {
        super( bot, label );
    }

    @Override
    protected SWTBotBrowser getWidget()
    {
        if( label == null )
        {
            return bot.browser();
        }

        return bot.browserWithLabel( label );
    }

    public void setUrl( String url )
    {
        getWidget().setUrl( url );
    }

}
